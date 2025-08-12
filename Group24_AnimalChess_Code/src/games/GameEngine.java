package games;

import animals.*;
import players.Player;
import squares.*;
import exceptions.InvalidMoveException;

/**
 * Game engine that handles move validation, execution, and game progress.
 */
public class GameEngine {

    private final GameState gameState;
    public static final int MAX_TURNS_WITHOUT_CAPTURE = 30;

    public GameEngine() {
        this.gameState = new GameState();
    }

    /**
     * Validates and executes a move.
     *
     * @param fromRow Source row
     * @param fromCol Source column
     * @param toRow Destination row
     * @param toCol Destination column
     * @throws InvalidMoveException if the move is invalid
     */
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol) throws InvalidMoveException {
        Board board = gameState.getBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        // Check if coordinates are valid
        if (!board.isWithinBounds(fromRow, fromCol) || !board.isWithinBounds(toRow, toCol)) {
            throw new InvalidMoveException("Coordinates outside of board bounds");
        }

        // Get squares and animal
        Square<Animal> fromSquare = board.getSquare(fromRow, fromCol);
        Square<Animal> toSquare = board.getSquare(toRow, toCol);

        if (fromSquare.isEmpty()) {
            throw new InvalidMoveException("No animal at the source square");
        }

        Animal animal = fromSquare.getAnimal();

        // Check if animal belongs to current player
        if (animal.getOwner() != currentPlayer) {
            throw new InvalidMoveException("You can only move your own animals");
        }

        // Check if non-Rat animals are trying to enter river
        if (toSquare instanceof RiverSquare && !(animal instanceof Rat)) {
            throw new InvalidMoveException(animal.getName() + " cannot enter the river.");
        }

        // Check if Lion/Tiger are trying to jump but blocked by Rat
        if ((animal instanceof Lion || animal instanceof Tiger)
                && !isAdjacentMove(fromRow, fromCol, toRow, toCol)) {

            // Check if Tiger is trying to jump horizontally
            if (animal instanceof Tiger && fromRow == toRow && Math.abs(fromCol - toCol) > 1) {
                throw new InvalidMoveException("Tiger cannot jump over the river horizontally.");
            }

            // Check if this could be a river jump attempt
            if (isRiverJumpBlocked(board, animal, fromRow, fromCol, toRow, toCol)) {
                throw new InvalidMoveException(animal.getName() + " cannot jump over the river. Rat is currently in the path.");
            }
        }

        // Check condition 3: moving into own Den
        if (toSquare instanceof DenSquare) {
            DenSquare<Animal> den = (DenSquare<Animal>) toSquare;
            if (den.getOwner() == animal.getOwner()) {
                throw new InvalidMoveException("Cannot move your animal into your own Den");
            }
        }

        // Check if trying to capture another animal
        if (!toSquare.isEmpty()) {
            Animal targetAnimal = toSquare.getAnimal();

            // Only proceed with capture checks if the target is an enemy
            if (targetAnimal.getOwner() != animal.getOwner()) {
                // Check condition 1: Rat coming out of river trying to capture Elephant
                if (animal instanceof Rat && targetAnimal instanceof Elephant
                        && fromSquare instanceof RiverSquare && !(toSquare instanceof RiverSquare)) {
                    throw new InvalidMoveException("Rat cannot capture Elephant when coming out of river.");
                }

                // Check if capture is possible according to game rules
                if (!animal.canCapture(targetAnimal)) {
                    // Check condition 2: trying to capture higher ranking animal
                    if (animal instanceof Elephant && targetAnimal instanceof Rat) {
                        throw new InvalidMoveException("Elephant cannot capture Rat.");
                    } else if (animal.getRank() < targetAnimal.getRank()) {
                        throw new InvalidMoveException(animal.getName() + " cannot capture " + targetAnimal.getName() + ".");
                    } else {
                        throw new InvalidMoveException("Cannot capture this animal.");
                    }
                }
            }
        }

        // Check if move is valid for this animal (other reasons)
        if (!animal.isValidMove(board, toSquare)) {
            throw new InvalidMoveException("Invalid move for this animal");
        }

        // Check if it's a capture before executing the move
        boolean isCapture = !toSquare.isEmpty() && toSquare.getAnimal().getOwner() != animal.getOwner();

        // Execute the move
        board.moveAnimal(fromRow, fromCol, toRow, toCol);

        // Update game state based on move
        if (isCapture) {
            gameState.resetTurnsWithoutCapture();
        } else {
            gameState.incrementTurnsWithoutCapture();
        }

        // Check for victory conditions
        checkGameStatus();

        // Switch to next player
        if (gameState.getStatus() == GameState.GameStatus.ONGOING) {
            gameState.switchPlayer();
        }
    }

    /**
     * Checks if the game has ended.
     */
    private void checkGameStatus() {
        Board board = gameState.getBoard();

        // Check for den capture (opponent's animal entered a den)
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Square<Animal> square = board.getSquare(r, c);
                if (square instanceof DenSquare && !square.isEmpty()) {
                    DenSquare<Animal> den = (DenSquare<Animal>) square;
                    if (den.isEnemyDen(square.getAnimal())) {
                        // Den captured
                        if (den.getOwner().isRedPlayer()) {
                            gameState.setStatus(GameState.GameStatus.GREEN_WINS);
                            gameState.setEndReason(GameState.GameEndReason.DEN_CAPTURED);
                        } else {
                            gameState.setStatus(GameState.GameStatus.RED_WINS);
                            gameState.setEndReason(GameState.GameEndReason.DEN_CAPTURED);
                        }
                        return;
                    }
                }
            }
        }

        // Check if a player has no animals left
        if (board.getPlayerAnimals(gameState.getRedPlayer()).isEmpty()) {
            gameState.setStatus(GameState.GameStatus.GREEN_WINS);
            gameState.setEndReason(GameState.GameEndReason.ALL_ANIMALS_CAPTURED);
            return;
        }

        if (board.getPlayerAnimals(gameState.getGreenPlayer()).isEmpty()) {
            gameState.setStatus(GameState.GameStatus.RED_WINS);
            gameState.setEndReason(GameState.GameEndReason.ALL_ANIMALS_CAPTURED);
            return;
        }

        // Check for draws (reach maximum moves with no capture)
        if (gameState.getTurnsWithoutCapture() >= MAX_TURNS_WITHOUT_CAPTURE) {
            gameState.setStatus(GameState.GameStatus.DRAW);
            gameState.setEndReason(GameState.GameEndReason.TOO_MANY_TURNS);
        }
    }

    /**
     * Check if a move is to an adjacent square
     */
    private boolean isAdjacentMove(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    /**
     * Check if a river jump is blocked by a Rat
     */
    private boolean isRiverJumpBlocked(Board board, Animal animal, int fromRow, int fromCol, int toRow, int toCol) {
        // Check for horizontal jump (same row)
        if (fromRow == toRow) {
            // Only Lions can jump horizontally
            if (!(animal instanceof Lion)) {
                return false;
            }

            int start = Math.min(fromCol, toCol);
            int end = Math.max(fromCol, toCol);

            for (int c = start + 1; c < end; c++) {
                Square<Animal> square = board.getSquare(fromRow, c);
                if (square instanceof RiverSquare && !square.isEmpty() && square.getAnimal() instanceof Rat) {
                    return true;  // Blocked by a Rat
                }
            }
        } // Check for vertical jump (same column)
        else if (fromCol == toCol) {
            int start = Math.min(fromRow, toRow);
            int end = Math.max(fromRow, toRow);

            for (int r = start + 1; r < end; r++) {
                Square<Animal> square = board.getSquare(r, fromCol);
                if (square instanceof RiverSquare && !square.isEmpty() && square.getAnimal() instanceof Rat) {
                    return true;  // Blocked by a Rat
                }
            }
        }

        return false;  // Not blocked
    }

    /**
     * Returns the current game state.
     *
     * @return current game state
     */
    public GameState getGameState() {
        return gameState;
    }
}
