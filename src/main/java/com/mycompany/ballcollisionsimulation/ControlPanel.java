/*
 * Control Panel - UI component for simulation controls
 * Provides empty bordered panel on the right side
 */

package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import java.awt.*;

/**
 * Control Panel - Empty UI component for future controls
 * @author Sentinail
 */
public class ControlPanel extends JPanel {
    GameState gameState;
    private int previousIndex = 0;
    
    public ControlPanel(GameState gameState) {
        this.gameState = gameState;
        setupLayout();
        setupComponents();
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
     * Set up components (currently empty with placeholder)
     */
    private void setupComponents() {
        // Add a placeholder label for now
        JLabel placeholderLabel = new JLabel("<html><center>Control Panel</center></html>");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setVerticalAlignment(SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        placeholderLabel.setForeground(Color.DARK_GRAY);

        // Gravity Label
        JLabel gravityLabel = new JLabel("Select Gravity: ");
        
        // Gravity Selector (JComboBox)
        String[] gravityOptions = {"Default","Earth Gravity", "Moon Gravity", "Mars Gravity", "Jupiter Gravity", "Custom Gravity"};
        JComboBox<String> gravitySelector = new JComboBox<>(gravityOptions);
        gravitySelector.setFocusable(false);

        gravitySelector.addActionListener(e -> {
            if(!gravitySelector.isPopupVisible()){
                return;
            }
            String selected = (String) gravitySelector.getSelectedItem();
                switch(selected) {
                case "Default":
                    updateGravity(300.0, gravitySelector);
                    break;
                case "Earth Gravity":
                    updateGravity(980.0, gravitySelector);
                    break;
                case "Moon Gravity":
                    updateGravity(165.0, gravitySelector);
                    break;
                case "Mars Gravity":
                    updateGravity(380.0, gravitySelector);
                    break;
                case "Jupiter Gravity":
                    updateGravity(2430.0, gravitySelector);
                    break;
                case "Custom Gravity":
                    String input = JOptionPane.showInputDialog(null,
                                    "Enter custom gravity (pixels/s²):",
                                    "Custom Gravity",
                                    JOptionPane.PLAIN_MESSAGE);
                     try{
                        if (input != null) {
                            double customValue = Double.parseDouble(input);
                            updateGravity(customValue, gravitySelector);
                        } else{
                            gravitySelector.setSelectedIndex(previousIndex);
                        }
                    } catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid number entered", "Error", JOptionPane.ERROR_MESSAGE);
                        // Restore Previous Selection
                        gravitySelector.setSelectedIndex(previousIndex);
                    }   
                    break;

                    
                
            }
        });

        // Disable gravity selector if gravity is off
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

        // Panel for Gravity Selector
        JPanel gravityPanel = new JPanel();
        gravityPanel.setLayout(new GridLayout(2,1,5,1));
        gravityPanel.setBackground(Color.lightGray);
        
        gravityPanel.add(gravityLabel);
        gravityPanel.add(gravitySelector);

        add(gravityPanel, BorderLayout.NORTH);
        add(placeholderLabel, BorderLayout.CENTER);
        
        // Add some empty space at the bottom
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 50));
        spacerPanel.setBackground(Color.LIGHT_GRAY);
        add(spacerPanel, BorderLayout.SOUTH);
    }

    private void updateGravity(double value, JComboBox<String> comboBox) {
        gameState.setGravity(value);
        previousIndex = comboBox.getSelectedIndex();
    }

}