package animals;

import games.Board;
import players.Player;
import squares.NormalSquare;
import squares.RiverSquare;
import squares.Square;
import squares.TrapSquare;

/**
 * Represents the Tiger piece.
 */
public class Tiger extends Animal {

    public Tiger(Player owner) {
        super(6, owner);
    }

    @Override
    public String getName() {
        return "Tiger";
    }

    /**
     * Checks if the Tiger can jump over a river. Tigers can only jump
     * vertically over rivers, not horizontally.
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
            // Tigers cannot jump horizontally
            return false;
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
