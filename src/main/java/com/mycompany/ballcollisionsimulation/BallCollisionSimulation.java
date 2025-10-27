/*
 * Ball Collision Simulation - Computer Programming 3
 * Demonstrates 2D physics simulation with collision detection and ball dragging
 * 
 * Main Application Class - Entry point and coordinator
 */

package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import javax.swing.WindowConstants;
import java.awt.*;

/**
 * Main application class for ball collision simulation
 * @author Sentinail
 */
public class BallCollisionSimulation extends JFrame {
    private GameState gameState;
    private GamePanel gamePanel;
    private LogPanel logPanel;
    private ControlPanel controlPanel;
    
    public BallCollisionSimulation() {
        initializeApplication();
        setupMenuBar();
        setupLayout();
        setupKeyBindings();
        startGameLoop();
    }
    
    /**
     * Initialize the application components and set up MVC architecture
     */
    private void initializeApplication() {
        setTitle("Ball Collision Simulation - CP3 Final Project");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Initialize game state (Model)
        gameState = new GameState();
        
        // Initialize panels (View)
        gamePanel = new GamePanel(gameState);
        logPanel = new LogPanel();
        controlPanel = new ControlPanel();
        
        // Set up observer pattern - LogPanel observes GameState
        gameState.addBallEventListener(logPanel);
    }
    
    /**
     * Set up the menu bar with empty menus
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        // Simulation Menu
        JMenu simulationMenu = new JMenu("Simulation");
        simulationMenu.setMnemonic('S');
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(simulationMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        // Set the menu bar
        setJMenuBar(menuBar);
    }
    
    /**
     * Set up the main layout using BorderLayout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        add(gamePanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.EAST);
    }
    
    /**
     * Set up key bindings for keyboard interactions
     */
    private void setupKeyBindings() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        // 'A' key to add ball
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0), "addBall");
        actionMap.put("addBall", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                gameState.addBall();
            }
        });
        
        // 'C' key to clear all balls
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0), "clearBalls");
        actionMap.put("clearBalls", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                gameState.clearAllBalls();
            }
        });
        
        // 'G' key to toggle gravity
        inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, 0), "toggleGravity");
        actionMap.put("toggleGravity", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                gameState.toggleGravity();
            }
        });
        
        // Number keys for quick ball addition (1-5 balls)
        for (int i = 1; i <= 5; i++) {
            final int ballCount = i;
            inputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0 + i, 0), "addBalls" + i);
            actionMap.put("addBalls" + i, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    for (int j = 0; j < ballCount; j++) {
                        gameState.addBall();
                    }
                }
            });
        }
    }
    
    /**
     * Start the game loop using Swing Timer
     */
    private void startGameLoop() {
        Timer gameTimer = new Timer(16, e -> { // ~60 FPS
            gameState.updateBalls(gamePanel.getWidth(), gamePanel.getHeight(), 16.0 / 1000.0); // Delta time in seconds
            gamePanel.repaint();
        });
        gameTimer.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BallCollisionSimulation().setVisible(true);
        });
    }
}
