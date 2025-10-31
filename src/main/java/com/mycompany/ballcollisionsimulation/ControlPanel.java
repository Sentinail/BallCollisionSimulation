/*
 * Control Panel - UI component for simulation controls
 * Provides gravity control and ball size control
 */
package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    
    public ControlPanel() {
        this.ballResizer = new BallResizer();
        setupLayout();
        setupComponents();
    }
    
    /**
     * Set the game state (called after construction)
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        setupGravityListener();
    }
    
    /**
     * Set up the layout and appearance of the control panel
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Control Panel"));
        setPreferredSize(new Dimension(200, 0));
        setMinimumSize(new Dimension(200, 400));
        setBackground(Color.LIGHT_GRAY);
    }
    
    /**
     * Set up components with both gravity and ball size controls
     */
    private void setupComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.LIGHT_GRAY);
        
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
        gravitySelector.setMaximumSize(new Dimension(180, 30));
        
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
        separator.setMaximumSize(new Dimension(180, 2));
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
        sizeSlider = new JSlider(JSlider.VERTICAL, 
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
        
        // Add info label at bottom
        mainPanel.add(Box.createVerticalGlue());
        
        JLabel infoLabel = new JLabel("<html><center><i>Use controls to adjust<br>simulation parameters</i></center></html>");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(infoLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        add(mainPanel, BorderLayout.CENTER);
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
                    gravitySelector.setSelectedIndex(0);  // Default to "Default" when enabled
                }
            }
        });
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
        }
    }
    
    /**
     * Update gravity value
     */
    private void updateGravity(double value) {
        if (gameState != null) {
            gameState.setGravity(value);
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
}