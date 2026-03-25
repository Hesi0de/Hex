package tests;

import java.util.List;

import algo.mcts.MCTSNode;
import algo.rave.RAVE;
import algo.rave.RAVENode;
import model.Board;
import model.Color;

/**
 * Classe de test pour la classe RAVE. Elle contient des méthodes de test pour vérifier le bon fonctionnement de la classe RAVE.
 */
public class TestRAVE {

    public void runAllTests() {
        testSearchDoesNotCrash();
        testSimulateStoresMoves();
        testBackpropagateRAVE();
    }

    /**
     * Méthode utilitaire pour vérifier une condition et lancer une exception avec un message d'erreur si la condition n'est pas respectée.
     * @param condition
     * @param message
     */
    public void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Error " + message);
        }
    }

    /**
     * Test pour vérifier que la méthode search de RAVE ne plante pas et retourne un coup valide.
     */
    public void testSearchDoesNotCrash() {
        Board board = new Board(3);
        RAVE rave = new RAVE(100);

        int[] move = rave.search(board, Color.BLUE);

        check(move != null, "Search retourne null");
        check(move.length == 2, "Move invalide");

        System.out.println("testSearchDoesNotCrash réussi");
    }

    /**
     * Test pour vérifier que la méthode simulate de RAVE stocke correctement les coups joués
     * et les couleurs des joueurs dans les listes lastSimMoves et lastSimColors.
      * On simule une partie à partir d'un nœud donné, puis on vérifie que les listes
      *  lastSimMoves et lastSimColors ne sont pas vides.
     */
    public void testSimulateStoresMoves() {
        Board board = new Board(3);
        RAVE rave = new RAVE(10);

        MCTSNode node = new RAVENode(board, null, null, Color.BLUE);

        rave.simulate(node);

        check(!rave.getLastSimMoves().isEmpty(), "RAVE: moves non enregistrés");
        check(rave.getLastSimMoves().size() == rave.getLastSimColors().size(),
            "RAVE: tailles incohérentes");

        System.out.println("testSimulateStoresMoves réussi");
    }

    /**
     * Test pour vérifier que la méthode backpropagateRAVE met à jour les statistiques du nœud et que les listes de coups et de couleurs ont des tailles cohérentes.
      * On simule une situation simple avec 2 coups joués, 
      * puis on appelle backpropagateRAVE et on vérifie que les visites du nœud racine
      *  ont été incrémentées et que les listes de coups et de couleurs ont des tailles cohérentes.
     */
    public void testBackpropagateRAVE() {
        Board board = new Board(3);
        RAVE rave = new RAVE(10);
    
        RAVENode root = new RAVENode(board, null, null, Color.BLUE);
    
        // simuler une situation simple
        List<int[]> moves = List.of(
            new int[]{0, 0},
            new int[]{1, 1}
        );
        
        List<Color> colors = List.of(
            Color.BLUE,
            Color.RED
        );
        
        rave.setLastSimMoves(moves);
        rave.setLastSimColors(colors);
    
        rave.backpropagateRAVE(root, Color.BLUE);
        check(
            rave.getLastSimMoves().size() == rave.getLastSimColors().size(),
            "Tailles incohérentes"
        );
        check(root.getVisits() > 0, "Visits non incrémenté");
    
        System.out.println("testBackpropagateRAVE réussi");
    }

}
