package model;

import java.util.*;

/**
 * La classe Game représente une partie de Hex. Elle gère l'état du plateau, les joueurs, et les règles du jeu.
 */
public class Game {

    /**
     * Le plateau de jeu, une instance de la classe Board.
     */
    private Board board;

    /**
     * Le joueur courant.
     */
    private Player currentPlayer;

    /**
     * Joueur 1, genéralement associé à la couleur bleue.
     */
    private final Player player1;
    /**
     * Joueur 2, genéralement associé à la couleur rouge.
     */
    private final Player player2;

    /**
     * Constructeur de la classe Game. Il initialise le plateau de jeu et les joueurs.
     * @param boardSize la taille du plateau de jeu (ex: 11 pour un plateau 11x11) 
     * @param p1 le joueur 1, généralement associé à la couleur bleue 
     * @param p2 le joueur 2, généralement associé à la couleur rouge
     */
    public Game(int boardSize, Player p1, Player p2) {
        this.board = new Board(boardSize);
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = p1;
    }


    /**
     * Retourne le joueur courant.
     * @return le joueur courant
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Réinitialise le joueur courant en fonction de la couleur donnée.
     * @param color la couleur du joueur à réinitialiser.
     */
    public void resetPlayer(Color color) {
        if (color == null) {
            currentPlayer = player1;
            return;
        }
        if (!player1.getColor().equals(color)) {
            switchPlayer();
        }

    }

    /**
     * echange les rôles des joueurs (le joueur 1 devient le joueur 2 et vice versa).
     * cette méthode doit être appelée lorsque le joueur 2 décide de faire un swap après son premier coup.
     */
    public void swapPlayers() {
        Color tmp = player1.getColor();
        player1.setColor(player2.getColor());
        player2.setColor(tmp);
    }


    /*public boolean isOver() {
        return getWinner() != null;
    }*/

    public void switchPlayer() {// nextPlayer()
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    /**
     * Vérifie si le coup joué par le joueur courant est valide.
     * @return
     */
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

    /*public Player getWinner() {
        Color winnerColor = board.getWinner(); 
        if (winnerColor == Board.BLUE){
            return player1;
        }
        else if(winnerColor == Board.RED){
            return player2;
        }
        return null;
         
    }*/

    /**
     * Retourne le plateau de jeu.
     * @return le plateau de jeu
     */
    public Board getBoard() {
        return board;
    }
}
