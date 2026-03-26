
package view;

import java.util.Scanner;

import model.*;

/**
 * Classe principale pour lancer le jeu de Hex. Elle initialise le plateau, les joueurs et gère la boucle de jeu.
 */
public class GameLauncher {

    /**
     * nettoye la console avant chaque tour de jeu.
     */
    private static void clearGameConsole() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Point d'entrée du programme. Initialise le jeu et gère la boucle de jeu.
     */
    public static void main(String[] args) {
        System.out.println("Welcome to HexGame");

        Board board = new Board(11);

        Scanner scanner = new Scanner(System.in);

        // Joueur Bleu
        MoveStrategy stratBlue = chooseStrategy(scanner, Color.BLUE);

        // Joueur Rouge
        MoveStrategy stratRed = chooseStrategy(scanner, Color.RED);

        Player p1 = new Player("Blue Player", Color.BLUE, stratBlue);
        Player p2 = new Player("Red Player", Color.RED, stratRed);

        Player currentPlayer = p1;

        Game game = new Game(board.getSize(), p1, p2);
        int firstMove[] = null; // pour stocker le premier coup joué par le premier joueur, important pour le
                                // swap
        boolean swapedHandled = false; // pour s'assurer que le swap est traité une seule fois

        while (!board.isBoardFull()) {
            clearGameConsole();

            if (!swapedHandled && currentPlayer == p2 && firstMove != null) {
                boolean doSwap = p2.getStrategy().decideSwap(board, firstMove);

                if (doSwap) {
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
                swapedHandled = true; // Même si le swap n'est pas effectué, on ne demande plus à p2 après son premier
                                      // coup

            }

            // coup normale comme avant
            int move[] = currentPlayer.decideMove(board);
            board.executeMove(move[0], move[1], currentPlayer.getColor());

            if (firstMove == null) {
                firstMove = move; // Enregistre le premier coup joué pour que p2 puisse décider du swap
            }
            System.out.println(currentPlayer.getName() + " played at (" + move[0] + ", " + move[1] + ")");

            board.computeWinningPath();
            System.out.println(board);

            Color winner = board.getWinner();
            if (winner != Color.EMPTY) {
                System.out.println("le gagnant est " + winner);
                break;
            }
            currentPlayer = (currentPlayer == p1) ? p2 : p1;

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Affiche un menu pour choisir la stratégie d'un joueur .
     * @param scanner pour lire l'entrée de l'utilisateur
     * @param color la couleur du joueur pour lequel on choisit la stratégie
     * @return la stratégie choisie par l'utilisateur
     */
    private static MoveStrategy chooseStrategy(Scanner scanner, Color color) {
        System.out.println("Choisissez une stratégie pour " + color + " :");
        System.out.println("1 - Random");
        System.out.println("2 - MCTS");
        System.out.println("3 - RAVE");

        int choice = scanner.nextInt();

        if (choice == 1) {
            return new RandomAIStrategy();
        }

        System.out.print("Entrez le budget : ");
        int budget = scanner.nextInt();

        switch (choice) {
            case 2:
                return new MCTSStrategy(budget, color);
            case 3:
                return new RAVEStrategy(budget, color);
            default:
                System.out.println("Choix invalide, Random par défaut.");
                return new RandomAIStrategy();
        }
    }
}