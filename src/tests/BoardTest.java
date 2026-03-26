package tests;
import model.*;

/**
 * Classe de test pour la classe Board. Elle contient des méthodes de test pour vérifier le bon fonctionnement du plateau du jeu.
 */
public class BoardTest {

    /**
     * Méthode principale pour exécuter tous les tests de la classe BoardTest.
     */
    public void runAllTests() {
        testCopy();
        testGetCell();
        testExecuteMove();
        testIsValidMove();
        testGetWinner();
    }

    /**
     * Méthode utilitaire pour vérifier une condition et lancer une AssertionError avec un message en cas d'échec.
     * @param condition La condition à vérifier.
     * @param message Le message d'erreur à afficher si la condition est fausse.
     */
    public void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Error " + message);
        }
    }
    /**
     * Test de la méthode copy() de la classe Board. Vérifie que la copie est correcte et indépendante de l'original.
     */
    public void testCopy() {
        Board board1 = new Board(5);
        board1.executeMove(0, 0, Color.RED);
        board1.executeMove(1, 1, Color.BLUE);
    
        Board board2 = board1.copy();
    
        check(board2.getCell(0, 0) == Color.RED, "Copy: (0,0) devrait être RED");
        check(board2.getCell(1, 1) == Color.BLUE, "Copy: (1,1) devrait être BLUE");
        check(board2.getCell(2, 2) == Color.EMPTY, "Copy: (2,2) devrait être EMPTY");
    
        // modifier l’original
        board1.executeMove(0, 1, Color.BLUE);
    
        check(board2.getCell(0, 0) == Color.RED, "Copy: la copie ne doit pas changer");
    
        System.out.println("testCopy réussi");
    }

    /**
     * Test de la méthode getCell() de la classe Board. Vérifie que les cellules retournent les bonnes couleurs.
     */
    public void testGetCell() {
        Board board = new Board(5);
        board.executeMove(0, 0, Color.RED);
        board.executeMove(1, 1, Color.BLUE);
    
        check(board.getCell(0, 0) == Color.RED, "GetCell: (0,0) RED");
        check(board.getCell(1, 1) == Color.BLUE, "GetCell: (1,1) BLUE");
        check(board.getCell(2, 2) == Color.EMPTY, "GetCell: (2,2) EMPTY");
    
        System.out.println("testGetCell réussi");
    }


    

    /**
     * Test de la méthode executeMove() de la classe Board. Vérifie que les coups sont correctement exécutés et que les règles sont respectées (pas de coup sur une case occupée).
     */
    public void testExecuteMove() {
        Board board = new Board(3);

        board.executeMove(0, 0, Color.BLUE);
        check(board.getCell(0, 0) == Color.BLUE, "ExecuteMove: case incorrecte");

        boolean exceptionThrown = false;
        // Essayer de jouer sur une case déjà occupée devrait lever une exception
        try {
            board.executeMove(0, 0, Color.RED);
        } catch (IllegalArgumentException e) {
            exceptionThrown = true;
        }

        check(exceptionThrown, "ExecuteMove: devrait lever une exception");

        System.out.println("testExecuteMove réussi");
    }

    /**
     * Test de la méthode isValidMove() de la classe Board. Vérifie que les coups valides sont reconnus comme tels et que les coups invalides (comme jouer sur une case occupée) sont correctement identifiés.
     */
    public void testIsValidMove() {
        Board board = new Board(3);

        check(board.isValidMove(1, 1), "Move devrait être valide");

        board.executeMove(1, 1, Color.RED);

        check(!board.isValidMove(1, 1), "Move ne devrait plus être valide");

        System.out.println("testIsValidMove réussi");
    }

    /**
     * Test de la méthode getWinner() de la classe Board. Vérifie que le gagnant est correctement identifié lorsque les conditions de victoire sont remplies (par exemple, une ligne complète pour un joueur).
     */
    public void testGetWinner() {
        Board board = new Board(3);

        // victoire BLEU (gauche → droite)
        board.executeMove(0, 0, Color.BLUE);
        board.executeMove(0, 1, Color.BLUE);
        board.executeMove(0, 2, Color.BLUE);

        check(board.getWinner() == Color.BLUE, "Winner devrait être BLUE");

        System.out.println("testGetWinner réussi");
    }
}
