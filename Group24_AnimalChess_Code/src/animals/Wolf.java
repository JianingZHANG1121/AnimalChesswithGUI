package animals;

import games.Board;
import players.Player;
import squares.Square;

/**
 * Represents the Wolf piece.
 */
public class Wolf extends Animal {

    public Wolf(Player owner) {
        super(4, owner);
    }

    @Override
    public String getName() {
        return "Wolf";
    }

    // Wolves have no special movement or capture rules
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
