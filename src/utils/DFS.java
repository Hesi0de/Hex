package utils;

import model.Board;
import model.Color;

import java.util.*;

public class DFS {

    public static List<int[]> findPath(Board board, Color color) {
        int size = board.getSize();
        boolean[][] visited = new boolean[size][size];
        List<int[]> path = new ArrayList<>();

        // départ
        for (int i = 0; i < size; i++) {
            int row = (color == Color.RED) ? 0 : i;
            int col = (color == Color.RED) ? i : 0;

            if (board.getCell(row, col) == color) {
                path.clear();
                if (dfs(board, color, row, col, visited, path)) {
                    return new ArrayList<>(path); // on a trouvé le chemin
                }
            }
        }
        return null; 
    }

    private static boolean dfs(Board board, Color color, int row, int col, boolean[][] visited, List<int[]> path) {
        int size = board.getSize();
        if (row < 0 || row >= size || col < 0 || col >= size) return false;
        if (visited[row][col] || board.getCell(row, col) != color) return false;

        visited[row][col] = true;
        path.add(new int[]{row, col});

        // notre Condition de victoire
        if ((color == Color.BLUE && col == size - 1) || (color == Color.RED && row == size - 1)) {
            return true; 
        }

        for (int[] neighbor : board.getNeighbors(row, col)) {
            if (dfs(board, color, neighbor[0], neighbor[1], visited, path)) {
                return true;
            }
        }

        path.remove(path.size() - 1); // on fait un backtrack si ce chemin ne mène pas vers la victoire
    return false;
}

}
