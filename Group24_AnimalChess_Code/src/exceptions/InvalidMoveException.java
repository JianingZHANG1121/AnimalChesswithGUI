package exceptions;

/**
 * Exception thrown when an invalid move occurs.
 */
public class InvalidMoveException extends Exception {

    public InvalidMoveException(String message) {
        super(message);
    }
}
