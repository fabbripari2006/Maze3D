package it.unibs.pajc.maze.model;

import java.util.Random;

public class MazeDemo {
	public static void main(String[] args) {
		Maze maze = MazeGenerator.generateMaze(30, 10, new Random());
		System.out.println(MazePrinter.toAscii(maze));
	}
}