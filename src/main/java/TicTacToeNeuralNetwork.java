import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.*;

public class TicTacToeNeuralNetwork {
    // Metoda do wizualizacji planszy
    public static void displayBoard(double[] board) {
        for (int i = 0; i < 9; i++) {
            if (i % 3 == 0) {
                System.out.println();
            }
            if (board[i] == 1) {
                System.out.print("X ");
            } else if (board[i] == -1) {
                System.out.print("O ");
            } else {
                System.out.print(". ");
            }
        }
        System.out.println();
    }

    public static boolean isValidMove(double[] board, int move) {
        return board[move] == 0;
    }

    private static double[][] convertListToArray(List<double[]> list) {
        double[][] array = new double[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    // Metoda do trenowania sieci neuronowej
    public static void trainingNetwork(BasicNetwork network, int robotPlayer) {
        for (int p0 = 0; p0 < 9; p0++) {
            for (int p1 = 0; p1 < 9; p1++) {
                if (p0 == p1) {
                    continue;
                }
                int player = 1;
                List<double[]> inputSet = new ArrayList<>();
                List<double[]> outputSet = new ArrayList<>();
                double[] board = new double[9];
                board[p0] = player;
                board[p1] = -player;
                do {
                    for (int j = 0; j < 9; j++) {
                        if (isValidMove(board, j)) {
                            if (player == robotPlayer) {
                                inputSet.add(board);
//                            System.out.println("Sytuacja na planszy:");
//                            displayBoard(board);
                                double[] convertTable = new double[9];
                                convertTable[j] = player;
                                outputSet.add(convertTable);
//                            System.out.println("Ruch robota:");
//                            displayBoard(convertTable);
                            }
                            board[j] = player;
                            break;
                        }
                    }
                    player = -player;
                } while (!checkWin(board, 1) && !checkWin(board, -1) && !isBoardFull(board));
                if (checkWin(board, robotPlayer) || isBoardFull(board)) {
                    if (checkWin(board, robotPlayer)) {
                        System.out.println("Wygrał robot! Dodaje dane do sieci");
                    } else {
                        System.out.println("Remis! Dodaje dane do sieci");
                    }
                    System.out.println("Wygrał robot! Dodaje dane do sieci");
                    BasicMLDataSet trainingSet = new BasicMLDataSet(convertListToArray(inputSet), convertListToArray(outputSet));
                    final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
                    int epoch = 1;
                    do {
                        train.iteration();
                        System.out.println("Epoka #" + epoch + ", Błąd: " + train.getError());
                        epoch++;
                    } while (train.getError() > 0.01);
                }
                if (checkWin(board, -robotPlayer)) {
                    System.out.println("Wygrał gracz! Nie dodaje danych do sieci");
                }
            }
        }
        System.out.println("Trenowanie zakończone!");
    }

    // Metoda do gry
    public static void playGame(BasicNetwork network) {
        Scanner scanner = new Scanner(System.in);
        double[] board = new double[9]; // Początkowy stan planszy

        while (true) {
            int move;
            // Gracz wprowadza ruch
            System.out.println("Podaj współrzędne ruchu (0-8): ");
            do {
                move = scanner.nextInt();
                if (move < 0 || move > 8) {
                    System.out.println("Nieprawidłowy ruch. Podaj współrzędne ruchu (0-8): ");
                } else if (!isValidMove(board, move)) {
                    System.out.println("To pole jest już zajęte. Podaj współrzędne ruchu (0-8): ");
                }
            } while (!isValidMove(board, move)); // Sprawdzanie, czy ruch jest poprawny (pole jest puste
            board[move] = 1; // Zakładamy, że gracz jest reprezentowany przez 1

            // Wyświetlanie planszy
            displayBoard(board);

            // Sprawdzanie, czy gracz wygrał
            if (checkWin(board, 1)) {
                System.out.println("Wygrałeś!");
                break;
            }

            // Sieć neuronowa oblicza ruch
            MLData inputMLData = new BasicMLData(board);
            MLData outputMLData = network.compute(inputMLData);
            int networkMove = getBestMove(outputMLData.getData());
            board[networkMove] = -1; // Zakładamy, że sieć neuronowa jest reprezentowana przez -1

            // Wyświetlanie planszy
            displayBoard(board);

            // Sprawdzanie, czy sieć neuronowa wygrała
            if (checkWin(board, -1)) {
                System.out.println("Sieć neuronowa wygrała!");
                break;
            }
        }

        scanner.close();
    }

    private static int getBestMove(double[] data) {
        int bestMove = 0;
        double bestValue = Double.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > bestValue) {
                bestValue = data[i];
                bestMove = i;
            }
        }
        return bestMove;
    }

    private static boolean isBoardFull(double[] board) {
        for (double v : board) {
            if (v == 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkWin(double[] board, int i) {
        // Sprawdzanie wierszy
        for (int j = 0; j < 3; j++) {
            if (board[j] == i && board[j + 3] == i && board[j + 6] == i) {
                return true;
            }
        }

        // Sprawdzanie kolumn
        for (int j = 0; j < 9; j += 3) {
            if (board[j] == i && board[j + 1] == i && board[j + 2] == i) {
                return true;
            }
        }

        // Sprawdzanie przekątnych
        if (board[0] == i && board[4] == i && board[8] == i) {
            return true;
        }
        return board[2] == i && board[4] == i && board[6] == i;
    }

    public static void main(String[] args) {

        DataIO dataIO = new DataIO();
        double[][] input;
        double[][] output;

//        // Generowanie i zapis danych treningowych
//        dataIO.generateDataIO();
//        dataIO.saveOutputData("nOutput.dat");
//        dataIO.saveInputData("nInput.dat");

        // Tworzenie sieci neuronowej
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 9)); // 9 wejść (3x3 plansza)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 18)); // Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        input = dataIO.loadInputData("nInput.dat");
        output = dataIO.loadOutputData("nOutput.dat");

        // Przygotowanie danych treningowych
        BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoka #" + epoch + ", Błąd: " + train.getError());
            epoch++;
        } while (train.getError() > 0.01);

        // Trenowanie sieci
        trainingNetwork(network, -1);

        // Testowanie sieci
        MLData inputMLData = new BasicMLData(input[0]);
        MLData outputMLData = network.compute(inputMLData);
        System.out.println("Wynik: " + outputMLData.getData(4)); // Przykładowe pole na planszy

        // Wyświetlanie planszy
        displayBoard(input[0]);

        // Rozpoczęcie gry
        playGame(network);

        Encog.getInstance().shutdown();
    }
}
