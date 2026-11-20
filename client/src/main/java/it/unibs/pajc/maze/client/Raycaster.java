package it.unibs.pajc.maze.client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import it.unibs.pajc.maze.model.Direction;
import it.unibs.pajc.maze.model.Maze;
import it.unibs.pajc.maze.model.Player;

public class Raycaster {

	private static final double FOV = Math.toRadians(66);
	private static final double TAN_FOV = Math.tan(FOV/2);
	private static final Color WALL_NS = new Color(29, 158, 117);
	private static final Color WALL_EW = new Color(15, 110, 86);
	private final Maze maze;

	public Raycaster(Maze maze) {
		this.maze = maze;
	}
	
	public RayHit cast(Player player, double cameraX) {
		double [] ray = rayDirection(player, cameraX);
		double rayX = ray[0];
		double rayY = ray[1];
		
		int mapX = player.getCellX();
		int mapY = player.getCellY();
		
		final double deltaDistX = rayX == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/rayX);
		final double deltaDistY = rayY == 0 ? Double.POSITIVE_INFINITY : Math.abs(1/rayY);
		
		final int stepX = rayX > 0 ? 1 : -1;
		final int stepY = rayY > 0 ? 1 : -1;
		
		double sideDistX;
		double sideDistY;
		if(rayX > 0) sideDistX = (mapX + 1 - player.getX()) * deltaDistX;
		else if (rayX < 0) sideDistX = (player.getX() - mapX) * deltaDistX;
		else sideDistX = Double.POSITIVE_INFINITY;
		if(rayY > 0) sideDistY = (mapY + 1 - player.getY()) * deltaDistY;
		else if (rayY < 0) sideDistY = (player.getY() - mapY) * deltaDistY;
		else sideDistY = Double.POSITIVE_INFINITY;
		
		double distance = 0;
		Direction face = null;
		
		for(int i = 0; i < maze.getWidth() + maze.getHeight() + 2; i++) {
			boolean moveX = sideDistX < sideDistY;;
			Direction moveDirection;

			if(moveX) {
				if(stepX==1) {
					moveDirection = Direction.EAST;
				} else {
					moveDirection = Direction.WEST;
				}
				
			} else if(stepY==1) {
				moveDirection = Direction.SOUTH;
			} else {
				moveDirection = Direction.NORTH;
			}
			
			if(maze.hasWall(mapX, mapY, moveDirection)) {
				distance = moveX ? sideDistX : sideDistY;
				face = moveDirection;
				break;
			} else {
				if(moveX) {
					mapX += stepX;
					sideDistX += deltaDistX;
				} else {
					mapY += stepY;
					sideDistY += deltaDistY;
				}
			}
		}
		
		if (face == null) return new RayHit(1E30, Direction.EAST, 0);
		
		double hitX = player.getX() + rayX * distance;
		double hitY = player.getY() + rayY * distance;
		
		double wallOffset = (face == Direction.EAST || face == Direction.WEST) ? hitY - Math.floor(hitY) : hitX - Math.floor(hitX);
		
		return new RayHit(distance, face, wallOffset);
	}
	
	double [] rayDirection(Player player, double cameraX) {
		double dirX = player.getDirX();
		double dirY = player.getDirY();
		
		double planeX = -dirY * TAN_FOV;
		double planeY = dirX * TAN_FOV;
		
		double rayX = dirX + planeX * cameraX;
		double rayY = dirY + planeY * cameraX;
		
		double [] ray = {rayX, rayY};
		return ray;
	}
	
	public void render(Player player, BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.cyan);
		g.fillRect(0, 0, width, height/2);
		
		g.setColor(Color.gray);
		g.fillRect(0, height/2, width, height/2);
		
		double projection = width / (2 * TAN_FOV);
		double cameraX;
		double [] zBuffer = new double[width];
		for (int x = 0; x < width; x++) {
			cameraX = 2.0 * (x + 0.5) / width - 1;
			RayHit hit = cast(player, cameraX);
			
			int lineHeight = (int) (projection / hit.distance());
			int start = Math.max(0, (height - lineHeight) / 2);
			int end = Math.min(height, (height + lineHeight) / 2);
			
			Color wallColor = wallColor(hit);
			g.setColor(wallColor);
			g.fillRect(x, start, 1, end - start);
			
			zBuffer[x] = hit.distance();
		}
		
		
		g.dispose();
	}

	private Color wallColor(RayHit hit) {
		Color base = (hit.face() == Direction.NORTH || hit.face() == Direction.SOUTH) ? WALL_NS : WALL_EW;
		double alpha = Math.max(0.2, 1 - hit.distance() * 0.11);
		int red = (int) (base.getRed() * alpha);
		int green = (int) (base.getGreen() * alpha);
		int blue = (int) (base.getBlue() * alpha);
		
		Color wallColor = new Color(red, green, blue);
		return wallColor;
	}
}
