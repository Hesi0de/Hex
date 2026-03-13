package algo.mcts;

import model.Board;
import model.Color;
import model.RandomAIStrategy;

import java.util.List;
public class MCTS {
    private final int budget;
    private final double explorationConstant; 

    public MCTS(int budget, double explorationConstant){
        this.budget = budget;
        this.explorationConstant = explorationConstant;

    }

    public MCTS(int budget){
        this(budget, Math.sqrt(2));
    }


    public int getBudget(){
        return budget;
    }

    public double getExplorationConstant(){
        return explorationConstant;
    }

    /**
     * méthode qui recherche le meilleur coup à jouer depuis l'état ectuel du Board
     */
    public int[] search(Board board, Color currentPlayer){
        //créer la racine 
        MCTSNode r = new MCTSNode(board.copy(), null, null, currentPlayer);

        for (int i = 0; i<budget; i++){
            MCTSNode selected = select(r);
            MCTSNode expanded = expand(selected);
            Color winner = simulate(expanded);
            backpropagate(expanded, winner);
        }
        return bestMove(r);

    }

    /**
     * phase 1
     * On descend dans l'arbre en choisissant le meilleur enfant , càd UCB1
     * jusquèà trouver un noeud non exploré ou terminal
     */
    public MCTSNode select(MCTSNode node){
        while(!node.isTerminal() && node.isFullyExpanded()){
            MCTSNode next = getBestChild(node);
            if(next == null || next == node){ //pas d'enfant ou pas de meilleur enfant
                break;
            }
            node = next;
        }
        return node;

    }

    public MCTSNode getBestChild(MCTSNode node){
        if(node.getChildren().isEmpty()){
            return node; //pas d'enfant, on retourne le noeud lui même
        }
        MCTSNode best = null;
        double bestScore = Double.MIN_VALUE;

        for( MCTSNode child : node.getChildren()){
            double score = child.getUCTValue();
            if( score > bestScore){
                bestScore = score;
                best = child;
            }
        }
        return best;
    }
    /**
     * phase 2
     *  Ajoute un nouveau noeud non exploré
     * */ 
    public MCTSNode expand(MCTSNode node){
        if (node.isTerminal()){//pas d'exploration
            return  node;
        }
        int[] untriedMove = node.getUntriedMove();
        if(untriedMove == null){ //tous les coup sont déjà explorés
            return node;
        }

        //on créer le nv board avec le coup joué
        Board statBoard = node.getBoard().copy();
        statBoard.executeMove(untriedMove[0], untriedMove[1], node.getCurrentPlayerColor());
        Color nextColor = node.getCurrentPlayerColor().opponentColor();
        MCTSNode child = new MCTSNode(statBoard, node, untriedMove, nextColor);
        node.addChild(child);
        return child;
        

    }

    /**
     * Jouer aléatoirement jusqu'à la fin de la partie
     */
    public Color simulate(MCTSNode node){
        Board stateBoard = node.getBoard().copy();
        Color colorPlayer = node.getCurrentPlayerColor();
       
        while(stateBoard.getWinner() == Color.EMPTY && !stateBoard.isBoardFull()){
            List<int[]> moves = stateBoard.getAvailableMoves();
            if(moves.isEmpty()){
                break;
            }
            int[] randomMove = moves.get((int)(Math.random()*moves.size()));
            stateBoard.executeMove(randomMove[0], randomMove[1], colorPlayer);
            
            colorPlayer = colorPlayer.opponentColor();
        }
        return stateBoard.getWinner();
    }

    /**
     * phase 4
     * remonter le res depuis le noeud jusquèà la racine 
     */
    public void backpropagate(MCTSNode node, Color winner){
        MCTSNode currentNode = node;
        while(currentNode !=null){
            currentNode.incrVisits();

            //on incremente les victoires si ce noeud appartient au gagnant
            Color previousPlayer = currentNode.getCurrentPlayerColor().opponentColor();

            if(previousPlayer == winner){
                currentNode.incrWins();
            }

            currentNode = currentNode.getParent(); //on remonte au parent
        }
    }
    public int[] bestMove(MCTSNode r){
        MCTSNode best = null;
        int bestVisits = -1;

        for(MCTSNode child : r.getChildren()){
            if(child.getVisits()> bestVisits){
                bestVisits = child.getVisits();
                best = child;
            }
        }

        if(best == null){
            //coup aléa si aucun enfant
            List<int[]> moves = r.getBoard().getAvailableMoves();
            return moves.get((int)(Math.random() * moves.size()));
        }
        return best.getMove();
    }



}