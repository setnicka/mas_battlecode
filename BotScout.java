package tardyteam;

import battlecode.common.*;

public class BotScout extends Bot {

	MapLocation target;

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

					// zkus se pohnout jednim z techto smeru, pokud to nelze
					// kvuli rubble, odstran ho, jinak zvol nahodny smer
					if (rc.canMove(dir_1)) {
						rc.move(dir_1);
					} else if (rc.canMove(dir_2)) {
						rc.move(dir_2);
					} else if (rc.canMove(dir_3)) {
						rc.move(dir_3);
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
