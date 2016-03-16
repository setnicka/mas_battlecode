package scouts;

import battlecode.common.*;

public class BotScout extends Bot {

	MapLocation target;
	int ZOMBIE_DISTANCE_LIMIT = 16;

	public BotScout(RobotController rc) {
		super(rc);

		try {
			// Sem patri kod, ktery se spusti pri vytvoreni robota.
			// Kod, ktery pravdepodobne vyuzije kazdy typ robota, by mel prijit
			// do konstruktoru tridy Bot.

			// nahodne vyber jednen cil, ke kteremu scout pujde
			int r = rand.nextInt(enemyInitialLoc.length);
			// z nejakeho duvudu mi prvni rand.nextInt(enemyInitialLoc.length)
			// vzdycky vrati 1, proto volam podruhe
			r = rand.nextInt(enemyInitialLoc.length);
			target = enemyInitialLoc[r];
			rc.setIndicatorString(0, "Tar: " + target);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	// pokud je zombie dal nez nastaveny limit a nasim pohybem bychom ji utekli jeste vic,
	// tak se rozhodneme nepohnout
	// idea je nesetrast ziskane zombiky cestou a dovest je do cile
	public void moveIfZombieNear(RobotInfo nearest, Direction dir) throws Exception {
		if (nearest != null) {
			int distance = nearest.location.distanceSquaredTo(rc.getLocation());
			int new_distance = nearest.location.distanceSquaredTo(rc.getLocation().add(dir));
			if (distance > ZOMBIE_DISTANCE_LIMIT && new_distance > distance) return;
		}
		rc.move(dir);
	}

	public void loop() {

		while (true) {
			try {
				MapLocation myLoc = rc.getLocation();
				if (myLoc.equals(target)) {
					// cil byl uz znicen nebo nebo presunul
					// vyber novy cil
					int r = rand.nextInt(enemyInitialLoc.length);
					target = enemyInitialLoc[r];
					rc.setIndicatorString(0, "Tar: " + target);
				}

				if (rc.isCoreReady()) {
					// naviguj se za svym 'target'
					
					// asi by chtelo doresit, ze tam souperuv archon uz vubec
					// byt nemusi, chtelo by to cestou koukat, jestli jsem
					// ho nahodou nepotkal

					// tri smery serazene podle priority
					Direction dir_1, dir_2, dir_3;
					dir_1 = myLoc.directionTo(target);
					dir_2 = dir_1.rotateLeft();
					dir_3 = dir_1.rotateRight();

					// podivame se, jaka je nejblizsi zombie
					RobotInfo nearest = null;
					for (RobotInfo robot: rc.senseNearbyRobots()) {
						if (robot.team != Team.ZOMBIE) continue;
						if (nearest == null) nearest = robot;
						int distance_nearest = nearest.location.distanceSquaredTo(rc.getLocation());
						int distance = robot.location.distanceSquaredTo(rc.getLocation());
						if (distance < distance_nearest) nearest = robot;
					}

					// zkus se pohnout jednim z techto smeru, pokud to nelze
					// kvuli rubble, odstran ho, jinak zvol nahodny smer
					if (rc.canMove(dir_1)) {
						//rc.move(dir_1);
						moveIfZombieNear(nearest, dir_1);
					} else if (rc.canMove(dir_2)) {
						//rc.move(dir_2);
						moveIfZombieNear(nearest, dir_2);
					} else if (rc.canMove(dir_3)) {
						//rc.move(dir_3);
						moveIfZombieNear(nearest, dir_3);
					} else if (rc.senseRubble(rc.getLocation().add(dir_1)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
						rc.clearRubble(dir_1);
					} else if (rc.senseRubble(rc.getLocation().add(dir_2)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
						rc.clearRubble(dir_2);
					} else if (rc.senseRubble(rc.getLocation().add(dir_3)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
						rc.clearRubble(dir_3);
					} else {
						// vyber nejaky nahodny smer
						dir_1 = directions[rand.nextInt(8)];
						if (rc.canMove(dir_1)) {
							rc.move(dir_1);
						}
					}
				}

				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

	}
}
