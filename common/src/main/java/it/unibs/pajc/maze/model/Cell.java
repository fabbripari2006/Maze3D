package it.unibs.pajc.maze.model;

import java.util.EnumMap;

public class Cell {

	private final EnumMap<Direction, Boolean> walls = new EnumMap<>(Direction.class);

	public Cell() {
		for(Direction d : Direction.values()) walls.put(d, true);
	}
	
	/*
	public EnumMap<Direction, Boolean> getWalls() {
		return new EnumMap<>(walls);
	}
	*/
	
	public boolean hasWall(Direction direction) {
		return walls.get(direction);
	}
	
	public void setWall(Direction direction, boolean present) {
		walls.put(direction, present);
	}
}
