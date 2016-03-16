package scoutsGuards;

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

				boolean shouldAttack = false;

				RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
				RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
				if (enemiesWithinRange.length > 0) {
					shouldAttack = true;
					if (rc.isWeaponReady()) {
						rc.attackLocation(enemiesWithinRange[rand.nextInt(enemiesWithinRange.length)].location);
					}
				} else if (zombiesWithinRange.length > 0) {
					shouldAttack = true;
					if (rc.isWeaponReady()) {
						rc.attackLocation(zombiesWithinRange[rand.nextInt(zombiesWithinRange.length)].location);
					}
				}
				
				if (!shouldAttack) {
					if (rc.isCoreReady()) {
						MapLocation myLoc = rc.getLocation();
						RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
						RobotInfo target = null;
						for (RobotInfo ri : nearbyRobots) {
							if (ri.team == enemyTeam || ri.team == Team.ZOMBIE) {
								if (target == null) target = ri;
								else if (target.location.distanceSquaredTo(myLoc) > ri.location.distanceSquaredTo(myLoc)) {
									target = ri;
								}
							}
						}
						if (target != null) {
							Direction dir_1, dir_2, dir_3;
							dir_1 = myLoc.directionTo(target.location);
							dir_2 = dir_1.rotateLeft();
							dir_3 = dir_1.rotateRight();
							
							if (rc.canMove(dir_1)) {
								rc.move(dir_1);
							} else if (rc.canMove(dir_2)) {
								rc.move(dir_2);
							} else if (rc.canMove(dir_3)) {
								rc.move(dir_3);
							} else {
								// vyber nejakej nahodnej smer
								dir_1 = directions[rand.nextInt(8)];
								if (rc.canMove(dir_1)) {
									rc.move(dir_1);
								}
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