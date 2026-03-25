package model.pathfinders;
import utils.UnionFind;
import model.*;

/**
 * Interface représentant un algorithme de recherche de chemin gagnant dans un jeu de Hex.
 */
public interface PathFinder{
    /**
     * Vérifie si le joueur de la couleur donnée a un chemin gagnant sur le plateau.
     * @param board Le plateau de jeu.
     * @param color La couleur du joueur pour lequel vérifier le chemin gagnant.
     * @return true si le joueur a un chemin gagnant, false sinon.
     */
    boolean hasWinningPath(Board board, Color color);

    /*default Color getWinner(Board board) {
        if (hasWinningPath(board, Color.BLUE)) {
            return Color.BLUE;
        }
        if (hasWinningPath(board, Color.RED)) {
            return Color.RED;
        }
        return Color.EMPTY;
    }*/
}