package model;

/**
 * Interface représentant une stratégie de déplacement pour un joueur IA.
 */
public interface MoveStrategy {
    
    /**
     * Méthode pour choisir un coup.
     * @return Un tableau de deux entiers représentant les coordonnées du coup choisi.
     */
    int[] chooseMove(Board board);

    /**
     * Méthode pour décider si le joueur souhaite faire un swap après le premier coup de l'adversaire.
     * @param board
     * @param firstMove
     * @return true si le joueur souhaite faire un swap, false sinon.
     */
    default boolean decideSwap(Board board, int[] firstMove) {
        return false;
    }
}
