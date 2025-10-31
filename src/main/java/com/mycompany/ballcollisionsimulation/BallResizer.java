/*
 * Ball Resizer - Manages ball size configuration
 * Handles size constraints and provides default size for new balls
 */

package com.mycompany.ballcollisionsimulation;

/**
 * Ball Resizer - Manages ball size settings for the simulation
 * @author Bianca
 */
public class BallResizer {
    private int currentRadius;
    private int minRadius;
    private int maxRadius;
    
    // Default size parameters
    public static final int DEFAULT_MIN_RADIUS = 10;
    public static final int DEFAULT_MAX_RADIUS = 50;
    public static final int DEFAULT_RADIUS = 20;
    
    /**
     * Constructor with default values
     */
    public BallResizer() {
        this.minRadius = DEFAULT_MIN_RADIUS;
        this.maxRadius = DEFAULT_MAX_RADIUS;
        this.currentRadius = DEFAULT_RADIUS;
    }
    
    /**
     * Constructor with custom min and max radius
     */
    public BallResizer(int minRadius, int maxRadius) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.currentRadius = (minRadius + maxRadius) / 2;
    }
    
    /**
     * Set the current radius for new balls
     * @param radius The desired radius
     * @return true if radius was set. False if out of bounds
     */
    public boolean setRadius(int radius) {
        if (radius >= minRadius && radius <= maxRadius) {
            this.currentRadius = radius;
            return true;
        }
        return false;
    }
    
    /**
     * Get the current radius setting
     */
    public int getCurrentRadius() {
        return currentRadius;
    }
    
    /**
     * Get the minimum allowed radius
     */
    public int getMinRadius() {
        return minRadius;
    }
    
    /**
     * Get the maximum allowed radius
     */
    public int getMaxRadius() {
        return maxRadius;
    }
    
    /**
     * Set new bounds for radius
     */
    public void setBounds(int minRadius, int maxRadius) {
        if (minRadius > 0 && maxRadius > minRadius) {
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
            
            // Adjust current radius if it's out of new bounds
            if (currentRadius < minRadius) {
                currentRadius = minRadius;
            } else if (currentRadius > maxRadius) {
                currentRadius = maxRadius;
            }
        }
    }
    
    /**
     * Reset to default radius
     */
    public void resetToDefault() {
        this.currentRadius = DEFAULT_RADIUS;
    }
    
    /**
     * Get a descriptive string of current settings
     */
    @Override
    public String toString() {
        return String.format("BallResizer[radius=%d, bounds=%d-%d]", 
            currentRadius, minRadius, maxRadius);
    }
}