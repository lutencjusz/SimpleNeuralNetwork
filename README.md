# Tic-Tac-Toe Game

This project contains three main classes responsible for the game of Tic-Tac-Toe with a limitation to 3 fields and without, in different variants.
## GUI Class

The `TicTacToeGraf` class is responsible for the graphical representation of the Tic-Tac-Toe game with a limitation to max. 3 Xs and Os on the board. It allows the user to interact with the game through a graphical interface that includes a 3x3 grid of buttons.
It enables gameplay in player versus player mode as well as player mode.

### Functionalities

- Displaying a 3x3 graphical grid.
- Handling user clicks and updating the game state.
- Displaying messages about the game outcome.

## Console Classes

The `TicTacToeNeuralNetwork` class implements the logic of the Tic-Tac-Toe game, using a neural network and a heuristic algorithm to assess the game state and make decisions.
The `TickTackToeMax3` class implements the logic of the Tic-Tac-Toe game with a limitation to max 3 Xs or Os, in player versus player game mode.

### Technologies

- **Neural Network**: For analyzing the board state and predicting the best move.
- **Heuristic Algorithm**: For evaluating one player's advantage in the given game state.

### Functionalities

- Generating moves for the computer player.
- Assessing the game state based on heuristic strategies and data from the neural network.
- Communicating with the user interface to update the game state.

## How to Run

```bash
# Cloning the repository
git clone <repository-url>

# Changing to the project directory
cd project-directory

# Launching the application
java -jar TicTacToe.jar
