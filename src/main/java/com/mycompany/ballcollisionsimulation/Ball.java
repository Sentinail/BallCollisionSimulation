/*
 * Ball class representing individual balls in the simulation
 * Handles ball physics, rendering, collision detection, and dragging
 */

package com.mycompany.ballcollisionsimulation;

import java.awt.*;
import java.util.Random;

/**
 * Ball class representing individual balls with physics and collision
 * @author Sentinail
 */
public class Ball {
    private double x, y;
    private double vx, vy; // velocity
    private int radius;
    private Color color;
    private double mass; // mass = area of circle
    private boolean isDragged;
    private double dragOffsetX, dragOffsetY;
    private static final Random random = new Random();
    private static final double SPRING_CONSTANT = 50000.0; // Spring constant for Hooke's Law
    
    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 10 + random.nextInt(20); // Random radius 10-30
        this.vx = (random.nextDouble() - 0.5) * 200; // Random velocity -100 to 100 pixels/second
        this.vy = (random.nextDouble() - 0.5) * 200;
        this.color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        this.mass = Math.PI * radius * radius; // mass = area of circle
        this.isDragged = false;
    }
    
    /**
     * Constructor for loading saved balls with specific properties
     */
    public Ball(double x, double y, double vx, double vy, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.color = color;
        this.mass = Math.PI * radius * radius;
        this.isDragged = false;
    }
    
    /**
     * Update ball position and apply physics
     */
    public void update(int panelWidth, int panelHeight, boolean gravityEnabled, double gravityX, double gravityY, double deltaTime) {
        if (!isDragged) {
            // Apply gravity if enabled
            if (gravityEnabled) {
                vx += gravityX * deltaTime;
                vy += gravityY * deltaTime;
            }
            
            // Update position based on velocity and delta time
            x += vx * deltaTime;
            y += vy * deltaTime;
            
            // Bounce off walls with energy conservation
            if (x - radius <= 0 || x + radius >= panelWidth) {
                vx = -vx * 0.8; // Energy loss on bounce
                x = Math.max(radius, Math.min(panelWidth - radius, x));
            }
            if (y - radius <= 0 || y + radius >= panelHeight) {
                vy = -vy * 0.8; // Energy loss on bounce
                y = Math.max(radius, Math.min(panelHeight - radius, y));
            }
        }
    }
    
    /**
     * Apply Hooke's Law for dragging effect
     */
    public void applyDragForce(double mouseX, double mouseY, double deltaTime) {
        if (isDragged) {
            // Calculate spring force using Hooke's Law: F = -k * displacement
            double targetX = mouseX - dragOffsetX;
            double targetY = mouseY - dragOffsetY;
            
            double displacementX = x - targetX;
            double displacementY = y - targetY;
            
            double forceX = -SPRING_CONSTANT * displacementX;
            double forceY = -SPRING_CONSTANT * displacementY;
            
            // Apply force to velocity: F = ma, so a = F/m, v += a * dt
            vx += (forceX / mass) * deltaTime;
            vy += (forceY / mass) * deltaTime;
            
            // Update position
            x += vx * deltaTime;
            y += vy * deltaTime;
            
            // Apply damping to prevent oscillation
            vx *= 0.9;
            vy *= 0.9;
        }
    }
    
    /**
     * Check collision with another ball and resolve it
     */
    public void handleCollision(Ball other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < (this.radius + other.radius) && distance > 0) {
            // Normalize vector
            double[] normal = normalize(dx, dy);
            double nx = normal[0];
            double ny = normal[1];
            
            // Separate overlapping balls
            double overlap = (this.radius + other.radius) - distance;
            double separationX = nx * overlap * 0.5;
            double separationY = ny * overlap * 0.5;
            
            this.x -= separationX;
            this.y -= separationY;
            other.x += separationX;
            other.y += separationY;
            
            // Calculate velocities before collision
            double vx1 = this.vx;
            double vy1 = this.vy;
            double vx2 = other.vx;
            double vy2 = other.vy;
            
            // Calculate tangent vector
            double tx = -ny;
            double ty = nx;
            
            // Calculate dot products
            double dpTan1 = (vx1 * tx) + (vy1 * ty);
            double dpTan2 = (vx2 * tx) + (vy2 * ty);
            double dpNorm1 = (vx1 * nx) + (vy1 * ny);
            double dpNorm2 = (vx2 * nx) + (vy2 * ny);
            
            // Calculate conservation of kinetic energy
            double m1 = this.mass;
            double m2 = other.mass;
            double v1 = ((dpNorm1 * (m1 - m2)) + 2 * m2 * dpNorm2) / (m1 + m2);
            double v2 = ((dpNorm2 * (m2 - m1)) + 2 * m1 * dpNorm1) / (m1 + m2);
            
            // Update velocities after collision
            this.vx = tx * dpTan1 + nx * v1;
            this.vy = ty * dpTan1 + ny * v1;
            other.vx = tx * dpTan2 + nx * v2;
            other.vy = ty * dpTan2 + ny * v2;
        }
    }
    
    /**
     * Normalize a vector
     */
    private double[] normalize(double x, double y) {
        double length = Math.sqrt(x * x + y * y);
        if (length != 0) {
            return new double[]{x / length, y / length};
        } else {
            return new double[]{0, 0};
        }
    }
    
    /**
     * Check if point is inside the ball
     */
    public boolean contains(double pointX, double pointY) {
        double dx = pointX - x;
        double dy = pointY - y;
        return (dx * dx + dy * dy) <= (radius * radius);
    }
    
    /**
     * Start dragging the ball
     */
    public void startDrag(double mouseX, double mouseY) {
        isDragged = true;
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }
    
    /**
     * Stop dragging the ball
     */
    public void stopDrag() {
        isDragged = false;
    }
    
    /**
     * Render the ball
     */
    public void paint(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
        
        // Draw border
        g2d.setColor(isDragged ? Color.RED : Color.BLACK);
        g2d.setStroke(new BasicStroke(isDragged ? 3 : 1));
        g2d.drawOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
        
        // Reset stroke
        g2d.setStroke(new BasicStroke(1));
    }
    
    // Getters
    public double getX() { 
        return x; 
    }
    
    public double getY() { 
        return y; 
    }
    
    public int getRadius() { 
        return radius; 
    }
    
    public double getMass() {
        return mass;
    }
    
    public boolean isDragged() {
        return isDragged;
    }
    
    public double getVelocityX() {
        return vx;
    }
    
    public double getVelocityY() {
        return vy;
    }
    
    public Color getColor() {
        return color;
    }
}