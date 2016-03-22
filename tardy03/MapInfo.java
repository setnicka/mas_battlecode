package tardy03;

import java.util.HashSet;
import java.util.Set;

import battlecode.common.MapLocation;

public class MapInfo {

	Set<MapLocation> zombieDens;
	
	public MapInfo(MapLocation[] myInitLoc, MapLocation[] enemyInitLoc) {
		zombieDens = new HashSet<MapLocation>();
	}
	
	public boolean addZombieDen(MapLocation ml) {
		return zombieDens.add(ml);
	}

}
