package tardy03;

import battlecode.common.*;

public class BotSoldier extends Bot {

	// mody robota
	private final int M_NORMAL = 0;

	int maxDistanceToParent = 20;
	
	public BotSoldier(RobotController rc) {
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
		if (mode == M_NORMAL) {
			RobotInfo target = findTarget(attackRange);

			if (target != null) {
				if (rc.isWeaponReady()) {
					rc.attackLocation(target.location);
				}
			} else {
				if (rc.isCoreReady()) {
					MapLocation myLoc = rc.getLocation();
					target = findTarget(sightRange);
					if (target != null) {
						// jdi za cilem
						move(myLoc.directionTo(target.location));
					} else if (parent.distanceSquaredTo(myLoc) > maxDistanceToParent) {
						// vrat se k archonovi, kdyz jsi daleko
						move(myLoc.directionTo(parent));
					} else if (parent.distanceSquaredTo(myLoc) < 8) {
						// bez od archona, kdyz jsi moc blizko
						move(myLoc.directionTo(parent).opposite());
					} else {
						// zkus se posunout od archona k okraji
						Direction d = myLoc.directionTo(parent).opposite();
						if (parent.distanceSquaredTo(myLoc.add(d)) < maxDistanceToParent) {
							move(d);
						}
					}
				}
			}
		}
	}

	@Override
	protected void set_mode() throws GameActionException {
	}
}
