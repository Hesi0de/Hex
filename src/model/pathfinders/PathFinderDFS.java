package model.pathfinders;

import model.Board;
import model.Color;
import utils.DFS;

import java.util.List;

/**
 * Implémentation de l'interface PathFinder utilisant une recherche en profondeur (DFS) pour trouver un chemin gagnant dans un jeu de Hex.
 */
public class PathFinderDFS implements PathFinder {

    /**
     * Vérifie si le joueur de la couleur donnée a un chemin gagnant sur le plateau en utilisant une recherche en profondeur (DFS).
     */
    @Override
    public boolean hasWinningPath(Board board, Color color) {
        return findWinningPath(board, color) != null;
    }

    /**
     * Trouve un chemin gagnant pour le joueur de la couleur donnée sur le plateau en utilisant une recherche en profondeur (DFS).
     * @param board Le plateau de jeu.
     * @param color La couleur du joueur pour lequel trouver le chemin gagnant.
     * @return Une liste de coordonnées représentant le chemin gagnant, ou null si aucun chemin gagnant n'est trouvé.
     */
    public List<int[]> findWinningPath(Board board, Color color) {
        return DFS.findPath(board, color);
    }
}