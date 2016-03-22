package tardy03;

import battlecode.common.MapLocation;

public class SignalUtils {
	public static final int MIN_X = 0;
	public static final int MAX_X = 1;
	public static final int MIN_Y = 2;
	public static final int MAX_Y = 3;
	public static final int ZOMBIE_DEN = 4;
	
	public static int encode(MapLocation ml) {
		return (ml.x + ml.y) * (ml.x + ml.y + 1) / 2 + ml.y;
	}
	
	public static  MapLocation decode(int z) {
		int w = (int) Math.floor((Math.sqrt(8 * z + 1) - 1) / 2);
		int t = w * (w + 1) / 2;
		int y = z - t;
		int x = w - y;
		return new MapLocation(x, y);
	}
}
