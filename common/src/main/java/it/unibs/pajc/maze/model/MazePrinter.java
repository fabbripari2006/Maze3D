package it.unibs.pajc.maze.model;

public class MazePrinter {

	private MazePrinter() {}

	public static String toAscii(Maze maze) {
		StringBuilder sb = new StringBuilder();
		int w = maze.getWidth();
		int h = maze.getHeight();

		for (int y = 0; y < h; y++) {
			// riga dei muri nord
			for (int x = 0; x < w; x++) {
				sb.append('+').append(maze.hasWall(x, y, Direction.NORTH) ? "---" : "   ");
			}
			sb.append("+\n");

			// riga delle celle con i muri ovest
			for (int x = 0; x < w; x++) {
				sb.append(maze.hasWall(x, y, Direction.WEST) ? '|' : ' ').append("   ");
			}
			sb.append(maze.hasWall(w - 1, y, Direction.EAST) ? '|' : ' ').append('\n');
		}

		// riga finale: muri sud dell'ultima riga
		for (int x = 0; x < w; x++) {
			sb.append('+').append(maze.hasWall(x, h - 1, Direction.SOUTH) ? "---" : "   ");
		}
		sb.append("+\n");

		return sb.toString();
	}
}