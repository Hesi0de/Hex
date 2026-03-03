package model.pathfinders;

import model.Board;
import model.Color;
import utils.DFS;

import java.util.List;

public class PathFinderDFS implements PathFinder {

    @Override
    public boolean hasWinningPath(Board board, Color color) {
        return findWinningPath(board, color) != null;
    }

    public List<int[]> findWinningPath(Board board, Color color) {
        return DFS.findPath(board, color);
    }
}