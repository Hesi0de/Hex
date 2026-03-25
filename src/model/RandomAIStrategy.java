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

    /**
     * Méthode pour décider de faire un swap ou pas. Ici, on choisit aléatoirement avec une probabilité de 50%.
     */
    @Override
    public boolean decideSwap(Board board, int[] firstMove) {
        // plusiers stratégies possibles pour décider de faire un swap ou pas, ici on choisit aléatoirement
        return Math.random() < 0.5; // 50% de chances de faire un swap
        
        // return true ; // oujours faire un swap(si on veut tester rapidenment)
        
        // ou stratégie plus intelligente : faire un swap si le coup de l'adversaire est du centre  de la grille et autours
        // if (firstMove[0] == center || firstMove[0] == center - 1 || firstMove[0] == center + 1 && firstMove[1] == center || firstMove[1] == center - 1 || firstMove[1] == center + 1) {
            //     return true; // effectuer le swap
            // }
            // return false; 
    }
}