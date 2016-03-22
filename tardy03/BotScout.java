package tardy03;

import battlecode.common.*;

public class BotScout extends Bot {

	MapLocation target;
	int ATTRACT_ZOMBIE_DISTANCE_LIMIT = 16;
	int PURSUIT_ZOMBIE_DISTANCE_LIMIT = 4;

	public BotScout(RobotController rc) {
		super(rc);

		try {
			// Sem patri kod, ktery se spusti pri vytvoreni robota.
			// Kod, ktery pravdepodobne vyuzije kazdy typ robota, by mel prijit
			// do konstruktoru tridy Bot.

			// nahodne vyber jednen cil, ke kteremu scout pujde
			int r = rand.nextInt(enemyInitialLoc.length);
			// z nejakeho duvudu mi prvni rand.nextInt(enemyInitialLoc.length)
			// vzdycky vrati 1, proto volam podruhe
			r = rand.nextInt(enemyInitialLoc.length);
			target = enemyInitialLoc[r];
			rc.setIndicatorString(0, "Tar: " + target);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	protected void run() throws GameActionException {
		MapLocation myLoc = rc.getLocation();
		if (myLoc.equals(target)) {
			// cil byl uz znicen nebo nebo presunul
			// vyber novy cil
			int r = rand.nextInt(enemyInitialLoc.length);
			target = enemyInitialLoc[r];
			rc.setIndicatorString(0, "Tar: " + target);
		}

		if (rc.isCoreReady()) {
			// naviguj se za svym 'target'

			// asi by chtelo doresit, ze tam souperuv archon uz vubec
			// byt nemusi, chtelo by to cestou koukat, jestli jsem
			// ho nahodou nepotkal

			Direction dir_to_target;
			dir_to_target = myLoc.directionTo(target);

			// podivame se, jaka je nejblizsi zombie
			RobotInfo nearest_zombie = null;
			for (RobotInfo robot: rc.senseNearbyRobots()) {
				if (robot.team != Team.ZOMBIE) continue;
				if (nearest_zombie == null) nearest_zombie = robot;
				int distance_nearest = nearest_zombie.location.distanceSquaredTo(rc.getLocation());
				int distance = robot.location.distanceSquaredTo(rc.getLocation());
				if (distance < distance_nearest) nearest_zombie = robot;
			}

			// a) pokud vidime zombie na hranici dohledu, tak ji zkusime prilakat krokem k ni
			// b) pokud je zombie dal nez nastaveny limit a nasim pohybem bychom ji utekli jeste vic,
			//    tak se rozhodneme nepohnout a pockame na ni
			// idea je nesetrast ziskane zombiky cestou a dovest je do cile
			if (nearest_zombie != null) {
				int distance = nearest_zombie.location.distanceSquaredTo(rc.getLocation());
				int new_distance = nearest_zombie.location.distanceSquaredTo(rc.getLocation().add(dir_to_target));
				// Pokud je zombie moc daleko, priblizime se k ni
				if (distance > ATTRACT_ZOMBIE_DISTANCE_LIMIT) move(rc.getLocation().directionTo(nearest_zombie.location));
				// Pokud je dostatecne blizko, nebo ji pohybem dal neuteceme, tak popojdeme
				else if (distance <= PURSUIT_ZOMBIE_DISTANCE_LIMIT || new_distance <= distance) move(dir_to_target);
			} else move(dir_to_target);
		}
	}

	@Override
	protected void set_mode() throws GameActionException {
	}
}
