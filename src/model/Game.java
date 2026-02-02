package model;

import java.util.*;

public class Game {

    private Board board;
    private Player currentPlayer;
    private final Player player1;
    private final Player player2;

    public Game(int boardSize, Player p1, Player p2) {
        this.board = new Board(boardSize);
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = p1;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void resetPlayer(Color color) {
        if (color == null) {
            currentPlayer = player1;
        }
        if (!player1.getColor().equals(color)) {
            switchPlayer();
        }

    }

    public boolean isOver() {
        return getWinner() != null;
    }

    public void switchPlayer() {// nextPlayer()
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public boolean isValid() {
        return true;

    }

    // public Player getWinner() { // a remplacer par la classe unionfind
    // int winnerColor = board.checkWinner(); // Renvoie 0, 1 ou 2
    // if (winnerColor == Board.BLUE)
    // return player1;
    // if (winnerColor == Board.RED)
    // return player2;
    // return null; // Pas encore de gagnant
    // }
    
    // public int checkWinner() {
    // // Est-ce que Bleu a relié Gauche et Droite ?
    // if (uf.isConnected(uf.getVirtualLeft(), uf.getVirtualRight())) {
    // return 1;
    // }
    // // Est-ce que Rouge a relié Haut et Bas ?
    // if (uf.isConnected(uf.getVirtualTop(), uf.getVirtualBottom())) {
    // return 2;
    // }
    // return 0; // Personne n'a gagné
    // }

    public Player getWinner() {
        return null;
    }

    public Board getBoard() {
        return board;
    }
}
