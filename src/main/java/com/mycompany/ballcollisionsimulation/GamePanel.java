/*
 * Game Panel - View component for collision simulation
 * Handles rendering of balls and mouse interactions
 */

package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Game Panel - View component for rendering and interaction in collision simulation
 * @author Sentinail
 */
public class GamePanel extends JPanel {
    private GameState gameState;
    
    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        setupMouseListeners();
    }
    
    private void setupMouseListeners() {
        // Mouse press and release for dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                gameState.handleMousePressed(e.getX(), e.getY());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                gameState.handleMouseReleased();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Add ball on double click if not clicking on existing ball
                if (e.getClickCount() == 2) {
                    boolean hitBall = false;
                    for (Ball ball : gameState.getBalls()) {
                        if (ball.contains(e.getX(), e.getY())) {
                            hitBall = true;
                            break;
                        }
                    }
                    if (!hitBall) {
                        Ball newBall = new Ball(e.getX(), e.getY());
                        gameState.getBalls().add(newBall);
                    }
                }
            }
        });
        
        // Mouse motion for dragging
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                gameState.handleMouseDragged(e.getX(), e.getY());
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Paint all balls
        for (Ball ball : gameState.getBalls()) {
            ball.paint(g2d);
        }
        
        // Paint instructions
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Controls: A=Add Ball | C=Clear All | G=Toggle Gravity | 1-5=Add Multiple Balls", 10, 20);
        g2d.drawString("Mouse: Click & Drag to move balls | Double-click empty space to add ball", 10, 35);
        g2d.drawString(String.format("Balls: %d | Gravity: %s (%.1f, %.1f)", 
            gameState.getBalls().size(), 
            gameState.isGravityEnabled() ? "ON" : "OFF",
            gameState.getGravityX(),
            gameState.getGravityY()), 10, 50);
        
        // Draw dragged ball connection line
        if (gameState.getDraggedBall() != null) {
            Ball draggedBall = gameState.getDraggedBall();
            Point mousePos = getMousePosition();
            if (mousePos != null) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
                g2d.drawLine((int)draggedBall.getX(), (int)draggedBall.getY(), mousePos.x, mousePos.y);
                g2d.setStroke(new BasicStroke(1));
            }
        }
    }
}