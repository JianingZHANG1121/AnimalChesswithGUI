package animals;

import games.Board;
import players.Player;
import squares.DenSquare;
import squares.RiverSquare;
import squares.Square;
import squares.TrapSquare;

/**
 * Abstract base class for all animal pieces in the game.
 */
public abstract class Animal {

    protected int rank;
    protected Player owner;
    protected Square<Animal> square; // The square the animal is currently on

    public Animal(int rank, Player owner) {
        this.rank = rank;
        this.owner = owner;
        this.square = null;
    }

    public int getRank() {
        // Special rule for trapped animals
        if (this.square instanceof TrapSquare) {
            TrapSquare<Animal> trap = (TrapSquare<Animal>) this.square;
            if (trap.isEnemyTrap(this)) {
                return 0; // Rank is reduced to 0 if in an enemy trap
            }
        }
        return rank;
    }

    public Player getOwner() {
        return owner;
    }

    public Square<Animal> getSquare() {
        return square;
    }

    public void setSquare(Square<Animal> square) {
        this.square = square;
    }

    /**
     * Determines if this animal can capture another animal. This method
     * considers rank, special abilities (Rat vs Elephant), and traps.
     *
     * @param target The animal to potentially capture.
     * @return true if this animal can capture the target, false otherwise.
     */
    public boolean canCapture(Animal target) {
        if (target == null || this.owner == target.getOwner()) {
            return false; // Cannot capture null or own piece
        }

        // Rule: Rat captures Elephant
        if (this instanceof Rat && target instanceof Elephant) {
            // Rat cannot capture Elephant from river
            return !(this.square instanceof RiverSquare);
        }

        // Rule: Elephant cannot capture Rat
        if (this instanceof Elephant && target instanceof Rat) {
            return false;
        }

        // Rule: Animals cannot attack pieces in the river from land
        if (target.getSquare() instanceof RiverSquare && !(this.square instanceof RiverSquare)) {
            return false;
        }

        // General capture rule: Higher or equal rank captures, considering traps
        return this.getRank() >= target.getRank();
    }

    /**
     * Checks if the animal can move from its current square to the target
     * square.
     *
     * @param board The game board.
     * @param targetSquare The destination square.
     * @return true if the basic move is valid, false otherwise.
     */
    public boolean isValidMove(Board board, Square<Animal> targetSquare) {
        if (targetSquare == null) {
            return false;
        }

        // Cannot move to a square occupied by own piece
        if (!targetSquare.isEmpty() && targetSquare.getAnimal().getOwner() == this.owner) {
            return false;
        }

        // Cannot enter own den
        if (targetSquare instanceof DenSquare) {
            DenSquare<Animal> den = (DenSquare<Animal>) targetSquare;
            if (den.getOwner() == this.owner) {
                return false;
            }
        }

        // Check if the target square terrain allows entry for this animal type
        if (!targetSquare.canEnter(this)) {
            return false;
        }

        // Basic orthogonal movement check
        int rowDiff = Math.abs(this.square.getRow() - targetSquare.getRow());
        int colDiff = Math.abs(this.square.getCol() - targetSquare.getCol());

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    // Abstract method to get the name of the animal
    public abstract String getName();
}
