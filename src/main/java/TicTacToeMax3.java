import model.BoardElement;
import model.DataModel;
import org.fusesource.jansi.Ansi;
import util.CheckStatusGame;

import java.util.*;

public class TicTacToeMax3 {

    static final Ansi.Color CROSS_COLOR = Ansi.Color.GREEN;
    static final Ansi.Color CIRCLE_COLOR = Ansi.Color.RED;
    static final Ansi.Color EMPTY_COLOR = Ansi.Color.WHITE;
    static final Ansi.Color DISAPPER_COLOR = Ansi.Color.WHITE;
    static double[] board = new double[9];
    static List<DataModel> dataModel = new ArrayList<>();
    static List<double[]> finalInputSet = new ArrayList<>();
    static List<double[]> finalOutputSet = new ArrayList<>();
    static Queue<Integer> circleBoard = new LinkedList<>();
    static Queue<Integer> crossBoard = new LinkedList<>();
    static int middleCircleBoard = -1;
    static int middleCrossBoard = -1;
    static int lastCircleMove = -1;
    static int lastCrossMove = -1;

    public static void displayBoard(BoardElement disappearColorInNextMove) {
        int disappearMove = -1;
        if (disappearColorInNextMove == BoardElement.CIRCLE && circleBoard.size() == 3) {
            disappearMove = circleBoard.peek();
        } else if (disappearColorInNextMove == BoardElement.CROSS && crossBoard.size() == 3) {
            disappearMove = crossBoard.peek();
        }
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                System.out.println();
            }
            if (board[i] == BoardElement.CROSS.getValue()) {
                if (disappearMove == i) {
                    System.out.print(Ansi.ansi().fg(DISAPPER_COLOR).a("X ").reset());
                } else if (crossBoard.size() > 1 && middleCrossBoard == i) {
                    System.out.print(Ansi.ansi().fg(CROSS_COLOR).a("X ").reset());
                } else {
                    System.out.print(Ansi.ansi().fgBright(CROSS_COLOR).a("X ").reset());
                }
            } else if (board[i] == BoardElement.CIRCLE.getValue()) {
                if (disappearMove == i) {
                    System.out.print(Ansi.ansi().fg(DISAPPER_COLOR).a("O ").reset());
                } else if (circleBoard.size() > 1 && middleCircleBoard == i) {
                    System.out.print(Ansi.ansi().fg(CIRCLE_COLOR).a("O ").reset());
                } else {
                    System.out.print(Ansi.ansi().fgBright(CIRCLE_COLOR).a("O ").reset());
                }
            } else {
                System.out.print(Ansi.ansi().fg(EMPTY_COLOR).a(". ").reset());
            }
        }
        System.out.println();
    }

    public static void removeMoveFromTableBoard(BoardElement boardElements) {
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

    public static void addMove(int move, BoardElement boardElements) {
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

    public static void playWithPayer() {
        BoardElement player = BoardElement.CROSS;
        Scanner scanner = new Scanner(System.in);
        displayBoard(BoardElement.CROSS);
        while (true) {
            int move;
            // Gracz wprowadza ruch
            System.out.println("Podaj współrzędne ruchu (0-8) dla " + player + ": ");
            do {
                move = scanner.nextInt();
                if (move < 0 || move > 8) {
                    System.out.println("Nieprawidłowy ruch. Podaj współrzędne ruchu (0-8): ");
                } else if (!CheckStatusGame.isValidMove(board, move)) {
                    System.out.println("To pole jest już zajęte. Podaj współrzędne ruchu (0-8): ");
                }
            } while (!CheckStatusGame.isValidMove(board, move)); // Sprawdzanie, czy ruch jest poprawny (pole jest puste
            removeMoveFromTableBoard(player.getOpposite());
            addMove(move, player);// Zakładamy, że gracz jest reprezentowany przez 1
            displayBoard(player);
            // sprawdza warunki zakończenia gry
            if (CheckStatusGame.checkWin(board, player.getValue())) {
                System.out.println("Wygrałeś!");
                break;
            } else if (CheckStatusGame.isBoardFull(board)) {
                System.out.println("Remis!");
                break;
            }
            player = player.getOpposite();
        }
        scanner.close();
    }

    public static void main(String[] args) {
        playWithPayer();
    }
}
