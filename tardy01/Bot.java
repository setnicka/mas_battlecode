package tardy01;

import java.util.Random;
import battlecode.common.*;

public abstract class Bot {
	Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
			Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };

	Random rand;
	RobotController rc;
	int sightRange;
	int attackRange;
	Team myTeam;
	Team enemyTeam;
	MapLocation parent = null;
	int maxDistanceToParent = 20;

	public Bot(RobotController rc) {
		try {
			// Inicializace promennych pro vsechny typy robotu.

			this.rc = rc;
			rand = new Random(rc.getID());
			myTeam = rc.getTeam();
			enemyTeam = myTeam.opponent();
			sightRange = rc.getType().sensorRadiusSquared;
			attackRange = rc.getType().attackRadiusSquared;

			// urci, ktery archon te vyrobil a uloz jeho pozici do 'parent'
			if (rc.getType() != RobotType.ARCHON) {
				RobotInfo[] nearbyRobots = rc.senseNearbyRobots(2, myTeam);
				for (RobotInfo ri : nearbyRobots) {
					if (ri.type == RobotType.ARCHON) {
						parent = ri.location;
						break;
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public abstract void loop();

	// Pokusi se v danem smeru posunout, pripadne v nekterem ze sousednich
	// smeru. Pokud v zadnem z techto smeru nejde udelat krok, pokusi se v
	// techto smerech odklidit rubber.
	// Pokud provede nejakou akci, vrati true, jinak false (uz dela akci porad,
	// takze zbytecny :))
	protected boolean moveInDirection(Direction dir) throws GameActionException {
		MapLocation loc = rc.getLocation();
		if (rc.canMove(dir)) {
			rc.move(dir);
		} else if (rc.canMove(dir.rotateLeft())) {
			rc.move(dir.rotateLeft());
		} else if (rc.canMove(dir.rotateRight())) {
			rc.move(dir.rotateRight());
		} else if (rc.getType() != RobotType.TTM
				&& rc.senseRubble(loc.add(dir)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
			rc.clearRubble(dir);
		} else if (rc.getType() != RobotType.TTM
				&& rc.senseRubble(loc.add(dir.rotateLeft())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
			rc.clearRubble(dir.rotateLeft());
		} else if (rc.getType() != RobotType.TTM
				&& rc.senseRubble(loc.add(dir.rotateRight())) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
			rc.clearRubble(dir.rotateRight());
		} else {
			for (int i = 0; i < 10; i++) {
				dir = directions[rand.nextInt(directions.length)];
				if (rc.canMove(dir)) {
					rc.move(dir);
					break;
				}
			}
		}

		return true;
	}

	// Najde cil v dosahu 'range'. Pokud muzu utocit na zombii, volim tu s
	// nejnizsim poctem zivotu. Pokud neni zadna zombie v dostrelu, zvol
	// nepritele s nejnizsim poctem zivotu.
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

	// zpracuje frontu zprav
	protected void processSignals() {
		Signal[] allSignals = rc.emptySignalQueue();
		for (Signal s : allSignals) {
			if (s.getTeam() == myTeam && s.getMessage().length > 1) {
				if (s.getMessage()[0] == -1) {
					// uprava maximalni vzdalenosti od rodice zatim moc
					// nefunguje, protoze archon, ktery je zablokovan sam
					// navysuje max. vzd. vys a vys
					// maxDistanceToParent = s.getMessage()[1];
					// rc.setIndicatorString(0, "New max: " +
					// maxDistanceToParent);
				}
			}
		}
	}

	// zakoduje souradnice x,y do jednoho intu pomoci Cantor pairing function
	protected int encodeLocation(MapLocation ml) {
		return (ml.x + ml.y) * (ml.x + ml.y + 1) / 2 + ml.y;
	}

	// dekoduje souradnice x,y z jednoho intu pomoci Cantor pairing function
	protected MapLocation decodeLocation(int z) {
		int w = (int) Math.floor((Math.sqrt(8 * z + 1) - 1) / 2);
		int t = w * (w + 1) / 2;
		int y = z - t;
		int x = w - y;
		return new MapLocation(x, y);
	}
}
