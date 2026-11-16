package it.unibs.pajc.maze.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class MazeGeneratorTest {

	//private static final String SIZES = "1,1; 2,1; 1,2; 2,2; 5,3; 7,4; 3,9; 20,20";

	// ---- helper ----

	private static int countReachable(Maze maze) {
		boolean[][] seen = new boolean[maze.getHeight()][maze.getWidth()];
		Deque<int[]> queue = new ArrayDeque<>();
		seen[0][0] = true;
		queue.add(new int[] { 0, 0 });
		int count = 0;

		while (!queue.isEmpty()) {
			int[] cur = queue.poll();
			count++;
			for (Direction d : Direction.values()) {
				if (maze.hasWall(cur[0], cur[1], d)) continue;
				int nx = cur[0] + d.getDx();
				int ny = cur[1] + d.getDy();
				if (!seen[ny][nx]) {
					seen[ny][nx] = true;
					queue.add(new int[] { nx, ny });
				}
			}
		}
		return count;
	}

	private static int countOpenWalls(Maze maze) {
		// ogni muro aperto si conta una volta sola guardando solo EAST e SOUTH
		int open = 0;
		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				if (!maze.hasWall(x, y, Direction.EAST)) open++;
				if (!maze.hasWall(x, y, Direction.SOUTH)) open++;
			}
		}
		return open;
	}

	// ---- test ----

	@ParameterizedTest
	@CsvSource(delimiter = ';', value = { "1,1", "2,1", "1,2", "2,2", "5,3", "7,4", "3,9", "20,20" })
	void allCellsAreReachable(String size) {
		String[] p = size.split(",");
		int w = Integer.parseInt(p[0].trim());
		int h = Integer.parseInt(p[1].trim());

		for (long seed = 0; seed < 10; seed++) {
			Maze maze = MazeGenerator.generateMaze(w, h, new Random(seed));
			assertEquals(w * h, countReachable(maze), "Celle isolate con seed " + seed);
		}
	}

	@ParameterizedTest
	@CsvSource(delimiter = ';', value = { "1,1", "2,1", "1,2", "2,2", "5,3", "7,4", "3,9", "20,20" })
	void mazeIsPerfect(String size) {
		String[] p = size.split(",");
		int w = Integer.parseInt(p[0].trim());
		int h = Integer.parseInt(p[1].trim());

		for (long seed = 0; seed < 10; seed++) {
			Maze maze = MazeGenerator.generateMaze(w, h, new Random(seed));
			assertEquals(w * h - 1, countOpenWalls(maze), "Numero di muri aperti errato con seed " + seed);
		}
	}

	@Test
	void borderWallsStayClosed() {
		Maze maze = MazeGenerator.generateMaze(7, 4, new Random(1));
		for (int x = 0; x < 7; x++) {
			assertTrue(maze.hasWall(x, 0, Direction.NORTH));
			assertTrue(maze.hasWall(x, 3, Direction.SOUTH));
		}
		for (int y = 0; y < 4; y++) {
			assertTrue(maze.hasWall(0, y, Direction.WEST));
			assertTrue(maze.hasWall(6, y, Direction.EAST));
		}
	}

	@Test
	void oneByOneKeepsAllWalls() {
		Maze maze = MazeGenerator.generateMaze(1, 1, new Random(1));
		for (Direction d : Direction.values()) {
			assertTrue(maze.hasWall(0, 0, d));
		}
	}

	@Test
	void sameSeedGivesSameMaze() {
		Maze a = MazeGenerator.generateMaze(10, 6, new Random(123));
		Maze b = MazeGenerator.generateMaze(10, 6, new Random(123));
		assertEquals(MazePrinter.toAscii(a), MazePrinter.toAscii(b));
	}

	@Test
	void differentSeedsGiveDifferentMazes() {
		Maze a = MazeGenerator.generateMaze(20, 20, new Random(1));
		Maze b = MazeGenerator.generateMaze(20, 20, new Random(2));
		assertNotEquals(MazePrinter.toAscii(a), MazePrinter.toAscii(b));
	}

	@Test
	void invalidSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> MazeGenerator.generateMaze(0, 5, new Random()));
		assertThrows(IllegalArgumentException.class, () -> MazeGenerator.generateMaze(5, -1, new Random()));
	}
}