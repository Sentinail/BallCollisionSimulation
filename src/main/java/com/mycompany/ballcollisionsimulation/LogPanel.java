/*
 * Log Panel - Observer implementing BallEventListener
 * Displays event messages for collision simulation
 */

package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Log Panel - Observer implementing BallEventListener for collision simulation
 * @author Sentinail
 */
public class LogPanel extends JPanel implements BallEventListener {
    private JTextArea logArea;
    private JScrollPane scrollPane;
    private boolean logsEnabled = true;
    private int collisionCount = 0;
    
    public LogPanel() {
        setupLayout();
        setupComponents();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Event Log"));
        setPreferredSize(new Dimension(0, 150));
    }
    
    private void setupComponents() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        
        scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Log control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JToggleButton logToggle = new JToggleButton("Logs ON");
        logToggle.setSelected(true);
        logToggle.addItemListener(e -> {
            logsEnabled = e.getStateChange() == ItemEvent.SELECTED;
            logToggle.setText("Logs " + (logsEnabled ? "ON" : "OFF"));
        });
        
        JButton clearLogBtn = new JButton("Clear Log");
        clearLogBtn.addActionListener(actionEvent -> {
            logArea.setText("");
            collisionCount = 0;
            addLogMessage("Log cleared - Ball Collision Simulation ready");
        });
        
        JLabel collisionLabel = new JLabel("Collisions: 0");
        
        controlPanel.add(logToggle);
        controlPanel.add(clearLogBtn);
        controlPanel.add(collisionLabel);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Initial log message
        addLogMessage("Ball Collision Simulation initialized - Physics with elastic collisions and dragging enabled");
    }
    
    @Override
    public void ballEventOccurred(BallEvent event) {
        if (logsEnabled) {
            String message = event.getMessage();
            
            // Count collisions
            if (event.getEventType() == BallEvent.Type.BALL_COLLISION) {
                collisionCount++;
                message += " [Collision #" + collisionCount + "]";
                
                // Update collision counter in UI
                Component[] components = ((JPanel)getComponent(1)).getComponents();
                for (Component comp : components) {
                    if (comp instanceof JLabel && ((JLabel)comp).getText().startsWith("Collisions:")) {
                        ((JLabel)comp).setText("Collisions: " + collisionCount);
                        break;
                    }
                }
            }
            
            // Add special formatting for physics parameter changes
            if (event.getEventType() == BallEvent.Type.GRAVITY_DIRECTION_CHANGED ||
                event.getEventType() == BallEvent.Type.SPRING_CONSTANT_CHANGED ||
                event.getEventType() == BallEvent.Type.GRAVITY_TOGGLED) {
                message = "⚙️ " + message;
            }
            
            addLogMessage(message);
        }
    }
    
    private void addLogMessage(String message) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
        logArea.append(String.format("[%s] %s%n", timestamp, message));
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}