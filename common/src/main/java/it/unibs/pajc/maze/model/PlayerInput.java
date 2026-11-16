package it.unibs.pajc.maze.model;

public record PlayerInput(double forward, double strafe, double turn) {
	public static final PlayerInput NONE = new PlayerInput(0, 0, 0);
}
