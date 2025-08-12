# AnimalChesswithGUI

EE3206 Java Programming & Application 

Group Project 24 - Animal Chess 

Member
- ZHANG Jianing (57855062)
- QIU Tianqin (57844754)



# HOW TO RUN THE GAME
1. Ensure you have Java Runtime Environment (JRE) version 8 or higher installed
2. Double-click the Group24_AnimalChess.jar file to launch the game
   - Alternatively, run from command line: java -jar Group24_AnimalChess.jar



# GAME OVERVIEW
Animal Chess (also known as Dou Shou Qi) is a traditional Chinese board game
- Two players control animals of different ranks and attempt to either capture all opponent's pieces or enter the opponent's den
- For more detailed background information, please visit https://en.wikipedia.org/wiki/Jungle_(board_game)



# ANIMAL RANKS (from weakest to strongest)
1. Rat (Rank 1)
2. Cat (Rank 2)
3. Dog (Rank 3)
4. Wolf (Rank 4)
5. Leopard (Rank 5)
6. Tiger (Rank 6)
7. Lion (Rank 7)
8. Elephant (Rank 8)



# GAME RULES
1. Two Players (Red and green) with 8 animals each
2. Players alternate moves with Red moving first
3. Animals can move one square horizontally or vertically (not diagonally)
4. Animals cannot move into their own den
5. Animals can capture enemy animals of equal or lower rank
6. Animals in traps have their rank reduced to 0 (can be captured by any enemy animal)
7. Special rules for Rat and Elephant:
   - Rat (rank 1) is the only animal that can enter river squares
   - Rat can capture Elephant (rank 8) unless the Rat is in the river
   - Elephant cannot capture Rat
8. Special rules for Lion and Tiger:
   - Lion can jump over a river vertically and horizontally
   - Tiger can jump over a river vertically only
   - They can jump across rivers only when no Rat of either color blocks the path
   - They jump from a square on one edge of the river to the next non-water square on the other side
   - If that square contains an enemy animal of equal or lower rank, Lion or Tiger capture it as part of their jump



# HOW TO PLAY
1. Click on one of your animals to select it (highlighted with a green border)
2. Click on an empty square or enemy animal to move/capture (If a move/capture is invalid, an corresponding error message with reason will be displayed)
3. Try to satisfy the one of the WINNING conditions



# WINNING THE GAME
The game ends when:
1. A player captures all enemy animals
2. A player moves an animal into the opponent's den
3. 30 moves occur without any captures (results in a draw)


# TROUBLESHOOTING
- If the game doesn't start, verify your Java installation
- Run from command line to see any error messages: java -jar AnimalChess.jar
- For best display, ensure your screen resolution meets the minimum requirements


# END
Hope you enjoy the game!
