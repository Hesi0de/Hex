package model;

import java.util.*;
import model.pathfinders.PathFinder;
import model.pathfinders.PathFinderUF;
import model.pathfinders.PathFinderDFS;

import utils.*;

/**
 * Représente la grille de jeu et gère les opérations liées à celle-ci.
 */
public class Board {
    /**
     * Taille de la grille
     */
    protected int size;
    /**
     * Constantes pour les couleurs utilisées dans l'affichage de la grille
     */
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    // private PathFinder pf ;
    /**
     * Matrice représentant l'état de la grille, où chaque cellule peut être vide, occupée par le joueur rouge ou occupée par le joueur bleu.
     */
    protected Color cells[][];

    /**
     * Liste des coordonnées formant le chemin gagnant, utilisée pour l'affichage du chemin gagnant sur la grille..
     */
    private List<int[]> winningPath = null;

    // attribut uf d'opti

    /**
     * Deux instances de Union-Find pour suivre les connexions des cellules occupées par les joueurs bleu et rouge.
     */
    private UnionFind ufB;

    /**
     * Deux instances de Union-Find pour suivre les connexions des cellules occupées par les joueurs bleu et rouge.
     */
    private UnionFind ufR;

    /**
     * Tableaux de déplacement pour les 6 directions possibles sur une grille hexagonale, utilisés pour trouver les voisins d'une cellule.
     */
    private static final int[] dRow = { -1, -1, 0, 0, 1, 1 };

    /**
     * Tableaux de déplacement pour les 6 directions possibles sur une grille hexagonale, utilisés pour trouver les voisins d'une cellule.
     */
    private static final int[] dCol = { 0, 1, -1, 1, -1, 0 };


    /**
     * Constructeur de la classe Board, qui initialise une grille de taille spécifiée avec toutes les cellules vides et prépare les structures de données nécessaires pour suivre les connexions des joueurs.
     * @param size la taille de la grille
     */
    public Board(int size) {
        this.size = size;
        this.cells = new Color[size][size];
        // this.pf = new PathFinderUF(this);
        this.ufB = new UnionFind(size);
        this.ufR = new UnionFind(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.cells[i][j] = Color.EMPTY;
            }
        }
    }

    /**
     * Constructeur privé de la classe Board pour clonner plus rapidement.
     * @param size la taille de la grille
     * @param cells la matrice représentant l'état de la grille
     * @param ufB   l'instance de Union-Find pour le joueur bleu
     * @param ufR   l'instance de Union-Find pour le joueur rouge
     */
    private Board(int size, Color[][] cells, UnionFind ufB, UnionFind ufR) {
        this.size = size;
        this.cells = cells;
        this.ufB = ufB;
        this.ufR = ufR;
    }

    /**
     * Retourne la taille de la grille.
     * @return la taille de la grille
     */
    public int getSize() {
        return this.size;
    }


    /**
     * Crée une copie de Board, y compris la matrice des cellules et les structures de données Union-Find,
     *  pour permettre des modifications sans affecter l'état original de la grille.
     * @return une nouvelle instance de Board qui est une copie de l'instance actuelle
     */
    public Board copy() {
        Color[][] newCells = new Color[size][size];
        for (int i = 0; i < this.size; i++) {
            System.arraycopy(this.cells[i], 0, newCells[i], 0, size);
        }
        return new Board(size, newCells, this.ufB.copy(), this.ufR.copy());
    }


    /**
     * Retourne la couleur de la cellule à la position spécifiée, ou Color.EMPTY si les coordonnées sont en dehors des limites de la grille.
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     * @return la couleur de la cellule à la position spécifiée, ou Color.EMPTY si les coordonnées sont en dehors des limites de la grille
     */
    public Color getCell(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return cells[row][col];
        }
        return Color.EMPTY;
    }

    /**
     * Modifie la couleur de la cellule à la position spécifiée, en vérifiant que les coordonnées sont valides.
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     * @param color la nouvelle couleur à assigner à la cellule
     */
    // Doit être private pour forcer le passage par executeMove !
    private void setCell(int row, int col, Color color) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            cells[row][col] = color;
        }
    }


    /**
     * Vérifie si la cellule à la position spécifiée est vide  et que les coordonnées sont valides.
     * @param row la ligne de la cellule
     * @param col   la colonne de la cellule
     * @return  true si la cellule est vide et les coordonnées sont valides, false sinon
     */
    public boolean isCellEmpty(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return false;
        }
        return cells[row][col] == Color.EMPTY;
    }

    /**
     * vérifie si un coup est valide
     * @param row la ligne de la cellule où le coup est joué
     * @param col la colonne de la cellule où le coup est joué
     * @return true si le coup est valide (c'est-à-dire que la cellule est vide et les coordonnées sont valides), false sinon
     */
    public boolean isValidMove(int row, int col) {
        return isCellEmpty(row, col);
    }

    /**
     * Exécute un coup sur la grille
     * @param row la ligne de la cellule où le coup est joué
     * @param col la colonne de la cellule où le coup est joué
     * @param color la couleur du joueur qui joue le coup
     */
    // opti incrémentale de execute move
    public void executeMove(int row, int col, Color color) {
        if (!isValidMove(row, col)) {
            throw new IllegalArgumentException("Invalid move at (" + row + ", " + col + ")");
        }
        setCell(row, col, color);

        int currentIndex = row * size + col;
        if (color == Color.BLUE) {
            if (col == 0)
                ufB.union(currentIndex, ufB.getVirtualLeft());
            if (col == size - 1)
                ufB.union(currentIndex, ufB.getVirtualRight());

            for (int d = 0; d < 6; d++) {
                int nRow = row + dRow[d];
                int nCol = col + dCol[d];
                if (nRow >= 0 && nRow < size && nCol >= 0 && nCol < size) {
                    if (cells[nRow][nCol] == Color.BLUE) {
                        ufB.union(currentIndex, nRow * size + nCol);
                    }
                }
            }
        } else if (color == Color.RED) {
            if (row == 0)
                ufR.union(currentIndex, ufR.getVirtualTop());
            if (row == size - 1)
                ufR.union(currentIndex, ufR.getVirtualBottom());
            for (int d = 0; d < 6; d++) {
                int nRow = row + dRow[d];
                int nCol = col + dCol[d];
                if (nRow >= 0 && nRow < size && nCol >= 0 && nCol < size) {
                    if (cells[nRow][nCol] == Color.RED) {
                        ufR.union(currentIndex, nRow * size + nCol);
                    }
                }
            }
        }
    }

    /**
     * Retourne la liste des coups disponibles sur la grille
     * @return une liste d'entiers représentant les coordonnées des cellules vides sur la grille.
     */
    public List<int[]> getAvailableMoves() {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (isCellEmpty(i, j)) {
                    moves.add(new int[] { i, j });
                }
            }

        }
        return moves;
    }

    /**
     * vérifie si la grille est pleine
     * @return true si la grille est pleine, false sinon
     */
    public boolean isBoardFull() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (isCellEmpty(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }// à voir si on en a besoin

    /**
     * Retourne les coordonnés des cellules voisins d'une cellule donnée
     * @param row la ligne de la cellule dont on veut trouver les voisins
     * @param col la colonne de la cellule dont on veut trouver les voisins
     * @return une liste d'entiers représentant les coordonnées des cellules voisines de la cellule spécifiée, en tenant compte des limites de la grille.
     */
    public List<int[]> getNeighbors(int row, int col) {
        List<int[]> res = new ArrayList<>();
        int[][] directions = {
                { -1, 0 }, // Nord-Ouest
                { -1, 1 }, // Nord-Est
                { 0, -1 }, // Ouest
                { 0, 1 }, // Est
                { 1, -1 }, // Sud-Ouest
                { 1, 0 },// Sud-Est
        };

        for (int[] d : directions) {
            int newRow = row + d[0];
            int newCol = col + d[1];
            if (newRow >= 0 && newRow < this.size && newCol >= 0 && newCol < this.size) {
                res.add(new int[] { newRow, newCol });
            }

        }
        return res;

    }


    /**
     * Vérifie si deux cellules sont voisines, càd si elles partagent une frontière sur la grille hexagonale.
     * @param row1 la ligne de la première cellule
     * @param col1 la colonne de la première cellule
     * @param row2 la ligne de la deuxième cellule
     * @param col2 la colonne de la deuxième cellule
     * @return true si les deux cellules sont voisines, false sinon
     */
    public boolean areNeigbhors(int row1, int col1, int row2, int col2) {
        List<int[]> neighbors = getNeighbors(row1, col1);
        for (int[] neighbor : neighbors) {
            if (neighbor[0] == row2 && neighbor[1] == col2) {
                return true;
            }
        }
        return false;
    }

    /**
     * vérifie si une couleur a un chemin gagnant sur la grille
     * @param color la couleur du joueur pour lequel on veut vérifier l'existence d'un chemin gagnant
     * @return true si la couleur a un chemin gagnant sur la grille, false sinon
     */
    public boolean hasPath(Color color) {
        if (color == Color.BLUE) {
            return ufB.isConnected(ufB.getVirtualLeft(), ufB.getVirtualRight());
        } else if (color == color.RED) {
            return ufR.isConnected(ufR.getVirtualBottom(), ufR.getVirtualTop());
        } else {
            return false;
        }

    }


    /**
     * Calcule le chemin gagnant pour la couleur qui a gagné, en utilisant une recherche en profondeur (DFS) 
     * pour trouver un chemin continu de cellules occupées par la même couleur reliant les bords opposés de la grille.
     */
    public void computeWinningPath() {
        PathFinderDFS pfDFS = new PathFinderDFS();
        // teste le Bleu
        winningPath = pfDFS.findWinningPath(this, Color.BLUE);
        if (winningPath != null) {
            return;
        }
        // sinon le Rouge
        winningPath = pfDFS.findWinningPath(this, Color.RED);
    }


    /**
     * Retourne la couleur du joueur gagnant, ou Color.EMPTY si aucun joueur n'a gagné.
     * @return la couleur du joueur gagnant, ou Color.EMPTY si aucun joueur n'a gagné
     */
    public Color getWinner() {
        if (hasPath(Color.RED)) {
            return Color.RED;
        }
        if (hasPath(Color.BLUE)) {
            return Color.BLUE;
        }
        return Color.EMPTY;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for (int col = 0; col < this.size; col++) {
            sb.append(col).append(" ");
        }
        sb.append("\n");

        sb.append("    ");
        for (int col = 0; col < this.size; col++) {
            sb.append(RED).append("_ ").append(RESET);
        }
        sb.append("\n\n");

        for (int row = 0; row < this.size; row++) {
            if (row < 10)
                sb.append(" ");
            sb.append(row).append(" ");

            // indentation hex
            for (int indent = 0; indent < row; indent++) {
                sb.append(" ");
            }
            sb.append(BLUE).append("\\ ").append(RESET);

            for (int col = 0; col < this.size; col++) {
                boolean inPath = false;
                String pathColor = ""; // couleur pour le chemin

                if (winningPath != null) {
                    for (int[] cell : winningPath) {
                        if (cell[0] == row && cell[1] == col) {
                            inPath = true;
                            break;
                        }
                    }

                    if (inPath) {
                        Color winnerColor = getCell(winningPath.get(0)[0], winningPath.get(0)[1]);
                        if (winnerColor == Color.BLUE) {
                            pathColor = "\u001B[96m"; // bleu clair
                        } else if (winnerColor == Color.RED) {
                            pathColor = "\u001B[91m"; // rouge clair
                        }
                    }
                }

                if (inPath) {
                    sb.append(pathColor).append("⬢").append("\u001B[0m").append(" ");
                } else {
                    sb.append(cells[row][col].toSymbol()).append(" ");
                }
            }
            sb.append(BLUE).append("\\ ").append(RESET);
            sb.append("\n");
        }
        sb.append(" ".repeat(size + 5));
        for (int col = 0; col < this.size; col++) {
            sb.append(RED).append("_ ").append(RESET);
        }
        sb.append("\n");
        return sb.toString();
    }

}
