import lombok.Getter;

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

    private enum BoardElements {
        CROSS,
        CIRCLE
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

    private int getWeight(int crossCount, int circleCount, BoardElements element) {
        Weight[] weights = element == BoardElements.CROSS ? weightsForCross : weightsForCircle;
        for (Weight weight : weights) {
            if (weight.getCrossCount() == crossCount && weight.getCircleCount() == circleCount) {
                return weight.getWeight();
            }
        }
        return 0;
    }

    private int getHeuristicValue(double[] board, int position, BoardElements element) {
        if (position == 4) return 100;
        int lineLocation = positionsLocation[position].boardLine;
        int columnLocation = positionsLocation[position].boardColumn;
        int diagonalLocation = positionsLocation[position].boardDiagonal;

        int crossCount = 0;
        int circleCount = 0;
        int lineWeight = 0;
        int columnWeight = 0;
        int diagonalWeight = 0;

        for (int linePosition : boardLines[lineLocation]) {
            if (board[linePosition] == 1) {
                crossCount++;
            } else if (board[linePosition] == -1) {
                circleCount++;
            }
        }
        lineWeight = getWeight(crossCount, circleCount, element);

        crossCount = 0;
        circleCount = 0;
        for (int columnPosition : boardColumns[columnLocation]) {
            if (board[columnPosition] == 1) {
                crossCount++;
            } else if (board[columnPosition] == -1) {
                circleCount++;
            }
        }
        columnWeight = getWeight(crossCount, circleCount, element);

        crossCount = 0;
        circleCount = 0;
        if (diagonalLocation != -1) {
            for (int diagonalPosition : boardDiagonals[diagonalLocation]) {
                if (board[diagonalPosition] == 1) {
                    crossCount++;
                } else if (board[diagonalPosition] == -1) {
                    circleCount++;
                }
            }
            diagonalWeight = getWeight(crossCount, circleCount, element);
        }
        return lineWeight + columnWeight + diagonalWeight;
    }

    private int getBestMove(double[] board, BoardElements element) {
        int bestMove = -1;
        int bestValue = -1;
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                int value = getHeuristicValue(board, i, element);
                if (value > bestValue) {
                    bestValue = value;
                    bestMove = i;
                }
                System.out.println("Pozycja: " + i + " Wartość: " + value);
            }
        }
        System.out.println("Najlepsza pozycja: " + bestMove + " Wartość: " + bestValue);
        return bestMove;
    }

    public int getBestMove(double[] board) {
        return getBestMove(board, BoardElements.CIRCLE);
    }
}

