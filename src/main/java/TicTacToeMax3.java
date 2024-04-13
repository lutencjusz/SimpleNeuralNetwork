import model.BoardElement;
import model.DataModel;
import org.fusesource.jansi.Ansi;
import util.CheckStatusGame;

import java.util.*;

public class TicTacToeMax3 {

    static double[] board = new double[9];
    static List<DataModel> dataModel = new ArrayList<>();
    static List<double[]> finalInputSet = new ArrayList<>();
    static List<double[]> finalOutputSet = new ArrayList<>();
    static Queue<Integer> circleBoard = new LinkedList<>();
    static Queue<Integer> crossBoard = new LinkedList<>();

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
            if (board[i] == 1) {
                if (disappearMove == i) {
                    System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("X ").reset());
                } else {
                    System.out.print(Ansi.ansi().fgBright(Ansi.Color.RED).a("X ").reset());
                }
            } else if (board[i] == -1) {
                if (disappearMove == i) {
                    System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("O ").reset());
                } else {
                    System.out.print(Ansi.ansi().fgBright(Ansi.Color.GREEN).a("O ").reset());
                }
            } else {
                System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(". ").reset());
            }
        }
        System.out.println();
    }

    public static void removeMoveFromTableBoard(int move, BoardElement boardElements) {
        if (boardElements == BoardElement.CIRCLE && circleBoard.size() == 3) {
            board[move] = 0;
            circleBoard.remove();
        } else if (boardElements == BoardElement.CROSS && crossBoard.size() == 3) {
            board[move] = 0;
            crossBoard.remove();
        } else if (boardElements == BoardElement.NULL) {
            throw new IllegalArgumentException("Nieprawidłowy element planszy!");
        }
    }

    public static void addMove(int move, BoardElement boardElements) {
        board[move] = boardElements.getValue();
        if (boardElements == BoardElement.CIRCLE) {
            circleBoard.add(move);
        } else if (boardElements == BoardElement.CROSS) {
            crossBoard.add(move);
        } else {
            throw new IllegalArgumentException("Nieprawidłowy element planszy!");
        }
    }

    public static void playWithPayer() {
        int player = BoardElement.CROSS.getValue();
        Scanner scanner = new Scanner(System.in);
        displayBoard(BoardElement.CROSS);
        while (true) {
            int move;
            // Gracz wprowadza ruch
            System.out.println("Podaj współrzędne ruchu (0-8): ");
            do {
                move = scanner.nextInt();
                if (move < 0 || move > 8) {
                    System.out.println("Nieprawidłowy ruch. Podaj współrzędne ruchu (0-8): ");
                } else if (!CheckStatusGame.isValidMove(board, move)) {
                    System.out.println("To pole jest już zajęte. Podaj współrzędne ruchu (0-8): ");
                }
            } while (!CheckStatusGame.isValidMove(board, move)); // Sprawdzanie, czy ruch jest poprawny (pole jest puste
            addMove(move, BoardElement.getBoardElement(player));// Zakładamy, że gracz jest reprezentowany przez 1
            displayBoard(BoardElement.getBoardElement(player));
            // sprawdza warunki zakończenia gry
            if (CheckStatusGame.checkWin(board, player)) {
                System.out.println("Wygrałeś!");
                break;
            } else if (CheckStatusGame.isBoardFull(board)) {
                System.out.println("Remis!");
                break;
            }
            removeMoveFromTableBoard(move, BoardElement.getBoardElement(player));
            player = -player;
        }
        scanner.close();
    }

    public static void main(String[] args) {
        playWithPayer();
    }
}
