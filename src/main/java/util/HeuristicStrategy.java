package util;

import lombok.Getter;
import model.BoardElement;

import java.util.Arrays;

@Getter
public class HeuristicStrategy {

    private final Weight[] weightsForCross = new Weight[]{
            new Weight(2, 0, 40),
            new Weight(0, 2, 20),
            new Weight(1, 0, 10),
            new Weight(0, 1, 5),
            new Weight(0, 0, 1),
            new Weight(1, 1, 0)
    };

    private final Weight[] weightsForCircle = new Weight[]{
            new Weight(2, 0, 20),
            new Weight(0, 2, 40),
            new Weight(1, 0, 5),
            new Weight(0, 1, 10),
            new Weight(0, 0, 1),
            new Weight(1, 1, 0)
    };

    private int[][] boardLines = new int[][]{
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
    };

    private int[][] boardColumns = new int[][]{
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
    };

    private int[][] boardDiagonals = new int[][]{
            {0, 4, 8},
            {2, 4, 6},
    };

    private final PositionLocation[] positionsLocation = new PositionLocation[]{
            new PositionLocation(0, 0, 0),
            new PositionLocation(0, 1, -1),
            new PositionLocation(0, 2, 1),
            new PositionLocation(1, 0, -1),
            new PositionLocation(-1, -1, -1),
            new PositionLocation(1, 2, -1),
            new PositionLocation(2, 0, 1),
            new PositionLocation(2, 1, -1),
            new PositionLocation(2, 2, 0)
    };

    private

    @Getter
    class PositionLocation {
        int boardLine;
        int boardColumn;
        int boardDiagonal;

        public PositionLocation(int boardLine, int boardColumn, int boardDiagonal) {
            this.boardLine = boardLine;
            this.boardColumn = boardColumn;
            this.boardDiagonal = boardDiagonal;
        }
    }

    @Getter
    class Weight {
        private final int crossCount;
        private final int circleCount;
        private final int weight;

        public Weight(int crossCount, int circleCount, int weight) {
            this.crossCount = crossCount;
            this.circleCount = circleCount;
            this.weight = weight;
        }
    }

    public HeuristicStrategy() {
    }

    private int getWeight(int crossCount, int circleCount, BoardElement element) {
        Weight[] weights = element == BoardElement.CROSS ? weightsForCross : weightsForCircle;
        for (Weight weight : weights) {
            if (weight.getCrossCount() == crossCount && weight.getCircleCount() == circleCount) {
                return weight.getWeight();
            }
        }
        return 0;
    }

    private int getHeuristicValue(double[] board, int position, BoardElement element) {
        // Wyjątki strategii heurystycznej
        if (position == 4) return 19; // Centrum ma najwyższą wartość

        int lineLocation = positionsLocation[position].boardLine;
        int columnLocation = positionsLocation[position].boardColumn;
        int diagonalLocation = positionsLocation[position].boardDiagonal;

        int lineWeight = calculateWeightForPosition(board, boardLines[lineLocation], element);
        int columnWeight = calculateWeightForPosition(board, boardColumns[columnLocation], element);
        int diagonalWeight = diagonalLocation != -1 ? calculateWeightForPosition(board, boardDiagonals[diagonalLocation], element) : 0;

        return lineWeight + columnWeight + diagonalWeight;
    }

    private int calculateWeightForPosition(double[] board, int[] positions, BoardElement element) {
        int crossCount = 0;
        int circleCount = 0;
        for (int position : positions) {
            if (board[position] == 1) {
                crossCount++;
            } else if (board[position] == -1) {
                circleCount++;
            }
        }
        return getWeight(crossCount, circleCount, element);
    }


    public int getBestMove(double[] board, BoardElement element, boolean isLogging) {
        int bestMove = -1;
        int bestValue = -1;

        // wyjątki strategii heurystycznej
        if (Arrays.equals(board, new double[]{1.0, 0.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, 1.0}) ||
                Arrays.equals(board, new double[]{0.0, 0.0, 1.0, 0.0, -1.0, 0.0, 1.0, 0.0, 0.0})) {
            if (isLogging) System.out.println("Nastąpił wyjątek strategii heurystycznej: Krzyżyki na rogach i kółko w środku. Wybieram pozycję 1");
            return 1;
        }

        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                int value = getHeuristicValue(board, i, element);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = i;
                }
                if (isLogging) System.out.println("Pozycja: " + i + " Wartość: " + value);
            }
        }
        if (isLogging) System.out.println("Najlepsza pozycja: " + bestMove + " Wartość: " + bestValue);
        return bestMove;
    }

    public int getBestMove(double[] board, boolean isLogging) {
        return getBestMove(board, BoardElement.CIRCLE, isLogging);
    }
}

