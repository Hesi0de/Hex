package analysis;

import model.*;

public class GameLauncher2 {

    public static void main(String[] args) {

        if (args.length != 8) {
            System.out.println("Usage:");
            System.out.println("size games ratio budgetBlue budgetRed algoBlue algoRed startColor");
            return;
        }

        int size = Integer.parseInt(args[0]);
        int games = Integer.parseInt(args[1]);
        String ratio = args[2];
        int budgetBlue = Integer.parseInt(args[3]);
        int budgetRed = Integer.parseInt(args[4]);
        String algoBlue = args[5];
        String algoRed = args[6];
        String startColor = args[7].toUpperCase();
        int blueWins = 0;

        int redWins = 0;

        for (int i = 0; i < games; i++) {

            Board board = new Board(size);

            MoveStrategy stratBlue = createStrategy(algoBlue, budgetBlue, Color.BLUE);
            MoveStrategy stratRed = createStrategy(algoRed, budgetRed, Color.RED);

            Player blue = new Player("BLUE", Color.BLUE, stratBlue);
            Player red = new Player("RED", Color.RED, stratRed);

            Player current = startColor.equals("BLUE") ? blue : red;

            while (board.getWinner() == Color.EMPTY && !board.isBoardFull()) {
                int[] move = current.decideMove(board);
                board.executeMove(move[0], move[1], current.getColor());

                current = (current == red) ? blue : red;
            }

            if (board.getWinner() == Color.RED) {
                redWins++;
            } else if (board.getWinner() == Color.BLUE) {
                blueWins++;
            }
        }

        if (blueWins > redWins) {
            System.out.println(
                games + "," +
                size + "," +
                ratio + "," +
                startColor + "," +
                "BLUE," +
                blueWins + "," +
                budgetBlue + "," +
                algoBlue + "," +
                redWins + "," +
                budgetRed + "," +
                algoRed
            );

        } else {
            System.out.println(
                games + "," +
                size + "," +
                ratio + "," +
                startColor + "," +
                "RED," +
                redWins + "," +
                budgetRed + "," +
                algoRed + "," +
                blueWins + "," +
                budgetBlue + "," +
                algoBlue
            );
        }
    }

    private static MoveStrategy createStrategy(String algo, int budget, Color color) {
        switch (algo.toLowerCase()) {
            case "mcts":
                return new MCTSStrategy(budget, color);
            case "rave":
                return new RAVEStrategy(budget, color);
            case "random":
                return new RandomAIStrategy();
            default:
                throw new IllegalArgumentException("Unknown algo: " + algo);
        }
    }
}