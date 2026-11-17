package it.unibs.pajc.maze.client;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.Timer;

import it.unibs.pajc.maze.model.Direction;
import it.unibs.pajc.maze.model.Game;
import it.unibs.pajc.maze.model.Maze;
import it.unibs.pajc.maze.model.Player;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final InputHandler inputHandler = new InputHandler();
	private final Game game;
	private final Player player;
	private long lastTick;
	private Timer timer;
	
	public GamePanel(Game game, Player player) {
		this.game = game;
		this.player = player;
		inputHandler.install(this);		
		
		timer = new Timer(16, e -> {
			long now = System.nanoTime();
			double dt = (now - lastTick) / 1E9; 
			lastTick = now;
			game.update(player.getId(), inputHandler.current(), dt);
			repaint();
		});
		lastTick = System.nanoTime();
		timer.start();
		
		setPreferredSize(new Dimension(800, 600));
	}

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    
	    Maze maze = game.getMaze();
	    int width = maze.getWidth();
	    int height = maze.getHeight();
	    double cellSize = Math.min((double) this.getWidth() / width, (double) this.getHeight() / height);
	    
	    g2.setColor(Color.black);
	    g2.setStroke(new BasicStroke(3f));
	    
	    for (int y=0; y<height; y++) {
	        for (int x=0; x<width; x++) {
	            if (maze.hasWall(x, y, Direction.NORTH)) {
	                g2.drawLine(
	                    (int) (x * cellSize), 
	                    (int) (y * cellSize), 
	                    (int) ((x + 1) * cellSize), 
	                    (int) (y * cellSize)
	                );
	            }
	            
	            if (maze.hasWall(x, y, Direction.WEST)) {
	                g2.drawLine(
	                    (int) (x * cellSize), 
	                    (int) (y * cellSize), 
	                    (int) (x * cellSize), 
	                    (int) ((y + 1) * cellSize)
	                );
	            }
	        }
	        
	        g2.drawLine(
                (int) (width * cellSize), 
                (int) (y * cellSize), 
                (int) (width * cellSize), 
                (int) ((y + 1) * cellSize)
            );
	    }
	    
	    for (int x=0; x<width; x++) {
	    	g2.drawLine(
                (int) (x * cellSize), 
                (int) (height * cellSize), 
                (int) ((x + 1) * cellSize), 
                (int) (height * cellSize)
            );
	    }
	    
	    double radius = Game.RADIUS * cellSize;
	    double centerX = player.getX() * cellSize;
	    double centerY = player.getY() * cellSize;
	    
	    g2.setColor(Color.yellow);
	    g2.fillRect(
	    	(int) (player.getTreasureX() * cellSize + cellSize * 0.15), 
	    	(int) (player.getTreasureY() * cellSize + cellSize * 0.15), 
	    	(int) (cellSize*0.7), 
	    	(int) (cellSize*0.7)
	    );
	    
	    g2.setColor(Color.red);
	    g2.fillOval(
	    	(int) (centerX - radius), 
	    	(int) (centerY - radius), 
	    	(int) (radius*2), 
	    	(int) (radius*2)
	    );
	    
	    g2.setStroke(new BasicStroke(1.5f));
	    g2.setColor(Color.blue);
	    g2.drawLine(
	    	(int) centerX, 
	    	(int) centerY, 
	    	(int) (centerX + player.getDirX() * cellSize * 0.9), 
	    	(int) (centerY + player.getDirY() * cellSize * 0.9)
	    );
	    
	    g2.setColor(Color.red);
	    g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
	    g2.drawString(String.format("x=%.2f  y=%.2f  angolo=%.0f°",
	    		player.getX(), player.getY(), Math.toDegrees(player.getAngle())), 10, 20);
	    
	    if (game.isOver()) {
	    	String line1 = "VITTORIA!";
	    	String line2 = "Il vincitore è " + game.getWinnerId().orElse("?");

	    	g2.setColor(Color.green);
	    	g2.setFont(new Font("SansSerif", Font.BOLD, 48));
	    	FontMetrics fm1 = g2.getFontMetrics();
	    	int x1 = (getWidth() - fm1.stringWidth(line1)) / 2;
	    	int y1 = getHeight() / 2;
	    	g2.drawString(line1, x1, y1);

	    	g2.setFont(new Font("SansSerif", Font.PLAIN, 24));
	    	FontMetrics fm2 = g2.getFontMetrics();
	    	int x2 = (getWidth() - fm2.stringWidth(line2)) / 2;
	    	int y2 = y1 + fm1.getHeight() / 2 + 10;
	    	g2.drawString(line2, x2, y2);
	    }
	}
	
	public void releaseKeys() {
		inputHandler.reset();
	}
	
	public void stop() {
		timer.stop();
	}
}
