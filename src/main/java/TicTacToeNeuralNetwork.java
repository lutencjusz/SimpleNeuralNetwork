import Model.DataIO;
import Model.DataModel;
import me.tongfei.progressbar.ProgressBar;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class TicTacToeNeuralNetwork {
    static List<DataModel> dataModel = new ArrayList<>();
    static List<double[]> finalInputSet = new ArrayList<>();
    static List<double[]> finalOutputSet = new ArrayList<>();
    static final double VALUE = -1;
    static final double TRAINING_WEIGHT = 1;
    static HeuristicStrategy heuristicStrategy = new HeuristicStrategy();

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
        int p1 = 4;
        for (int p0 = 0; p0 < 9; p0++) {
            if (p0 == 4) p1 = 0;
            for (int p2 = 0; p2 < 9; p2++) {
                int player = -1;
                // kontrola czy nie ma powtórzeń
                Set<Integer> sumControl = new HashSet<>();
                sumControl.add(p0);
                sumControl.add(p1);
                sumControl.add(p2);
                if (sumControl.size() < 3) {
                    continue;
                }
                List<double[]> inputSet = new ArrayList<>();
                List<double[]> outputSet = new ArrayList<>();
                double[] board = new double[9];
                // Ustawienie początkowego stanu planszy
                board[p0] = -robotPlayer;
                inputSet.add(board.clone());
                outputSet.add(convertNumberToArray(p1, TRAINING_WEIGHT));
                board[p1] = robotPlayer;
                board[p2] = -robotPlayer;
                do {
                    int j = heuristicStrategy.getBestMove(board, robotPlayer == player ? HeuristicStrategy.BoardElements.CIRCLE : HeuristicStrategy.BoardElements.CROSS, true);
                    if (isValidMove(board, j)) {
                        if (player == robotPlayer) {
                            inputSet.add(board.clone());
//                            System.out.println("Sytuacja na planszy:");
//                            displayBoard(board);
                            outputSet.add(convertNumberToArray(j, TRAINING_WEIGHT));
                        }
                        board[j] = player;
//                        System.out.println("Ruch robota: " + (robotPlayer == player ? "robota" : "gracza"));
//                        displayBoard(board);
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
        System.out.println("Przygotowanie danych zakończone!");
        convertArraysToDataModel(convertListToArray(finalInputSet), convertListToArray(finalOutputSet));
        System.out.println("Konwersja do JSON zakończona!");

    }

    public static void convertArraysToDataModel(double[][] input, double[][] output) {
        for (int i = 0; i < input.length; i++) {
            DataModel newModel = new DataModel(input[i], output[i]);
            dataModel.add(newModel);
        }
    }

    // Metoda do gry
    public static void playGameWithPlayer(BasicNetwork network) {
        int player = 1;
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
            if (player == -1) {
                finalInputSet.add(board.clone());
                finalOutputSet.add(convertNumberToArray(move, VALUE));
            }

            board[move] = player; // Zakładamy, że gracz jest reprezentowany przez 1

            // Wyświetlanie planszy
            displayBoard(board);

            // Sprawdzanie, czy gracz wygrał
            if (checkWin(board, player) || isBoardFull(board)) {
                System.out.println("Wygrałeś lub remis! Koniec gry.");
                System.out.println("Czy zapisać dane z gry do sieci? (t/n)");
                String saveData = scanner.next();
                if (saveData.equals("t")) {
                    convertArraysToDataModel(convertListToArray(finalInputSet), convertListToArray(finalOutputSet));
                    DataIO.addDataToFileInJSON("dataWin.json", dataModel);
                }
                break;
            }
            displayBoard(board);
            player = -player;
        }
        scanner.close();

    }

    public static void playGameWithAI(BasicNetwork network) {
        int player = 1;
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

            board[move] = player; // Zakładamy, że gracz jest reprezentowany przez 1

            // Wyświetlanie planszy
            displayBoard(board);

            // Sprawdzanie, czy gracz wygrał
            if (checkWin(board, player)) {
                System.out.println("Wygrałeś! Koniec gry.");
                break;
            }
            if (isBoardFull(board)) {
                System.out.println("Remis! Koniec gry.");
                break;
            }

            // Sieć neuronowa oblicza ruch
            MLData inputMLData = new BasicMLData(board);
            MLData outputMLData = network.compute(inputMLData);
            int networkMove = getBestMove(outputMLData.getData());
            int heuristicMove = heuristicStrategy.getBestMove(board, false);
            if (heuristicMove != networkMove) {
                System.out.println("Ruch sieci neuronowej (" + networkMove + ") różni się od ruchu heurystycznego (" + heuristicMove + "). Wybrano ruch sieci.");
            }
            if (!isValidMove(board, networkMove)) {
                heuristicMove = heuristicStrategy.getBestMove(board, true);
                System.out.println("Nieprawidłowy ruch sieci neuronowej (" + networkMove + "). Wybrano ruch heurystyczny: " + heuristicMove);
                networkMove = heuristicMove;
            }

            // Algorytm heurystyczny oblicza ruch
//            int networkMove = heuristicStrategy.getBestMove(board);

            finalInputSet.add(board.clone());
            board[networkMove] = -1; // Zakładamy, że sieć neuronowa jest reprezentowana przez -1
            finalOutputSet.add(convertNumberToArray(move, VALUE));

            // Wyświetlanie planszy
            displayBoard(board);

            // Sprawdzanie, czy sieć neuronowa wygrała
            if (checkWin(board, -1)) {
                System.out.println("Sieć neuronowa wygrała!");
                System.out.println("Czy zapisać dane z gry do sieci? (t/n)");
                String saveData = scanner.next();
                if (saveData.equals("t")) {
                    convertArraysToDataModel(convertListToArray(finalInputSet), convertListToArray(finalOutputSet));
                    DataIO.addDataToFileInJSON("dataWin.json", dataModel);
                }
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

        final int TIME_OF_TRAINING_IN_MINUTES = 10;

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
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 144)); // Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronamiukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronamiukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        // Zbieranie danych do trenowania sieci
        trainingNetwork(network, -1);

        // Zapisanie danych do pliku
//        dataIO.saveDataToFileInJSON("dataWin.json", dataModel);
        dataIO.saveDataToFileInJSON("dataTraining.json", dataModel);

//        input = dataIO.loadInputData("nInput.dat");
//        output = dataIO.loadOutputData("nOutput.dat");
//
        // Uczenie sieci
        BasicMLDataSet trainingSet = new BasicMLDataSet(convertListToArray(finalInputSet), convertListToArray(finalOutputSet));
//        BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(TIME_OF_TRAINING_IN_MINUTES);
        try (ProgressBar pb = new ProgressBar("Trening", 100)) {
            do {
                train.iteration();
                Thread.sleep(10);
                if (epoch++ % 100 == 0) {
                    Double a = train.getError() * 100;
                    BigDecimal bd = new BigDecimal(Double.toString(train.getError()));
                    bd = bd.setScale(4, RoundingMode.HALF_UP);
                    pb.stepTo(a.longValue());
                    pb.setExtraMessage("E: " + epoch / 1000 + "K, " + bd.doubleValue() + " minęło: " + (LocalDateTime.now().getMinute() - start.getMinute()) + " minut.");
                }
            } while (train.getError() > 0.01 && LocalDateTime.now().isBefore(end));
            if (LocalDateTime.now().isAfter(end)) {
                System.out.println("Przekroczono czas treningu, który wynosił " + TIME_OF_TRAINING_IN_MINUTES + " minut.");
            }
            train.finishTraining();
        }

        // Zapisanie sieci
        EncogDirectoryPersistence.saveObject(new File("tictactoe.eg"), network);


        // Testowanie sieci
//        MLData inputMLData = new BasicMLData(input[0]);
//        MLData outputMLData = network.compute(inputMLData);
//        System.out.println("Wynik: " + outputMLData.getData(4)); // Przykładowe pole na planszy

        // Rozpoczęcie gry
        playGameWithAI(network);
        Encog.getInstance().shutdown();

//        // użycie algorytmu heurystycznego
//        double[] board = new double[9];
//        int player = 1;
//        int move;
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.println("Podaj współrzędne ruchu (0-8): ");
//            do {
//                move = scanner.nextInt();
//                if (move < 0 || move > 8) {
//                    System.out.println("Nieprawidłowy ruch. Podaj współrzędne ruchu (0-8): ");
//                } else if (board[move] != 0) {
//                    System.out.println("To pole jest już zajęte. Podaj współrzędne ruchu (0-8): ");
//                }
//            } while (board[move] != 0);
//            board[move] = player;
//            // Wyświetlanie planszy
//            displayBoard(board);
//            if (checkWin(board, player)) {
//                System.out.println("Wygrałeś!");
//                break;
//            }
//            if (isBoardFull(board)) {
//                System.out.println("Remis!");
//                break;
//            }
//            player = -player;
//            int bestMove = heuristicStrategy.getBestMove(board);
//            board[bestMove] = player;
//            // Wyświetlanie planszy
//            displayBoard(board);
//            if (checkWin(board, player)) {
//                System.out.println("Wygrał komputer!");
//                break;
//            }
//            if (isBoardFull(board)) {
//                System.out.println("Remis!");
//                break;
//            }
//            player = -player;
//        }
    }
}
