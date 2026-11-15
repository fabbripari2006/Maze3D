package it.unibs.pajc.maze.model;

public class Maze {

	private final int width, height;
	private final Cell[][] grid;
	
	public Maze(int width, int height) throws IllegalArgumentException{
		if(width<1 || height<1) throw new IllegalArgumentException("Le dimensioni devono essere almeno 1x1");
		
		this.width = width;
		this.height = height;
		this.grid = new Cell[height][width];
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				grid[y][x] = new Cell();
			}
		}
	}
	
	private boolean isInside(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	public boolean hasWall(int x, int y, Direction direction) {
		if(!isInside(x, y)) return true;
		return grid[y][x].hasWall(direction);
	}
	
	public void destroyWall(int x, int y, Direction direction) {
		int x2 = x + direction.getDx();
		int y2 = y + direction.getDy();
		if(!(isInside(x, y) && isInside(x2, y2))) return;
		
		grid[y][x].setWall(direction, false);
		grid[y2][x2].setWall(direction.opposite(), false);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
