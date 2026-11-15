package it.unibs.pajc.maze.model;

public class Maze {

	private int width, height;
	private Cell[][] grid;
	
	public Maze(int width, int height) {
		this.width = width;
		this.height = height;
		
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) grid[i][j] = new Cell();
		}
	}
	
	private boolean isInside(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	public void destroyWall(int x, int y, Direction direction) {
		if(!isInside(x, y)) return;
		
		grid[y][x].setWall(direction, false);
		grid[y+direction.getDy()][x+direction.getDx()].setWall(direction.opposite(), false);
	}
}
