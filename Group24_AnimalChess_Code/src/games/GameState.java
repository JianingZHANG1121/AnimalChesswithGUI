package games;

import players.Player;

/**
 * Manages the overall state of the Animal Chess game.
 */
public class GameState {

    private final Board board;
    private final Player redPlayer;
    private final Player greenPlayer;
    private Player currentPlayer;
    private GameStatus status;
    private GameEndReason endReason;
    private int turnsWithoutCapture;

    public enum GameStatus {
        ONGOING,
        RED_WINS,
        GREEN_WINS,
        DRAW
    }

    public enum GameEndReason {
        NONE, // Game still ongoing
        DEN_CAPTURED, // A player captured opponent's den
        ALL_ANIMALS_CAPTURED, // A player has no all animals
        TOO_MANY_TURNS      // Draw due to reaching maximum turns without capture
    }

    public GameState() {
        this.redPlayer = new Player("Red", true);
        this.greenPlayer = new Player("Green", false);
        this.board = new Board(redPlayer, greenPlayer);
        this.currentPlayer = redPlayer; // Red player starts first
        this.status = GameStatus.ONGOING;
        this.endReason = GameEndReason.NONE;
        this.turnsWithoutCapture = 0;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameEndReason getEndReason() {
        return endReason;
    }

    public void setEndReason(GameEndReason reason) {
        this.endReason = reason;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public Player getGreenPlayer() {
        return greenPlayer;
    }

    /**
     * Switches the current player to the other player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == redPlayer) ? greenPlayer : redPlayer;
    }

    /**
     * Updates the game status.
     *
     * @param newStatus The new status of the game.
     */
    public void setStatus(GameStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Resets the count of turns without a capture.
     */
    public void resetTurnsWithoutCapture() {
        this.turnsWithoutCapture = 0;
    }

    /**
     * Increments the count of turns without a capture.
     */
    public void incrementTurnsWithoutCapture() {
        this.turnsWithoutCapture++;
    }

    public int getTurnsWithoutCapture() {
        return turnsWithoutCapture;
    }
}
