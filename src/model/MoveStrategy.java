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
}
