/*
 * Game State - Model class implementing Observer Pattern
 * Manages the collision simulation data and notifies observers of changes
 */

package com.mycompany.ballcollisionsimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Game State - Model class implementing Observer Pattern for collision simulation
 * @author Sentinail
 */
public class GameState {
    private List<Ball> balls;
    private boolean gravityEnabled;
    private double gravityX;
    private double gravityY;
    private List<BallEventListener> listeners;
    private Ball draggedBall;
    private double mouseX;
    private double mouseY;
    
    public GameState() {
        balls = new ArrayList<>();
        listeners = new ArrayList<>();
        gravityEnabled = true;
        gravityX = 0;
        gravityY = 300; // Default downward gravity (pixels/second^2)
        draggedBall = null;
    }
    
    // Observer pattern methods
    public void addBallEventListener(BallEventListener listener) {
        listeners.add(listener);
    }
    
    private void fireBallEvent(BallEvent.Type type, String message) {
        BallEvent event = new BallEvent(this, type, message);
        for (BallEventListener listener : listeners) {
            listener.ballEventOccurred(event);
        }
    }
    
    // Ball management
    public void addBall() {
        Ball ball = new Ball(200 + Math.random() * 300, 100 + Math.random() * 200);
        balls.add(ball);
        fireBallEvent(BallEvent.Type.BALL_CREATED, 
            String.format("Ball created at (%.0f, %.0f). Total balls: %d. Mass: %.1f", 
                ball.getX(), ball.getY(), balls.size(), ball.getMass()));
    }
    
    public void clearAllBalls() {
        int count = balls.size();
        balls.clear();
        draggedBall = null;
        fireBallEvent(BallEvent.Type.BALLS_CLEARED, 
            String.format("All %d balls cleared", count));
    }
    
    public void toggleGravity() {
        gravityEnabled = !gravityEnabled;
        fireBallEvent(BallEvent.Type.GRAVITY_TOGGLED, 
            "Gravity " + (gravityEnabled ? "enabled" : "disabled"));
    }
    
    public void setGravityDirection(double gx, double gy) {
        this.gravityX = gx;
        this.gravityY = gy;
        if (gravityEnabled) {
            fireBallEvent(BallEvent.Type.GRAVITY_TOGGLED, 
                String.format("Gravity direction changed to (%.1f, %.1f)", gx, gy));
        }
    }
    
    // Mouse interaction methods
    public void handleMousePressed(double x, double y) {
        mouseX = x;
        mouseY = y;
        
        // Find ball under mouse cursor
        for (Ball ball : balls) {
            if (ball.contains(x, y)) {
                draggedBall = ball;
                ball.startDrag(x, y);
                fireBallEvent(BallEvent.Type.BALL_DRAGGED, 
                    String.format("Ball at (%.0f, %.0f) started dragging", ball.getX(), ball.getY()));
                break;
            }
        }
    }
    
    public void handleMouseDragged(double x, double y) {
        mouseX = x;
        mouseY = y;
    }
    
    public void handleMouseReleased() {
        if (draggedBall != null) {
            draggedBall.stopDrag();
            fireBallEvent(BallEvent.Type.BALL_RELEASED, 
                String.format("Ball at (%.0f, %.0f) released from dragging", 
                    draggedBall.getX(), draggedBall.getY()));
            draggedBall = null;
        }
    }
    
    // Physics update
    public void updateBalls(int panelWidth, int panelHeight, double deltaTime) {
        // Update ball physics
        for (Ball ball : balls) {
            if (ball == draggedBall) {
                ball.applyDragForce(mouseX, mouseY, deltaTime);
            } else {
                ball.update(panelWidth, panelHeight, gravityEnabled, gravityX, gravityY, deltaTime);
            }
        }
        
        // Check collisions between all pairs of balls
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball1 = balls.get(i);
                Ball ball2 = balls.get(j);
                
                // Check if collision occurred
                double dx = ball2.getX() - ball1.getX();
                double dy = ball2.getY() - ball1.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance < (ball1.getRadius() + ball2.getRadius())) {
                    ball1.handleCollision(ball2);
                    fireBallEvent(BallEvent.Type.BALL_COLLISION, 
                        String.format("Collision between balls at (%.0f, %.0f) and (%.0f, %.0f)", 
                            ball1.getX(), ball1.getY(), ball2.getX(), ball2.getY()));
                }
            }
        }
    }
    
    // Getters
    public List<Ball> getBalls() { 
        return balls; 
    }
    
    public boolean isGravityEnabled() { 
        return gravityEnabled; 
    }
    
    public double getGravityX() {
        return gravityX;
    }
    
    public double getGravityY() {
        return gravityY;
    }
    
    public Ball getDraggedBall() {
        return draggedBall;
    }
}