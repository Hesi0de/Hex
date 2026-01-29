
package model;


public class GameLauncher {
    public static void main(String[] args) {
        System.out.println("Welcome to HexGame");

        Board board = new Board(14);
        /*board.setCell(0, 0, Color.Colors.RED);
        board.setCell(0, 1, Color.Colors.BLUE);
        board.setCell(1, 0, Color.Colors.RED);
        board.setCell(1, 1, Color.Colors.EMPTY);
        board.setCell(7, 7, Color.Colors.BLUE);*/
        
        Player p1 = new RandomAIPlayer("Blue AI", Color.BLUE);
        Player p2 = new RandomAIPlayer("Red AI", Color.RED);
        Player currentPlayer = p1;

        while(!board.isBoardFull()){
            int move[] = currentPlayer.chooseMove(board);
            board.executeMove(move[0], move[1], currentPlayer.getColor());
            System.out.println(currentPlayer.getName() + " played at (" + move[0] + ", " + move[1] + ")");
            System.out.println(board);
            currentPlayer = (currentPlayer == p1) ? p2 : p1;
        }

    }
}