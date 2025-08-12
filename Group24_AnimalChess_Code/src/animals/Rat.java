package animals;

import games.Board;
import players.Player;
import squares.RiverSquare;
import squares.Square;

/**
 * Represents the Rat piece.
 */
public class Rat extends Animal {

    public Rat(Player owner) {
        super(1, owner);
    }

    @Override
    public String getName() {
        return "Rat";
    }

    /**
     * Rats can enter River squares.
     */
    @Override
    public boolean isValidMove(Board board, Square<Animal> targetSquare) {
        boolean basicValid = super.isValidMove(board, targetSquare);
        if (!basicValid && !(targetSquare instanceof RiverSquare)) {
            return false;
        }

        // Check if it's a normal move or moving into/within the river
        int rowDiff = Math.abs(this.square.getRow() - targetSquare.getRow());
        int colDiff = Math.abs(this.square.getCol() - targetSquare.getCol());

        if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
            if (targetSquare.canEnter(this)) {
                // Ensure target square is empty or contains an enemy that can be captured
                if (targetSquare.isEmpty() || canCapture(targetSquare.getAnimal())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Override canCapture for the special Rat vs Elephant rule.
     */
    @Override
    public boolean canCapture(Animal target) {
        if (target == null || this.owner == target.getOwner()) {
            return false;
        }

        // Rule: Rat captures Elephant (unless Rat is in the river)
        if (target instanceof Elephant) {
            // Rat in river cannot capture Elephant
            return !(this.square instanceof RiverSquare);
        }

        // Use the general capture logic
        return super.canCapture(target);
    }
}
