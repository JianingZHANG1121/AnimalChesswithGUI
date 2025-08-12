package gui;

import animals.*;
import exceptions.InvalidMoveException;
import games.Board;
import games.GameEngine;
import games.GameState;
import interfaces.Startable;
import squares.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * GUI for the Animal Chess game.
 */
public class UserInterface implements Startable {

    private final GameEngine gameEngine;
    private JFrame frame;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JLabel movesRemainingLabel;
    private JLabel redCapturedLabel;
    private JLabel greenCapturedLabel;
    private JButton[][] squareButtons;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // Colors for the board
    private final Color NORMAL_COLOR = Color.WHITE; // Color for background
    private final Color RIVER_COLOR = new Color(0, 0, 205); // Color for river
    private final Color TRAP_COLOR = new Color(255, 215, 0); // Color for traps
    private final Color RED_DEN_COLOR = new Color(255, 100, 100); // Color for red den
    private final Color GREEN_DEN_COLOR = new Color(50, 205, 50); // Color for green den
    private final Color RED_PIECE_COLOR = Color.RED; // Color for red player
    private final Color GREEN_PIECE_COLOR = new Color(0, 128, 0); // Color for green player

    // Animal emojis and symbols
    private Map<String, String> animalEmojis;
    private Map<String, ImageIcon> redAnimalIcons;
    private Map<String, ImageIcon> greenAnimalIcons;

    public UserInterface(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        initializeAnimalSymbols();
    }

    private void initializeAnimalSymbols() {

        animalEmojis = new HashMap<>();
        animalEmojis.put("Rat", "🐀");
        animalEmojis.put("Cat", "🐱");
        animalEmojis.put("Dog", "🐕");
        animalEmojis.put("Wolf", "🐺");
        animalEmojis.put("Leopard", "🐆");
        animalEmojis.put("Tiger", "🐯");
        animalEmojis.put("Lion", "🦁");
        animalEmojis.put("Elephant", "🐘");

        // Create custom icons with animal symbols
        redAnimalIcons = new HashMap<>();
        greenAnimalIcons = new HashMap<>();

        for (String animal : animalEmojis.keySet()) {
            redAnimalIcons.put(animal, createSymbolIcon(animal, animalEmojis.get(animal), RED_PIECE_COLOR));
            greenAnimalIcons.put(animal, createSymbolIcon(animal, animalEmojis.get(animal), GREEN_PIECE_COLOR));
        }
    }

    private ImageIcon createSymbolIcon(String name, String emoji, Color color) {
        // Create a blank image for the icon
        int size = 56;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smoother text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Make transparent background
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, size, size);
        g2d.setComposite(AlphaComposite.SrcOver);

        // Draw emoji in center
        g2d.setColor(color);
        g2d.setFont(new Font("Dialog", Font.PLAIN, 22));
        FontMetrics fm = g2d.getFontMetrics();
        int emojiWidth = fm.stringWidth(emoji);
        g2d.drawString(emoji, (size - emojiWidth) / 2, 22);

        // Draw name below
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        fm = g2d.getFontMetrics();
        int nameWidth = fm.stringWidth(name);
        g2d.drawString(name, (size - nameWidth) / 2, 44);

        g2d.dispose();

        return new ImageIcon(image);
    }

    @Override
    public void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }

        // Create and set up the window
        frame = new JFrame("Animal Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // Create status panel with two labels
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(new Color(220, 220, 220));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Player turn label
        statusLabel = new JLabel("Red player's turn");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(RED_PIECE_COLOR);
        statusPanel.add(statusLabel);

        // Moves remaining label
        movesRemainingLabel = new JLabel("Moves until draw: 30");
        movesRemainingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        movesRemainingLabel.setForeground(Color.BLACK);
        statusPanel.add(movesRemainingLabel);

        frame.add(statusPanel, BorderLayout.NORTH);

        // Create board panel with padding
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createBoardPanel();
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        frame.add(boardContainer, BorderLayout.CENTER);

        // Create info panel for captured pieces
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        infoPanel.setBackground(new Color(220, 220, 220));

        // Labels for captured pieces - now saving references to these labels
        redCapturedLabel = new JLabel("Red Captured: None");
        redCapturedLabel.setForeground(RED_PIECE_COLOR);
        redCapturedLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        greenCapturedLabel = new JLabel("Green Captured: None");
        greenCapturedLabel.setForeground(GREEN_PIECE_COLOR);
        greenCapturedLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        infoPanel.add(redCapturedLabel);
        infoPanel.add(greenCapturedLabel);
        frame.add(infoPanel, BorderLayout.SOUTH);

        // Set frame properties
        frame.pack();
        frame.setMinimumSize(new Dimension(550, 850));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Update UI
        updateUI();
    }

    private void createBoardPanel() {
        Board board = gameEngine.getGameState().getBoard();
        boardPanel = new JPanel(new GridLayout(board.getRows(), board.getCols()));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        squareButtons = new JButton[board.getRows()][board.getCols()];

        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(70, 70));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFocusPainted(false);

                // Add border to all buttons
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                final int row = r;
                final int col = c;

                button.addActionListener(e -> handleSquareClick(row, col));

                squareButtons[r][c] = button;
                boardPanel.add(button);
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        GameState gameState = gameEngine.getGameState();

        // If game is over, do nothing
        if (gameState.getStatus() != GameState.GameStatus.ONGOING) {
            return;
        }

        Board board = gameState.getBoard();
        Square<Animal> clickedSquare = board.getSquare(row, col);

        // If no piece is selected, select one if it belongs to current player
        if (selectedRow == -1 && selectedCol == -1) {
            if (!clickedSquare.isEmpty() && clickedSquare.getAnimal().getOwner() == gameState.getCurrentPlayer()) {
                selectedRow = row;
                selectedCol = col;
                squareButtons[row][col].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
            }
        } // If a piece is already selected
        else {
            // If selecting the same piece, deselect it
            if (selectedRow == row && selectedCol == col) {
                selectedRow = -1;
                selectedCol = -1;
                updateUI();
                return;
            }

            // Attempt to move
            try {
                gameEngine.makeMove(selectedRow, selectedCol, row, col);
                selectedRow = -1;
                selectedCol = -1;
                updateUI();

                // Check game status after move
                if (gameState.getStatus() != GameState.GameStatus.ONGOING) {
                    announceGameResult(gameState.getStatus(), gameState.getEndReason());
                }
            } catch (InvalidMoveException e) {
                JOptionPane.showMessageDialog(frame, "Invalid move: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                selectedRow = -1;
                selectedCol = -1;
                updateUI();
            }
        }
    }

    private void updateUI() {
        Board board = gameEngine.getGameState().getBoard();
        GameState gameState = gameEngine.getGameState();

        // Update status label
        String currentPlayerText = gameState.getCurrentPlayer().isRedPlayer() ? "Red" : "Green";
        statusLabel.setText(currentPlayerText + " player's turn");
        statusLabel.setForeground(gameState.getCurrentPlayer().isRedPlayer() ? RED_PIECE_COLOR : GREEN_PIECE_COLOR);

        // Update moves remaining label
        int turnsWithoutCapture = gameState.getTurnsWithoutCapture();
        int movesRemaining = GameEngine.MAX_TURNS_WITHOUT_CAPTURE - turnsWithoutCapture;
        movesRemainingLabel.setText("Moves until draw: " + movesRemaining);

        // If moves remaining is low, highlight with warning color
        if (movesRemaining <= 5) {
            movesRemainingLabel.setForeground(Color.RED);
            movesRemainingLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        } else {
            movesRemainingLabel.setForeground(Color.BLACK);
            movesRemainingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        // Update board
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                JButton button = squareButtons[r][c];
                Square<Animal> square = board.getSquare(r, c);

                // Reset border
                if (r != selectedRow || c != selectedCol) {
                    button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                }

                // Default settings
                button.setText("");
                button.setIcon(null);
                button.setHorizontalTextPosition(JButton.CENTER);
                button.setVerticalTextPosition(JButton.BOTTOM);

                // Set background color and text based on square type
                if (square instanceof RiverSquare) {
                    button.setBackground(RIVER_COLOR);
                    button.setForeground(Color.WHITE);
                    button.setText("River");
                    button.setFont(new Font("SansSerif", Font.BOLD, 12));
                } else if (square instanceof TrapSquare) {
                    TrapSquare<Animal> trap = (TrapSquare<Animal>) square;
                    button.setBackground(TRAP_COLOR);
                    button.setText("Trap");
                    button.setForeground(trap.getOwner().isRedPlayer() ? RED_PIECE_COLOR : GREEN_PIECE_COLOR);
                    button.setFont(new Font("SansSerif", Font.BOLD, 12));
                } else if (square instanceof DenSquare) {
                    DenSquare<Animal> den = (DenSquare<Animal>) square;
                    button.setBackground(den.getOwner().isRedPlayer() ? RED_DEN_COLOR : GREEN_DEN_COLOR);
                    button.setText("Den");
                    button.setForeground(den.getOwner().isRedPlayer() ? RED_PIECE_COLOR : GREEN_PIECE_COLOR);
                    button.setFont(new Font("SansSerif", Font.BOLD, 12));
                } else {
                    button.setBackground(NORMAL_COLOR);
                }

                // If square has an animal, show it
                if (!square.isEmpty()) {
                    Animal animal = square.getAnimal();
                    String animalName = animal.getName();

                    if (animal.getOwner().isRedPlayer()) {
                        button.setIcon(redAnimalIcons.get(animalName));
                    } else {
                        button.setIcon(greenAnimalIcons.get(animalName));
                    }

                    // Ensure the button background fits the terrain
                    if (square instanceof RiverSquare) {
                        button.setText(""); // Clear the river text if an animal is present
                    }
                }
            }
        }

        // Update captured pieces lists
        updateCapturedPieces();
    }

    private void updateCapturedPieces() {
        Board board = gameEngine.getGameState().getBoard();
        StringBuilder redCaptured = new StringBuilder("<html>Red Captured: ");
        StringBuilder greenCaptured = new StringBuilder("<html>Green Captured: ");

        if (board.getRedCaptured().isEmpty()) {
            redCaptured.append("None");
        } else {
            for (Animal animal : board.getRedCaptured()) {
                redCaptured.append(animal.getName()).append(", ");
            }
            // Remove last comma
            redCaptured.setLength(redCaptured.length() - 2);
        }
        redCaptured.append("</html>");

        if (board.getGreenCaptured().isEmpty()) {
            greenCaptured.append("None");
        } else {
            for (Animal animal : board.getGreenCaptured()) {
                greenCaptured.append(animal.getName()).append(", ");
            }
            // Remove last comma
            greenCaptured.setLength(greenCaptured.length() - 2);
        }
        greenCaptured.append("</html>");

        // Update the labels directly - simplifies the update process
        redCapturedLabel.setText(redCaptured.toString());
        greenCapturedLabel.setText(greenCaptured.toString());
    }

    private void announceGameResult(GameState.GameStatus status, GameState.GameEndReason reason) {
        String winnerMessage = "";
        String reasonMessage = "";

        // Determine the winner message
        switch (status) {
            case RED_WINS:
                winnerMessage = "Red player wins!";
                statusLabel.setForeground(RED_PIECE_COLOR);
                break;
            case GREEN_WINS:
                winnerMessage = "Green player wins!";
                statusLabel.setForeground(GREEN_PIECE_COLOR);
                break;
            case DRAW:
                winnerMessage = "The game ended in a draw.";
                statusLabel.setForeground(Color.BLACK);
                break;
            default:
                break;
        }

        // Determine the reason message
        switch (reason) {
            case DEN_CAPTURED:
                reasonMessage = "Reason: Enemy den was captured!";
                break;
            case ALL_ANIMALS_CAPTURED:
                reasonMessage = "Reason: All opponent's animals were captured!";
                break;
            case TOO_MANY_TURNS:
                reasonMessage = "Reason: 30 turns without capture (stalemate).";
                break;
            default:
                break;
        }

        statusLabel.setText("Game Over: " + winnerMessage);
        movesRemainingLabel.setText(""); // Clear the moves remaining label at game end

        // Show dialog with both winner and reason information
        JOptionPane.showMessageDialog(frame,
                winnerMessage + "\n" + reasonMessage,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Inner class needed for BufferedImage
    private static class BufferedImage extends java.awt.image.BufferedImage {

        public BufferedImage(int width, int height, int imageType) {
            super(width, height, imageType);
        }
    }
}
