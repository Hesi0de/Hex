package model;
import java.util.*;
public class Board {
    protected int size;

    protected Color cells[][];

    public Board(int size) {
        this.size = size;
        this.cells = new Color[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.cells[i][j] = Color.EMPTY;
            }
        }
    }
    

    public int getSize() {
        return this.size; 
    }

    public Board copy(){
        Board newBoard = new Board(size);

        for(int i=0; i<this.size; i++){
           System.arraycopy(this.cells[i], 0, newBoard.cells[i], 0, size);
        }
        return newBoard;
    }

    
 
    public Color getCell(int row, int col) {
        if (row >= 0 && row < size && col >= 0 && col < size) {
            return cells[row][col];
        }
        return Color.EMPTY;
    }


    public void setCell(int row, int col, Color color) {
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
    public boolean isValidMove(int row, int col){
        return isCellEmpty(row,col);
    }

    /**
     * Exécute un coup sur la grille
     */
    public void executeMove(int row, int col, Color color){
        if(!isValidMove(row, col)){
            throw new IllegalArgumentException("Invalid move at (" + row + ", " + col + ")");
        }
        setCell(row, col, color);
    }

    /**
     * Retourne la liste des coups disponibles sur la grille
     */
    public List<int[]> getAvailableMoves(){
        List<int[]> moves = new ArrayList<>();
        for(int i = 0; i< this.size; i++){
            for(int j = 0; j< this.size; j++){
                if(isCellEmpty(i,j)){
                    moves.add(new int[]{i,j});
                }
            }

        }
        return moves;
    }

    /**
     * vérifie si la grille est pleine
     */
   public boolean isBoardFull(){
        for(int i=0; i< this.size; i++){
            for(int j =0; j< this.size; j++){
                if(isCellEmpty(i,j)){
                    return false;
                }
            }
        }
        return true;
    }//à voir si on en a besoin 

    /**
     * Retourne les coordonnés des cellules voisins d'une cellule donnée
     */
    public List<int[]> getNeighbors(int row, int col){
        List<int[]> res = new ArrayList<>();
        int[][] directions = {
            {-1,0},//Nord-Ouest
            {-1,1},//Nord-Est
            {0,-1},//Ouest
            {0,1},//Est
            {1,-1},//Sud-Ouest
            {1,0},//Sud-Est
        };

        for(int[] d : directions){
            int newRow = row + d[0];
            int newCol = col +d[1];
            if(newRow >= 0 && newRow < this.size && newCol >=0 && newCol < this.size){
                res.add(new int[]{newRow, newCol});
            }

        }
        return res;

    }

    public boolean areNeigbhors(int row1, int col1, int row2, int col2){
        List<int[]> neighbors = getNeighbors(row1,col1);
        for(int[] neighbor : neighbors){
            if(neighbor[0] == row2 && neighbor[1] == col2){
                return true;
            }
        }
        return false;
    }


    // Vérifie si une couleur a un chemin gagnant sur la grille
    public boolean hasPath(Color color){

        return true ;

    }

    public Color getWinner(){
        if(hasPath(Color.RED)){
            return Color.RED;
        }
        if(hasPath(Color.BLUE)){
            return Color.BLUE;
        }
        return Color.EMPTY;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for(int col = 0; col < this.size; col++){
            if(col < 10) sb.append(" "); // Alignement pour les colonnes à un chiffre
            sb.append(col).append(" ");// Affichage des numéros
        }
        sb.append("\n");
        
        for(int row = 0; row < this.size; row++){
            if(row < 10) sb.append(" "); 
            sb.append(row).append(" ");

            //indentation hex
            for(int indent = 0; indent < row; indent++){
                sb.append(" ");
            }

            for(int col = 0; col < this.size; col++){
                sb.append(cells[row][col].toSymbol()).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    

 

}
