package games;

import java.util.ArrayList;
import java.util.List;

import animals.Animal;
import animals.Cat;
import animals.Dog;
import animals.Elephant;
import animals.Leopard;
import animals.Lion;
import animals.Rat;
import animals.Tiger;
import animals.Wolf;
import players.Player;
import squares.DenSquare;
import squares.NormalSquare;
import squares.RiverSquare;
import squares.Square;
import squares.TrapSquare;

/**
 * Represents the game board, containing all kinds of squares and managing piece
 * placement.
 */
public class Board {

    private static final int ROWS = 9;
    private static final int COLS = 7;
    private final Square<Animal>[][] grid;
    private final Player redPlayer;
    private final Player greenPlayer;
    private final List<Animal> redCaptured;
    private final List<Animal> greenCaptured;

    @SuppressWarnings("unchecked")
    public Board(Player redPlayer, Player greenPlayer) {
        this.redPlayer = redPlayer;
        this.greenPlayer = greenPlayer;
        this.grid = new Square[ROWS][COLS];
        this.redCaptured = new ArrayList<>();
        this.greenCaptured = new ArrayList<>();
        initializeBoard();
        setupInitialPieces();
    }

    /**
     * Initializes the board with the correct terrain types.
     */
    private void initializeBoard() {
        // First fill all squares with normal squares
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new NormalSquare<Animal>(r, c);
            }
        }

        // Green player den at position (0, 3) 
        grid[0][3] = new DenSquare<Animal>(0, 3, greenPlayer);

        // Red player den at position (8, 3)
        grid[8][3] = new DenSquare<Animal>(8, 3, redPlayer);

        // Green player traps around den
        grid[0][2] = new TrapSquare<Animal>(0, 2, greenPlayer);
        grid[1][3] = new TrapSquare<Animal>(1, 3, greenPlayer);
        grid[0][4] = new TrapSquare<Animal>(0, 4, greenPlayer);

        // Red player traps around den
        grid[7][3] = new TrapSquare<Animal>(7, 3, redPlayer);
        grid[8][2] = new TrapSquare<Animal>(8, 2, redPlayer);
        grid[8][4] = new TrapSquare<Animal>(8, 4, redPlayer);

        // River squares in the central part of the board (rows 3-5, cols 1-2 and 4-5)
        for (int r = 3; r <= 5; r++) {
            for (int c = 1; c <= 5; c++) {
                // Skip the middle column (3)
                if (c != 3) {
                    grid[r][c] = new RiverSquare<Animal>(r, c);
                }
            }
        }
    }

    /**
     * Places the initial set of animal pieces on the board according to the
     * image pattern.
     */
    private void setupInitialPieces() {
        // Clear previous placements if any
        clearBoard();

        // Green (green player) pieces (top rows)
        placeAnimal(new Lion(greenPlayer), 0, 0);      // Top left
        placeAnimal(new Tiger(greenPlayer), 0, 6);     // Top right
        placeAnimal(new Dog(greenPlayer), 1, 1);       // Second row
        placeAnimal(new Cat(greenPlayer), 1, 5);       // Second row
        placeAnimal(new Rat(greenPlayer), 2, 0);       // Third row
        placeAnimal(new Leopard(greenPlayer), 2, 2);   // Third row
        placeAnimal(new Wolf(greenPlayer), 2, 4);      // Third row
        placeAnimal(new Elephant(greenPlayer), 2, 6);  // Third row

        // Red player pieces (bottom rows)
        placeAnimal(new Elephant(redPlayer), 6, 0);   // Seventh row
        placeAnimal(new Wolf(redPlayer), 6, 2);       // Seventh row
        placeAnimal(new Leopard(redPlayer), 6, 4);    // Seventh row
        placeAnimal(new Rat(redPlayer), 6, 6);        // Seventh row
        placeAnimal(new Cat(redPlayer), 7, 1);        // Eighth row
        placeAnimal(new Dog(redPlayer), 7, 5);        // Eighth row
        placeAnimal(new Tiger(redPlayer), 8, 0);      // Bottom row
        placeAnimal(new Lion(redPlayer), 8, 6);       // Bottom row
    }

    /**
     * Clears all animals from the board.
     */
    private void clearBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c] != null) {
                    grid[r][c].setAnimal(null);
                }
            }
        }
    }

    /**
     * Places an animal on a specific square.
     *
     * @param animal The animal to place.
     * @param r The row index (0-based).
     * @param c The column index (0-based).
     */
    public void placeAnimal(Animal animal, int r, int c) {
        if (isWithinBounds(r, c)) {
            Square<Animal> square = grid[r][c];
            if (square != null) {
                // Remove animal from its old square if it was on one
                if (animal.getSquare() != null) {
                    animal.getSquare().setAnimal(null);
                }
                // Clear the target square if occupied (capture)
                if (!square.isEmpty()) {
                    captureAnimal(square.getAnimal());
                }
                // Place the animal
                square.setAnimal(animal);
                animal.setSquare(square);
            }
        }
    }

    /**
     * Moves an animal from one square to another. Assumes the move is valid.
     *
     * @param fromR Start row index.
     * @param fromC Start column index.
     * @param toR Destination row index.
     * @param toC Destination column index.
     */
    public void moveAnimal(int fromR, int fromC, int toR, int toC) {
        if (!isWithinBounds(fromR, fromC) || !isWithinBounds(toR, toC)) {
            return;
        }

        Square<Animal> startSquare = getSquare(fromR, fromC);
        Square<Animal> endSquare = getSquare(toR, toC);

        if (startSquare == null || startSquare.isEmpty()) {
            return; // No animal to move
        }
        Animal movingAnimal = startSquare.getAnimal();

        // Check for capture
        if (endSquare != null && !endSquare.isEmpty()) {
            captureAnimal(endSquare.getAnimal());
        }

        // Move the animal
        if (endSquare != null) {
            endSquare.setAnimal(movingAnimal);
            movingAnimal.setSquare(endSquare);
        }
        startSquare.setAnimal(null);
    }

    /**
     * Handles capturing an animal.
     *
     * @param capturedAnimal The animal that was captured.
     */
    private void captureAnimal(Animal capturedAnimal) {
        if (capturedAnimal == null) {
            return;
        }

        capturedAnimal.setSquare(null); // Remove from board

        // Add to the appropriate captured list based on the animal's owner
        if (capturedAnimal.getOwner() == redPlayer) {
            greenCaptured.add(capturedAnimal);
        } else {
            redCaptured.add(capturedAnimal);
        }
        // The square's animal reference is cleared in moveAnimal/placeAnimal
    }

    /**
     * Gets the square at the specified coordinates.
     *
     * @param r Row index (0-based).
     * @param c Column index (0-based).
     * @return The Square object, or null if out of bounds.
     */
    public Square<Animal> getSquare(int r, int c) {
        if (isWithinBounds(r, c)) {
            return grid[r][c];
        }
        return null;
    }

    /**
     * Checks if the given coordinates are within the board boundaries.
     *
     * @param r Row index (0-based).
     * @param c Column index (0-based).
     * @return true if within bounds, false otherwise.
     */
    public boolean isWithinBounds(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }

    public int getRows() {
        return ROWS;
    }

    public int getCols() {
        return COLS;
    }

    public List<Animal> getRedCaptured() {
        return redCaptured;
    }

    public List<Animal> getGreenCaptured() {
        return greenCaptured;
    }

    // Method to get all animals for a specific player still on the board
    public List<Animal> getPlayerAnimals(Player player) {
        List<Animal> animals = new ArrayList<>();

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Square<Animal> sq = grid[r][c];
                if (sq != null && !sq.isEmpty() && sq.getAnimal().getOwner() == player) {
                    animals.add(sq.getAnimal());
                }
            }
        }

        return animals;
    }
}
