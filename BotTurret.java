package tardyteam;

import battlecode.common.*;

public class BotTurret extends Bot {

	public BotTurret(RobotController rc) {
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
				if (rc.isWeaponReady()) {
					RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
					RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
					if (enemiesWithinRange.length > 0) {
						for (RobotInfo enemy : enemiesWithinRange) {
							// Check whether the enemy is in a valid attack
							// range (turrets have a minimum range)
							if (rc.canAttackLocation(enemy.location)) {
								rc.attackLocation(enemy.location);
								break;
							}
						}
					} else if (zombiesWithinRange.length > 0) {
						for (RobotInfo zombie : zombiesWithinRange) {
							if (rc.canAttackLocation(zombie.location)) {
								rc.attackLocation(zombie.location);
								break;
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
