package animals;

import games.Board;
import players.Player;
import squares.NormalSquare;
import squares.RiverSquare;
import squares.Square;
import squares.TrapSquare;

/**
 * Represents the Lion piece.
 */
public class Lion extends Animal {

    public Lion(Player owner) {
        super(7, owner);
    }

    @Override
    public String getName() {
        return "Lion";
    }

    /**
     * Checks if the Lion can jump over a river.
     *
     * @param board The game board.
     * @param targetSquare The destination square across the river.
     * @return true if the jump is valid, false otherwise.
     */
    public boolean canJumpRiver(Board board, Square<Animal> targetSquare) {
        // Must be jumping to land or trap
        if (!(targetSquare instanceof NormalSquare || targetSquare instanceof TrapSquare)) {
            return false;
        }

        int startRow = this.square.getRow();
        int startCol = this.square.getCol();
        int endRow = targetSquare.getRow();
        int endCol = targetSquare.getCol();

        // Check for horizontal jump (across river)
        if (startRow == endRow) {
            // Must have valid distance to jump
            int colDiff = Math.abs(startCol - endCol);
            if (colDiff <= 1) {
                return false; // Too close for a jump
            }

            // Check each square in the path
            int step = (endCol > startCol) ? 1 : -1;
            for (int c = startCol + step; c != endCol; c += step) {
                Square<Animal> pathSquare = board.getSquare(startRow, c);

                // Path must contain only river squares
                if (!(pathSquare instanceof RiverSquare)) {
                    return false;
                }

                // Path must not contain a Rat
                if (!pathSquare.isEmpty() && pathSquare.getAnimal() instanceof Rat) {
                    return false;
                }
            }
            return true;
        } // Check for vertical jump (across river)
        else if (startCol == endCol) {
            // Must have valid distance to jump
            int rowDiff = Math.abs(startRow - endRow);
            if (rowDiff <= 1) {
                return false;
            }

            // Check each square in the path
            int step = (endRow > startRow) ? 1 : -1;
            for (int r = startRow + step; r != endRow; r += step) {
                Square<Animal> pathSquare = board.getSquare(r, endCol);

                // Path must contain only river squares
                if (!(pathSquare instanceof RiverSquare)) {
                    return false;
                }

                // Path must not contain a Rat
                if (!pathSquare.isEmpty() && pathSquare.getAnimal() instanceof Rat) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean isValidMove(Board board, Square<Animal> targetSquare) {
        // Check standard move validity first
        boolean basicValid = super.isValidMove(board, targetSquare);

        // Check if it's a normal move
        if (basicValid) {
            int rowDiff = Math.abs(this.square.getRow() - targetSquare.getRow());
            int colDiff = Math.abs(this.square.getCol() - targetSquare.getCol());
            if ((rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)) {
                if (targetSquare.isEmpty() || canCapture(targetSquare.getAnimal())) {
                    return true;
                }
            }
        }

        // If not a normal move, check for river jump
        if (canJumpRiver(board, targetSquare)) {
            if (targetSquare.isEmpty() || canCapture(targetSquare.getAnimal())) {
                return true;
            }
        }

        return false;
    }
}
