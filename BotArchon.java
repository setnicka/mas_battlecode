package tardyteam;

import battlecode.common.*;

public class BotArchon extends Bot {

	// pravdepodobnost stavby turretu pri jejich nedostatku v okoli
	double p_turret = 0.96;

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

	private void buildAdjacetTurret() {
		// postavi turret na jednom ze 4 policek v okoli archona (SOUTH, NORTH,
		// EAST, WEST)
		try {
			if (rc.hasBuildRequirements(RobotType.TURRET)) {
				Direction dirToBuild = directions[rand.nextInt(4) * 2];
				for (int i = 0; i < 4; i++) {
					if (rc.canBuild(dirToBuild, RobotType.TURRET)) {
						rc.build(dirToBuild, RobotType.TURRET);
						break;
					} else {
						dirToBuild = dirToBuild.rotateLeft();
						dirToBuild = dirToBuild.rotateLeft();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void buidScout() {
		// postavi scouta v nahodnem smeru od archona
		try {
			if (rc.hasBuildRequirements(RobotType.SCOUT)) {
				Direction dirToBuild = directions[rand.nextInt(8)];
				for (int i = 0; i < 8; i++) {
					if (rc.canBuild(dirToBuild, RobotType.SCOUT)) {
						rc.build(dirToBuild, RobotType.SCOUT);
						break;
					} else {
						dirToBuild = dirToBuild.rotateLeft();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void loop() {

		while (true) {
			try {
				if (rc.isCoreReady()) {
					RobotInfo[] adjacentRobots = rc.senseNearbyRobots(1, myTeam);
					int adjacentTurrents = 0;
					for (RobotInfo robot : adjacentRobots) {
						if (robot.type == RobotType.TURRET)
							adjacentTurrents++;
					}

					if (adjacentTurrents < 4 && rand.nextDouble() < p_turret) {
						buildAdjacetTurret();
					} else {
						buidScout();
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
