package it.unibs.pajc.maze.model;

public class Player {

	private static final double TWO_PI = 2 * Math.PI;
	
	private final String id;
	private double x, y;
	private double angle;
	private final int treasureX, treasureY;
	
	public Player(String id, double x, double y, double angle, int treasureX, int treasureY) {
		if(id==null || id.isBlank()) throw new IllegalArgumentException("Id non valido");
		this.id = id;
		this.x = x;
		this.y = y;
		this.angle = normalize(angle);
		this.treasureX = treasureX;
		this.treasureY = treasureY;
	}

	private double normalize(double angle) {
		return (angle%TWO_PI + TWO_PI) %TWO_PI;
	}

	public double getX() {return x;}
	public double getY() {return y;}
	public double getAngle() {return angle;}
	public String getId() {return id;}
	public int getTreasureX() {return treasureX;}
	public int getTreasureY() {return treasureY;}

	void setX(double x) {this.x = x;}
	void setY(double y) {this.y = y;}
	void setAngle(double angle) {this.angle = normalize(angle);}
	
	public int getCellX() { return (int) Math.floor(x); }
	public int getCellY() { return (int) Math.floor(y); }
	public double getDirX() { return Math.cos(angle); }
	public double getDirY() { return Math.sin(angle); }

	public boolean isOnTreasure() {
		return getCellX() == treasureX && getCellY() == treasureY;
	}

}
