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
    
    public ControlPanel() {
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
        
        add(placeholderLabel, BorderLayout.CENTER);
        
        // Add some empty space at the bottom
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(0, 50));
        spacerPanel.setBackground(Color.LIGHT_GRAY);
        add(spacerPanel, BorderLayout.SOUTH);
    }
}