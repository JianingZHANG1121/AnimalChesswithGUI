package movements;

import animals.Animal;
import games.Board;
import interfaces.Moveable;
import squares.NormalSquare;
import squares.RiverSquare;
import squares.Square;
import squares.TrapSquare;

/**
 * River Jump Movement: Lion can jump over a river vertically and horizontally,
 * Tiger can jump over a river vertically.
 */
public class RiverJumpMovement implements Moveable {

    /**
     * Checks if an animal can jump over a river to the target square.
     */
    private boolean canJumpRiver(Animal animal, Board board, Square<Animal> targetSquare) {

        // Can only land on normal square or trap square after jumping
        if (!(targetSquare instanceof NormalSquare || targetSquare instanceof TrapSquare)) {
            return false;
        }

        Square<Animal> currentSquare = animal.getSquare();
        int startRow = currentSquare.getRow();
        int startCol = currentSquare.getCol();
        int endRow = targetSquare.getRow();
        int endCol = targetSquare.getCol();

        // Check for horizontal jump across the river
        if (startRow == endRow && Math.abs(startCol - endCol) == 3) {
            int riverCol1 = Math.min(startCol, endCol) + 1;
            int riverCol2 = Math.min(startCol, endCol) + 2;
            // Check if all intermediate squares are river squares
            if (board.getSquare(startRow, riverCol1) instanceof RiverSquare
                    && board.getSquare(startRow, riverCol2) instanceof RiverSquare) {
                // Check if river squares are empty (no blocking Rat of either colors)
                return board.getSquare(startRow, riverCol1).isEmpty()
                        && board.getSquare(startRow, riverCol2).isEmpty()
                        && (targetSquare.isEmpty() || animal.canCapture(targetSquare.getAnimal()));
            }
        } // Check for vertical jump (across river)
        else if (startCol == endCol && Math.abs(startRow - endRow) == 4) {
            int riverRow1 = Math.min(startRow, endRow) + 1;
            int riverRow2 = Math.min(startRow, endRow) + 2;
            int riverRow3 = Math.min(startRow, endRow) + 3;

            // Check if all intermediate squares are river squares
            if (board.getSquare(riverRow1, startCol) instanceof RiverSquare
                    && board.getSquare(riverRow2, startCol) instanceof RiverSquare
                    && board.getSquare(riverRow3, startCol) instanceof RiverSquare) {
                // Check if river squares are empty (no blocking Rat of either colors)
                return board.getSquare(riverRow1, startCol).isEmpty()
                        && board.getSquare(riverRow2, startCol).isEmpty()
                        && board.getSquare(riverRow3, startCol).isEmpty()
                        && (targetSquare.isEmpty() || animal.canCapture(targetSquare.getAnimal()));
            }
        }

        return false;
    }

    @Override
    public boolean isValidMove(Animal animal, Board board, Square<Animal> targetSquare) {

        if (animal == null || animal.getSquare() == null || targetSquare == null || board == null) {
            return false;
        }

        // Check if this is a normal move
        NormalMovement normalMove = new NormalMovement();
        if (normalMove.isValidMove(animal, board, targetSquare)) {
            return true;
        }

        // If not a normal move, check for a river jump
        return canJumpRiver(animal, board, targetSquare);
    }
}
