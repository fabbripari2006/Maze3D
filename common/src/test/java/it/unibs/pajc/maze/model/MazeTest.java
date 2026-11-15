package it.unibs.pajc.maze.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MazeTest {

	@Test
	void constructorCreatesRectangularMaze() {
		Maze maze = new Maze(5, 3);
		assertEquals(5, maze.getWidth());
		assertEquals(3, maze.getHeight());
	}

	@Test
	void constructorRejectsInvalidSize() {
		assertThrows(IllegalArgumentException.class, () -> new Maze(0, 3));
		assertThrows(IllegalArgumentException.class, () -> new Maze(5, 0));
		assertThrows(IllegalArgumentException.class, () -> new Maze(-1, -1));
	}

	@Test
	void allWallsClosedInitially() {
		Maze maze = new Maze(5, 3);
		for (int y = 0; y < maze.getHeight(); y++) {
			for (int x = 0; x < maze.getWidth(); x++) {
				for (Direction d : Direction.values()) {
					assertTrue(maze.hasWall(x, y, d),
							"Muro mancante in (" + x + "," + y + ") " + d);
				}
			}
		}
	}

	@Test
	void destroyWallOpensBothFaces() {
		Maze maze = new Maze(5, 3);
		maze.destroyWall(2, 1, Direction.EAST);

		assertFalse(maze.hasWall(2, 1, Direction.EAST));
		assertFalse(maze.hasWall(3, 1, Direction.WEST));
	}

	@Test
	void destroyWallDoesNotTouchOtherWalls() {
		Maze maze = new Maze(5, 3);
		maze.destroyWall(2, 1, Direction.EAST);

		assertTrue(maze.hasWall(2, 1, Direction.NORTH));
		assertTrue(maze.hasWall(2, 1, Direction.SOUTH));
		assertTrue(maze.hasWall(2, 1, Direction.WEST));
		assertTrue(maze.hasWall(3, 1, Direction.EAST));
	}

	@Test
	void destroyWallWorksVertically() {
		Maze maze = new Maze(5, 3);
		maze.destroyWall(4, 0, Direction.SOUTH);

		assertFalse(maze.hasWall(4, 0, Direction.SOUTH));
		assertFalse(maze.hasWall(4, 1, Direction.NORTH));
	}

	@Test
	void destroyWallOnBorderDoesNothing() {
		Maze maze = new Maze(5, 3);

		maze.destroyWall(0, 0, Direction.NORTH);
		maze.destroyWall(0, 0, Direction.WEST);
		maze.destroyWall(4, 2, Direction.EAST);
		maze.destroyWall(4, 2, Direction.SOUTH);

		assertTrue(maze.hasWall(0, 0, Direction.NORTH));
		assertTrue(maze.hasWall(0, 0, Direction.WEST));
		assertTrue(maze.hasWall(4, 2, Direction.EAST));
		assertTrue(maze.hasWall(4, 2, Direction.SOUTH));
	}

	@Test
	void destroyWallFromOutsideDoesNothing() {
		Maze maze = new Maze(5, 3);
		assertDoesNotThrow(() -> maze.destroyWall(-1, 0, Direction.EAST));
		assertDoesNotThrow(() -> maze.destroyWall(5, 0, Direction.WEST));
		assertTrue(maze.hasWall(0, 0, Direction.WEST));
	}

	@Test
	void hasWallOutsideGridIsTrue() {
		Maze maze = new Maze(5, 3);
		assertTrue(maze.hasWall(-1, 0, Direction.EAST));
		assertTrue(maze.hasWall(5, 0, Direction.WEST));
		assertTrue(maze.hasWall(0, 3, Direction.NORTH));
	}
}