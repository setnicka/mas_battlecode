package scouts;

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
                    RobotType typeToBuild = RobotType.SCOUT;
                    if (rc.hasBuildRequirements(typeToBuild)) {
                        // Choose a random direction to try to build in
                        Direction dirToBuild = directions[rand.nextInt(8)];
                        for (int i = 0; i < 8; i++) {
                            // If possible, build in this direction
                            if (rc.canBuild(dirToBuild, typeToBuild)) {
                                rc.build(dirToBuild, typeToBuild);
                                break;
                            } else {
                                // Rotate the direction to try
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
