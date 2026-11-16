package it.unibs.pajc.maze.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

class GameTest {

	private static final double EPS = 1e-9;

	// ---- helper ----

	private static Maze openMaze(int w, int h) {
		Maze maze = new Maze(w, h);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x + 1 < w) maze.destroyWall(x, y, Direction.EAST);
				if (y + 1 < h) maze.destroyWall(x, y, Direction.SOUTH);
			}
		}
		return maze;
	}

	private static Player placed(Game g, String id, double x, double y, double angle) {
		g.addPlayer(id);
		Player p = g.getPlayer(id);
		p.setX(x);
		p.setY(y);
		p.setAngle(angle);
		return p;
	}

	// ---- addPlayer ----

	@Test
	void spawnAndTreasureAreInOppositeQuadrants() {
		int[][] sizes = { { 10, 10 }, { 7, 5 }, { 5, 5 }, { 2, 2 } };
		for (int[] s : sizes) {
			int w = s[0], h = s[1];
			for (long seed = 0; seed < 50; seed++) {
				Game g = new Game(MazeGenerator.generateMaze(w, h, new Random(seed)), new Random(seed));
				g.addPlayer("a");
				Player p = g.getPlayer("a");
				String ctx = w + "x" + h + " seed " + seed;
				assertTrue(p.getCellX() < Math.max(1, w / 2) && p.getCellY() < Math.max(1, h / 2), "spawn: " + ctx);
				assertTrue(p.getTreasureX() >= w - Math.max(1, w / 2) && p.getTreasureX() < w, "tesoro x: " + ctx);
				assertTrue(p.getTreasureY() >= h - Math.max(1, h / 2) && p.getTreasureY() < h, "tesoro y: " + ctx);
			}
		}
	}

	@Test
	void spawnIsAtCellCenter() {
		Game g = new Game(MazeGenerator.generateMaze(8, 8, new Random(3)), new Random(3));
		g.addPlayer("a");
		Player p = g.getPlayer("a");
		assertEquals(p.getCellX() + 0.5, p.getX(), EPS);
		assertEquals(p.getCellY() + 0.5, p.getY(), EPS);
	}

	@Test
	void spawnAngleFacesAnOpenSide() {
		for (long seed = 0; seed < 50; seed++) {
			Maze maze = MazeGenerator.generateMaze(6, 6, new Random(seed));
			Game g = new Game(maze, new Random(seed));
			g.addPlayer("a");
			Player p = g.getPlayer("a");

			int dx = (int) Math.round(p.getDirX());
			int dy = (int) Math.round(p.getDirY());
			Direction facing = null;
			for (Direction d : Direction.values()) {
				if (d.getDx() == dx && d.getDy() == dy) facing = d;
			}
			assertNotNull(facing, "angolo non allineato a una direzione, seed " + seed);
			assertFalse(maze.hasWall(p.getCellX(), p.getCellY(), facing), "guarda un muro, seed " + seed);
		}
	}

	@Test
	void oneByOneMazeUsesAngleZero() {
		Game g = new Game(new Maze(1, 1), new Random(0));
		g.addPlayer("a");
		assertEquals(0, g.getPlayer("a").getAngle(), EPS);
	}

	@Test
	void duplicateIdIsRejected() {
		Game g = new Game(new Maze(4, 4), new Random(0));
		g.addPlayer("a");
		assertThrows(IllegalArgumentException.class, () -> g.addPlayer("a"));
	}

	@Test
	void removePlayerRemovesIt() {
		Game g = new Game(new Maze(4, 4), new Random(0));
		g.addPlayer("a");
		g.removePlayer("a");
		assertNull(g.getPlayer("a"));
		assertTrue(g.getPlayers().isEmpty());
	}

	@Test
	void sameSeedGivesSameSpawn() {
		Game a = new Game(MazeGenerator.generateMaze(10, 10, new Random(5)), new Random(9));
		Game b = new Game(MazeGenerator.generateMaze(10, 10, new Random(5)), new Random(9));
		a.addPlayer("p");
		b.addPlayer("p");
		assertEquals(a.getPlayer("p").getX(), b.getPlayer("p").getX(), EPS);
		assertEquals(a.getPlayer("p").getAngle(), b.getPlayer("p").getAngle(), EPS);
		assertEquals(a.getPlayer("p").getTreasureX(), b.getPlayer("p").getTreasureX());
	}

	// ---- update: movimento ----

	@Test
	void forwardMovesAlongFacingDirection() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0); // EST
		g.update("a", new PlayerInput(1, 0, 0), 0.05);
		assertEquals(2.5 + Game.SPEED * 0.05, p.getX(), EPS);
		assertEquals(2.5, p.getY(), EPS);
	}

	@Test
	void backwardMovesOpposite() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0);
		g.update("a", new PlayerInput(-1, 0, 0), 0.05);
		assertEquals(2.5 - Game.SPEED * 0.05, p.getX(), EPS);
	}

	@Test
	void strafePositiveGoesRight() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0); // guarda EST: la destra è SUD (y cresce)
		g.update("a", new PlayerInput(0, 1, 0), 0.05);
		assertEquals(2.5, p.getX(), EPS);
		assertEquals(2.5 + Game.SPEED * 0.05, p.getY(), EPS);
	}

	@Test
	void turnChangesAngleOnly() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0);
		g.update("a", new PlayerInput(0, 0, 1), 0.05);
		assertEquals(Game.TURN_SPEED * 0.05, p.getAngle(), EPS);
		assertEquals(2.5, p.getX(), EPS);
		assertEquals(2.5, p.getY(), EPS);
	}

	@Test
	void turningNegativeKeepsAngleInRange() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0);
		g.update("a", new PlayerInput(0, 0, -1), 0.05);
		assertTrue(p.getAngle() >= 0 && p.getAngle() < 2 * Math.PI);
	}

	// ---- update: collisioni ----

	@Test
	void playerCannotLeaveTheMaze() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, Math.PI); // OVEST
		for (int i = 0; i < 200; i++) g.update("a", new PlayerInput(1, 0, 0), 0.05);
		assertTrue(p.getX() >= Game.RADIUS - EPS);
	}

	@Test
	void closedWallStopsThePlayer() {
		Game g = new Game(new Maze(5, 1), new Random(0)); // tutti i muri chiusi
		Player p = placed(g, "a", 0.5, 0.5, 0);
		for (int i = 0; i < 100; i++) g.update("a", new PlayerInput(1, 0, 0), 0.05);
		assertTrue(p.getX() <= 1 - Game.RADIUS + EPS);
	}

	@Test
	void playerSlidesAlongWall() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 0.3, -Math.PI / 4); // nord-est, muro nord chiuso
		g.update("a", new PlayerInput(1, 0, 0), 0.1);
		assertEquals(0.3, p.getY(), EPS);      // y bloccata
		assertTrue(p.getX() > 2.5);            // x avanza
	}

	@Test
	void playerPassesThroughOpenWall() {
		Maze maze = new Maze(3, 1);
		maze.destroyWall(0, 0, Direction.EAST);
		Game g = new Game(maze, new Random(0));
		Player p = placed(g, "a", 0.5, 0.5, 0);
		for (int i = 0; i < 20; i++) g.update("a", new PlayerInput(1, 0, 0), 0.05);
		assertTrue(p.getX() > 1.0);
	}

	// ---- update: sicurezza dell'input ----

	@Test
	void hugeDtIsClamped() {
		Game g = new Game(openMaze(20, 20), new Random(0));
		Player p = placed(g, "a", 10.5, 10.5, 0);
		g.update("a", new PlayerInput(1, 0, 0), 1000);
		assertEquals(10.5 + Game.SPEED * Game.MAX_DT, p.getX(), EPS);
	}

	@Test
	void negativeDtDoesNothing() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0);
		g.update("a", new PlayerInput(1, 0, 1), -1);
		assertEquals(2.5, p.getX(), EPS);
		assertEquals(0, p.getAngle(), EPS);
	}

	@Test
	void hugeInputIsClamped() {
		Game g = new Game(openMaze(20, 20), new Random(0));
		Player p = placed(g, "a", 10.5, 10.5, 0);
		g.update("a", new PlayerInput(1000, 0, 0), 0.05);
		assertEquals(10.5 + Game.SPEED * 0.05, p.getX(), EPS);
	}

	@Test
	void nanInputIsIgnored() {
		Game g = new Game(openMaze(5, 5), new Random(0));
		Player p = placed(g, "a", 2.5, 2.5, 0);
		g.update("a", new PlayerInput(Double.NaN, Double.NaN, Double.NaN), 0.05);
		assertEquals(2.5, p.getX(), EPS);
		assertEquals(2.5, p.getY(), EPS);
		assertEquals(0, p.getAngle(), EPS);
	}

	@Test
	void updateOnUnknownPlayerDoesNotThrow() {
		Game g = new Game(new Maze(3, 3), new Random(0));
		assertDoesNotThrow(() -> g.update("nessuno", new PlayerInput(1, 0, 0), 0.05));
	}

	// ---- vittoria ----

	@Test
	void noWinnerAtStart() {
		Game g = new Game(openMaze(6, 6), new Random(0));
		g.addPlayer("a");
		assertFalse(g.isOver());
		assertTrue(g.getWinnerId().isEmpty());
	}

	@Test
	void reachingTreasureEndsTheGame() {
		Game g = new Game(openMaze(2, 1), new Random(0)); // tesoro sempre in (1,0)
		placed(g, "a", 0.7, 0.5, 0);
		for (int i = 0; i < 10 && !g.isOver(); i++) g.update("a", new PlayerInput(1, 0, 0), 0.1);
		assertTrue(g.isOver());
		assertEquals("a", g.getWinnerId().orElseThrow());
	}

	@Test
	void oneByOneMazeEndsAfterFirstUpdate() {
		Game g = new Game(new Maze(1, 1), new Random(0));
		g.addPlayer("a");
		g.update("a", PlayerInput.NONE, 0.016);
		assertTrue(g.isOver());
		assertEquals("a", g.getWinnerId().orElseThrow());
	}

	@Test
	void nobodyMovesAfterTheGameIsOver() {
		Game g = new Game(openMaze(2, 1), new Random(0));
		placed(g, "a", 0.7, 0.5, 0);
		placed(g, "b", 0.3, 0.5, 0);
		for (int i = 0; i < 10 && !g.isOver(); i++) g.update("a", new PlayerInput(1, 0, 0), 0.1);
		assertTrue(g.isOver());

		Player b = g.getPlayer("b");
		double bx = b.getX();
		g.update("b", new PlayerInput(1, 0, 0), 0.1);
		assertEquals(bx, b.getX(), EPS);
	}

	@Test
	void firstToReachTreasureWins() {
		Game g = new Game(openMaze(2, 1), new Random(0));
		placed(g, "a", 0.7, 0.5, 0);
		placed(g, "b", 0.7, 0.5, 0);
		for (int i = 0; i < 10 && !g.isOver(); i++) {
			g.update("a", new PlayerInput(1, 0, 0), 0.1);
			g.update("b", new PlayerInput(1, 0, 0), 0.1);
		}
		assertEquals("a", g.getWinnerId().orElseThrow());
	}
}