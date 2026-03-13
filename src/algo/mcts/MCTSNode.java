package algo.mcts;

import model.Board;
import model.Color;

import java.util.ArrayList;
import java.util.List;

public class MCTSNode {
    private Board board; //état du plateu à ce noeud
    private MCTSNode parent; //noeud parent , null pour la racine
    private List<MCTSNode> children; // les noeuds déjà explorés 
    private List<int[]> untriedMoves; // coups pas encore explorés
    private int[] move; // Le coup qui a mené à ce noued {row, col}
    private Color currentPlayerColor; // Le joueur qui VIENT de jouer pour arriver ici
    private int visits; //nbre de fois ce noeud a été visité
    private double wins;//nbre de victoires enregistrées

    public MCTSNode(Board board, MCTSNode parent, int[] move, Color currentPlayerColor) {
        this.board = board;
        this.parent = parent;
        this.move = move;
        this.currentPlayerColor = currentPlayerColor;
        this.children = new ArrayList<>();
        this.untriedMoves = new ArrayList<>(board.getAvailableMoves());
        this.visits = 0;
        this.wins = 0.0;
    }
    /**
     * Formule UCB1 (Upper Confidence Bound 1), Score pour la séléction
     *UCB1 = wins/visits + C * sqrt(ln(parentVisits)/visits)
    */
    public double getUCTValue() {//UCB1
        if (visits == 0){
            return Integer.MAX_VALUE; // Noeud jamais visité; Priorité max
        }

        if(parent == null){
            return wins / visits; // Racine, pas de terme d'exploration
        }
        double explorationConstant = Math.sqrt(2);
        // C = racine(2) est standard, à ajuster,peut être en param
        double exploitation = wins / visits;
        return exploitation + explorationConstant * Math.sqrt(Math.log(parent.getVisits()) / visits);
    }

    /**
     * méthode qui ajout un enfant à ce noeud
     */
    public void addChild(MCTSNode child) {
        children.add(child);
    }

    /**
     * retourne un coup non encore exploré et le retire de la liste  ([row, col])
     */
    public int[] getUntriedMove(){
        if(untriedMoves.isEmpty()){
            return null;
        }

        //choisir un coup aléa parmi les non explorés
        int index = (int) (Math.random() * untriedMoves.size());
        return untriedMoves.remove(index);
    }

    public void incrVisits(){
        visits++;
    }

    public void incrWins(){
        wins++;
    }
    /* 
    public void updateStats(double value) {
        visits++;
        wins += value;
    }*/

    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * true si tous les coup ont déjà été explorés
     */
    public boolean isFullyExpanded(){
        return untriedMoves.isEmpty();
    }

    /**
     * true is le noeud est terminal, càd la partie est terminée
     */
    public boolean isTerminal(){
        return board.getWinner() !=Color.EMPTY || board.isBoardFull();
    }
    
    public List<MCTSNode> getChildren() {
        return children;
    }
    public Board getBoard() { 
        return board; 
    }
    public int[] getMove() { 
        return move; 
    }
    public int getVisits() {
        return visits; 
    }
    public MCTSNode getParent(){
        return parent;
    }
    public double getWins(){
        return wins;
    }

    public Color getCurrentPlayerColor(){
        return currentPlayerColor;
    }

}