package squares;

import animals.Animal;

/**
 * Represents a normal square on the board.
 *
 * @param <T>
 */
public class NormalSquare<T extends Animal> extends Square<T> {

    public NormalSquare(int row, int col) {
        super(row, col);
    }

    @Override
    public boolean canEnter(Animal animal) {
        // Any animal can enter a normal square
        return true;
    }

    @Override
    public String getType() {
        return "Normal";
    }
}
