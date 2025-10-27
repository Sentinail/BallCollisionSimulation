/*
 * Custom Event Listener Interface
 * Defines the contract for listening to ball events
 */

package com.mycompany.ballcollisionsimulation;

import java.util.EventListener;

/**
 * Custom event listener interface for ball events
 * @author Sentinail
 */
public interface BallEventListener extends EventListener {
    void ballEventOccurred(BallEvent event);
}