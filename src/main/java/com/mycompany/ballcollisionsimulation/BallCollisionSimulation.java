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
import java.io.*;

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
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        
        // Initialize game state (Model)
        gameState = new GameState();
        
        // Initialize panels (View)
        gamePanel = new GamePanel(gameState);
        logPanel = new LogPanel();
        controlPanel = new ControlPanel(gameState);
        
        // Connect control panel to game state
        gameState.setControlPanel(controlPanel);
        
        // Set up observer pattern - LogPanel observes GameState
        gameState.addBallEventListener(logPanel);
    }
    
    /**
     * Set up the menu bar with File, View, Help, and About Us menus
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem saveItem = new JMenuItem("Save Simulation...");
        saveItem.setMnemonic('S');
        saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveItem.addActionListener(e -> saveSimulation());
        
        JMenuItem loadItem = new JMenuItem("Load Simulation...");
        loadItem.setMnemonic('L');
        loadItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        loadItem.addActionListener(e -> loadSimulation());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JCheckBoxMenuItem toggleLogItem = new JCheckBoxMenuItem("Show Log Panel", true);
        JCheckBoxMenuItem toggleControlItem = new JCheckBoxMenuItem("Show Control Panel", true);
        
        toggleLogItem.addActionListener(e -> {
            logPanel.setVisible(toggleLogItem.isSelected());
            revalidate();
        });
        
        toggleControlItem.addActionListener(e -> {
            controlPanel.setVisible(toggleControlItem.isSelected());
            revalidate();
        });
        
        viewMenu.add(toggleLogItem);
        viewMenu.add(toggleControlItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem controlsItem = new JMenuItem("Keyboard & Mouse Controls");
        controlsItem.addActionListener(e -> showHelpDialog());
        helpMenu.add(controlsItem);
        
        JMenuItem physicsItem = new JMenuItem("About Physics Simulation");
        physicsItem.addActionListener(e -> showPhysicsInfoDialog());
        helpMenu.add(physicsItem);
        
        // About Us Menu
        JMenu aboutMenu = new JMenu("About Us");
        aboutMenu.setMnemonic('A');
        
        JMenuItem aboutItem = new JMenuItem("Development Team");
        aboutItem.addActionListener(e -> showAboutDialog());
        
        aboutMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);
        
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
    
    /**
     * Get the default saves directory, creating it if it doesn't exist
     */
    private File getSavesDirectory() {
        File savesDir = new File("src/main/assets/saves");
        if (!savesDir.exists()) {
            savesDir.mkdirs();
        }
        return savesDir;
    }
    
    /**
     * Save the current simulation state to a file
     */
    private void saveSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Simulation");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Simulation Files (*.sim)", "sim"));
        
        // Set default directory to src/main/assets/saves
        fileChooser.setCurrentDirectory(getSavesDirectory());
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Ensure .sim extension
            if (!file.getName().toLowerCase().endsWith(".sim")) {
                file = new File(file.getAbsolutePath() + ".sim");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Save gravity settings
                writer.println("GRAVITY_ENABLED:" + gameState.isGravityEnabled());
                writer.println("GRAVITY_X:" + gameState.getGravityX());
                writer.println("GRAVITY_Y:" + gameState.getGravityY());
                writer.println("SPRING_CONSTANT: " + gameState.getSpringConstant());
                writer.println("NEW_BALL_RADIUS: " + controlPanel.getCurrentRadius());
                writer.println("BALL_COUNT:" + gameState.getBalls().size());
                
                // Save each ball's state
                for (Ball ball : gameState.getBalls()) {
                    writer.println(String.format("BALL:%.2f,%.2f,%.2f,%.2f,%d,%d,%d,%d",
                        ball.getX(), ball.getY(),
                        ball.getVelocityX(), ball.getVelocityY(),
                        ball.getRadius(),
                        ball.getColor().getRed(),
                        ball.getColor().getGreen(),
                        ball.getColor().getBlue()
                    ));
                }

                writer.println("OBSTACLE_COUNT:" + gameState.getObstacles().size());
                for (Obstacle obstacle : gameState.getObstacles()) {
                    writer.println(String.format("OBSTACLE:RECT,%.2f,%.2f,%.2f,%.2f",
                        obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight()));
                }
                
                JOptionPane.showMessageDialog(this,
                    String.format("Simulation saved successfully!\nBalls saved: %d\nObstacles saved: %d",
                        gameState.getBalls().size(), gameState.getObstacles().size()),
                    "Save Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving simulation: " + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Load a simulation state from a file
     */
    private void loadSimulation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Simulation");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Simulation Files (*.sim)", "sim"));
        
        // Set default directory to src/main/assets/saves
        fileChooser.setCurrentDirectory(getSavesDirectory());
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Clear current simulation
                gameState.clearAllBalls();
                gameState.clearObstacles();
                
                String line;
                int ballsLoaded = 0;
                int obstaclesLoaded = 0;
                
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("GRAVITY_ENABLED:")) {
                        boolean enabled = Boolean.parseBoolean(line.substring(16));
                        if (enabled != gameState.isGravityEnabled()) {
                            gameState.toggleGravity();
                        }
                    } else if (line.startsWith("GRAVITY_X:")) {
                        double gx = Double.parseDouble(line.substring(10));
                        double gy = gameState.getGravityY();
                        gameState.setGravityDirection(gx, gy);
                    } else if (line.startsWith("GRAVITY_Y:")) {
                        double gy = Double.parseDouble(line.substring(10));
                        double gx = gameState.getGravityX();
                        gameState.setGravityDirection(gx, gy);
                    } else if (line.startsWith("SPRING_CONSTANT:")) {
                        String value = line.substring("SPRING_CONSTANT:".length()).trim();
                        try {
                            double springConstant = Double.parseDouble(value);
                            if (springConstant != gameState.getSpringConstant()) {
                                gameState.setSpringConstant(springConstant);
                            }
                        } catch (NumberFormatException ignored) {
                            // Ignore malformed value to maintain backward compatibility
                        }
                    } else if (line.startsWith("NEW_BALL_RADIUS:")) {
                        String value = line.substring("NEW_BALL_RADIUS:".length()).trim();
                        try {
                            int radius = Integer.parseInt(value);
                            if (controlPanel != null) {
                                controlPanel.setCurrentRadius(radius);
                            }
                        } catch (NumberFormatException ignored) {
                            // Ignore malformed value for backward compatibility
                        }
                    } else if (line.startsWith("OBSTACLE_COUNT:")) {
                        // No action needed; obstacles are read individually
                    } else if (line.startsWith("BALL:")) {
                        String[] parts = line.substring(5).split(",");
                        if (parts.length == 8) {
                            double x = Double.parseDouble(parts[0]);
                            double y = Double.parseDouble(parts[1]);
                            double vx = Double.parseDouble(parts[2]);
                            double vy = Double.parseDouble(parts[3]);
                            int radius = Integer.parseInt(parts[4]);
                            int r = Integer.parseInt(parts[5]);
                            int g = Integer.parseInt(parts[6]);
                            int b = Integer.parseInt(parts[7]);
                            
                            Ball ball = new Ball(x, y, vx, vy, radius, new Color(r, g, b));
                            gameState.getBalls().add(ball);
                            ballsLoaded++;
                        }
                    } else if (line.startsWith("OBSTACLE:")) {
                        String[] parts = line.substring("OBSTACLE:".length()).split(",");
                        if (parts.length >= 5) {
                            String shape = parts[0].trim();
                            if ("RECT".equalsIgnoreCase(shape)) {
                                double x = Double.parseDouble(parts[1]);
                                double y = Double.parseDouble(parts[2]);
                                double width = Double.parseDouble(parts[3]);
                                double height = Double.parseDouble(parts[4]);
                                gameState.addObstacleRaw(x, y, width, height);
                                obstaclesLoaded++;
                            }
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this,
                    String.format("Simulation loaded successfully!\nBalls loaded: %d\nObstacles loaded: %d",
                        ballsLoaded, obstaclesLoaded),
                    "Load Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading simulation: " + e.getMessage(),
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Show the help dialog with keyboard and mouse controls
     */
    private void showHelpDialog() {
        String helpText =
                "Keyboard Controls:\n" +
                "  A - Add a ball\n" +
                "  C - Clear all balls\n" +
                "  G - Toggle gravity on/off\n" +
                "  1–5 - Add multiple balls\n\n" +
                "Mouse Controls:\n" +
                "  Click & drag - Move balls with spring force\n" +
                "  Double-click - Add ball at mouse position\n\n" +
                "Obstacle Editing:\n" +
                "  Toggle 'Edit Obstacles' in Control Panel\n" +
                "  Left-click canvas to add or drag obstacles\n" +
                "  Right-click obstacle to remove it\n\n" +
                "File Operations:\n" +
                "  Ctrl+S - Save simulation\n" +
                "  Ctrl+O - Load simulation\n" +
                "  Save files are stored in: src/main/assets/saves/\n\n" +
                "Tips:\n" +
                "  • Balls collide elastically with each other and walls\n" +
                "  • Gravity affects all balls when enabled\n" +
                "  • You can save/load the simulation anytime!";
        JOptionPane.showMessageDialog(this, helpText, "Help - Controls", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show the physics information dialog
     */
    private void showPhysicsInfoDialog() {
        String physicsText =
                "Physics Simulation Details:\n\n" +
                "• Elastic collisions conserve energy and momentum\n" +
                "• Dragging uses Hooke's Law (F = -k * x)\n" +
                "• Gravity vector: (0, 300) when enabled\n" +
                "• Rectangular obstacles collide elastically with balls\n" +
                "• Wall collisions reduce velocity slightly\n" +
                "• Frame update rate: ~60 FPS\n\n" +
                "This simulation uses MVC and the Observer design pattern.";
        JOptionPane.showMessageDialog(this, physicsText, "Help - Physics Simulation", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show the About Us dialog with developer information
     */
    private void showAboutDialog() {
        String aboutMessage = "Developers:\n\n" +
                "Wilson G. Ponseca\n" +
                "Bianca Mackenzie C. Liong\n" +
                "Pio Daniel E. Cordoves\n" +
                "Alejandro D. Alvarez\n" +
                "Louis Jefferson V. Samosino";
        
        JOptionPane.showMessageDialog(
                this,
                aboutMessage,
                "About Us",
                JOptionPane.INFORMATION_MESSAGE
        );
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
