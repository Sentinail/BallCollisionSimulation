/*
 * MenuBar Manager - Modular menu bar component
 * Manages application menu bar creation and organization
 */

package com.mycompany.ballcollisionsimulation;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * MenuBar Manager - Creates and manages the application menu bar
 * Separates menu concerns from the main application class
 * @author Sentinail
 */
public class MenuBarManager {
    private final BallCollisionSimulation mainApp;
    private final GamePanel gamePanel;
    private final LogPanel logPanel;
    private final ControlPanel controlPanel;
    
    public MenuBarManager(BallCollisionSimulation mainApp, GamePanel gamePanel, 
                         LogPanel logPanel, ControlPanel controlPanel) {
        this.mainApp = mainApp;
        this.gamePanel = gamePanel;
        this.logPanel = logPanel;
        this.controlPanel = controlPanel;
    }
    
    /**
     * Create and return the complete menu bar
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());
        menuBar.add(createAboutMenu());
        
        return menuBar;
    }
    
    /**
     * Create File menu with save/load functionality
     */
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem saveItem = new JMenuItem("Save Simulation...");
        saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        saveItem.addActionListener(e -> mainApp.saveSimulation());
        
        JMenuItem loadItem = new JMenuItem("Load Simulation...");
        loadItem.setMnemonic(KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        loadItem.addActionListener(e -> mainApp.loadSimulation());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic(KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        return fileMenu;
    }
    
    /**
     * Create View menu with UI toggles
     */
    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JCheckBoxMenuItem toggleLogItem = new JCheckBoxMenuItem("Show Log Panel", true);
        JCheckBoxMenuItem toggleControlItem = new JCheckBoxMenuItem("Show Control Panel", true);
        JCheckBoxMenuItem toggleGridItem = new JCheckBoxMenuItem("Show Grid", true);
        JCheckBoxMenuItem toggleInstructionsItem = new JCheckBoxMenuItem("Show Instructions", true);
        
        toggleLogItem.addActionListener(e -> {
            logPanel.setVisible(toggleLogItem.isSelected());
            mainApp.revalidate();
        });
        
        toggleControlItem.addActionListener(e -> {
            controlPanel.setVisible(toggleControlItem.isSelected());
            mainApp.revalidate();
        });
        
        toggleGridItem.addActionListener(e -> gamePanel.setGridVisible(toggleGridItem.isSelected()));
        
        toggleInstructionsItem.addActionListener(e -> gamePanel.setInstructionsVisible(toggleInstructionsItem.isSelected()));
        
        viewMenu.add(toggleLogItem);
        viewMenu.add(toggleControlItem);
        viewMenu.addSeparator();
        viewMenu.add(toggleGridItem);
        viewMenu.add(toggleInstructionsItem);
        
        return viewMenu;
    }
    
    /**
     * Create Help menu with documentation
     */
    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem controlsItem = new JMenuItem("Keyboard & Mouse Controls");
        controlsItem.addActionListener(e -> mainApp.showHelpDialog());
        helpMenu.add(controlsItem);
        
        JMenuItem physicsItem = new JMenuItem("About Physics Simulation");
        physicsItem.addActionListener(e -> mainApp.showPhysicsInfoDialog());
        helpMenu.add(physicsItem);
        
        return helpMenu;
    }
    
    /**
     * Create About menu with team information
     */
    private JMenu createAboutMenu() {
        JMenu aboutMenu = new JMenu("About Us");
        aboutMenu.setMnemonic(KeyEvent.VK_A);
        
        JMenuItem aboutItem = new JMenuItem("Development Team");
        aboutItem.addActionListener(e -> mainApp.showAboutDialog());
        
        aboutMenu.add(aboutItem);
        
        return aboutMenu;
    }
}