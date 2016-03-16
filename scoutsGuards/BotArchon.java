package scoutsGuards;

import battlecode.common.*;

public class BotArchon extends Bot {

	public BotArchon(RobotController rc) {
		super(rc);

		try {
			// Sem patri kod, ktery se spusti pri vytvoreni robota.
			// Kod, ktery pravdepodobne vyuzije kazdy typ robota, by mel prijit
			// do konstruktoru tridy Bot.
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void loop() {

		while (true) {
			try {
				if (rc.isCoreReady()) {
					int nearbyZombies = 0;
					int nearbyEnemies = 0;
					RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
					for (RobotInfo ri : nearbyRobots) {
						if (ri.team == enemyTeam) nearbyEnemies++;
						else if	(ri.team == Team.ZOMBIE) nearbyZombies++;
					}
					
					RobotType typeToBuild = RobotType.SCOUT;
					if (nearbyEnemies + nearbyZombies > 0) typeToBuild = RobotType.GUARD;
					
					if (rc.hasBuildRequirements(typeToBuild)) {
						Direction dirToBuild = directions[rand.nextInt(8)];
						for (int i = 0; i < 8; i++) {
							if (rc.canBuild(dirToBuild, typeToBuild)) {
								rc.build(dirToBuild, typeToBuild);
								break;
							} else {
								dirToBuild = dirToBuild.rotateLeft();
							}
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
