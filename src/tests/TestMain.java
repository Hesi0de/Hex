package tests;


public class TestMain {

    public static void main(String[] args) {

        System.out.println("===== TESTS BOARD =====");
        BoardTest boardTest = new BoardTest();
        boardTest.runAllTests();

        System.out.println("\n===== TESTS GAME =====");
        GameTest gameTest = new GameTest();
        gameTest.runAllTests();
 
        System.out.println("\n===== TESTS UNION-FIND =====");
        TestUF ufTest = new TestUF();
        ufTest.runAllTests();

        System.out.println("\n===== TESTS MCTS =====");
        TestMCTS mctsTest = new TestMCTS();
        mctsTest.runAllTests();

        System.out.println("\n===== TESTS RAVE =====");
        TestRAVE raveTest = new TestRAVE();
        raveTest.runAllTests();

        System.out.println("\n TOUS LES TESTS SONT PASSÉS !");
    }
}