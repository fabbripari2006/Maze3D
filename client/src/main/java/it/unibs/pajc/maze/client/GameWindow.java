package it.unibs.pajc.maze.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import it.unibs.pajc.maze.model.*;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private final GamePanel panel;

	public GameWindow(Game game, Player player) {
		super("Maze3D");
		panel = new GamePanel(game, player);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				panel.releaseKeys();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				panel.stop();
			}
		});
		
		add(panel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

}
