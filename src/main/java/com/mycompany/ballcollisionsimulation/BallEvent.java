/*
 * Custom Event System - BallEvent
 * Represents events that occur in the ball collision simulation
 */

package com.mycompany.ballcollisionsimulation;

import java.util.EventObject;

/**
 * Custom event class for ball-related events
 * @author Sentinail
 */
public class BallEvent extends EventObject {
    public enum Type {
        BALL_CREATED, BALL_COLLISION, BALL_DRAGGED, BALL_RELEASED, GRAVITY_TOGGLED, BALLS_CLEARED
    }
    
    private Type eventType;
    private String message;
    
    public BallEvent(Object source, Type eventType, String message) {
        super(source);
        this.eventType = eventType;
        this.message = message;
    }
    
    public Type getEventType() { 
        return eventType; 
    }
    
    public String getMessage() { 
        return message; 
    }
}