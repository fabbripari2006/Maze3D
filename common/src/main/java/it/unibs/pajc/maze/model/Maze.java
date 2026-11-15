package it.unibs.pajc.maze.model;

public class Maze {

	private Cell[][] grid;
	
	public Maze(int width, int height) {
		for(int i=0; i<height; i++) {
			for(int j=0; j<width; j++) grid[i][j] = new Cell();
		}
	}
	
	public void destroyWall(int x, int y, Direction direction) {
		grid[y][x].setWall(direction, false);
		grid[y+direction.getDy()][x+direction.getDx()].setWall(direction.opposite(), false);
	}
}
