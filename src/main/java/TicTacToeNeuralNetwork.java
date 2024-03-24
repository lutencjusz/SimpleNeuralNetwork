import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.Scanner;

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

    // Metoda do gry
    public static void playGame(BasicNetwork network) {
        Scanner scanner = new Scanner(System.in);
        double[] board = new double[9]; // Początkowy stan planszy

        while (true) {
            // Gracz wprowadza ruch
            System.out.println("Podaj współrzędne ruchu (0-8): ");
            int move = scanner.nextInt();
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
        // Tworzenie sieci neuronowej
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 9)); // 9 wejść (3x3 plansza)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 18)); // Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        // Dane treningowe (plansze i oczekiwane wyjścia)
        double[][] input = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0}, // Pusta plansza
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Krzyżyk w lewym górnym rogu
                {0, 1, 0, 0, 0, 0, 0, 0, 0}, // Krzyżyk na górze na środku
                {0, 0, 1, 0, 0, 0, 0, 0, 0}, // Krzyżyk w prawym górnym rogu
                {0, 0, 0, 1, 0, 0, 0, 0, 0}, // Krzyżyk w lewym środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Krzyżyk na środku
                {0, 0, 0, 0, 0, 1, 0, 0, 0}, // Krzyżyk w prawym środku
                {0, 0, 0, 0, 0, 0, 1, 0, 0}, // Krzyżyk w lewym dolnym rogu
                {0, 0, 0, 0, 0, 0, 0, 1, 0}, // Krzyżyk na dole na środku
                {0, 0, 0, 0, 0, 0, 0, 0, 1}, // Krzyżyk w prawym dolnym rogu
        };

        final ResilientPropagation train = getResilientPropagation(input, network);
        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoka #" + epoch + ", Błąd: " + train.getError());
            epoch++;
        } while (train.getError() > 0.01);

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

    private static ResilientPropagation getResilientPropagation(double[][] input, BasicNetwork network) {
        double[][] output = {
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {1, 0, 0, 0, 0, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
                {0, 0, 0, 0, 1, 0, 0, 0, 0}, // Oczekiwane wyjście dla krzyżyka na środku
        };

        // Przygotowanie danych treningowych
        BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);

        // Trenowanie sieci
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        return train;
    }
}
