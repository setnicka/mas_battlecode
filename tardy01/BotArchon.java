package tardy01;

import battlecode.common.*;

public class BotArchon extends Bot {

	MapLocation nest;
	boolean onPosition = false;
	int stuckCounter = 0;

	public BotArchon(RobotController rc) {
		super(rc);

		try {

			// urci pozici na mape (nest), do ktere se archoni presunou
			MapLocation myCenter = center(rc.getInitialArchonLocations(myTeam));
			MapLocation enemyCenter = center(rc.getInitialArchonLocations(enemyTeam));
			nest = myCenter.add(enemyCenter.directionTo(myCenter), 15);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	// Spocita teziste (~prumer) z pole pozic.
	private MapLocation center(MapLocation[] locations) {
		int centerX = 0;
		int centerY = 0;
		for (MapLocation ml : locations) {
			centerX += ml.x;
			centerY += ml.y;
		}
		centerX /= locations.length;
		centerY /= locations.length;
		return new MapLocation(centerX, centerY);
	}

	// Metoda navadi archona k 'nest', pokud bylo 'nest' puvodne umisteno mimo
	// herni plan, bude 'nest' premisteno.
	private void moveToNest() throws GameActionException {
		MapLocation loc = rc.getLocation();
		Direction dir = loc.directionTo(nest);
		moveInDirection(loc.directionTo(nest));

		// aktualizuj pozici a smer k hnizdu
		loc = rc.getLocation();
		dir = loc.directionTo(nest);

		if (!rc.onTheMap(loc.add(dir))) {
			// hnizdo umisteno mimo mapu, posun ho do mapy
			if (!rc.onTheMap(loc.add(Direction.NORTH)) && loc.y > nest.y) {
				nest = new MapLocation(nest.x, loc.y);
			} else if (!rc.onTheMap(loc.add(Direction.SOUTH)) && loc.y < nest.y) {
				nest = new MapLocation(nest.x, loc.y);
			}
			if (!rc.onTheMap(loc.add(Direction.EAST)) && loc.x < nest.x) {
				nest = new MapLocation(loc.x, nest.y);
			} else if (!rc.onTheMap(loc.add(Direction.WEST)) && loc.x > nest.x) {
				nest = new MapLocation(loc.x, nest.y);
			}
		}

		if (nest.distanceSquaredTo(rc.getLocation()) < 3) {
			// jsem u hnizda
			onPosition = true;
			rc.setIndicatorString(0, "On position." + nest.toString() + dir.toString());
		}
	}

	private boolean buildInDirection(RobotType type, Direction dirToBuild) throws GameActionException {
		if (rc.isCoreReady() && rc.canBuild(dirToBuild, type)) {
			rc.build(dirToBuild, type);
			return true;
		}
		return false;
	}

	// Pokusi se ve svem okoli postavit danou jednotku.
	private boolean buildInRandomDirection(RobotType type) throws GameActionException {
		Direction dirToBuild = directions[rand.nextInt(8)];
		for (int i = 0; i < 8; i++) {
			if (buildInDirection(type, dirToBuild)) {
				return true;
			} else {
				dirToBuild = dirToBuild.rotateLeft();
			}
		}

		return false;
	}

	// Pokusi se ve svem dostrelu opravit nekterou z vlastnich jednotek.
	private void repairSomeone() throws GameActionException {
		RobotInfo[] nearbyRobots = rc.senseNearbyRobots(attackRange, myTeam);
		for (RobotInfo ri : nearbyRobots) {
			if (ri.maxHealth > ri.health && ri.type != RobotType.ARCHON) {
				rc.repair(ri.location);
				break;
			}
		}
	}

	public void loop() {

		while (true) {

			try {
				processSignals();

				if (!onPosition) {
					// jdi do hnizda
					if (rc.isCoreReady()) {
						moveToNest();
						// rc.setIndicatorString(0, nest.toString());
					}
				} else {

					if (rc.isCoreReady()) {
						RobotType typeToBuild = RobotType.GUARD;
						//if (rc.getRobotCount() > 20) {
						//	typeToBuild = RobotType.SCOUT;
						//} else {
						//	typeToBuild = RobotType.GUARD;
						//}
						// if (rand.nextBoolean())
						// typeToBuild = RobotType.GUARD;
						// else
						// typeToBuild = RobotType.SOLDIER;

						if (rc.hasBuildRequirements(typeToBuild)) {
							buildInRandomDirection(typeToBuild);
						}
					}
				}

				repairSomeone();

				Clock.yield();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
