
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
        MoveStrategy mctsStrat1 = new MCTSStrategy(10, Color.BLUE);
        MoveStrategy mctsStrat2 = new MCTSStrategy(50, Color.RED);

        Player p1 = new Player("Blue MCTS", Color.BLUE, mctsStrat1);
        Player p2 = new Player("Red MCTS", Color.RED, mctsStrat2);
        Player currentPlayer = p1;

        Game game = new Game(board.getSize(), p1, p2);
        int firstMove[] = null; // pour stocker le premier coup joué par le premier joueur, important pour le swap
        boolean swapedHandled = false; // pour s'assurer que le swap est traité une seule fois

        while (!board.isBoardFull()) {
            clearGameConsole();

            if(!swapedHandled && currentPlayer == p2 && firstMove != null){
                boolean doSwap = p2.getStrategy().decideSwap(board, firstMove);

                if(doSwap) {
                    Color p2AvantSwap = p2.getColor(); // Stocke la couleur de p2 avant le swap pour l'affichage
                    game.swapPlayers();
                    // La cellule déjà posée change de couleur (elle appartient maintenant à p2)
                    // board.setCell(firstMove[0], firstMove[1], p2AvantSwap);

                    System.out.println(p2.getName() + " a utilisé la règle du SWAP !");
                    System.out.println("Les couleurs ont été échangées.");
                    swapedHandled = true;

                    // Après le swap, c'est au joueur 1 de jouer (les rôles ont été échangés)
                    currentPlayer = p1;
                    
                    continue;
                }
                swapedHandled = true; // Même si le swap n'est pas effectué, on ne demande plus à p2 après son premier coup
            
            }

            // coup normale comme avant
            int move[] = currentPlayer.decideMove(board);
            board.executeMove(move[0], move[1], currentPlayer.getColor());
            
            if(firstMove == null){
                firstMove = move; // Enregistre le premier coup joué pour que p2 puisse décider du swap
            }
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