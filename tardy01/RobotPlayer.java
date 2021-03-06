package tardy01;

import battlecode.common.*;

public class RobotPlayer {

    @SuppressWarnings("unused")

    public static void run(RobotController rc) throws Exception {
    	
    	Bot robot;
    	
    	switch (rc.getType()) {
	    	case ARCHON:
				robot = new BotArchon(rc);
		        break;	
	
		    case GUARD:
		    	robot = new BotGuard(rc);
		    	break;
	
		    case SOLDIER:
		    	robot = new BotSoldier(rc);
		    	break;
		    	
		    default:
		    	throw new Exception("weird robot type " + rc.getType());
    	}
    	
    	robot.loop();
    }
}
