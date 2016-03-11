package tardyteam;

import java.util.Random;

import battlecode.common.*;

public abstract class Bot {
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
            Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    
    RobotType[] robotTypes = {RobotType.SCOUT, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.GUARD, RobotType.GUARD, RobotType.VIPER, RobotType.TURRET};
    
    Random rand;
    int myAttackRange;
    Team myTeam;
    Team enemyTeam;
    RobotController rc;
    
    public Bot(RobotController rc) {
    	rand = new Random(rc.getID());
        myAttackRange = 0;
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        this.rc = rc;
	}

	public abstract void loop();
}
