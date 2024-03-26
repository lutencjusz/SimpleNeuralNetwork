import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import me.tongfei.progressbar.*;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class TicTacToeNeuralNetwork {

    static List<double[]> finalInputSet = new ArrayList<>();
    static List<double[]> finalOutputSet = new ArrayList<>();
    static final double VALUE = 0.9;

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

    private static double[] convertNumberToArray(int number, double value) {
        double[] array = new double[9];
        array[number] = value;
        return array;
    }

    // Metoda do trenowania sieci neuronowej
    public static void trainingNetwork(BasicNetwork network, int robotPlayer) {
        int p1 = 5;
        for (int p0 = 0; p0 < 9; p0++) {
            if (p0 == 5) p1 = 0;
            for (int p2 = 0; p2 < 9; p2++) {
                for (int p3 = 0; p3 < 9; p3++) {
                    for (int p4 = 0; p4 < 9; p4++) {
                        int player = -1;
                        // kontrola czy nie ma powtórzeń
                        Set<Integer> sumControl = new HashSet<>();
                        sumControl.add(p0);
                        sumControl.add(p1);
                        sumControl.add(p2);
                        sumControl.add(p3);
                        sumControl.add(p4);
                        if (sumControl.size() < 5) {
                            continue;
                        }
                        List<double[]> inputSet = new ArrayList<>();
                        List<double[]> outputSet = new ArrayList<>();
                        double[] board = new double[9];
                        // Ustawienie początkowego stanu planszy
                        board[p0] = -robotPlayer;
                        inputSet.add(board.clone());
                        outputSet.add(convertNumberToArray(p1, VALUE));
                        board[p1] = robotPlayer;
                        board[p2] = -robotPlayer;
                        inputSet.add(board.clone());
                        outputSet.add(convertNumberToArray(p3, VALUE));
                        board[p3] = robotPlayer;
                        board[p4] = -robotPlayer;
                        do {
                            for (int j = 0; j < 9; j++) {
                                if (isValidMove(board, j)) {
                                    if (player == robotPlayer) {
                                        inputSet.add(board.clone());
//                            System.out.println("Sytuacja na planszy:");
//                            displayBoard(board);
                                        outputSet.add(convertNumberToArray(j, VALUE));
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
                                System.out.println("Wygrał robot! Dodaje dane do sieci, epoka#" + p0);
                            } else {
                                System.out.println("Remis! Dodaje dane do sieci, epoka#" + p0);
                            }
                            // Dodanie danych z gry do danych testowych
                            finalInputSet.addAll(inputSet);
                            finalOutputSet.addAll(outputSet);

                        }
                        if (checkWin(board, -robotPlayer)) {
                            System.out.println("Wygrał gracz! Nie dodaje danych do sieci");
                        }
                    }
                }
            }
        }
        System.out.println("Trenowanie zakończone!");
    }

    // Metoda do gry
    public static void playGame(BasicNetwork network) {
        Scanner scanner = new Scanner(System.in);
        double[] board = new double[9]; // Początkowy stan planszy
        displayBoard(board);
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

    public static void main(String[] args) throws IOException, InterruptedException {

        DataIO dataIO = new DataIO();
        double[][] input = new double[0][9];
        double[][] output;

//        // Generowanie i zapis danych treningowych
//        dataIO.generateDataIO();
//        dataIO.saveOutputData("nOutput.dat");
//        dataIO.saveInputData("nInput.dat");

        // Tworzenie sieci neuronowej
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 9)); // 9 wejść (3x3 plansza)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 18)); // Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronamiukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        // Zbieranie danych do trenowania sieci
        trainingNetwork(network, -1);

//        input = dataIO.loadInputData("nInput.dat");
//        output = dataIO.loadOutputData("nOutput.dat");
//
        // Przygotowanie danych treningowych
        BasicMLDataSet trainingSet = new BasicMLDataSet(convertListToArray(finalInputSet), convertListToArray(finalOutputSet));
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(5);
        try (ProgressBar pb = new ProgressBar("Trening", 100)) {
            do {
                train.iteration();
                Thread.sleep(10);
                if (epoch++ % 100 == 0) {
                    Double a = train.getError() * 100;
                    BigDecimal bd = new BigDecimal(Double.toString(train.getError()));
                    bd = bd.setScale(4, RoundingMode.HALF_UP);
                    pb.stepTo(a.longValue());
                    pb.setExtraMessage("E: " + epoch/1000 + "K, " + bd.doubleValue());
                }
            } while (train.getError() > 0.01 && LocalDateTime.now().isBefore(end));
            train.finishTraining();
        }

        // Zapisanie sieci
        EncogDirectoryPersistence.saveObject(new File("tictactoe.eg"), network);


//        // Testowanie sieci
//        MLData inputMLData = new BasicMLData(input[0]);
//        MLData outputMLData = network.compute(inputMLData);
//        System.out.println("Wynik: " + outputMLData.getData(4)); // Przykładowe pole na planszy

        // Rozpoczęcie gry
        playGame(network);

        Encog.getInstance().shutdown();
    }
}
