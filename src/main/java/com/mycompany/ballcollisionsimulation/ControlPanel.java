/*
 * Control Panel - UI component for simulation controls
 * Provides gravity control and ball size control
 */
package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static javax.swing.ScrollPaneConstants.*;
import java.awt.*;

/**
 * Control Panel - UI component with gravity and ball size controls
 * @author Sentinail
 */
public class ControlPanel extends JPanel {
    private GameState gameState;
    private BallResizer ballResizer;
    private JSlider sizeSlider;
    private JLabel sizeValueLabel;
    private JComboBox<String> gravitySelector;
    private int previousIndex = 0;
    private JSpinner obstacleWidthSpinner;
    private JSpinner obstacleHeightSpinner;
    private JToggleButton obstacleModeToggle;
    private JLabel obstacleCountLabel;
    private JSlider springConstantSlider;
    private JLabel springConstantValueLabel;
    
    public ControlPanel(GameState gameState) {
        this.gameState = gameState;
        this.ballResizer = new BallResizer();
        setupLayout();
        setupComponents();
        setupGravityListener();
        setupObstacleListener();
    }
    
    
    /**
     * Set up the layout and appearance of the control panel
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Control Panel"));
        setPreferredSize(new Dimension(220, 0)); // Increased width to prevent scrollbar overlap
        setMinimumSize(new Dimension(220, 400));
        setBackground(Color.LIGHT_GRAY);
    }
    
    /**
     * Set up components with both gravity and ball size controls
     */
    private void setupComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY);
        // Remove the problematic border - we'll handle spacing differently
        
        // Add spacing at top
        mainPanel.add(Box.createVerticalStrut(10));
        
        // ========== GRAVITY CONTROL SECTION ==========
        JPanel gravityPanel = new JPanel();
        gravityPanel.setLayout(new BoxLayout(gravityPanel, BoxLayout.Y_AXIS));
        gravityPanel.setBackground(Color.LIGHT_GRAY);
        gravityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Gravity Control")
        ));
        
        JLabel gravityLabel = new JLabel("Select Gravity:");
        gravityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gravityPanel.add(gravityLabel);
        
        gravityPanel.add(Box.createVerticalStrut(5));
        
        // Gravity Selector (JComboBox)
        String[] gravityOptions = {"Default", "Earth Gravity", "Moon Gravity", 
                                   "Mars Gravity", "Jupiter Gravity", "Custom Gravity"};
        gravitySelector = new JComboBox<>(gravityOptions);
        gravitySelector.setFocusable(false);
        gravitySelector.setMaximumSize(new Dimension(160, 30)); // Reduced width to leave space for scrollbar
        
        gravitySelector.addActionListener(e -> {
            if (!gravitySelector.isPopupVisible()) {
                return;
            }
            handleGravitySelection();
        });
        
        gravityPanel.add(gravitySelector);
        gravityPanel.add(Box.createVerticalStrut(10));
        
        mainPanel.add(gravityPanel);
        
        // Add separator
        mainPanel.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(160, 2)); // Reduced to match other components
        mainPanel.add(separator);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // ========== BALL SIZE CONTROL SECTION ==========
        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.Y_AXIS));
        sizePanel.setBackground(Color.LIGHT_GRAY);
        sizePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Ball Size")
        ));
        
        // Title label
        JLabel titleLabel = new JLabel("New Ball Radius");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sizePanel.add(titleLabel);
        
        sizePanel.add(Box.createVerticalStrut(10));
        
        // Size value display
        sizeValueLabel = new JLabel(String.format("%d pixels", ballResizer.getCurrentRadius()));
        sizeValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sizeValueLabel.setForeground(new Color(0, 100, 200));
        sizePanel.add(sizeValueLabel);
        
        sizePanel.add(Box.createVerticalStrut(15));
        
        // Slider
        sizeSlider = new JSlider(SwingConstants.VERTICAL, 
            ballResizer.getMinRadius(), 
            ballResizer.getMaxRadius(), 
            ballResizer.getCurrentRadius());
        
        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setMinorTickSpacing(5);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setBackground(Color.LIGHT_GRAY);
        
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = sizeSlider.getValue();
                ballResizer.setRadius(value);
                sizeValueLabel.setText(String.format("%d pixels", value));
            }
        });
        
        sizePanel.add(sizeSlider);
        
        sizePanel.add(Box.createVerticalStrut(10));
        
        // Reset button
        JButton resetButton = new JButton("Reset to Default");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> {
            ballResizer.resetToDefault();
            sizeSlider.setValue(ballResizer.getCurrentRadius());
            sizeValueLabel.setText(String.format("%d pixels", ballResizer.getCurrentRadius()));
        });
        sizePanel.add(resetButton);
        
        mainPanel.add(sizePanel);
        
        // Add separator
        mainPanel.add(Box.createVerticalStrut(10));
        JSeparator obstacleSeparator = new JSeparator();
        obstacleSeparator.setMaximumSize(new Dimension(160, 2)); // Reduced width
        mainPanel.add(obstacleSeparator);
        mainPanel.add(Box.createVerticalStrut(10));

        // ========== OBSTACLE CONTROL SECTION ==========
        JPanel obstaclePanel = new JPanel();
        obstaclePanel.setLayout(new BoxLayout(obstaclePanel, BoxLayout.Y_AXIS));
        obstaclePanel.setBackground(Color.LIGHT_GRAY);
        obstaclePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Obstacles")
        ));

        obstacleModeToggle = new JToggleButton("Edit Obstacles");
        obstacleModeToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstacleModeToggle.addActionListener(e -> {
            if (gameState != null) {
                boolean enabled = obstacleModeToggle.isSelected();
                gameState.setObstacleEditMode(enabled);
                obstacleModeToggle.setText(enabled ? "Editing Obstacles" : "Edit Obstacles");
            }
        });
        obstaclePanel.add(obstacleModeToggle);

        obstaclePanel.add(Box.createVerticalStrut(10));

        JLabel widthLabel = new JLabel("Width (px):");
        widthLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstaclePanel.add(widthLabel);

        obstacleWidthSpinner = new JSpinner(new SpinnerNumberModel((int) Obstacle.DEFAULT_WIDTH, 20, 600, 10));
        obstacleWidthSpinner.setMaximumSize(new Dimension(120, 25)); // Reduced width
        obstaclePanel.add(obstacleWidthSpinner);

        obstaclePanel.add(Box.createVerticalStrut(10));

        JLabel heightLabel = new JLabel("Height (px):");
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstaclePanel.add(heightLabel);

        obstacleHeightSpinner = new JSpinner(new SpinnerNumberModel((int) Obstacle.DEFAULT_HEIGHT, 20, 400, 10));
        obstacleHeightSpinner.setMaximumSize(new Dimension(120, 25)); // Reduced width
        obstaclePanel.add(obstacleHeightSpinner);

        obstaclePanel.add(Box.createVerticalStrut(15));

        JButton clearObstaclesButton = new JButton("Clear Obstacles");
        clearObstaclesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearObstaclesButton.addActionListener(e -> {
            if (gameState != null) {
                gameState.clearObstacles();
            }
        });
        obstaclePanel.add(clearObstaclesButton);

        obstaclePanel.add(Box.createVerticalStrut(10));

        obstacleCountLabel = new JLabel("Obstacles: 0");
        obstacleCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstaclePanel.add(obstacleCountLabel);

        JLabel obstacleTipLabel = new JLabel("<html><center>Left-click: add/move<br>Right-click: remove</center></html>");
        obstacleTipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        obstacleTipLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        obstacleTipLabel.setForeground(Color.DARK_GRAY);
        obstaclePanel.add(Box.createVerticalStrut(8));
        obstaclePanel.add(obstacleTipLabel);

        mainPanel.add(obstaclePanel);

        // Add separator
        mainPanel.add(Box.createVerticalStrut(10));
        JSeparator springSeparator = new JSeparator();
        springSeparator.setMaximumSize(new Dimension(160, 2)); // Reduced width
        mainPanel.add(springSeparator);
        mainPanel.add(Box.createVerticalStrut(10));

        // ========== SPRING CONSTANT CONTROL SECTION ==========
        JPanel springPanel = new JPanel();
        springPanel.setLayout(new BoxLayout(springPanel, BoxLayout.Y_AXIS));
        springPanel.setBackground(Color.LIGHT_GRAY);
        springPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Drag Force")
        ));

        // Title label
        JLabel springTitleLabel = new JLabel("Spring Constant");
        springTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        springTitleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        springPanel.add(springTitleLabel);

        springPanel.add(Box.createVerticalStrut(10));

        // Spring constant value display
        double currentSpringConstant = gameState.getSpringConstant();
        springConstantValueLabel = new JLabel(String.format("%.0f N/m", currentSpringConstant));
        springConstantValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        springConstantValueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        springConstantValueLabel.setForeground(new Color(0, 100, 200));
        springPanel.add(springConstantValueLabel);

        springPanel.add(Box.createVerticalStrut(15));

        // Spring constant slider (horizontal for space efficiency)
        springConstantSlider = new JSlider(SwingConstants.HORIZONTAL, 10000, 100000, (int)currentSpringConstant);
        springConstantSlider.setMajorTickSpacing(20000);
        springConstantSlider.setMinorTickSpacing(10000);
        springConstantSlider.setPaintTicks(true);
        springConstantSlider.setPaintLabels(false); // We'll set custom labels
        springConstantSlider.setBackground(Color.LIGHT_GRAY);
        springConstantSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        springConstantSlider.setMaximumSize(new Dimension(150, 60)); // Reduced width to prevent scrollbar overlap
        
        // Create custom abbreviated labels
        @SuppressWarnings("UseOfObsoleteCollectionType")
        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(10000, new JLabel("10k"));
        labelTable.put(30000, new JLabel("30k"));
        labelTable.put(50000, new JLabel("50k"));
        labelTable.put(70000, new JLabel("70k"));
        labelTable.put(90000, new JLabel("90k"));
        springConstantSlider.setLabelTable(labelTable);
        springConstantSlider.setPaintLabels(true);

        springConstantSlider.addChangeListener(e -> {
            if (springConstantSlider.getValueIsAdjusting()) {
                return;
            }
            double newValue = springConstantSlider.getValue();
            gameState.setSpringConstant(newValue);
            springConstantValueLabel.setText(String.format("%.0f N/m", newValue));
        });

        springPanel.add(springConstantSlider);

        springPanel.add(Box.createVerticalStrut(10));

        // Info text
        JLabel springInfoLabel = new JLabel("<html><center><i>Controls ball dragging<br>stiffness</i></center></html>");
        springInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        springInfoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        springInfoLabel.setForeground(Color.DARK_GRAY);
        springPanel.add(springInfoLabel);

        mainPanel.add(springPanel);

        // Add info label at bottom
        mainPanel.add(Box.createVerticalGlue());
        
        JLabel infoLabel = new JLabel("<html><center><i>Use controls to adjust<br>simulation parameters</i></center></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(infoLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Create a wrapper panel with controlled width to prevent content overlap with scrollbar
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.LIGHT_GRAY);
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.setPreferredSize(new Dimension(190, wrapperPanel.getPreferredSize().height));
        
        // Make the panel scrollable
        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Set up gravity event listener (called after gameState is set)
     */
    private void setupGravityListener() {
        if (gameState == null) return;
        
        // Disable gravity selector if gravity is off initially
        if (!gameState.isGravityEnabled()) {
            gravitySelector.setEnabled(false);
        }
        
        // Listen for gravity toggle events to enable/disable gravity selector
        gameState.addBallEventListener(e -> {
            if (e.getEventType() == BallEvent.Type.GRAVITY_TOGGLED) {
                if (!gameState.isGravityEnabled()) {
                    gravitySelector.setEnabled(false);
                } else {
                    gravitySelector.setEnabled(true);
                    gravitySelector.setSelectedIndex(previousIndex); // Previous Index or Gravity Mode selected will return rather than "Default"
                }
            }
        });
    }

    private void setupObstacleListener() {
        if (gameState == null) {
            return;
        }

        gameState.addBallEventListener(event -> {
            switch (event.getEventType()) {
                case OBSTACLE_CREATED:
                case OBSTACLE_REMOVED:
                case OBSTACLE_MOVED:
                case OBSTACLES_CLEARED:
                    updateObstacleCount();
                    break;
                default:
                    break;
            }
        });
        updateObstacleCount();
    }

    private void updateObstacleCount() {
        if (obstacleCountLabel != null && gameState != null) {
            obstacleCountLabel.setText(String.format("Obstacles: %d", gameState.getObstacles().size()));
        }
    }
    
    /**
     * Handle gravity selection from combo box
     */
    private void handleGravitySelection() {
        if (gameState == null) return;
        
        String selected = (String) gravitySelector.getSelectedItem();
        switch (selected) {
            case "Default":
                updateGravity(300.0);
                break;
            case "Earth Gravity":
                updateGravity(980.0);
                break;
            case "Moon Gravity":
                updateGravity(165.0);
                break;
            case "Mars Gravity":
                updateGravity(380.0);
                break;
            case "Jupiter Gravity":
                updateGravity(2430.0);
                break;
            case "Custom Gravity":
                String input = JOptionPane.showInputDialog(null,
                                "Enter custom gravity (pixels/s²):",
                                "Custom Gravity",
                                JOptionPane.PLAIN_MESSAGE);
                try {
                    if (input != null) {
                        double customValue = Double.parseDouble(input);
                        updateGravity(customValue);
                    } else {
                        gravitySelector.setSelectedIndex(previousIndex);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, 
                        "Invalid number entered", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    // Restore Previous Selection
                    gravitySelector.setSelectedIndex(previousIndex);
                }   
                break;
            default:
                // Handle unknown selection - restore previous
                gravitySelector.setSelectedIndex(previousIndex);
                break;
        }
    }
    
    /**
     * Update gravity value
     */
    private void updateGravity(double value) {
        if (gameState != null) {
            gameState.setGravityDirection(0, value);
            previousIndex = gravitySelector.getSelectedIndex();
        }
    }
    
    /**
     * Get the BallResizer instance
     */
    public BallResizer getBallResizer() {
        return ballResizer;
    }
    
    /**
     * Get the current radius setting
     */
    public int getCurrentRadius() {
        return ballResizer.getCurrentRadius();
    }

    /**
     * Update the current radius control to match a loaded simulation.
     */
    public void setCurrentRadius(int radius) {
        int clamped = Math.max(ballResizer.getMinRadius(), Math.min(ballResizer.getMaxRadius(), radius));
        ballResizer.setRadius(clamped);
        if (sizeSlider != null) {
            sizeSlider.setValue(clamped);
        }
        if (sizeValueLabel != null) {
            sizeValueLabel.setText(String.format("%d pixels", ballResizer.getCurrentRadius()));
        }
    }

    /**
     * Update the spring constant control to match a loaded simulation.
     */
    public void setCurrentSpringConstant(double springConstant) {
        double clamped = Math.max(10000, Math.min(100000, springConstant));
        if (springConstantSlider != null) {
            springConstantSlider.setValue((int)clamped);
        }
        if (springConstantValueLabel != null) {
            springConstantValueLabel.setText(String.format("%.0f N/m", clamped));
        }
    }

    public double getObstacleWidth() {
        if (obstacleWidthSpinner != null) {
            return ((Number) obstacleWidthSpinner.getValue()).doubleValue();
        }
        return Obstacle.DEFAULT_WIDTH;
    }

    public double getObstacleHeight() {
        if (obstacleHeightSpinner != null) {
            return ((Number) obstacleHeightSpinner.getValue()).doubleValue();
        }
        return Obstacle.DEFAULT_HEIGHT;
    }

}