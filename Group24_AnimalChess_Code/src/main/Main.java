package main;

import games.GameEngine;
import gui.UserInterface;
import interfaces.Startable;

/**
 * Main Class for the Animal Chess game.
 */
public class Main {

    public static void main(String[] args) {

        // Initialize game engine
        GameEngine gameEngine = new GameEngine();
        Startable ui;

        // Create interface
        ui = new UserInterface(gameEngine);

        // Start the game
        ui.start();

        // Print successful start message
        System.out.println("Animal Chess Game has Started Successfully.");

    }
}
