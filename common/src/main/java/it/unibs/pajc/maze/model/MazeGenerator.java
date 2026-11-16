package it.unibs.pajc.maze.model;

import java.util.*;

public class MazeGenerator {
	private record Position(int x, int y) {}

	public static Maze generateMaze(int width, int height, Random random) throws IllegalArgumentException{
		Maze maze = new Maze(width, height);
		boolean[][] visited = new boolean[height][width];
		Deque<Position> stack = new ArrayDeque<>();
		
		visited[0][0] = true;
		stack.push(new Position(0, 0));
		
		while(!stack.isEmpty()) {
			Position current = stack.peek();
			int x = current.x();
			int y = current.y();
			
			List<Direction> neighbours = getUnvisitedNeighbours(maze, visited, current);
			
			if(!neighbours.isEmpty()) {
				Direction randomNeighbour = getRandomNeighbour(neighbours, random);
				int nx = x + randomNeighbour.getDx();
				int ny = y + randomNeighbour.getDy();
				maze.destroyWall(x, y, randomNeighbour);
				visited[ny][nx] = true;
				stack.push(new Position(nx, ny));
				
			} else {
				stack.pop();
			}
		}
		
		return maze;
	}
	
	private static List<Direction> getUnvisitedNeighbours(Maze maze, boolean[][] visited, Position position) {
		ArrayList<Direction> neighbours = new ArrayList<>();
		
		for(Direction d : Direction.values()) {
			int nx = position.x() + d.getDx();
			int ny = position.y() + d.getDy();
			
			if(maze.isInside(nx, ny) && !visited[ny][nx]) neighbours.add(d);
		}
		
		return neighbours;
	}
	
	private static Direction getRandomNeighbour(List<Direction> neighbours, Random random) {
		return neighbours.get(random.nextInt(neighbours.size()));
	}
}
