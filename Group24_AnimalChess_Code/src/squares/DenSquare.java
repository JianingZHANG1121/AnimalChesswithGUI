package squares;

import animals.Animal;
import players.Player;

/**
 * Represent a den square on the board.
 *
 * @param <T>
 */
public class DenSquare<T extends Animal> extends Square<T> {

    private final Player owner;

    public DenSquare(int row, int col, Player owner) {
        super(row, col);
        this.owner = owner;
    }

    /**
     * Checks if the entering animal is an enemy.
     *
     * @param animal The animal trying to enter.
     * @return true if the animal belongs to the opponent, false otherwise.
     */
    public boolean isEnemyDen(Animal animal) {
        return canEnter(animal);
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String getType() {
        return "Den";
    }

    @Override
    public boolean canEnter(Animal animal) {
        // An animal can enter the den only if it belongs to the opponent
        return animal != null && animal.getOwner() != this.owner;
    }
}
