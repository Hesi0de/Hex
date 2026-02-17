package model.pathfinders;
import utils.UnionFind;
import model.*;
public interface PathFinder{
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