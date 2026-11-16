package it.unibs.pajc.maze.model;

final class Collision {

	public static boolean isBlocked(Maze maze, double x, double y, double raggio) {
		int cx = (int) Math.floor(x);
		int cy = (int) Math.floor(y);
		
		double fx = x - cx;
		double fy = y - cy;
		
		boolean nearW = fx < raggio;
		boolean nearE = fx > 1 - raggio;
		boolean nearN = fy < raggio;
		boolean nearS = fy > 1 - raggio;
		
		if (nearW && maze.hasWall(cx, cy, Direction.WEST)) return true;
		if (nearE && maze.hasWall(cx, cy, Direction.EAST)) return true;
		if (nearN && maze.hasWall(cx, cy, Direction.NORTH)) return true;
		if (nearS && maze.hasWall(cx, cy, Direction.SOUTH)) return true;

		if (nearW && nearN && (maze.hasWall(cx - 1, cy, Direction.NORTH) || maze.hasWall(cx, cy - 1, Direction.WEST))) return true;
		if (nearE && nearN && (maze.hasWall(cx + 1, cy, Direction.NORTH) || maze.hasWall(cx, cy - 1, Direction.EAST))) return true;
		if (nearW && nearS && (maze.hasWall(cx - 1, cy, Direction.SOUTH) || maze.hasWall(cx, cy + 1, Direction.WEST))) return true;
		if (nearE && nearS && (maze.hasWall(cx + 1, cy, Direction.SOUTH) || maze.hasWall(cx, cy + 1, Direction.EAST))) return true;
		
		return false;
	}
}
