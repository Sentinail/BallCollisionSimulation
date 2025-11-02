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
    private boolean gridVisible = true; // Grid visibility toggle
    private boolean instructionsVisible = true; // Instructions visibility toggle
    
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
                if (gameState.isObstacleEditMode()) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        gameState.removeObstacleAt(e.getX(), e.getY());
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        boolean grabbed = gameState.handleObstacleMousePressed(e.getX(), e.getY());
                        if (!grabbed) {
                            gameState.addObstacleAt(e.getX(), e.getY());
                            gameState.handleObstacleMousePressed(e.getX(), e.getY());
                        }
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    gameState.handleMousePressed(e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (gameState.isObstacleEditMode()) {
                    gameState.handleObstacleMouseReleased();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    gameState.handleMouseReleased();
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameState.isObstacleEditMode()) {
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    boolean hitBall = false;
                    for (Ball ball : gameState.getBalls()) {
                        if (ball.contains(e.getX(), e.getY())) {
                            hitBall = true;
                            break;
                        }
                    }
                    if (!hitBall) {
                        gameState.addBallAt(e.getX(), e.getY());
                    }
                }
            }
        });
        
        // Mouse motion for dragging
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (gameState.isObstacleEditMode()) {
                    gameState.handleObstacleMouseDragged(e.getX(), e.getY());
                } else {
                    gameState.handleMouseDragged(e.getX(), e.getY());
                }
            }
        });
    }
    
    /**
     * Set grid visibility
     */
    public void setGridVisible(boolean visible) {
        this.gridVisible = visible;
        repaint();
    }
    
    /**
     * Get grid visibility
     */
    public boolean isGridVisible() {
        return gridVisible;
    }
    
    /**
     * Set instructions visibility
     */
    public void setInstructionsVisible(boolean visible) {
        this.instructionsVisible = visible;
        repaint();
    }
    
    /**
     * Get instructions visibility
     */
    public boolean isInstructionsVisible() {
        return instructionsVisible;
    }
    
    /**
     * Draw a grid background for better visual reference
     */
    private void drawGrid(Graphics2D g2d) {
        int gridSize = 25; // Grid cell size in pixels
        int width = getWidth();
        int height = getHeight();
        
        // Set grid color (light gray)
        g2d.setColor(new Color(230, 230, 230));
        g2d.setStroke(new BasicStroke(1));
        
        // Draw vertical lines
        for (int x = gridSize; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }
        
        // Draw horizontal lines
        for (int y = gridSize; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
        
        // Reset stroke for other drawing operations
        g2d.setStroke(new BasicStroke(1));
    }
    
    /**
     * Draw instruction text and game information
     */
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Draw instruction lines
        g2d.drawString("Controls: A=Add Ball | C=Clear All | G=Toggle Gravity | 1-5=Add Multiple Balls", 10, 20);
        g2d.drawString("Mouse: Click & Drag to move balls | Double-click empty space to add ball", 10, 35);
        g2d.drawString(String.format("Balls: %d | Gravity: %s (%.1f, %.1f)", 
            gameState.getBalls().size(), 
            gameState.isGravityEnabled() ? "ON" : "OFF",
            gameState.getGravityX(),
            gameState.getGravityY()), 10, 50);
        g2d.drawString(String.format("Obstacles: %d | Mode: %s (left-click add/move, right-click remove)",
            gameState.getObstacles().size(),
            gameState.isObstacleEditMode() ? "EDIT" : "VIEW"), 10, 65);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid background only if visible
        if (gridVisible) {
            drawGrid(g2d);
        }

        for (Obstacle obstacle : gameState.getObstacles()) {
            obstacle.paint(g2d);
        }
        
        // Paint all balls
        for (Ball ball : gameState.getBalls()) {
            ball.paint(g2d);
        }
        
        // Paint instructions only if visible
        if (instructionsVisible) {
            drawInstructions(g2d);
        }
        
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