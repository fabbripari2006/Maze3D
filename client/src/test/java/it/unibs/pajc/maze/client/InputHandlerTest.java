package it.unibs.pajc.maze.client;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibs.pajc.maze.model.PlayerInput;

class InputHandlerTest {

	private static final double EPS = 1e-9;
	private static final double INV_SQRT_TWO = 1 / Math.sqrt(2);

	private InputHandler handler;
	private JPanel panel;

	@BeforeEach
	void setUp() {
		handler = new InputHandler();
		panel = new JPanel();
		handler.install(panel);
	}

	// ---- helper ----

	private void press(String key) {
		fire(key + ".press");
	}

	private void release(String key) {
		fire(key + ".release");
	}

	private void fire(String actionName) {
		panel.getActionMap().get(actionName)
				.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, actionName));
	}

	// ---- registrazione dei binding ----

	@Test
	void installRegistersPressAndReleaseForEveryKey() {
		String[] keys = { "W", "A", "S", "D", "LEFT", "RIGHT" };
		for (String k : keys) {
			assertEquals(k + ".press",
					panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("pressed " + k)),
					"Binding di pressione mancante per " + k);
			assertEquals(k + ".release",
					panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("released " + k)),
					"Binding di rilascio mancante per " + k);
			assertNotNull(panel.getActionMap().get(k + ".press"));
			assertNotNull(panel.getActionMap().get(k + ".release"));
		}
	}

	// ---- singoli tasti ----

	@Test
	void noKeysGivesNone() {
		assertEquals(PlayerInput.NONE, handler.current());
	}

	@Test
	void wMovesForward() {
		press("W");
		assertEquals(1, handler.current().forward(), EPS);
		assertEquals(0, handler.current().strafe(), EPS);
		assertEquals(0, handler.current().turn(), EPS);
	}

	@Test
	void sMovesBackward() {
		press("S");
		assertEquals(-1, handler.current().forward(), EPS);
	}

	@Test
	void dStrafesRight() {
		press("D");
		assertEquals(1, handler.current().strafe(), EPS);
		assertEquals(0, handler.current().forward(), EPS);
	}

	@Test
	void aStrafesLeft() {
		press("A");
		assertEquals(-1, handler.current().strafe(), EPS);
	}

	@Test
	void rightArrowTurnsPositive() {
		press("RIGHT");
		assertEquals(1, handler.current().turn(), EPS);
	}

	@Test
	void leftArrowTurnsNegative() {
		press("LEFT");
		assertEquals(-1, handler.current().turn(), EPS);
	}

	// ---- combinazioni ----

	@Test
	void oppositeKeysCancelOut() {
		press("W");
		press("S");
		press("A");
		press("D");
		press("LEFT");
		press("RIGHT");
		assertEquals(PlayerInput.NONE, handler.current());
	}

	@Test
	void diagonalMovementIsNormalized() {
		press("W");
		press("D");
		PlayerInput in = handler.current();
		assertEquals(INV_SQRT_TWO, in.forward(), EPS);
		assertEquals(INV_SQRT_TWO, in.strafe(), EPS);
	}

	@Test
	void diagonalBackwardLeftKeepsSigns() {
		press("S");
		press("A");
		PlayerInput in = handler.current();
		assertEquals(-INV_SQRT_TWO, in.forward(), EPS);
		assertEquals(-INV_SQRT_TWO, in.strafe(), EPS);
	}

	@Test
	void turningDoesNotAffectNormalization() {
		press("W");
		press("RIGHT");
		PlayerInput in = handler.current();
		assertEquals(1, in.forward(), EPS);
		assertEquals(1, in.turn(), EPS);
	}

	// ---- rilascio, ripetizioni, reset ----

	@Test
	void releaseStopsTheMovement() {
		press("W");
		release("W");
		assertEquals(PlayerInput.NONE, handler.current());
	}

	@Test
	void releasingOneKeyKeepsTheOthers() {
		press("W");
		press("D");
		release("D");
		PlayerInput in = handler.current();
		assertEquals(1, in.forward(), EPS);
		assertEquals(0, in.strafe(), EPS);
	}

	@Test
	void repeatedPressDoesNotStack() {
		press("W");
		press("W");
		press("W");
		assertEquals(1, handler.current().forward(), EPS);
		release("W");
		assertEquals(0, handler.current().forward(), EPS);
	}

	@Test
	void releaseWithoutPressDoesNothing() {
		assertDoesNotThrow(() -> release("W"));
		assertEquals(PlayerInput.NONE, handler.current());
	}

	@Test
	void resetClearsAllKeys() {
		press("W");
		press("D");
		press("LEFT");
		handler.reset();
		assertEquals(PlayerInput.NONE, handler.current());
	}
}