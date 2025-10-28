# Ball Collision Simulation - Project Documentation

## Project Overview

A Java Swing-based 2D physics simulation demonstrating elastic ball collisions, realistic physics with gravity, and interactive ball dragging using Hooke's Law. The project implements the Model-View-Controller (MVC) pattern with Observer design pattern for event handling.

## Project Structure

```
BallCollisionSimulation/
├── src/main/java/com/mycompany/ballcollisionsimulation/
│   ├── BallCollisionSimulation.java    # Main application class (Controller)
│   ├── GameState.java                  # Model - Game logic and physics
│   ├── GamePanel.java                  # View - Game rendering
│   ├── LogPanel.java                   # View - Event logging (Observer)
│   ├── ControlPanel.java               # View - Control interface (empty)
│   ├── Ball.java                       # Entity - Individual ball physics
│   ├── BallEvent.java                  # Event - Custom event system
│   └── BallEventListener.java          # Interface - Event listener

```

## Class Responsibilities

### 1. BallCollisionSimulation (Main Controller)

- **Role**: Entry point and application coordinator
- **Responsibilities**:
    - Initialize MVC components
    - Set up UI layout using BorderLayout
    - Configure keyboard shortcuts (A, C, G, 1-5)
    - Manage game loop with Swing Timer (~60 FPS)
    - Coordinate between Model and View components

### 2. GameState (Model)

- **Role**: Core game logic and physics engine
- **Responsibilities**:
    - Manage ball collection and lifecycle
    - Implement Observer pattern for event notifications
    - Handle mouse interactions (press, drag, release)
    - Control gravity settings and physics parameters
    - Coordinate collision detection between balls

### Physics Implementation Details:

**Dragging System (Hooke's Law)**:

```java
// Spring force calculation: F = -k * displacement
double forceX = -SPRING_CONSTANT * displacementX;
double forceY = -SPRING_CONSTANT * displacementY;

// Apply force to velocity: F = ma, so a = F/m, v += a * dt
vx += (forceX / mass) * deltaTime;
vy += (forceY / mass) * deltaTime;

```

- Uses spring constant (50,000) for realistic dragging feel
- Applies damping (0.9) to prevent oscillation
- Calculates displacement from mouse to ball center
- Force is proportional to displacement distance

**Collision Detection & Resolution (Elastic Collision)**:

```java
// 1. Collision Detection
double distance = Math.sqrt(dx * dx + dy * dy);
if (distance < (radius1 + radius2))

// 2. Separation of overlapping balls
double overlap = (radius1 + radius2) - distance;
// Move balls apart to prevent sticking

// 3. Calculate normal and tangent vectors
double[] normal = normalize(dx, dy);
double tx = -ny; // Tangent perpendicular to normal
double ty = nx;

// 4. Project velocities onto normal and tangent
double dpNorm1 = (vx1 * nx) + (vy1 * ny);
double dpNorm2 = (vx2 * nx) + (vy2 * ny);

// 5. Apply conservation of momentum and energy
double v1 = ((dpNorm1 * (m1 - m2)) + 2 * m2 * dpNorm2) / (m1 + m2);
double v2 = ((dpNorm2 * (m2 - m1)) + 2 * m1 * dpNorm1) / (m1 + m2);

```

- **Normal Vector**: Direction of collision force
- **Tangent Vector**: Perpendicular to normal, velocity preserved
- **Conservation Laws**: Applies both momentum and kinetic energy conservation
- **Mass-based**: Heavier balls affect lighter ones more significantly

### 3. Ball (Entity)

- **Role**: Individual ball physics and properties
- **Responsibilities**:
    - Position, velocity, and mass management
    - Individual physics updates (gravity, movement)
    - Drag force application using Hooke's Law
    - Collision resolution with other balls
    - Boundary collision (wall bouncing with energy loss)
    - Visual rendering with drag state indication

**Key Physics Features**:

- Mass calculation: `mass = π × radius²`
- Energy loss on wall bounce: `velocity *= 0.8`
- Random initial properties (size, color, velocity)
- Visual feedback for dragged state (red border)

### 4. GamePanel (View)

- **Role**: Game rendering and mouse interaction handling
- **Responsibilities**:
    - Render all balls with anti-aliasing
    - Handle mouse events (click, drag, double-click)
    - Display game statistics and controls information
    - Show drag connection line between mouse and dragged ball
    - Provide visual feedback for user interactions

**Display Features**:

```java
// Game state information
g2d.drawString(String.format("Balls: %d | Gravity: %s (%.1f, %.1f)",
    gameState.getBalls().size(),
    gameState.isGravityEnabled() ? "ON" : "OFF",
    gameState.getGravityX(),
    gameState.getGravityY()), 10, 50);

// Drag visualization
if (draggedBall != null) {
    g2d.setColor(Color.RED);
    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0));
    g2d.drawLine(ballCenter, mousePosition);
}

```

### 5. LogPanel (Observer)

- **Role**: Event logging and collision statistics
- **Responsibilities**:
    - Implement BallEventListener interface
    - Display timestamped event messages
    - Track collision count and statistics
    - Provide log controls (enable/disable, clear)
    - Auto-scroll to latest events

### 6. Event System (BallEvent & BallEventListener)

- **Role**: Decoupled communication between components
- **Event Types**:
    - `BALL_CREATED`: New ball added to simulation
    - `BALL_COLLISION`: Collision between two balls
    - `BALL_DRAGGED`: Ball started being dragged
    - `BALL_RELEASED`: Ball released from dragging
    - `GRAVITY_TOGGLED`: Gravity state changed
    - `BALLS_CLEARED`: All balls removed

## Controls and Interactions

### Keyboard Controls

- **A**: Add single ball at random position
- **C**: Clear all balls from simulation
- **G**: Toggle gravity on/off
- **1-5**: Add multiple balls (1-5 respectively)

### Mouse Controls

- **Click & Drag**: Move balls using physics-based dragging
- **Double-click**: Add ball at mouse position
- **Release**: Stop dragging and apply final velocity

## Unimplemented Features

### 1. Menu Bar System

The application includes a menu bar structure with empty menus:

- **File Menu**: Could implement save/load simulation states
- **About Us**: The list of developers in this project
- **…(You are free to add more)**

### 2. Control Panel

Currently displays placeholder content. Potential implementations:

- **Physics Controls**: Gravity strength/direction sliders
- **Ball Properties**: Size, mass, velocity adjusters
- **…(You are free to add more)**

---

*Feel free to extend this project with any features that interest you! The modular design using MVC and Observer patterns makes it easy to add new functionality without disrupting existing code.*