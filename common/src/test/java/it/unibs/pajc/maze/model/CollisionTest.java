package it.unibs.pajc.maze.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CollisionTest {

	private static final double R = 0.2;

	@Test
	void centerOfCellIsFree() {
		Maze maze = new Maze(3, 3);
		assertFalse(Collision.isBlocked(maze, 1.5, 1.5, R));
	}

	@Test
	void closedWallBlocksOnEachSide() {
		Maze maze = new Maze(3, 3);
		assertTrue(Collision.isBlocked(maze, 1.05, 1.5, R)); // ovest
		assertTrue(Collision.isBlocked(maze, 1.95, 1.5, R)); // est
		assertTrue(Collision.isBlocked(maze, 1.5, 1.05, R)); // nord
		assertTrue(Collision.isBlocked(maze, 1.5, 1.95, R)); // sud
	}

	@Test
	void openWallAllowsPassageFromBothSides() {
		Maze maze = new Maze(2, 1);
		maze.destroyWall(0, 0, Direction.EAST);
		assertFalse(Collision.isBlocked(maze, 0.95, 0.5, R));
		assertFalse(Collision.isBlocked(maze, 1.05, 0.5, R));
	}

	@Test
	void justOutsideRadiusIsFree() {
		Maze maze = new Maze(3, 3);
		assertFalse(Collision.isBlocked(maze, 1.21, 1.5, R));
		assertFalse(Collision.isBlocked(maze, 1.79, 1.5, R));
	}

	@Test
	void outsideTheGridIsBlocked() {
		Maze maze = new Maze(3, 3);
		assertTrue(Collision.isBlocked(maze, -0.1, 0.5, R));
		assertTrue(Collision.isBlocked(maze, 3.1, 0.5, R));
		assertTrue(Collision.isBlocked(maze, 0.5, -0.1, R));
		assertTrue(Collision.isBlocked(maze, 0.5, 3.1, R));
	}

	@Test
	void negativeCoordinatesJustBelowZeroAreBlocked() {
		Maze maze = new Maze(3, 3);
		assertTrue(Collision.isBlocked(maze, -0.01, 1.5, R));
	}

	@Test
	void cornerPostBlocksEvenIfCurrentCellWallsAreOpen() {
		// cella (0,1): vicino all'angolo nord-est, il muro EST di (0,0) arriva fin lì
		Maze maze = new Maze(2, 2);
		maze.destroyWall(0, 0, Direction.SOUTH);
		maze.destroyWall(1, 0, Direction.SOUTH);
		maze.destroyWall(0, 1, Direction.EAST);
		assertTrue(Collision.isBlocked(maze, 0.95, 1.05, R));
	}

	@Test
	void cornerWithoutPostIsFree() {
		// tutto aperto: nessun pilastro, l'angolo interno è libero
		Maze maze = new Maze(2, 2);
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < 2; x++) {
				if (x + 1 < 2) maze.destroyWall(x, y, Direction.EAST);
				if (y + 1 < 2) maze.destroyWall(x, y, Direction.SOUTH);
			}
		}
		assertFalse(Collision.isBlocked(maze, 0.95, 0.95, R));
		assertFalse(Collision.isBlocked(maze, 1.05, 1.05, R));
	}
}