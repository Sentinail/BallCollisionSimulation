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
    private double springConstant;
    private ControlPanel controlPanel; // NEW: Reference for dynamic radius
    private List<Obstacle> obstacles;
    private Obstacle draggedObstacle;
    private double obstacleOffsetX;
    private double obstacleOffsetY;
    private boolean obstacleEditMode;
    
    public GameState() {
        balls = new ArrayList<>();
        listeners = new ArrayList<>();
        gravityEnabled = true;
        gravityX = 0;
        gravityY = 300; // Default downward gravity (pixels/second^2)
        draggedBall = null;
        springConstant = 50000.0; // Default spring constant for Hooke's Law
        controlPanel = null; // NEW: Initially null
        obstacles = new ArrayList<>();
        draggedObstacle = null;
        obstacleOffsetX = 0;
        obstacleOffsetY = 0;
        obstacleEditMode = false;
    }
    
    // NEW: Setter for control panel
    public void setControlPanel(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
    }

    public boolean isObstacleEditMode() {
        return obstacleEditMode;
    }

    public void setObstacleEditMode(boolean enabled) {
        obstacleEditMode = enabled;
        if (!enabled && draggedObstacle != null) {
            draggedObstacle.setSelected(false);
            draggedObstacle = null;
        }
        if (enabled) {
            draggedBall = null;
        }
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
    
    // MODIFIED: Ball management - uses radius from control panel
    public void addBall() {
        // NEW: Get radius from control panel if available, otherwise use default
        int radius = controlPanel != null ? 
            controlPanel.getCurrentRadius() : 
            BallResizer.DEFAULT_RADIUS;
        
        Ball ball = new Ball(200 + Math.random() * 300, 100 + Math.random() * 200, radius);
        balls.add(ball);
        fireBallEvent(BallEvent.Type.BALL_CREATED, 
            String.format("Ball created at (%.0f, %.0f) with radius %d. Total balls: %d. Mass: %.1f", 
                ball.getX(), ball.getY(), radius, balls.size(), ball.getMass()));
    }
    
    /**
     * NEW: Add a ball at a specific position (for mouse double-click)
     */
    public void addBallAt(double x, double y) {
        int radius = controlPanel != null ? 
            controlPanel.getCurrentRadius() : 
            BallResizer.DEFAULT_RADIUS;

        Ball ball = new Ball(x, y, radius);
        balls.add(ball);
        fireBallEvent(BallEvent.Type.BALL_CREATED, 
            String.format("Ball created at (%.0f, %.0f) with radius %d. Total balls: %d. Mass: %.1f", 
                ball.getX(), ball.getY(), radius, balls.size(), ball.getMass()));
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void addObstacleAt(double centerX, double centerY) {
        double width = controlPanel != null ? controlPanel.getObstacleWidth() : Obstacle.DEFAULT_WIDTH;
        double height = controlPanel != null ? controlPanel.getObstacleHeight() : Obstacle.DEFAULT_HEIGHT;
        double topLeftX = centerX - width / 2.0;
        double topLeftY = centerY - height / 2.0;
        Obstacle obstacle = new Obstacle(topLeftX, topLeftY, width, height);
        obstacles.add(obstacle);
        fireBallEvent(BallEvent.Type.OBSTACLE_CREATED,
            String.format("Obstacle created at (%.0f, %.0f) size %.0fx%.0f. Total obstacles: %d",
                topLeftX, topLeftY, width, height, obstacles.size()));
    }

    public void addObstacleRaw(double x, double y, double width, double height) {
        Obstacle obstacle = new Obstacle(x, y, width, height);
        obstacles.add(obstacle);
        fireBallEvent(BallEvent.Type.OBSTACLE_CREATED,
            String.format("Obstacle loaded at (%.0f, %.0f) size %.0fx%.0f. Total obstacles: %d",
                obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight(), obstacles.size()));
    }

    public void removeObstacleAt(double x, double y) {
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Obstacle obstacle = obstacles.get(i);
            if (obstacle.contains(x, y)) {
                obstacles.remove(i);
                if (obstacle == draggedObstacle) {
                    draggedObstacle = null;
                }
                fireBallEvent(BallEvent.Type.OBSTACLE_REMOVED,
                    String.format("Obstacle removed from (%.0f, %.0f). Remaining obstacles: %d",
                        obstacle.getX(), obstacle.getY(), obstacles.size()));
                break;
            }
        }
    }

    public void clearObstacles() {
        int count = obstacles.size();
        obstacles.clear();
        draggedObstacle = null;
        if (count > 0) {
            fireBallEvent(BallEvent.Type.OBSTACLES_CLEARED,
                String.format("All %d obstacles cleared", count));
        }
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
        fireBallEvent(BallEvent.Type.GRAVITY_DIRECTION_CHANGED, 
            String.format("Gravity direction changed to (%.1f, %.1f)", gx, gy));
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

    public boolean handleObstacleMousePressed(double x, double y) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.contains(x, y)) {
                draggedObstacle = obstacle;
                obstacleOffsetX = x - obstacle.getX();
                obstacleOffsetY = y - obstacle.getY();
                obstacle.setSelected(true);
                return true;
            }
        }
        draggedObstacle = null;
        return false;
    }

    public void handleObstacleMouseDragged(double x, double y) {
        if (draggedObstacle != null) {
            double newX = x - obstacleOffsetX;
            double newY = y - obstacleOffsetY;
            draggedObstacle.setPosition(newX, newY);
        }
    }

    public void handleObstacleMouseReleased() {
        if (draggedObstacle != null) {
            draggedObstacle.setSelected(false);
            fireBallEvent(BallEvent.Type.OBSTACLE_MOVED,
                String.format("Obstacle moved to (%.0f, %.0f)",
                    draggedObstacle.getX(), draggedObstacle.getY()));
            draggedObstacle = null;
        }
    }
    
    // Physics update
    public void updateBalls(int panelWidth, int panelHeight, double deltaTime) {
        // Update ball physics
        for (Ball ball : balls) {
            if (ball == draggedBall) {
                ball.applyDragForce(mouseX, mouseY, springConstant, deltaTime, panelWidth, panelHeight);
            } else {
                ball.update(panelWidth, panelHeight, gravityEnabled, gravityX, gravityY, deltaTime);
            }

            for (Obstacle obstacle : obstacles) {
                if (resolveBallObstacleCollision(ball, obstacle)) {
                    fireBallEvent(BallEvent.Type.BALL_OBSTACLE_COLLISION,
                        String.format("Ball at (%.0f, %.0f) collided with obstacle at (%.0f, %.0f)",
                            ball.getX(), ball.getY(), obstacle.getX(), obstacle.getY()));
                }
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
    
    public double getSpringConstant() {
        return springConstant;
    }
    
    public void setSpringConstant(double springConstant) {
        double oldConstant = this.springConstant;
        this.springConstant = springConstant;
        fireBallEvent(BallEvent.Type.SPRING_CONSTANT_CHANGED, 
            String.format("Spring constant changed from %.1f to %.1f", oldConstant, springConstant));
    }

    private boolean resolveBallObstacleCollision(Ball ball, Obstacle obstacle) {
        double closestX = clamp(ball.getX(), obstacle.getX(), obstacle.getX() + obstacle.getWidth());
        double closestY = clamp(ball.getY(), obstacle.getY(), obstacle.getY() + obstacle.getHeight());
        double dx = ball.getX() - closestX;
        double dy = ball.getY() - closestY;
        double distanceSquared = dx * dx + dy * dy;
        double radius = ball.getRadius();

        if (distanceSquared > radius * radius) {
            return false;
        }

        double distance = Math.sqrt(distanceSquared);
        double nx;
        double ny;

        if (distance == 0) {
            double centerX = obstacle.getX() + obstacle.getWidth() / 2.0;
            double centerY = obstacle.getY() + obstacle.getHeight() / 2.0;
            double diffX = ball.getX() - centerX;
            double diffY = ball.getY() - centerY;
            if (Math.abs(diffX) < Math.abs(diffY)) {
                nx = diffX >= 0 ? 1 : -1;
                ny = 0;
            } else {
                nx = 0;
                ny = diffY >= 0 ? 1 : -1;
            }
            distance = 0.0001;
        } else {
            nx = dx / distance;
            ny = dy / distance;
        }

        double penetration = radius - distance;
        ball.moveBy(nx * penetration, ny * penetration);

        double dot = ball.getVelocityX() * nx + ball.getVelocityY() * ny;
        if (dot < 0) {
            double restitution = 0.9;
            double newVx = ball.getVelocityX() - (1 + restitution) * dot * nx;
            double newVy = ball.getVelocityY() - (1 + restitution) * dot * ny;
            ball.setVelocity(newVx, newVy);
            return true;
        }

        return false;
    }

    private double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}