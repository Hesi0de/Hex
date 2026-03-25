package tests;

import algo.mcts.MCTS;
import algo.mcts.MCTSNode;
import model.Board;
import model.Color;


/**
 * Classe de test pour la classe MCTS. Elle contient des méthodes de test pour vérifier le bon fonctionnement de la classe MCTS.
 * c'est particuler car on ne peut pas tester de manière déterministe les résultats de MCTS
 * donc on teste le comportement .
 */
public class TestMCTS {

    public void runAllTests() {
        testSearchDoesNotCrash();
        testMoveInAvailableMoves();
        testSimulateReturnsValidColor();
    }

    /**
     * Méthode utilitaire pour vérifier une condition et lancer une exception avec un message d'erreur si la condition n'est pas respectée.
     * @param condition
     * @param message
     */
    private void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Error " + message);
        }
    }

    /**
     * Test pour vérifier que la méthode search de MCTS ne plante pas et retourne un coup valide.
     */
    public void testSearchDoesNotCrash() {
        Board board = new Board(3);
        MCTS mcts = new MCTS(100);

        int[] move = mcts.search(board, Color.BLUE);

        check(move != null, "Search retourne null");
        check(move.length == 2, "Move invalide");

        System.out.println("testSearchDoesNotCrash réussi");
    }


    /**
     * Test pour vérifier que le coup retourné par la méthode search de MCTS est bien présent dans la liste des coups disponibles du plateau.
     */
    public void testMoveInAvailableMoves() {
        Board board = new Board(3);
        MCTS mcts = new MCTS(100);
    
        int[] move = mcts.search(board, Color.BLUE);
    
        boolean found = false;
        for (int[] m : board.getAvailableMoves()) {
            if (m[0] == move[0] && m[1] == move[1]) {
                found = true;
                break;
            }
        }
    
        check(found, "Move non présent dans les coups possibles");
    
        System.out.println("testMoveInAvailableMoves réussi");
    }


    /**
     * Test pour vérifier que la méthode simulate de MCTS retourne une couleur valide (RED, BLUE ou EMPTY).
     */
    public void testSimulateReturnsValidColor() {
        Board board = new Board(3);
        MCTS mcts = new MCTS(100);

        MCTSNode node = new MCTSNode(board, null, null, Color.BLUE);

        Color winner = mcts.simulate(node);

        check(winner == Color.RED || winner == Color.BLUE || winner == Color.EMPTY,
            "Résultat simulate invalide");

        System.out.println("testSimulate réussi");
    }
}
