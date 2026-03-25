package tests;

import model.Color;
import model.Game;
import model.Player;

/**
 * Classe de test pour la classe Game. Elle contient des méthodes de test pour vérifier le bon fonctionnement de la logique du jeu.
 */
public class GameTest {

    /**
     * Méthode principale pour exécuter tous les tests de la classe GameTest.
     */
    public void runAllTests() {
        testInitialization();
        testSwitchPlayer();
        testSwapPlayers();
        testResetPlayer();
        testGetBoard();
    }

    /**
     * Méthode utilitaire pour vérifier une condition et lancer une RuntimeException avec un message en cas d'échec.
     * @param condition
     * @param message
     */
    private void check(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Error " + message);
        }
    }

    /**
     * Test de l'initialisation du jeu. Vérifie que le joueur courant est correctement défini au début du jeu.
     */
    public void testInitialization() {
        Player p1 = new Player("P1", Color.BLUE);
        Player p2 = new Player("P2", Color.RED);

        Game game = new Game(5, p1, p2);

        check(game.getCurrentPlayer() == p1, "Initialisation: currentPlayer devrait être p1");

        System.out.println("testInitialization réussi");
    }


    /**
    * Test de la méthode switchPlayer() de la classe Game. Vérifie que le joueur courant change correctement après chaque appel.
    */
    public void testSwitchPlayer() {
        Player p1 = new Player("P1", Color.BLUE);
        Player p2 = new Player("P2", Color.RED);

        Game game = new Game(5, p1, p2);

        game.switchPlayer();
        check(game.getCurrentPlayer() == p2, "Switch: devrait être p2");

        game.switchPlayer();
        check(game.getCurrentPlayer() == p1, "Switch: devrait revenir à p1");

        System.out.println("testSwitchPlayer réussi");
    }

    /**
     * Test de la méthode swapPlayers() de la classe Game. Vérifie que les couleurs des joueurs sont échangées correctement après l'appel de la méthode.
     */
    public void testSwapPlayers() {
        Player p1 = new Player("P1", Color.BLUE);
        Player p2 = new Player("P2", Color.RED);

        Game game = new Game(5, p1, p2);

        game.swapPlayers();

        check(p1.getColor() == Color.RED, "Swap: p1 doit devenir RED");
        check(p2.getColor() == Color.BLUE, "Swap: p2 doit devenir BLUE");

        System.out.println("testSwapPlayers réussi");
    }

    /**
     * Test de la méthode resetPlayer() de la classe Game. Vérifie que le joueur courant revient au joueur initial après l'appel de la méthode.
     */
    public void testResetPlayer() {
        Player p1 = new Player("P1", Color.BLUE);
        Player p2 = new Player("P2", Color.RED);

        Game game = new Game(5, p1, p2);

        game.resetPlayer(Color.BLUE);

        check(game.getCurrentPlayer() == p1, "Reset: devrait revenir à p1");

        System.out.println("testResetPlayer réussi");
    }

    /**
     * Test de la méthode getBoard() de la classe Game. Vérifie que le plateau de jeu est correctement initialisé et accessible.
     */
    public void testGetBoard() {
        Player p1 = new Player("P1", Color.BLUE);
        Player p2 = new Player("P2", Color.RED);

        Game game = new Game(5, p1, p2);

        check(game.getBoard() != null, "Board ne doit pas être null");

        System.out.println("testGetBoard réussi");
    }
}
