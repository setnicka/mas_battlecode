package tardy01;

import battlecode.common.*;

public class BotGuard extends Bot {

	public BotGuard(RobotController rc) {
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
				processSignals();
				
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
							moveInDirection(myLoc.directionTo(target.location));
						} else if (parent.distanceSquaredTo(myLoc) > maxDistanceToParent) {
							// vrat se k archonovi, kdyz jsi daleko
							moveInDirection(myLoc.directionTo(parent));
						} else if (parent.distanceSquaredTo(myLoc) < 8) {
							// bez od archona, kdyz jsi moc blizko
							moveInDirection(myLoc.directionTo(parent).opposite());
						} else {
							// zkus se posunout od archona k okraji
							Direction d = myLoc.directionTo(parent).opposite();
							if (parent.distanceSquaredTo(myLoc.add(d)) < maxDistanceToParent) {
								moveInDirection(d);
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