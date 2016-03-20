package tardy02;

import battlecode.common.*;

public class BotScout extends Bot {

	// mody robota
	private final int M_NORMAL = 0;

	Direction lastDirection = directions[rand.nextInt(8)];

	public BotScout(RobotController rc) {
		super(rc);

		try {
			// inicializace
			if (parent != null)
				lastDirection = parent.directionTo(rc.getLocation());

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void run() throws GameActionException {
		RobotInfo[] nearbyRobots = rc.senseHostileRobots(rc.getLocation(), sightRange);
		for (RobotInfo ri : nearbyRobots) {
			if (ri.type == RobotType.ZOMBIEDEN) {
				if (mapInfo.addZombieDen(ri.location)) {
					// nove objevene hnizdo
					rc.broadcastMessageSignal(SignalUtils.ZOMBIE_DEN, SignalUtils.encode(ri.location),
							sightRange * 100);
				}
			}
		}

		if (rc.isCoreReady()) {
			Direction[] directions = { lastDirection, lastDirection.rotateLeft(), lastDirection.rotateRight(),
					lastDirection.rotateLeft().rotateLeft(), lastDirection.rotateRight().rotateRight(),
					lastDirection.rotateRight().opposite(), lastDirection.rotateLeft().opposite(),
					lastDirection.opposite() };

			for (Direction d : directions) {
				if (rc.canMove(d)) {
					rc.move(d);
					lastDirection = d;
					return;
				}
			}

			for (Direction d : directions) {
				if (rc.senseRubble(rc.getLocation().add(d)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
					rc.clearRubble(d);
					lastDirection = d;
					return;
				}
			}
		}
	}

	@Override
	protected void set_mode() throws GameActionException {
	}
}
