package it.unibs.pajc.maze.client;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import it.unibs.pajc.maze.model.Direction;
import it.unibs.pajc.maze.model.Maze;
import it.unibs.pajc.maze.model.MazeGenerator;
import it.unibs.pajc.maze.model.Player;

class RaycasterTest {

	private static final double EPS = 1e-9;
	private static final double TAN = Math.tan(Math.toRadians(33)); // tan(FOV/2) con FOV = 66°

	// ---- helper ----

	private static Player player(double x, double y, double angle) {
		return new Player("a", x, y, angle, 0, 0);
	}

	private static Maze openRow(int w) {
		Maze maze = new Maze(w, 1);
		for (int x = 0; x + 1 < w; x++) maze.destroyWall(x, 0, Direction.EAST);
		return maze;
	}

	// ---- rayDirection ----

	@Test
	void centerRayFollowsPlayerDirection() {
		Raycaster rc = new Raycaster(new Maze(1, 1));
		double[] r = rc.rayDirection(player(0.5, 0.5, 0), 0);
		assertEquals(1, r[0], EPS);
		assertEquals(0, r[1], EPS);
	}

	@Test
	void edgeRaysSpreadByTanHalfFov() {
		Raycaster rc = new Raycaster(new Maze(1, 1));
		double[] left = rc.rayDirection(player(0.5, 0.5, 0), -1);
		double[] right = rc.rayDirection(player(0.5, 0.5, 0), 1);
		assertEquals(1, left[0], EPS);
		assertEquals(-TAN, left[1], EPS);   // sinistra = nord (y negativa)
		assertEquals(1, right[0], EPS);
		assertEquals(TAN, right[1], EPS);   // destra = sud (y positiva)
	}

	@Test
	void rightSideIsRotatedWithThePlayer() {
		Raycaster rc = new Raycaster(new Maze(1, 1));
		// guarda a sud: la destra è ovest (x negativa)
		double[] right = rc.rayDirection(player(0.5, 0.5, Math.PI / 2), 1);
		assertTrue(right[0] < 0);
		assertEquals(1, right[1], EPS);
	}

	// ---- cast: direzioni cardinali ----

	@Test
	void facingEastHitsEastWall() {
		Raycaster rc = new Raycaster(new Maze(3, 1));
		RayHit hit = rc.cast(player(0.5, 0.5, 0), 0);
		assertEquals(0.5, hit.distance(), EPS);
		assertEquals(Direction.EAST, hit.face());
	}

	@Test
	void facingWestHitsWestWall() {
		Raycaster rc = new Raycaster(new Maze(3, 1));
		RayHit hit = rc.cast(player(0.5, 0.5, Math.PI), 0);
		assertEquals(0.5, hit.distance(), EPS);
		assertEquals(Direction.WEST, hit.face());
	}

	@Test
	void facingSouthHitsSouthWall() {
		Raycaster rc = new Raycaster(new Maze(3, 1));
		RayHit hit = rc.cast(player(0.5, 0.5, Math.PI / 2), 0);
		assertEquals(0.5, hit.distance(), EPS);
		assertEquals(Direction.SOUTH, hit.face());
	}

	@Test
	void facingNorthHitsNorthWall() {
		Raycaster rc = new Raycaster(new Maze(3, 1));
		RayHit hit = rc.cast(player(0.5, 0.5, 3 * Math.PI / 2), 0);
		assertEquals(0.5, hit.distance(), EPS);
		assertEquals(Direction.NORTH, hit.face());
	}

	// ---- cast: muri aperti ----

	@Test
	void rayPassesThroughOpenWall() {
		Maze maze = new Maze(3, 1);
		maze.destroyWall(0, 0, Direction.EAST);
		RayHit hit = new Raycaster(maze).cast(player(0.5, 0.5, 0), 0);
		assertEquals(1.5, hit.distance(), EPS);
		assertEquals(Direction.EAST, hit.face());
	}

	@Test
	void rayTravelsAcrossWholeOpenRow() {
		RayHit hit = new Raycaster(openRow(5)).cast(player(0.5, 0.5, 0), 0);
		assertEquals(4.5, hit.distance(), EPS);
		assertEquals(Direction.EAST, hit.face());
	}

	@Test
	void rayGoingWestPassesThroughOpenWall() {
		RayHit hit = new Raycaster(openRow(5)).cast(player(4.5, 0.5, Math.PI), 0);
		assertEquals(4.5, hit.distance(), EPS);
		assertEquals(Direction.WEST, hit.face());
	}

	// ---- cast: proprietà geometriche ----

	@Test
	void distanceIsPerpendicularNoFisheye() {
		// muro piatto davanti: tutti i raggi devono dare la stessa distanza
		Raycaster rc = new Raycaster(new Maze(3, 3));
		for (double cx = -1; cx <= 1; cx += 0.25) {
			RayHit hit = rc.cast(player(0.5, 0.5, 0), cx);
			assertEquals(0.5, hit.distance(), EPS, "cameraX " + cx);
			assertEquals(Direction.EAST, hit.face(), "cameraX " + cx);
		}
	}

	@Test
	void diagonalRayDistance() {
		RayHit hit = new Raycaster(new Maze(1, 1)).cast(player(0.5, 0.5, Math.PI / 4), 0);
		assertEquals(0.5 / Math.cos(Math.PI / 4), hit.distance(), EPS);
	}

	@Test
	void sideRayCanHitAnotherFace() {
		// giocatore vicino al muro sud, raggio destro (verso sud-est) colpisce prima il sud
		Raycaster rc = new Raycaster(new Maze(3, 3));
		RayHit hit = rc.cast(player(0.5, 0.9, 0), 1);
		assertEquals(Direction.SOUTH, hit.face());
	}

	// ---- wallOffset ----

	@Test
	void wallOffsetOnEastWallUsesY() {
		RayHit hit = new Raycaster(new Maze(3, 1)).cast(player(0.5, 0.25, 0), 0);
		assertEquals(Direction.EAST, hit.face());
		assertEquals(0.25, hit.wallOffset(), EPS);
	}

	@Test
	void wallOffsetOnSouthWallUsesX() {
		RayHit hit = new Raycaster(new Maze(3, 1)).cast(player(0.8, 0.5, Math.PI / 2), 0);
		assertEquals(Direction.SOUTH, hit.face());
		assertEquals(0.8, hit.wallOffset(), EPS);
	}

	@Test
	void wallOffsetStaysBetweenZeroAndOne() {
		Maze maze = MazeGenerator.generateMaze(8, 8, new Random(7));
		Raycaster rc = new Raycaster(maze);
		for (double a = 0; a < 2 * Math.PI; a += 0.1) {
			for (double cx = -1; cx <= 1; cx += 0.5) {
				RayHit hit = rc.cast(player(3.5, 3.5, a), cx);
				assertTrue(hit.wallOffset() >= -EPS && hit.wallOffset() <= 1 + EPS,
						"offset " + hit.wallOffset() + " angolo " + a + " cameraX " + cx);
			}
		}
	}

	// ---- robustezza ----

	@Test
	void everyRayHitsSomethingFiniteInRandomMazes() {
		for (long seed = 0; seed < 20; seed++) {
			Maze maze = MazeGenerator.generateMaze(10, 7, new Random(seed));
			Raycaster rc = new Raycaster(maze);
			Random rnd = new Random(seed);
			for (int i = 0; i < 200; i++) {
				double x = rnd.nextInt(10) + 0.5;
				double y = rnd.nextInt(7) + 0.5;
				double angle = rnd.nextDouble() * 2 * Math.PI;
				double cx = rnd.nextDouble() * 2 - 1;
				RayHit hit = rc.cast(player(x, y, angle), cx);
				assertNotNull(hit.face());
				assertTrue(hit.distance() > 0 && hit.distance() < 1e9,
						"distanza " + hit.distance() + " seed " + seed);
			}
		}
	}

	@Test
	void axisAlignedRaysDoNotProduceNaN() {
		Raycaster rc = new Raycaster(openRow(4));
		double[] angles = { 0, Math.PI / 2, Math.PI, 3 * Math.PI / 2 };
		for (double a : angles) {
			RayHit hit = rc.cast(player(1.5, 0.5, a), 0);
			assertFalse(Double.isNaN(hit.distance()), "angolo " + a);
			assertFalse(Double.isNaN(hit.wallOffset()), "angolo " + a);
		}
	}
}