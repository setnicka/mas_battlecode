package tardy03;

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
		    	
		    case SCOUT:
		    	robot = new BotScout(rc);
		    	break;
		    	
		    case SOLDIER:
		    	robot = new BotSoldier(rc);
		        break;
		    
		    case TURRET:
		    	robot = new BotTurret(rc);
		    	break;
		    	
		    case VIPER:
		    	robot = new BotViper(rc);
		    	break;
		    	
		    default:
		    	throw new Exception("weird robot type " + rc.getType());
    	}
    	
    	robot.loop();
    }
}
