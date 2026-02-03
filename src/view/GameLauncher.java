
package view;
import model.*;


public class GameLauncher {
    public static void main(String[] args) {
        System.out.println("Welcome to HexGame");

        Board board = new Board(14);
        /*
         * board.setCell(0, 0, Color.Colors.RED);
         * board.setCell(0, 1, Color.Colors.BLUE);
         * board.setCell(1, 0, Color.Colors.RED);
         * board.setCell(1, 1, Color.Colors.EMPTY);
         * board.setCell(7, 7, Color.Colors.BLUE);
         */
        MoveStrategy RandomAIStrategy = new RandomAIStrategy();
        Player p1 = new Player("Blue Ai", Color.BLUE, RandomAIStrategy);
        Player p2 = new Player("Red Ai", Color.RED, RandomAIStrategy);
        // public static final String ANSI_RESET = "";
        // public static final String ANSI_GREEN = "";
        Player currentPlayer = p1;

        while (!board.isBoardFull()) {
            int move[] = currentPlayer.decideMove(board);
            board.executeMove(move[0], move[1], currentPlayer.getColor());
            System.out.println(currentPlayer.getName() + " played at (" + move[0] + ", " + move[1] + ")");
            System.out.println(board);
            currentPlayer = (currentPlayer == p1) ? p2 : p1;
            

            System.out.println("\u001B[1;31m" + "⬢" + "\u001B[1;34m" + "⬢");
        }

    }
}