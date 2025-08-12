package squares;

import animals.Animal;

/**
 * Represents a single square on the Animal Chess board.
 *
 * @param <T>
 */
public abstract class Square<T extends Animal> {

    private final int row;
    private final int col;
    private T animal; // The animal currently occupying this square, null if empty

    public Square(int row, int col) {
        this.row = row;
        this.col = col;
        this.animal = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public T getAnimal() {
        return animal;
    }

    public void setAnimal(T animal) {
        this.animal = animal;
    }

    public boolean isEmpty() {
        return this.animal == null;
    }

    // Abstract method to indicate if a specific animal can enter this square
    public abstract boolean canEnter(Animal animal);

    // Abstract method to get the type of the square
    public abstract String getType();
}
