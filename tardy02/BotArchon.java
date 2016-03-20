package tardy02;

import battlecode.common.*;

public class BotArchon extends Bot {

	// mody robota
	private final int M_NORMAL = 0;
	private final int M_DANGER = 1;
	private final int M_TURTLE = 2;

	Direction lastDirection = Direction.OMNI;
	boolean chosenone = false;

	public BotArchon(RobotController rc) {
		super(rc);

		try {
			// inicializace

			// urci the chosenone archona
			MapLocation[] myInitLocations = rc.getInitialArchonLocations(myTeam);
			MapLocation my_center = center(myInitLocations);
			MapLocation en_center = center(rc.getInitialArchonLocations(enemyTeam));
			MapLocation chosenPosition = null;
			if (my_center.distanceSquaredTo(en_center) > 50) {
				for (MapLocation ml : myInitLocations) {
					if (chosenPosition == null) {
						chosenPosition = ml;
					}
					if (chosenPosition.distanceSquaredTo(my_center) > ml.distanceSquaredTo(my_center)) {
						chosenPosition = ml;
					}
				}
			} else {
				for (MapLocation ml : myInitLocations) {
					if (chosenPosition == null) {
						chosenPosition = ml;
					}
					if (chosenPosition.distanceSquaredTo(my_center) < ml.distanceSquaredTo(my_center)) {
						chosenPosition = ml;
					}
				}
			}

			if (chosenPosition.equals(rc.getLocation())) {
				chosenone = true;
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void run() throws GameActionException {
		if (mode == M_NORMAL) {
			// nedelej nic
		} else if (mode == M_DANGER) {
			// utikej
			if (rc.isCoreReady()) {
				RobotInfo[] nearbyRobots = rc.senseHostileRobots(rc.getLocation(), sightRange);
				MapLocation loc = rc.getLocation();
				Direction dirFromEnemy = center(nearbyRobots).directionTo(loc);
				Direction dirPreffered = loc.directionTo(loc.add(dirFromEnemy).add(lastDirection));
				if (dirPreffered == Direction.OMNI) {
					if (lastDirection == Direction.OMNI) {
						dirPreffered = directions[rand.nextInt(directions.length)];
					} else {
						dirPreffered = lastDirection.rotateLeft().rotateLeft();
					}
				}

				lastDirection = escape(dirPreffered);
			}
		} else if (mode == M_TURTLE) {
			// stav si armadu
			if (rc.isCoreReady()) {
				RobotType typeToBuild = RobotType.GUARD;
				if (rc.hasBuildRequirements(typeToBuild)) {
					Direction dirToBuild = directions[rand.nextInt(8)];
					for (int i = 0; i < 8; i++) {
						if (rc.canBuild(dirToBuild, typeToBuild)) {
							rc.build(dirToBuild, typeToBuild);
							break;
						} else {
							dirToBuild = dirToBuild.rotateLeft();
						}
					}
				}
			}
		}
	}

	@Override
	protected void set_mode() throws GameActionException {
		MapLocation loc = rc.getLocation();
		RobotInfo[] nearbyRobots = rc.senseHostileRobots(loc, sightRange);
		if (nearbyRobots.length > 0) {
			mode = M_DANGER;
		} else if (chosenone) {
			mode = M_TURTLE;
		} else {
				mode = M_NORMAL;
		}
	}
}
