package it.unibs.pajc.maze.client;

import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;

import it.unibs.pajc.maze.model.PlayerInput;

public class InputHandler {

	private static final double SQRT_TWO = Math.sqrt(2);
	
	private final Set<String> keys = new HashSet<>();
	
	public void install(JComponent c) {
		String [] keyNames = {"W", "A", "S", "D", "LEFT", "RIGHT"};
		
		for (String s : keyNames) {
			bind(c, s, 
					() -> {
						keys.add(s);
					},
					() -> {
						keys.remove(s);
					});
		}
	}
	
	private void bind(JComponent c, String key, Runnable press, Runnable release) {
		KeyStroke keyPressed = KeyStroke.getKeyStroke("pressed " + key);
		KeyStroke keyReleased = KeyStroke.getKeyStroke("released " + key);
		String actionPress = key + ".press";
		String actionRelease = key + ".release";
		
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyPressed, actionPress);
		c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyReleased, actionRelease);
		
		c.getActionMap().put(actionPress, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(press!=null) {
                	press.run();
                }
            }
        });
		c.getActionMap().put(actionRelease, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(release!=null) {
                	release.run();
                }
            }
        });
	}
	
	public PlayerInput current() {
		double forward = (keys.contains("W") ? 1 : 0) - (keys.contains("S") ? 1 : 0);
		double strafe = (keys.contains("D") ? 1 : 0) - (keys.contains("A") ? 1 : 0);
		double turn = (keys.contains("RIGHT") ? 1 : 0) - (keys.contains("LEFT") ? 1 : 0);
		
		if (forward!=0 && strafe!=0) {
			forward = forward / SQRT_TWO;
			strafe= strafe / SQRT_TWO;
		}
		
		return new PlayerInput(forward, strafe, turn);
	}
	
	public void reset() {
		keys.clear();
	}
}
