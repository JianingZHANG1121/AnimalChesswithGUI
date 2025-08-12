package movements;

import animals.Animal;
import games.Board;
import interfaces.Moveable;
import squares.Square;

/**
 * Normal movement: move one square vertically or horizontally.
 */
public class NormalMovement implements Moveable {

    @Override
    public boolean isValidMove(Animal animal, Board board, Square<Animal> targetSquare) {
        if (animal == null || animal.getSquare() == null || targetSquare == null) {
            return false;
        }

        // Check if it's a normal movement
        int rowDiff = Math.abs(animal.getSquare().getRow() - targetSquare.getRow());
        int colDiff = Math.abs(animal.getSquare().getCol() - targetSquare.getCol());

        if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
            if (targetSquare.canEnter(animal)
                    && (targetSquare.isEmpty() || animal.canCapture(targetSquare.getAnimal()))) {
                return true;
            }
        }

        return false;
    }
}
