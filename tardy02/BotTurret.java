package tardy02;

import battlecode.common.*;

public class BotTurret extends Bot {

	// mody robota
	private final int M_NORMAL = 0;

	public BotTurret(RobotController rc) {
		super(rc);

		try {
			// inicializace
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void run() throws GameActionException {
		
	}

	@Override
	protected void set_mode() throws GameActionException {
	}
}
