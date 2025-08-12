package squares;

import animals.Animal;
import players.Player;

/**
 * Represents a trap square on the board.
 *
 * @param <T>
 */
public class TrapSquare<T extends Animal> extends Square<T> {

    private final Player owner;

    public TrapSquare(int row, int col, Player owner) {
        super(row, col);
        this.owner = owner;
    }

    /**
     * Checks if the animal entering the trap belongs to the opponent of the
     * trap owner.
     *
     * @param animal The animal entering the trap.
     * @return true if the animal is an enemy, false otherwise.
     */
    public boolean isEnemyTrap(Animal animal) {
        return animal != null && animal.getOwner() != this.owner;
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String getType() {
        return "Trap";
    }

    @Override
    public boolean canEnter(Animal animal) {
        // Any animal can enter a trap square
        return true;
    }
}
