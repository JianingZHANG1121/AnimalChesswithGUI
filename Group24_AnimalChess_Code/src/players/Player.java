package players;

/**
 * Represents a player in the Animal Chess game.
 */
public class Player {

    private final String name;
    private final boolean isRedPlayer;

    public Player(String name, boolean isRedPlayer) {
        this.name = name;
        this.isRedPlayer = isRedPlayer;
    }

    public String getName() {
        return name;
    }

    public boolean isRedPlayer() {
        return isRedPlayer;
    }
}
