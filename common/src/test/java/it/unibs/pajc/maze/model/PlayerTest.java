package it.unibs.pajc.maze.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerTest {

	private static final double EPS = 1e-9;

	@Test
	void constructorNormalizesAngle() {
		assertEquals(Math.PI, new Player("a", 0, 0, 7 * Math.PI, 0, 0).getAngle(), EPS);
		assertEquals(3 * Math.PI / 2, new Player("a", 0, 0, -Math.PI / 2, 0, 0).getAngle(), EPS);
	}

	@Test
	void setAngleNormalizesAngle() {
		Player p = new Player("a", 0, 0, 0, 0, 0);
		p.setAngle(-Math.PI / 2);
		assertEquals(3 * Math.PI / 2, p.getAngle(), EPS);
		p.setAngle(9 * Math.PI);
		assertEquals(Math.PI, p.getAngle(), EPS);
	}

	@Test
	void angleStaysInRangeAfterManyRotations() {
		Player p = new Player("a", 0, 0, 0, 0, 0);
		for (int i = 0; i < 1000; i++) {
			p.setAngle(p.getAngle() - 0.37);
			assertTrue(p.getAngle() >= 0 && p.getAngle() < 2 * Math.PI);
		}
	}

	@Test
	void invalidIdIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> new Player(null, 0, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Player("", 0, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new Player("   ", 0, 0, 0, 0, 0));
	}

	@Test
	void cellUsesFloor() {
		Player p = new Player("a", 2.7, 3.2, 0, 0, 0);
		assertEquals(2, p.getCellX());
		assertEquals(3, p.getCellY());

		Player neg = new Player("b", -0.1, -0.5, 0, 0, 0);
		assertEquals(-1, neg.getCellX());
		assertEquals(-1, neg.getCellY());
	}

	@Test
	void directionVectorFollowsAngle() {
		Player east = new Player("a", 0, 0, 0, 0, 0);
		assertEquals(1, east.getDirX(), EPS);
		assertEquals(0, east.getDirY(), EPS);

		Player south = new Player("b", 0, 0, Math.PI / 2, 0, 0);
		assertEquals(0, south.getDirX(), EPS);
		assertEquals(1, south.getDirY(), EPS);
	}

	@Test
	void isOnTreasureOnlyInTreasureCell() {
		Player p = new Player("a", 3.5, 4.5, 0, 3, 4);
		assertTrue(p.isOnTreasure());
		p.setX(2.9);
		assertFalse(p.isOnTreasure());
		p.setX(3.99);
		p.setY(5.0);
		assertFalse(p.isOnTreasure());
	}
}