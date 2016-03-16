package tardyteam;

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

			// Nahodne chovani. Kopie z examplefuncsplayer.

			try {
				int fate = rand.nextInt(1000);

				if (fate % 5 == 3) {
					// Send a normal signal
					rc.broadcastSignal(80);
				}

				boolean shouldAttack = false;

				// If this robot type can attack, check for enemies within range
				// and attack one
				if (myAttackRange > 0) {
					RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
					RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
					if (enemiesWithinRange.length > 0) {
						shouldAttack = true;
						// Check if weapon is ready
						if (rc.isWeaponReady()) {
							rc.attackLocation(enemiesWithinRange[rand.nextInt(enemiesWithinRange.length)].location);
						}
					} else if (zombiesWithinRange.length > 0) {
						shouldAttack = true;
						// Check if weapon is ready
						if (rc.isWeaponReady()) {
							rc.attackLocation(zombiesWithinRange[rand.nextInt(zombiesWithinRange.length)].location);
						}
					}
				}

				if (!shouldAttack) {
					if (rc.isCoreReady()) {
						if (fate < 600) {
							// Choose a random direction to try to move in
							Direction dirToMove = directions[fate % 8];
							// Check the rubble in that direction
							if (rc.senseRubble(rc.getLocation().add(dirToMove)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
								// Too much rubble, so I should clear it
								rc.clearRubble(dirToMove);
								// Check if I can move in this direction
							} else if (rc.canMove(dirToMove)) {
								// Move
								rc.move(dirToMove);
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