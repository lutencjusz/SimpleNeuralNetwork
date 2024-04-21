import model.BoardElement;
import model.DataIO;
import model.DataModel;
import me.tongfei.progressbar.ProgressBar;
import model.DataModelGpt;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import util.CheckStatusGame;
import util.HeuristicStrategy;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Klasa reprezentująca grę w kółko i krzyżyk z wykorzystaniem sieci neuronowej oraz algorytmu heurystycznego.
 */
public class TicTacToeNeuralNetwork {

    static final int TIME_OF_TRAINING_IN_MINUTES = 10;
    static final BoardElement ROBOT_PAYER = BoardElement.CIRCLE;

    static final String SYSTEM_MESSAGE = "Oprzyj rozwiązanie na modelu treningowym fine-tuningu GPT-3.0";
    static List<DataModel> dataModel = new ArrayList<>();
    static List<DataModelGpt> dataModelGptList = new ArrayList<>();
    static List<double[]> finalInputSet = new ArrayList<>();
    static List<double[]> finalOutputSet = new ArrayList<>();
    static final double VALUE = -1;
    static final double TRAINING_WEIGHT = 1;
    static HeuristicStrategy heuristicStrategy = new HeuristicStrategy();


    // Trenowanie sieci neuronowej
    public static void trainingNetwork(BasicNetwork network, int robotPlayer) {
        int p1 = 4;
        for (int p0 = 0; p0 < 9; p0++) {
            if (p0 == 4) p1 = 0;
            for (int p2 = 0; p2 < 9; p2++) {
                int player = ROBOT_PAYER.getValue();
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
                outputSet.add(CheckStatusGame.convertNumberToArray(p1, TRAINING_WEIGHT));
                board[p1] = robotPlayer;
                board[p2] = -robotPlayer;
                do {
                    int j = heuristicStrategy.getBestMove(board, BoardElement.getBoardElement(robotPlayer), false, false);
                    if (CheckStatusGame.isValidMove(board, j)) {
                        if (player == robotPlayer) {
                            inputSet.add(board.clone());
//                            System.out.println("Sytuacja na planszy:");
//                            displayBoard(board);
                            outputSet.add(CheckStatusGame.convertNumberToArray(j, TRAINING_WEIGHT));
                        }
                        board[j] = player;
//                        System.out.println("Ruch robota: " + (robotPlayer == player ? "robota" : "gracza"));
//                        displayBoard(board);
                    }
                    player = -player;
                } while (!CheckStatusGame.checkWin(board, 1) && !CheckStatusGame.checkWin(board, -1) && !CheckStatusGame.isBoardFull(board));
                if (CheckStatusGame.checkWin(board, robotPlayer) || CheckStatusGame.isBoardFull(board)) {
                    if (CheckStatusGame.checkWin(board, robotPlayer)) {
                        System.out.println("Wygrał robot! Dodaje dane do sieci, epoka#" + p0);
                    } else {
                        System.out.println("Remis! Dodaje dane do sieci, epoka#" + p0);
                    }
                    // Dodanie danych z gry do danych testowych
                    finalInputSet.addAll(inputSet);
                    finalOutputSet.addAll(outputSet);

                }
                if (CheckStatusGame.checkWin(board, -robotPlayer)) {
                    System.out.println("Wygrał gracz! Nie dodaje danych do sieci");
                }
            }
        }
        System.out.println("Przygotowanie danych zakończone!");
        convertArraysToDataModel(SYSTEM_MESSAGE, CheckStatusGame.convertListToArray(finalInputSet), CheckStatusGame.convertListToArray(finalOutputSet));
        System.out.println("Konwersja do JSON zakończona!");

    }

    public static void convertArraysToDataModel(String systemMessage, double[][] input, double[][] output) {
        DataModel[] messagesStructure = new DataModel[3];
        for (int i = 0; i < input.length; i++) {
            messagesStructure[0] = new DataModel("system", systemMessage);
            messagesStructure[1] = new DataModel("user", input[i]);
            messagesStructure[2] = new DataModel("assistant", output[i]);
            dataModelGptList.add(new DataModelGpt(messagesStructure));
        }
    }

    // Metoda do gry
    public static void playGameWithPlayer(BasicNetwork network) {
        int player = BoardElement.CROSS.getValue();
        Scanner scanner = new Scanner(System.in);
        double[] board = new double[9]; // Początkowy stan planszy
        CheckStatusGame.displayBoard(board);
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
            if (player == -1) {
                finalInputSet.add(board.clone());
                finalOutputSet.add(CheckStatusGame.convertNumberToArray(move, VALUE));
            }

            board[move] = player; // Zakładamy, że gracz jest reprezentowany przez 1

            // Wyświetlanie planszy
            CheckStatusGame.displayBoard(board);

            // Sprawdzanie, czy gracz wygrał
            if (CheckStatusGame.checkWin(board, player) || CheckStatusGame.isBoardFull(board)) {
                System.out.println("Wygrałeś lub remis! Koniec gry.");
                System.out.println("Czy zapisać dane z gry do sieci? (t/n)");
                String saveData = scanner.next();
                if (saveData.equals("t")) {
                    convertArraysToDataModel(SYSTEM_MESSAGE, CheckStatusGame.convertListToArray(finalInputSet), CheckStatusGame.convertListToArray(finalOutputSet));
                    DataIO.addDataToFileInJSON("dataWin.json", dataModelGptList);
                }
                break;
            }
            CheckStatusGame.displayBoard(board);
            player = -player;
        }
        scanner.close();

    }

    public static void playGameWithAI(BasicNetwork network) {
        int player = BoardElement.CROSS.getValue();
        Scanner scanner = new Scanner(System.in);
        double[] board = new double[9]; // Początkowy stan planszy
        CheckStatusGame.displayBoard(board);
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

            board[move] = player; // Zakładamy, że gracz jest reprezentowany przez 1

            // Wyświetlanie planszy
            CheckStatusGame.displayBoard(board);

            // Sprawdzanie, czy gracz wygrał
            if (CheckStatusGame.checkWin(board, player)) {
                System.out.println("Wygrałeś! Koniec gry.");
                break;
            }
            if (CheckStatusGame.isBoardFull(board)) {
                System.out.println("Remis! Koniec gry.");
                break;
            }

            // Sieć neuronowa oblicza ruch
            MLData inputMLData = new BasicMLData(board);
            MLData outputMLData = network.compute(inputMLData);
            int networkMove = getBestMove(outputMLData.getData());
            int heuristicMove = heuristicStrategy.getBestMove(board, false, false);
            if (heuristicMove != networkMove) {
                System.out.println("Ruch sieci neuronowej (" + networkMove + ") różni się od ruchu heurystycznego (" + heuristicMove + "). Wybrano ruch sieci.");
            }
            if (!CheckStatusGame.isValidMove(board, networkMove)) {
                heuristicMove = heuristicStrategy.getBestMove(board, true, false);
                System.out.println("Nieprawidłowy ruch sieci neuronowej (" + networkMove + "). Wybrano ruch heurystyczny: " + heuristicMove);
                networkMove = heuristicMove;
            }

            // Algorytm heurystyczny oblicza ruch
//            int networkMove = heuristicStrategy.getBestMove(board);

            finalInputSet.add(board.clone());
            board[networkMove] = BoardElement.CIRCLE.getValue();
            ; // Zakładamy, że sieć neuronowa jest reprezentowana przez -1
            finalOutputSet.add(CheckStatusGame.convertNumberToArray(move, VALUE));

            // Wyświetlanie planszy
            CheckStatusGame.displayBoard(board);

            // Sprawdzanie, czy sieć neuronowa wygrała
            if (CheckStatusGame.checkWin(board, BoardElement.CIRCLE.getValue())) {
                System.out.println("Sieć neuronowa wygrała!");
                System.out.println("Czy zapisać dane z gry do sieci? (t/n)");
                String saveData = scanner.next();
                if (saveData.equals("t")) {
                    convertArraysToDataModel(SYSTEM_MESSAGE, CheckStatusGame.convertListToArray(finalInputSet), CheckStatusGame.convertListToArray(finalOutputSet));
                    DataIO.addDataToFileInJSON("dataWinGpt.json", dataModelGptList);
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
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 144)); // Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronamiukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronamiukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami// Warstwa ukryta z 18 neuronami
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9)); // 9 wyjść (3x3 plansza)
        network.getStructure().finalizeStructure();
        network.reset();

        // Zbieranie danych do trenowania sieci
        trainingNetwork(network, ROBOT_PAYER.getValue());

        // Zapisanie danych do pliku
//        dataIO.saveDataToFileInJSON("dataWin.json", dataModel);
        dataIO.saveDataToFileInJSON("dataTrainingGpt.json", dataModelGptList);

//        input = dataIO.loadInputData("nInput.dat");
//        output = dataIO.loadOutputData("nOutput.dat");
//
        // Uczenie sieci
        BasicMLDataSet trainingSet = new BasicMLDataSet(CheckStatusGame.convertListToArray(finalInputSet), CheckStatusGame.convertListToArray(finalOutputSet));
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
