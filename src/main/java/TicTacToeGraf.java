import model.BoardElement;
import model.DataModel;
import util.CheckStatusGame;
import util.HeuristicStrategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Queue;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Klasa reprezentująca graficzny interfejs użytkownika gry w kółko i krzyżyk z ograniczeniem do 3 pól.
 */
public class TicTacToeGraf extends JFrame implements ActionListener {
    HeuristicStrategy heuristicStrategy = new HeuristicStrategy();
    final boolean IS_PLAY_WITH_COMPUTER = true;
    final int SLEEP_INTERVAL_IN_MILI = 1000;
    private BoardElement player = BoardElement.CROSS;
    private final Color CROSS_COLOR = Color.GREEN.darker();
    private final Color LIGHT_CROSS_COLOR = Color.GREEN.brighter();
    private final Color CIRCLE_COLOR = Color.pink;
    private final Color LIGHT_CIRCLE_COLOR = Color.red;
    private final Color DISAPPER_COLOR = Color.gray;
    private double[] board = new double[9];
    private java.util.List<DataModel> dataModel = new ArrayList<>();
    private java.util.List<double[]> finalInputSet = new ArrayList<>();
    private List<double[]> finalOutputSet = new ArrayList<>();
    private Queue<Integer> circleBoard = new LinkedList<>();
    private Queue<Integer> crossBoard = new LinkedList<>();
    private int middleCircleBoard = -1;
    private int middleCrossBoard = -1;
    private int lastCircleMove = -1;
    private int lastCrossMove = -1;
    private JButton[][] buttons = new JButton[3][3];// true for X, false for O

    public TicTacToeGraf() {
        super("Kółko i Krzyżyk");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        initializeButtons();
        resetButtons();
    }

    private void initializeButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new Font("Courier New", Font.BOLD, 100));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].addActionListener(this);
                add(buttons[row][col]);
            }
        }
    }

    private void displayBoard(BoardElement disappearColorInNextMove) {
        int disappearMove = -1;
        if (disappearColorInNextMove == BoardElement.CIRCLE && circleBoard.size() == 3) {
            disappearMove = circleBoard.peek();
        } else if (disappearColorInNextMove == BoardElement.CROSS && crossBoard.size() == 3) {
            disappearMove = crossBoard.peek();
        }
        int counter = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[counter] == BoardElement.CIRCLE.getValue()) {
                    buttons[row][col].setText("O");
                    if (disappearMove == counter) {
                        buttons[row][col].setForeground(DISAPPER_COLOR);
                    } else if (circleBoard.size() > 1 && middleCircleBoard == counter) {
                        buttons[row][col].setForeground(CIRCLE_COLOR);
                    } else {
                        buttons[row][col].setForeground(LIGHT_CIRCLE_COLOR);
                    }
                } else if (board[counter] == BoardElement.CROSS.getValue()) {
                    buttons[row][col].setText("X");
                    if (disappearMove == counter) {
                        buttons[row][col].setForeground(DISAPPER_COLOR);
                    } else if (crossBoard.size() > 1 && middleCrossBoard == counter) {
                        buttons[row][col].setForeground(CROSS_COLOR);
                    } else {
                        buttons[row][col].setForeground(LIGHT_CROSS_COLOR);
                    }
                } else {
                    buttons[row][col].setText("");
                }
                counter++;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int move = -1;
        JButton buttonClicked = (JButton) e.getSource();
        if (!buttonClicked.getText().isEmpty()) {
            return;
        }
        for (int i = 0; i < buttons.length; i++) {
            for (int j = 0; j < buttons[i].length; j++) {
                if (buttons[i][j] == buttonClicked) {
                    move = i * 3 + j;
                    // Możesz tutaj wykonać dodatkowe działania po identyfikacji przycisku
                }
            }
        }// gets the particular button that was clicked
        if (player == BoardElement.CROSS) {
            if (buttonClicked.getText().equals("")) {
                buttonClicked.setForeground(CROSS_COLOR);
                buttonClicked.setText("X");
            }
        }
        removeMoveFromTableBoard(player.getOpposite());
        addMove(move, player);
        displayBoard(player);
        if (CheckStatusGame.checkWin(board, player.getValue())) {
            JOptionPane.showMessageDialog(null, "Gracz " + player + " wygrał!");
            resetButtons();
            return;
        }
        player = player.getOpposite();
        if (IS_PLAY_WITH_COMPUTER && player == BoardElement.CIRCLE) {
            super.update(this.getGraphics());
            try {
                sleep(SLEEP_INTERVAL_IN_MILI);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            int computerMove = heuristicStrategy.getBestMove(board, false, true);
            removeMoveFromTableBoard(player.getOpposite());
            addMove(computerMove, player);
            displayBoard(player);
            if (CheckStatusGame.checkWin(board, player.getValue())) {
                System.out.println("Komputer wygrał!");
                JOptionPane.showMessageDialog(null, "Gracz " + player + " wygrał!");
                resetButtons();
            }
            player = player.getOpposite();
        }
    }

    private void resetButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        middleCircleBoard = -1;
        middleCrossBoard = -1;
        lastCircleMove = -1;
        lastCrossMove = -1;
        circleBoard.clear();
        crossBoard.clear();
        Arrays.fill(board, 0);
    }

    public void removeMoveFromTableBoard(BoardElement boardElements) {
        if (boardElements == BoardElement.CIRCLE && circleBoard.size() == 3) {
            board[circleBoard.peek()] = 0;
            circleBoard.remove();
        } else if (boardElements == BoardElement.CROSS && crossBoard.size() == 3) {
            board[crossBoard.peek()] = 0;
            crossBoard.remove();
        } else if (boardElements == BoardElement.NULL) {
            throw new IllegalArgumentException("Nieprawidłowy element planszy!");
        }
    }

    public void addMove(int move, BoardElement boardElements) {
        board[move] = boardElements.getValue();
        if (boardElements == BoardElement.CIRCLE) {
            if (circleBoard.size() > 0) {
                middleCircleBoard = lastCircleMove;
            }
            lastCircleMove = move;
            circleBoard.add(move);
        } else if (boardElements == BoardElement.CROSS) {
            if (crossBoard.size() > 0) {
                middleCrossBoard = lastCrossMove;
            }
            lastCrossMove = move;
            crossBoard.add(move);
        } else {
            throw new IllegalArgumentException("Nieprawidłowy element planszy!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGraf().setVisible(true));
    }
}
