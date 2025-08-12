package squares;

import animals.Animal;
import animals.Rat;

/**
 * Represents a river square on the board.
 *
 * @param <T>
 */
public class RiverSquare<T extends Animal> extends Square<T> {

    public RiverSquare(int row, int col) {
        super(row, col);
    }

    @Override
    public boolean canEnter(Animal animal) {
        // Only Rats can enter river squares
        return animal instanceof Rat;
    }

    @Override
    public String getType() {
        return "River";
    }
}
