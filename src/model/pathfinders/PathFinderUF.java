// package model.pathfinders;
// import utils.UnionFind;
// import java.util.*;
// import model.*;

// public class PathFinderUF implements PathFinder{
    
//     private UnionFind unionF;
//     private Board board;
//     public PathFinderUF(Board board){
//         this.board = board;
//         this.unionF = new UnionFind(board.getSize());


//     }

//     @Override
//     public boolean hasWinningPath(Board board, Color color){
        
//         //UnionFind unionF = new UnionFind(board.getSize())
//         for(int i=0; i< board.getSize(); i++){
//             for(int j =0; j< board.getSize(); j++){
//                 if(board.getCell(i,j) != color){
//                     continue;
//                 }

//                 int size = board.getSize();
//                 int currIndex = i*size+j;

//                 if(color == Color.BLUE){
//                     if(j == 0){
//                         unionF.union(currIndex, unionF.getVirtualLeft());
//                     }
//                     if( j == size -1){
//                         unionF.union(currIndex, unionF.getVirtualRight());
//                     }
//                 }
//                 else if(color == Color.RED){
//                     if(i == 0){
//                         unionF.union(currIndex, unionF.getVirtualTop());
//                     }
//                     if( i == size -1){
//                         unionF.union(currIndex, unionF.getVirtualBottom());
//                     }
//                 }

//                 List<int[]> neighbors = board.getNeighbors(i,j);
//                 for(int k=0; k<neighbors.size();k++){
//                     int[] neighbor = neighbors.get(k);
//                     int neigbRow = neighbor[0];
//                     int neigbCol = neighbor[1];

//                     if(board.getCell(neigbRow, neigbCol) == color){
//                         int neighborIndex = neigbRow*size+neigbCol;
//                         unionF.union(currIndex, neighborIndex);
//                     }
                    
//                 }
//             }
//         }
//         if(color == Color.BLUE){
//             return unionF.isConnected(unionF.getVirtualLeft(), unionF.getVirtualRight());
//         }
//         else if(color == Color.RED){
//             return unionF.isConnected(unionF.getVirtualTop(), unionF.getVirtualBottom());

//         }
//         return false;
//     }

// }

package model.pathfinders;

import model.Board;
import model.Color;
import utils.UnionFind;

public class PathFinderUF implements PathFinder {
    
    public PathFinderUF(Board board) {
        // On n'a plus besoin de stocker "board" ou "unionF" comme attributs de classe
        // car on les gère directement de manière optimale dans la méthode en dessous.
    }

    @Override
    public boolean hasWinningPath(Board board, Color color) {
        int size = board.getSize();
        
        UnionFind localUf = new UnionFind(size); 
        
        // 2. Tableaux primitifs pour les 6 directions (zéro allocation dans la boucle)
        int[] dRow = {-1, -1, 0, 0, 1, 1};
        int[] dCol = {0, 1, -1, 1, -1, 0};

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(board.getCell(i, j) != color) {
                    continue;
                }

                int currIndex = i * size + j;

                // 3. Connexion aux bords virtuels
                if(color == Color.BLUE) {
                    if(j == 0) localUf.union(currIndex, localUf.getVirtualLeft());
                    if(j == size - 1) localUf.union(currIndex, localUf.getVirtualRight());
                } else if(color == Color.RED) {
                    if(i == 0) localUf.union(currIndex, localUf.getVirtualTop());
                    if(i == size - 1) localUf.union(currIndex, localUf.getVirtualBottom());
                }

                // 4. Vérification des voisins INLINE
                for(int d = 0; d < 6; d++) {
                    int nRow = i + dRow[d];
                    int nCol = j + dCol[d];

                    // Si le voisin est dans la grille
                    if(nRow >= 0 && nRow < size && nCol >= 0 && nCol < size) {
                        if(board.getCell(nRow, nCol) == color) {
                            int neighborIndex = nRow * size + nCol;
                            localUf.union(currIndex, neighborIndex);
                        }
                    }
                }
            }
        }

        // 5. Vérification finale
        if(color == Color.BLUE) {
            return localUf.isConnected(localUf.getVirtualLeft(), localUf.getVirtualRight());
        } else if(color == Color.RED) {
            return localUf.isConnected(localUf.getVirtualTop(), localUf.getVirtualBottom());
        }
        
        return false;
    }
}