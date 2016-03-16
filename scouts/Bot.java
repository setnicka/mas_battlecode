package scouts;

import java.util.Random;

import battlecode.common.*;

public abstract class Bot {
	Direction[] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
			Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST };

	RobotType[] robotTypes = { RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
			RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET };

	Random rand;
	int myAttackRange;
	Team myTeam;
	Team enemyTeam;
	RobotController rc;
	MapLocation[] enemyInitialLoc;

	public Bot(RobotController rc) {
		try {
			// Inicializace promennych pro vsechny typy robotu.
			
			rand = new Random(rc.getID());
			myAttackRange = rc.getType().attackRadiusSquared;
			myTeam = rc.getTeam();
			enemyTeam = myTeam.opponent();
			this.rc = rc;
			enemyInitialLoc = rc.getInitialArchonLocations(enemyTeam);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public abstract void loop();
}
