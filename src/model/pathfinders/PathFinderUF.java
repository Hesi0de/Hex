package model.pathfinders;
import utils.UnionFind;
import java.util.*;
import model.*;

public class PathFinderUF implements PathFinder{
    
    private UnionFind unionF;
    private Board board;
    public PathFinderUF(Board board){
        this.board = board;
        this.unionF = new UnionFind(board.getSize());


    }

    @Override
    public boolean hasWinningPath(Board board, Color color){
        
        //UnionFind unionF = new UnionFind(board.getSize())
        for(int i=0; i< board.getSize(); i++){
            for(int j =0; j< board.getSize(); j++){
                if(board.getCell(i,j) != color){
                    continue;
                }

                int size = board.getSize();
                int currIndex = i*size+j;

                if(color == Color.BLUE){
                    if(j == 0){
                        unionF.union(currIndex, unionF.getVirtualLeft());
                    }
                    if( j == size -1){
                        unionF.union(currIndex, unionF.getVirtualRight());
                    }
                }
                else if(color == Color.RED){
                    if(i == 0){
                        unionF.union(currIndex, unionF.getVirtualTop());
                    }
                    if( i == size -1){
                        unionF.union(currIndex, unionF.getVirtualBottom());
                    }
                }

                List<int[]> neighbors = board.getNeighbors(i,j);
                for(int k=0; k<neighbors.size();k++){
                    int[] neighbor = neighbors.get(k);
                    int neigbRow = neighbor[0];
                    int neigbCol = neighbor[1];

                    if(board.getCell(neigbRow, neigbCol) == color){
                        int neighborIndex = neigbRow*size+neigbCol;
                        unionF.union(currIndex, neighborIndex);
                    }
                    
                }
            }
        }
        if(color == Color.BLUE){
            return unionF.isConnected(unionF.getVirtualLeft(), unionF.getVirtualRight());
        }
        else if(color == Color.RED){
            return unionF.isConnected(unionF.getVirtualTop(), unionF.getVirtualBottom());

        }
        return false;
    }

}