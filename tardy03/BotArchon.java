package tardy03;

import battlecode.common.*;

public class BotArchon extends Bot {

	// mody robota
	private final int M_NORMAL = 0;
	private final int M_DANGER = 1;
	private final int M_TURTLE = 2;

	Direction lastDirection = Direction.OMNI;
	boolean chosenone = false;
	private int runaway_counter = 0;
	private int guard_counter = 0;
	private final int RUNAWAY_LIMIT = 25;
	private final int GUARD_LIMIT = 15;

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
		RobotInfo[] nearbyRobots = rc.senseHostileRobots(rc.getLocation(), sightRange);
		boolean are_there_zombies = false;
		for (RobotInfo robot: nearbyRobots) if (robot.team == Team.ZOMBIE) are_there_zombies = true;
		if (mode == M_NORMAL) {
			// pokud objevis parts, pohni se k nim
			MapLocation[] nearby_parts = rc.sensePartLocations(-1);
			if (nearby_parts.length > 0) {
				// Nahodne zamichani
				for (int i = nearby_parts.length - 1; i > 0; i--) {
					int index = rand.nextInt(i + 1);
					MapLocation a = nearby_parts[index];
					nearby_parts[index] = nearby_parts[i];
					nearby_parts[i] = a;
				}

				MapLocation best_parts = nearby_parts[0];
				double best_parts_amount = rc.senseParts(best_parts);
				for (MapLocation parts : nearby_parts) {
					if (rc.senseParts(parts) > best_parts_amount) {
						best_parts = parts;
						best_parts_amount = rc.senseParts(parts);
					}
				}
				if (rc.isCoreReady()) {
					move(rc.getLocation().directionTo(best_parts));
				}
			}
			// jinak nedelej nic
		} else if (mode == M_DANGER) {
			// utikej
			if (rc.isCoreReady()) {
				MapLocation loc = rc.getLocation();
				Direction dirToEnemy = loc.directionTo(center(nearbyRobots));
				Direction dirFromEnemy = dirToEnemy.opposite();

				// kazde K-te kolo vyrob scouta a zkus ve smeru k nepriteli (pro odlakani)
				if (rc.getHealth() > rc.getType().maxHealth/2
					&& are_there_zombies && runaway_counter >= RUNAWAY_LIMIT
					&& rc.hasBuildRequirements(RobotType.SCOUT)
				) {
					Direction[] directions = {dirToEnemy, dirToEnemy.rotateLeft(), dirToEnemy.rotateRight(),
						dirToEnemy.rotateLeft().rotateLeft(), dirToEnemy.rotateRight().rotateRight()};
					for (int i = 0; i < directions.length; i++) {
						Direction dirToBuild = directions[0];
						if (rc.canBuild(dirToBuild, RobotType.SCOUT)) {
							rc.build(dirToBuild, RobotType.SCOUT);
							runaway_counter = 0;
							return;
						}
					}
				}
				// Pokud utikame (nebo se nepovedlo postavit scouta):
				runaway_counter++;

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
				// Pokud vidi zombie a uz dlouho nebyl postaven scout, postav scouta
				RobotType typeToBuild = (are_there_zombies && guard_counter > GUARD_LIMIT) ? RobotType.SCOUT : RobotType.GUARD;
				if (rc.hasBuildRequirements(typeToBuild)) {
					Direction dirToBuild = directions[rand.nextInt(8)];
					for (int i = 0; i < 8; i++) {
						if (rc.canBuild(dirToBuild, typeToBuild)) {
							rc.build(dirToBuild, typeToBuild);
							if (typeToBuild == RobotType.GUARD) guard_counter++;
							else guard_counter = 0;
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
