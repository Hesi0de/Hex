package model;

import java.util.List;

/**
 * Classe représentant un joueur IA qui choisit des coups aléatoires.
 */
public class RandomAIStrategy implements MoveStrategy {
    /**
     * Constructeur de la classe RandomAIPlayer.
     * 
     * @param name
     * @param color
     */
    // public RandomAIPlayer(String name, Color color) {
    // super(name, color);
    // }

    /**
     * Méthode pour choisir un coup aléatoire.
     * 
     * @return Un tableau de deux entiers représentant les coordonnées du coup
     *         choisi.
     */
    @Override
    public int[] chooseMove(Board board) {
        // int boardSize = 14; // Taille de la grille(temporaire)
        // choix de coup aléatoire
        // int row = (int) (Math.random() * boardSize);
        // int col = (int) (Math.random() * boardSize);
        List<int[]> moves = board.getAvailableMoves();
        // nextmove= moves
        int moveIndex = (int) (Math.random() * moves.size());
        return moves.get(moveIndex);
        // if (board.isValidMove(row, col)){
        // return new int[] {row, col};
        // }else{
        // return chooseMove(board);
        // }
    }
}