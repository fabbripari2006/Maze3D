package it.unibs.pajc.maze.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import it.unibs.pajc.maze.model.Game;
import it.unibs.pajc.maze.model.Maze;
import it.unibs.pajc.maze.model.MazeGenerator;
import it.unibs.pajc.maze.model.Player;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.awt.event.ActionEvent;

public class ClientApp {

	private static final int MAX_COLUMN = 50;
	private static final int MIN_COLUMN = 2;
	private static final int MAX_ROW = 50;
	private static final int MIN_ROW = 2;
	private JFrame frmMazed;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApp window = new ClientApp();
					window.frmMazed.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMazed = new JFrame();
		frmMazed.setResizable(false);
		frmMazed.setTitle("Maze3D");
		frmMazed.setBounds(350, 200, 400, 300);
		frmMazed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMazed.setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		frmMazed.getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{100, 200, 100};
		gbl_panel.rowHeights = new int[]{87, 50, 20, 25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblTitle = new JLabel("MAZE3D");
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblTitle.gridx = 1;
		gbc_lblTitle.gridy = 0;
		panel.add(lblTitle, gbc_lblTitle);
		lblTitle.setFont(new Font("Papyrus", Font.PLAIN, 36));
		
		JPanel pnlSpinner = new JPanel();
		GridBagConstraints gbc_pnlSpinner = new GridBagConstraints();
		gbc_pnlSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_pnlSpinner.fill = GridBagConstraints.BOTH;
		gbc_pnlSpinner.gridx = 1;
		gbc_pnlSpinner.gridy = 1;
		panel.add(pnlSpinner, gbc_pnlSpinner);
		GridBagLayout gbl_pnlSpinner = new GridBagLayout();
		gbl_pnlSpinner.columnWidths = new int[]{100, 100};
		gbl_pnlSpinner.rowHeights = new int[]{25, 20};
		gbl_pnlSpinner.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_pnlSpinner.rowWeights = new double[]{Double.MIN_VALUE};
		pnlSpinner.setLayout(gbl_pnlSpinner);
		
		JLabel lblRow = new JLabel("Row number");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		pnlSpinner.add(lblRow, gbc_lblNewLabel);
		
		JSpinner rowSpinner = new JSpinner();
		rowSpinner.setModel(new SpinnerNumberModel(15, MIN_ROW, MAX_ROW, 1));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 0;
		pnlSpinner.add(rowSpinner, gbc_spinner);
		
		JLabel lblColumn = new JLabel("Column number");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 1;
		pnlSpinner.add(lblColumn, gbc_lblNewLabel_1);
		
		JSpinner columnSpinner = new JSpinner();
		columnSpinner.setModel(new SpinnerNumberModel(15, MIN_COLUMN, MAX_COLUMN, 1));
		GridBagConstraints gbc_spinner_1 = new GridBagConstraints();
		gbc_spinner_1.gridx = 1;
		gbc_spinner_1.gridy = 1;
		pnlSpinner.add(columnSpinner, gbc_spinner_1);
		
		JButton playButton = new JButton("Play (Single player)");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startSinglePlayer(rowSpinner, columnSpinner);
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 3;
		panel.add(playButton, gbc_btnNewButton);
	}

	private void startSinglePlayer(JSpinner rSpinner, JSpinner cSpinner) {
		int row = (int) rSpinner.getValue();
		int column = (int) cSpinner.getValue();
		
		Maze maze = MazeGenerator.generateMaze(row, column, new Random());
		Game game = new Game(maze, new Random());
		Player player = game.addPlayer("local");
		GameWindow window = new GameWindow(game, player);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				frmMazed.setVisible(true);
			}
		});
		window.setVisible(true);
	}
}
