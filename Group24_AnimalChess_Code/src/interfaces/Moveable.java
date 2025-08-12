package interfaces;

import animals.Animal;
import games.Board;
import squares.Square;

/**
 * Interface for animal movement patterns.
 */
public interface Moveable {

    /**
     * Checks if a movement is valid according to the strategy.
     *
     * @param animal The animal attempting to move
     * @param board The game board
     * @param targetSquare The destination square
     * @return true if the move is valid, false otherwise
     */
    boolean isValidMove(Animal animal, Board board, Square<Animal> targetSquare);
}
