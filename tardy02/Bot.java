package tardy02;

import java.util.Random;

import battlecode.common.*;

public abstract class Bot {
	Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
			Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };

	RobotController rc;
	Random rand;
	int sightRange;
	int attackRange;
	Team myTeam;
	Team enemyTeam;
	MapInfo mapInfo;
	MapLocation parent = null;
	int mode;

	public Bot(RobotController rc) {
		try {
			// inicializace

			this.rc = rc;
			rand = new Random(rc.getID());
			sightRange = rc.getType().sensorRadiusSquared;
			attackRange = rc.getType().attackRadiusSquared;
			myTeam = rc.getTeam();
			enemyTeam = myTeam.opponent();
			mapInfo = new MapInfo(rc.getInitialArchonLocations(myTeam), rc.getInitialArchonLocations(enemyTeam));
			mode = 0;

			// Urci, ktery archon te vyrobil a uloz jeho pozici do 'parent'.
			// Tohle neni uplne zaruceny, obcas archon hnedka uhne, takze neni
			// do vzdalenosti 2. Pokud to nastane, tak si jako parent uloz svoji
			// pocatecni pozici.
			RobotInfo[] nearbyRobots = rc.senseNearbyRobots(2, myTeam);
			for (RobotInfo ri : nearbyRobots) {
				if (ri.type == RobotType.ARCHON) {
					parent = ri.location;
					break;
				}
			}
			if (parent == null) {
				parent = rc.getLocation();
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// hlavni nekonecny cyklus robota
	public void loop() {
		while (true) {

			try {
				processSignals();
				set_mode();
				run();
				repair();

				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	// Metoda obsahujici hlavni rozhodovaci mechanismus robota.
	protected abstract void run() throws GameActionException;

	// Metoda volana v kazdem cyklu, ktera ma za ukol nastavit mod robota pro
	// aktualni kolo.
	protected abstract void set_mode() throws GameActionException;

	// V pripade archona tato metoda zajisti opraveni nektereho naseho robota.
	// Kazdy robot ji vola na konci sveho kola.
	protected void repair() throws GameActionException {
		if (rc.getType() == RobotType.ARCHON) {
			RobotInfo[] nearbyRobots = rc.senseNearbyRobots(attackRange, myTeam);
			for (RobotInfo ri : nearbyRobots) {
				if (ri.maxHealth > ri.health && ri.type != RobotType.ARCHON) {
					rc.repair(ri.location);
					break;
				}
			}
		}
	}

	// Zpracovani prichozich signalu. Kazdy robot ji vola na zacatku sveho kola.
	protected void processSignals() throws GameActionException {
		Signal[] signals = rc.emptySignalQueue();
		for (Signal s : signals) {
			if (s.getTeam() != myTeam) {
				continue;
			}
			if (s.getMessage().length == 2) {
				switch (s.getMessage()[0]) {
				case SignalUtils.ZOMBIE_DEN:
					mapInfo.addZombieDen(SignalUtils.decode(s.getMessage()[1]));
					break;
				}
			}
		}
	}

	// Spocita teziste (~prumer) z pole pozic.
	protected MapLocation center(MapLocation[] locations) {
		if (locations.length == 0) {
			return rc.getLocation();
		}
		int centerX = 0;
		int centerY = 0;
		for (MapLocation ml : locations) {
			centerX += ml.x;
			centerY += ml.y;
		}
		centerX /= locations.length;
		centerY /= locations.length;
		return new MapLocation(centerX, centerY);
	}

	// Spocita teziste (~prumer) z pole inforaci o robotech.
	protected MapLocation center(RobotInfo[] array) {
		if (array.length == 0) {
			return rc.getLocation();
		}
		int centerX = 0;
		int centerY = 0;
		for (RobotInfo ri : array) {
			centerX += ri.location.x;
			centerY += ri.location.y;
		}
		centerX /= array.length;
		centerY /= array.length;
		return new MapLocation(centerX, centerY);
	}

	// Najde vhodny cil pro utok do vzdalenosti 'range'.
	protected RobotInfo findTarget(int range) {
		RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(Math.min(range, sightRange), enemyTeam);
		RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(Math.min(range, sightRange), Team.ZOMBIE);

		RobotInfo target = null;

		for (RobotInfo ri : zombiesWithinRange) {
			if (target == null) {
				target = ri;
			} else if (ri.health < target.health) {
				target = ri;
			}
		}

		if (target != null)
			return target;

		for (RobotInfo ri : enemiesWithinRange) {
			if (target == null) {
				target = ri;
			} else if (ri.health < target.health) {
				target = ri;
			}
		}

		return target;
	}

	// Metoda pohybuje robotem, preferuje smer 'dir'. Radeji zvoli kterykoliv
	// smer, pak teprve odklizi rubble.
	protected Direction escape(Direction dir) throws GameActionException {

		Direction[] directions = { dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(),
				dir.rotateRight().rotateRight(), dir.rotateRight().opposite(), dir.rotateLeft().opposite(),
				dir.opposite() };

		for (Direction d : directions) {
			if (rc.canMove(d)) {
				rc.move(d);
				return d;
			}
		}

		for (Direction d : directions) {
			if (rc.senseRubble(rc.getLocation().add(d)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
				rc.clearRubble(d);
				return d;
			}
		}

		return Direction.OMNI;
	}

	// Metoda pohybuje robotem, preferuje smer 'dir'. Umoznuje pohyb pouze ve
	// smeru 'dir' +/- 1. Pokud neni mozny pohyb, odklizi rubble.
	protected Direction move(Direction dir) throws GameActionException {
		Direction[] directions = { dir, dir.rotateLeft(), dir.rotateRight() };

		for (Direction d : directions) {
			if (rc.canMove(d)) {
				rc.move(d);
				return d;
			}
		}

		for (Direction d : directions) {
			if (rc.senseRubble(rc.getLocation().add(d)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
				rc.clearRubble(d);
				return d;
			}
		}

		return Direction.OMNI;
	}
}
