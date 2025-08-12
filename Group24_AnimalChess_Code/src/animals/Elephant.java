package animals;

import games.Board;
import players.Player;
import squares.Square;

/**
 * Represents the Elephant piece.
 */
public class Elephant extends Animal {

    public Elephant(Player owner) {
        super(8, owner);
    }

    @Override
    public String getName() {
        return "Elephant";
    }

    @Override
    public boolean canCapture(Animal target) {
        // Elephant cannot capture Rat
        if (target instanceof Rat) {
            return false;
        }
        // Otherwise, use the general capture logic
        return super.canCapture(target);
    }

    @Override
    public boolean isValidMove(Board board, Square<Animal> targetSquare) {

        boolean basicValid = super.isValidMove(board, targetSquare);

        // Check if it's a normal move
        if (basicValid) {
            int rowDiff = Math.abs(this.square.getRow() - targetSquare.getRow());
            int colDiff = Math.abs(this.square.getCol() - targetSquare.getCol());
            if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
                // Ensure target square is empty or contains an enemy that can be captured
                if (targetSquare.isEmpty() || canCapture(targetSquare.getAnimal())) {
                    return true;
                }
            }
        }
        return false;
    }
}
