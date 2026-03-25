package algo.mcts;

import model.Board;
import model.Color;
import model.RandomAIStrategy;

import java.util.List;
/**
 * Classe principale de l'algorithme MCTS pour le jeu de Hex.
 */
public class MCTS {
    /**
     * Le nombre de simulations à effectuer pour chaque recherche de coup
     */
    private final int budget;
    
    /**
     * Le paramètre d'exploration utilisé dans la formule UCB1 pour équilibrer exploration et exploitation
     */
    private final double explorationConstant; 

    /**
     * Constructeur de MCTS
     * @param budget le nombre de simulations à effectuer pour chaque recherche de coup
     * @param explorationConstant le paramètre d'exploration utilisé dans la formule UCB1 pour équilibrer exploration et exploitation
     */
    public MCTS(int budget, double explorationConstant){
        this.budget = budget;
        this.explorationConstant = explorationConstant;

    }

    /**
     * Constructeur de MCTS avec explorationConstant par défaut
     * @param budget
     */
    public MCTS(int budget){
        this(budget, Math.sqrt(2));
    }

    /**
     * getter pour le budget de simulations
     * @return le nombre de simulations à effectuer pour chaque recherche de coup
     */
    public int getBudget(){
        return budget;
    }

    /**
     * getter pour le paramètre d'exploration
     * @return le paramètre d'exploration utilisé dans la formule UCB1 pour équilibrer exploration et exploitation
     */
    public double getExplorationConstant(){
        return explorationConstant;
    }

    /**
     * méthode qui recherche le meilleur coup à jouer depuis l'état ectuel du Board
     * @param board l'état actuel du Board
     * @param currentPlayer la couleur du joueur courant
     * @return un tableau d'entiers représentant le coup à jouer (ligne, colonne)
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
     * @param node le noeud à partir duquel commencer la sélection
     * @return le noeud sélectionné pour l'expansion
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


    /**
     * Sélectionne le meilleur enfant d'un noeud en utilisant la formule UCB1
     * @param node le noeud dont on veut trouver le meilleur enfant
     * @return le meilleur enfant du noeud donné en paramètre selon la formule UCB1
     */
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
     * @param node le noeud à partir duquel on veut faire l'expansion
     * @return le noeud enfant nouvellement créé ou le noeud lui même si c'est un noeud terminal ou si tous les coups ont déjà été explorés
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
     * @param node le noeud à partir duquel on veut faire la simulation
     * @return la couleur du gagnant de la partie simulée
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
     * @param node le noeud à partir duquel on veut faire la backpropagation
     * @param winner la couleur du gagnant de la partie simulée
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

    /**
     * phase 5 : choisir le meilleur coup à jouer depuis la racine, càd l'enfant avec le plus de visites
     * @param r la racine de l'arbre MCTS
     * @return un tableau d'entiers représentant le coup à jouer (ligne, colonne) correspondant à l'enfant de la racine avec le plus de visites.
     *  Si la racine n'a pas d'enfant, retourne un coup aléatoire parmi les coups disponibles sur le board de la racine.
     */
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