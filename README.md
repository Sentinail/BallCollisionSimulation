# Ball Collision Simulation - Project Documentation

## Project Overview

A comprehensive Java Swing-based 2D physics simulation featuring elastic ball collisions, realistic physics with customizable gravity, interactive ball dragging using Hooke's Law, obstacle collision detection, and complete simulation persistence. The project implements the Model-View-Controller (MVC) pattern with Observer design pattern for event handling, fulfilling the Computer Programming 3 requirement to develop an application using Mouse, Menu, and Timer Events.

## Project Requirements Fulfillment

This project successfully implements all three required event types for Computer Programming 3:

### ✅ Timer Events
- **60fps Game Loop**: Uses Swing Timer (16ms intervals) for smooth physics simulation
- **Real-time Physics**: Continuous collision detection, gravity application, and position updates
- **Performance Optimized**: Efficient delta time calculations for consistent physics across different frame rates

### ✅ Mouse Events  
- **Interactive Ball Dragging**: Click and drag balls using realistic spring physics
- **Ball Creation**: Double-click to create new balls at cursor location
- **Obstacle Manipulation**: Move and resize obstacles in edit mode
- **Context-Sensitive Interactions**: Different mouse behaviors based on current mode

### ✅ Menu Events
- **File Operations**: Save/load complete simulation states with physics parameters
- **View Controls**: Toggle grid, instructions, and UI panel visibility
- **Help System**: Comprehensive controls reference and physics documentation
- **About Dialog**: Team information and project details

## Project Structure

```
BallCollisionSimulation/
├── src/main/java/com/mycompany/ballcollisionsimulation/
│   ├── BallCollisionSimulation.java    # Main application class (Controller)
│   ├── GameState.java                  # Model - Game logic and physics
│   ├── GamePanel.java                  # View - Game rendering & mouse interaction
│   ├── LogPanel.java                   # View - Event logging (Observer)
│   ├── ControlPanel.java               # View - Comprehensive physics controls
│   ├── MenuBarManager.java             # View - Modular menu system
│   ├── Ball.java                       # Entity - Individual ball physics
│   ├── Obstacle.java                   # Entity - Rectangular obstacles
│   ├── BallResizer.java                # Utility - Ball size management
│   ├── BallEvent.java                  # Event - Custom event system
│   └── BallEventListener.java          # Interface - Event listener
├── saves/                              # Auto-created directory for .sim files
└── README.md                           # This documentation

```

## Key Features

### 🎯 Advanced Physics System
- **Elastic Collision Physics**: Conservation of momentum and energy with mass-based calculations
- **Hooke's Law Dragging**: Realistic spring physics for interactive ball manipulation
- **Customizable Gravity**: Earth, Moon, Mars, Jupiter presets plus custom values
- **Variable Spring Constants**: Adjustable from 10k to 100k N/m for different drag feels
- **Obstacle Collision**: Axis-aligned rectangular obstacles with realistic ball bouncing

### 🎮 Interactive Controls
- **Real-time Parameter Adjustment**: Modify physics while simulation is running
- **Scrollable Control Panel**: Comprehensive UI with abbreviated labels (10k, 50k, etc.)
- **Ball Size Control**: Visual slider for new ball radius (10-50 pixels)
- **Obstacle Editor**: Create, move, and resize obstacles with visual feedback
- **Visual Toggles**: Grid overlay, instruction display, drag force visualization

### 💾 Persistence System  
- **Complete State Saving**: Preserves ball positions, velocities, colors, and all physics parameters
- **Detailed Load Feedback**: Shows loaded physics summary with spring constant, gravity, ball radius
- **Auto-organized Storage**: Saves directory automatically created and managed
- **Backward Compatibility**: Graceful handling of older save file formats

### 📊 Monitoring & Feedback
- **Real-time Event Logging**: Categorized events with timestamps ([Physics], [Action], [System])
- **Collision Statistics**: Track total collisions during simulation
- **Visual State Indicators**: Red borders for dragged balls, selection highlighting for obstacles
- **Performance Display**: Ball count and physics parameter readouts

## Class Responsibilities

### 1. BallCollisionSimulation (Main Controller)

- **Role**: Entry point and application coordinator implementing MVC pattern
- **Key Responsibilities**:
    - Initialize all MVC components and establish relationships
    - Set up comprehensive UI layout using BorderLayout with scrollable panels
    - Configure keyboard shortcuts (A, C, G, 1-5, Escape)
    - Manage 60fps game loop with delta time calculations
    - Coordinate save/load operations with detailed user feedback
    - Integrate modular MenuBarManager for clean architecture

**Timer Events Implementation**: 
```java
// 60fps game loop for smooth physics simulation
gameTimer = new Timer(16, e -> {
    double deltaTime = calculateDeltaTime();
    gameState.updateBalls(gamePanel.getWidth(), gamePanel.getHeight(), deltaTime);
    gamePanel.repaint();
});
```

### 2. GameState (Model)

- **Role**: Core physics engine and simulation state manager
- **Enhanced Responsibilities**:
    - Manage ball and obstacle collections with lifecycle management
    - Implement Observer pattern with comprehensive event types
    - Handle context-sensitive mouse interactions (ball dragging vs obstacle editing)
    - Control gravity with customizable direction and magnitude
    - Coordinate multi-phase collision detection (ball-ball, ball-obstacle, ball-boundary)
    - Maintain spring constant and other physics parameters
    - Fire events for all significant state changes

**Advanced Event Types**:
```java
public enum Type {
    BALL_CREATED, BALL_COLLISION, BALL_DRAGGED, BALL_RELEASED,
    GRAVITY_TOGGLED, GRAVITY_DIRECTION_CHANGED,
    SPRING_CONSTANT_CHANGED, BALLS_CLEARED,
    OBSTACLE_CREATED, OBSTACLE_REMOVED, OBSTACLE_MOVED,
    OBSTACLES_CLEARED, BALL_OBSTACLE_COLLISION
}
```

### Physics Implementation Details:

**Enhanced Dragging System (Hooke's Law)**:

```java
// Spring force calculation: F = -k * displacement
double targetX = mouseX - dragOffsetX;
double targetY = mouseY - dragOffsetY;
double displacementX = x - targetX;
double displacementY = y - targetY;

double forceX = -springConstant * displacementX;  // Customizable spring constant (10k-100k)
double forceY = -springConstant * displacementY;

// Apply force to velocity: F = ma, so a = F/m, v += a * dt
vx += (forceX / mass) * deltaTime;
vy += (forceY / mass) * deltaTime;

// Apply damping and boundary constraints
vx *= 0.9;  // Prevent oscillation
vy *= 0.9;
```

- **User-Configurable Spring Constant**: Range from 10,000 to 100,000 N/m via control panel slider
- **Boundary Yo-yo Effect**: Balls bounce off edges when dragged to boundaries
- **Proper Offset Handling**: Maintains exact grab point for natural dragging feel
- **Real-time Parameter Updates**: Spring constant changes affect ongoing drag operations immediately

**Enhanced Collision Detection & Resolution**:

```java
// 1. Multi-phase Collision Detection
// Ball-Ball collisions with proper overlap resolution
double distance = Math.sqrt(dx * dx + dy * dy);
if (distance < (radius1 + radius2) && distance > 0) {
    
    // 2. Prevent sticking with proper separation
    double overlap = (radius1 + radius2) - distance;
    double separateX = (dx / distance) * (overlap / 2);
    double separateY = (dy / distance) * (overlap / 2);
    
    // 3. Calculate collision vectors
    double[] normal = normalize(dx, dy);
    double nx = normal[0], ny = normal[1];
    
    // 4. Apply realistic collision physics
    double v1 = ((dpNorm1 * (m1 - m2)) + 2 * m2 * dpNorm2) / (m1 + m2);
    double v2 = ((dpNorm2 * (m2 - m1)) + 2 * m1 * dpNorm1) / (m1 + m2);
    
    // 5. Fire collision event with position data
    fireBallEvent(BallEvent.Type.BALL_COLLISION, 
        String.format("Collision at (%.1f, %.1f)", x, y));
}

// Ball-Obstacle collision detection
for (Obstacle obstacle : obstacles) {
    if (ball.intersects(obstacle)) {
        ball.handleObstacleCollision(obstacle);
        fireBallEvent(BallEvent.Type.BALL_OBSTACLE_COLLISION,
            String.format("Ball hit obstacle at (%.1f, %.1f)", 
                obstacle.getX(), obstacle.getY()));
    }
}
```

- **Comprehensive Collision Types**: Ball-ball, ball-obstacle, and ball-boundary
- **Advanced Separation Logic**: Prevents balls from sticking together during collisions
- **Mass-Realistic Physics**: Heavier balls influence lighter ones more significantly  
- **Event-Driven Feedback**: All collisions generate events for logging and statistics

### 3. Ball (Enhanced Entity)

- **Role**: Individual physics entity with complete behavior and rendering
- **Enhanced Responsibilities**:
    - **Advanced Physics**: Position, velocity, mass management with configurable parameters
    - **Multi-type Collision Handling**: Resolve collisions with balls, obstacles, and boundaries
    - **State-Aware Rendering**: Visual feedback for dragging, selection, and collision states
    - **Boundary Intelligence**: Proper edge collision with energy conservation
    - **Drag Offset Management**: Maintains exact mouse grab point for natural interaction

**Enhanced Physics Features**:
- **Configurable Mass**: `mass = π × radius²` with user-adjustable radius
- **Realistic Energy Loss**: Wall bounce with `velocity *= 0.8`
- **Visual State Indicators**: Red border during dragging, collision highlighting
- **Smooth Physics Integration**: Delta time-based updates for consistent behavior

### 4. ControlPanel (Comprehensive Physics Interface)

- **Role**: Scrollable user interface for real-time physics parameter control
- **Complete Implementation Features**:
    - **Gravity Control**: Preset buttons (Earth: 9.81, Moon: 1.62, Mars: 3.71, Jupiter: 24.79) plus custom input
    - **Ball Size Control**: Visual slider with real-time preview (10-50 pixels)
    - **Spring Constant Adjustment**: Slider with abbreviated labels (10k, 30k, 50k, 70k, 100k N/m)
    - **Obstacle Controls**: Width/height spinners and edit mode toggle
    - **Scrollable Design**: Proper layout to prevent scrollbar from covering content
    - **Real-time Synchronization**: All parameters update simulation immediately

**User Experience Enhancements**:
```java
// Abbreviated label formatting for readability
private String formatSpringConstant(double value) {
    return value >= 1000 ? 
        String.format("%.0fk N/m", value / 1000) : 
        String.format("%.0f N/m", value);
}

// Real-time physics parameter updates
springConstantSlider.addChangeListener(e -> {
    double value = springConstantSlider.getValue();
    gameState.setSpringConstant(value);  // Immediate effect on dragging
    updateSpringConstantLabel(value);
});
```

### 5. GamePanel (Enhanced Rendering & Interaction)

- **Role**: Primary view component with comprehensive visual features and mouse handling
- **Enhanced Display Features**:
    - **Anti-aliased Rendering**: Professional-quality graphics with smooth edges
    - **Optional Visual Aids**: Toggle-able grid, instruction overlay, drag force lines
    - **Context-Sensitive Display**: Different information based on current mode
    - **Performance Indicators**: Real-time ball count, physics parameters, collision count
    - **Visual Feedback**: Drag connections, selection highlighting, collision effects

**Mouse Events Implementation**:
```java
// Context-sensitive mouse handling
public void mousePressed(MouseEvent e) {
    if (gameState.isObstacleEditMode()) {
        // Obstacle manipulation mode
        gameState.handleObstacleMousePressed(e.getX(), e.getY());
    } else {
        // Ball interaction mode  
        gameState.handleMousePressed(e.getX(), e.getY());
    }
}

// Intelligent double-click detection
public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && !gameState.isObstacleEditMode()) {
        gameState.addBall(e.getX(), e.getY(), 
            controlPanel.getCurrentRadius());
    }
}
```

### 6. MenuBarManager (Modular Menu System)

- **Role**: Comprehensive menu functionality separated from main application
- **Complete Menu Implementation**:
    - **File Menu**: Save/load simulations with detailed physics parameter preservation
    - **View Menu**: Toggle grid, instructions, panels, and visual aids
    - **Help Menu**: Controls reference, physics documentation, keyboard shortcuts  
    - **About Menu**: Team information and project details

**Menu Events Implementation**:
```java
// File operations with detailed feedback
saveItem.addActionListener(e -> app.saveSimulation());
loadItem.addActionListener(e -> app.loadSimulation());

// View toggles with immediate visual response
toggleGridItem.addActionListener(e -> 
    app.getGamePanel().setGridVisible(toggleGridItem.isSelected()));

// Help system with comprehensive documentation
controlsItem.addActionListener(e -> app.showHelpDialog());
```

### 7. Enhanced Event System & Logging

- **LogPanel**: Real-time event monitoring with categorization and statistics
- **BallEvent System**: Comprehensive event types covering all simulation activities
- **Observer Pattern**: Clean separation between event producers and consumers

**Advanced Logging Features**:
```java
// Categorized event logging
private String getEventPrefix(BallEvent.Type eventType) {
    switch (eventType) {
        case SPRING_CONSTANT_CHANGED:
        case GRAVITY_TOGGLED:
        case GRAVITY_DIRECTION_CHANGED:
            return "[Physics] ";
        case BALL_CREATED:
        case BALL_DRAGGED:
        case BALLS_CLEARED:
            return "[Action] ";
        default:
            return "[System] ";
    }
}
```

### 8. Obstacle System

- **Role**: Environmental objects that create complex physics scenarios
- **Features**:
    - **Axis-aligned Rectangles**: Efficient collision detection and rendering
    - **Interactive Editing**: Move and resize obstacles with visual feedback
    - **Physics Integration**: Realistic collision response with proper bouncing
    - **Visual States**: Different rendering for normal and selected states

### 9. Enhanced Save/Load System

**Complete Simulation Persistence**:
```java
// Comprehensive state saving
writer.println("GRAVITY_ENABLED:" + gameState.isGravityEnabled());
writer.println("GRAVITY_X:" + gameState.getGravityX());
writer.println("GRAVITY_Y:" + gameState.getGravityY());
writer.println("SPRING_CONSTANT:" + gameState.getSpringConstant());
writer.println("NEW_BALL_RADIUS:" + controlPanel.getCurrentRadius());

// Individual ball state preservation
for (Ball ball : gameState.getBalls()) {
    writer.println(String.format("BALL:%.2f,%.2f,%.2f,%.2f,%d,%d,%d,%d",
        ball.getX(), ball.getY(), ball.getVelocityX(), ball.getVelocityY(),
        ball.getRadius(), ball.getColor().getRed(), 
        ball.getColor().getGreen(), ball.getColor().getBlue()));
}
```

**Enhanced Load Feedback**:
```java
// Detailed physics summary on load
physicsInfo.append("Physics Parameters:\n");
physicsInfo.append("• Gravity: ").append(gravityEnabled ? "Enabled" : "Disabled");
physicsInfo.append("• Spring Constant: ").append(formatSpringConstant(loadedSpringConstant));
physicsInfo.append("• New Ball Radius: ").append(loadedBallRadius).append(" px");
```

## Controls and Interactions

### Keyboard Shortcuts

- **A**: Add single ball at random position with current radius setting
- **C**: Clear all balls from simulation (with confirmation)  
- **G**: Toggle gravity on/off (affects all balls immediately)
- **1-5**: Add multiple balls at once (1-5 respectively)
- **Escape**: Exit obstacle edit mode and return to normal interaction

### Mouse Controls

**Normal Mode (Ball Interaction):**
- **Click & Drag**: Move balls using physics-based spring dragging
- **Double-click**: Create new ball at mouse position with current radius
- **Release**: Stop dragging and apply momentum to the ball

**Obstacle Edit Mode:**
- **Click & Drag**: Move existing obstacles around the simulation area
- **Click on Empty Space**: Create new obstacle at that location
- **Drag Handles**: Resize obstacles by dragging their edges
- **Right-click** (planned): Context menu for obstacle operations

### Menu System

**File Menu:**
- **Save Simulation**: Persist complete state including physics parameters to .sim file
- **Load Simulation**: Restore saved state with detailed physics summary dialog

**View Menu:**
- **Toggle Grid**: Show/hide background grid for visual reference
- **Toggle Instructions**: Show/hide control instructions overlay
- **Toggle Control Panel**: Show/hide the physics control interface
- **Toggle Log Panel**: Show/hide the event logging panel

**Help Menu:**  
- **Controls**: Comprehensive keyboard and mouse controls reference
- **Physics Info**: Detailed explanation of physics formulas and concepts

**About Menu:**
- **About Us**: Team information and project development details

### Control Panel Features

**Gravity Controls:**
- **Preset Buttons**: Earth (9.81), Moon (1.62), Mars (3.71), Jupiter (24.79) m/s²
- **Custom Input**: Set precise gravity values with X and Y components
- **Toggle Switch**: Enable/disable gravity with visual feedback

**Ball Configuration:**
- **Size Slider**: Adjust radius for new balls (10-50 pixels) with real-time preview
- **Visual Feedback**: Current radius displayed with abbreviated format

**Physics Parameters:**
- **Spring Constant**: Drag stiffness control (10k-100k N/m) with abbreviated labels
- **Real-time Updates**: Changes affect ongoing ball dragging immediately

**Obstacle Controls:**
- **Size Spinners**: Set width and height for new obstacles
- **Edit Mode Toggle**: Switch between ball interaction and obstacle editing
- **Visual Indicators**: Clear feedback for current mode

## Technical Architecture

### Design Patterns Used

**Model-View-Controller (MVC):**
- **Model**: GameState manages all simulation data and physics
- **View**: GamePanel, LogPanel, ControlPanel handle display and user interface  
- **Controller**: BallCollisionSimulation coordinates between model and views

**Observer Pattern:**
- **Subject**: GameState fires events for all state changes
- **Observers**: LogPanel listens for events and updates display accordingly
- **Benefits**: Loose coupling, easy to add new observers, reactive UI updates

**Modular Architecture:**
- **MenuBarManager**: Separated menu concerns from main application
- **BallResizer**: Centralized ball size management and constraints
- **Event System**: Custom events for comprehensive communication

### Performance Optimizations

**Efficient Collision Detection:**
- **Distance Pre-check**: Quick distance calculation before expensive collision resolution
- **Boundary Optimization**: Early exit for balls far from boundaries
- **Delta Time Physics**: Consistent behavior regardless of frame rate

**Memory Management:**
- **Object Reuse**: Minimize object creation in game loop
- **Efficient Data Structures**: ArrayList for dynamic collections, appropriate data types
- **Resource Cleanup**: Proper cleanup of graphics resources

**UI Responsiveness:**
- **Event-Driven Updates**: UI only updates when necessary via Observer pattern
- **Scrollable Controls**: Prevents UI overflow while maintaining functionality
- **Visual State Caching**: Efficient rendering with state-based optimizations

## File Format Specification

### .sim File Structure
```
GRAVITY_ENABLED:true
GRAVITY_X:0.0
GRAVITY_Y:9.81
SPRING_CONSTANT:50000.0
NEW_BALL_RADIUS:20
BALL_COUNT:3
BALL:100.50,200.75,50.25,-30.10,15,255,128,64
BALL:300.25,150.80,-25.40,45.20,20,64,255,128
OBSTACLE_COUNT:2
OBSTACLE:50.0,100.0,120.0,40.0
OBSTACLE:400.0,300.0,80.0,60.0
```

**Format Details:**
- **Header**: Physics parameters and configuration
- **Ball Data**: Position (x,y), velocity (vx,vy), radius, color RGB
- **Obstacle Data**: Position (x,y), dimensions (width,height)
- **Backward Compatibility**: Graceful handling of missing parameters

## Development Team

**Project Contributors:**
- **Sentinail** - Lead Developer, Physics Engine, Architecture Design
- **Team Members** - UI Development, Testing, Documentation

**Development Timeline:**
- **Phase 1**: Core MVC architecture and basic ball physics
- **Phase 2**: Enhanced controls, spring dragging, collision system
- **Phase 3**: Obstacle system, comprehensive UI, event logging
- **Phase 4**: Menu system, save/load functionality, visual enhancements
- **Phase 5**: Performance optimization, documentation, testing

## Future Enhancement Opportunities