
package view;
import model.*;


public class GameLauncher {
    private static void clearGameConsole(){
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }
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
        Player currentPlayer = p1;


        while (!board.isBoardFull()) {
            clearGameConsole();
            int move[] = currentPlayer.decideMove(board);
            board.executeMove(move[0], move[1], currentPlayer.getColor());
            System.out.println(currentPlayer.getName() + " played at (" + move[0] + ", " + move[1] + ")");
            
            board.computeWinningPath();
            System.out.println(board);
                         
            Color winner = board.getWinner();
            if(winner != Color.EMPTY){
                System.out.println("le gagnant est " + winner);
                break;
            }
            currentPlayer = (currentPlayer == p1) ? p2 : p1;

            try{
                Thread.sleep(20);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }
}