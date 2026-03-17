package model;

import java.util.*;
import model.pathfinders.PathFinder;
import model.pathfinders.PathFinderUF;
import model.pathfinders.PathFinderDFS;

import utils.*;

public class Board {
    protected int size;
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    // private PathFinder pf ;
    protected Color cells[][];
    private List<int[]> winningPath = null;
    // attribut uf d'opti
    private UnionFind ufB;
    private UnionFind ufR;
    private static final int[] dRow = { -1, -1, 0, 0, 1, 1 };
    private static final int[] dCol = { 0, 1, -1, 1, -1, 0 };

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
    // constructeur privé pour clonner plus rapide

    private Board(int size, Color[][] cells, UnionFind ufB, UnionFind ufR) {
        this.size = size;
        this.cells = cells;
        this.ufB = ufB;
        this.ufR = ufR;
    }

    public int getSize() {
        return this.size;
    }

    public Board copy() {
        Color[][] newCells = new Color[size][size];
        for (int i = 0; i < this.size; i++) {
            System.arraycopy(this.cells[i], 0, newCells[i], 0, size);
        }
        return new Board(size, newCells, this.ufB.copy(), this.ufR.copy());
    }

    public Color getCell(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return cells[row][col];
        }
        return Color.EMPTY;
    }

    // Doit être private pour forcer le passage par executeMove !
    private void setCell(int row, int col, Color color) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            cells[row][col] = color;
        }
    }

    public boolean isCellEmpty(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            return false;
        }
        return cells[row][col] == Color.EMPTY;
    }

    /**
     * vérifie si un coup est valide
     */
    public boolean isValidMove(int row, int col) {
        return isCellEmpty(row, col);
    }

    /**
     * Exécute un coup sur la grille
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

    public boolean areNeigbhors(int row1, int col1, int row2, int col2) {
        List<int[]> neighbors = getNeighbors(row1, col1);
        for (int[] neighbor : neighbors) {
            if (neighbor[0] == row2 && neighbor[1] == col2) {
                return true;
            }
        }
        return false;
    }

    // on vérifie si une couleur a un chemin gagnant sur la grille
    public boolean hasPath(Color color) {
        if (color == Color.BLUE) {
            return ufB.isConnected(ufB.getVirtualLeft(), ufB.getVirtualRight());
        } else if (color == color.RED) {
            return ufR.isConnected(ufR.getVirtualBottom(), ufR.getVirtualTop());
        } else {
            return false;
        }

    }

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
