package it.unibs.pajc.maze.model;

import java.util.*;

public class Game {
	public static final double RADIUS = 0.2;
	public static final double SPEED = 2.5;
	public static final double TURN_SPEED = 2.5;
	public static final double MAX_DT = 0.1;
	
	private final Maze maze;
	private final Random random;
	private final Map<String, Player> players = new LinkedHashMap<>();
	private String winnerId = null;
	
	public Game(Maze maze, Random random) {
		this.maze = maze;
		this.random = random;
	}
	
	public Player addPlayer(String id) {
		if(players.containsKey(id)) throw new IllegalArgumentException("Id giocatore già esistente");
		
		int halfWidth = Math.max(maze.getWidth() / 2, 1);
		int halfHeight = Math.max(maze.getHeight() / 2, 1);
		
		double spawnX = random.nextDouble(halfWidth);
		double spawnY = random.nextDouble(halfHeight);
		int cellSpawnX = (int) Math.floor(spawnX);
		int cellSpawnY = (int) Math.floor(spawnY);
		
		int cellTreasureX = maze.getWidth() - halfWidth + random.nextInt(halfWidth);
		int cellTreasureY = maze.getHeight() - halfHeight + random.nextInt(halfHeight);
		
		List<Direction> directionsWithNoWall = new ArrayList<Direction>();
		
		for(Direction d : Direction.values()) {
			if (!maze.hasWall(cellSpawnX, cellSpawnY, d)) directionsWithNoWall.add(d); 
		}
		Direction spawnDirection;
		double angle;
		
		if(!directionsWithNoWall.isEmpty()) {
			spawnDirection = directionsWithNoWall.get(random.nextInt(directionsWithNoWall.size()));
			angle = Math.atan2(spawnDirection.getDy(), spawnDirection.getDx());
		} else {
			angle = 0;
		}
		
		Player player = new Player(id, cellSpawnX + 0.5, cellSpawnY + 0.5, angle, cellTreasureX, cellTreasureY);
		players.put(id, player);
		
		return player;
	}
	
	public void removePlayer(String id) {
		players.remove(id);
	}
	
	public void update(String id, PlayerInput input, double dt) {
		Player p = players.get(id);
		if(p == null || isOver()) return;
		
		dt = Math.min(Math.max(dt, 0), MAX_DT);
		double forward = clamp(input.forward());
		double strafe = clamp(input.strafe());
		double turn = clamp(input.turn());
		
		p.setAngle(p.getAngle() + turn * TURN_SPEED * dt);
		
		double cos = Math.cos(p.getAngle());
		double sin = Math.sin(p.getAngle());
		
		double dx = (cos * forward - sin * strafe) * SPEED * dt;
		double dy = (sin * forward + cos * strafe) * SPEED * dt;
		
		double nx = p.getX() + dx;
		if (!Collision.isBlocked(maze, nx, p.getY(), RADIUS)) p.setX(nx);
		double ny = p.getY() + dy;
		if (!Collision.isBlocked(maze, p.getX(), ny, RADIUS)) p.setY(ny);

		if (p.isOnTreasure()) winnerId = id;
	}
	
	private static double clamp(double v) {
		return Double.isNaN(v) ? 0 : Math.max(-1, Math.min(1, v));
	}
	
	public Maze getMaze() { return maze; }
	public Optional<String> getWinnerId() { return Optional.ofNullable(winnerId); }
	public boolean isOver() { return winnerId != null; }
	public Collection<Player> getPlayers() { return Collections.unmodifiableCollection(players.values()); }
	public Player getPlayer(String id) { return players.get(id); }
}
