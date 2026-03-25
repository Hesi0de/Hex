package algo.rave;
import java.util.ArrayList;
import java.util.List;

import algo.mcts.MCTS;
import algo.mcts.MCTSNode;
import model.Board;
import model.Color;


/**
 * RAVE (Rapid Action Value Estimation) étend MCTS.
 *
 * Différences clés par rapport à MCTS :
 * 1. Les nœuds sont des RAVENode (stats AMAF en plus)
 * 2. La sélection utilise le score RAVE au lieu de UCT pur
 * 3. La simulation retourne aussi la liste des coups joués (pour AMAF)
 * 4. La backpropagation met à jour les stats AMAF en plus des stats MCTS
 */
public class RAVE extends MCTS {
    /**
     * Pour stocker les coups joués et les couleurs des joueurs lors de la simulation, nécessaires pour la mise à jour AMAF
     */
    private List<int[]> lastSimMoves = new ArrayList<>();
    private List<Color> lastSimColors = new ArrayList<>();

    /**
     * Constructeur de RAVE 
     * @param budget le nombre de simulations à effectuer
     * @param explorationConstant le paramètre d'exploration pour le score RAVE
     */
    public RAVE(int budget, double explorationConstant) {
        super(budget, explorationConstant);
    }

    /**
     * Constructeur de RAVE avec explorationConstant par défaut
     * @param budget
     */
    public RAVE(int budget) {
        super(budget);
    }

    /**
     * retourne la liste des coups joués lors de la dernière simulation, nécessaire pour la mise à jour AMAF
     * @return la liste des coups joués lors de la dernière simulation
     */
    public List<int[]> getLastSimMoves() {
        return lastSimMoves;
    }

    /**
     * retourne la liste des couleurs des joueurs lors de la dernière simulation, nécessaire pour la mise à jour AMAF
     * @return la liste des couleurs des joueurs lors de la dernière simulation
     */
    public List<Color> getLastSimColors() {
        return lastSimColors;
    }

    /**
     * setter pour la liste des coups joués lors de la dernière simulation, nécessaire pour la mise à jour AMAF
     * @param lastSimMoves la liste des coups joués lors de la dernière simulation
     */
    public void setLastSimMoves(List<int[]> lastSimMoves) {
        this.lastSimMoves = lastSimMoves;
    }

    /**
     * setter pour la liste des couleurs des joueurs lors de la dernière simulation, nécessaire pour la mise à jour AMAF
     * @param lastSimColors la liste des couleurs des joueurs lors de la dernière simulation
     */
    public void setLastSimColors(List<Color> lastSimColors) {
        this.lastSimColors = lastSimColors;
    }

    /**
     * Override : la fonction de recherche principale reste la même, mais elle utilise des RAVENode et la backpropagation RAVE.
     * @param board l'état actuel du plateau
     * @param currentPlayer la couleur du joueur courant
     * @return le meilleur coup trouvé sous forme d'un tableau [row, col]
     */
    @Override
    public int[] search(Board board, Color currentPlayer) {
        //créer la racine 
        RAVENode r = new RAVENode(board.copy(), null, null, currentPlayer);
        for (int i = 0; i < getBudget(); i++) {
            RAVENode selected = (RAVENode) select(r);
            RAVENode expanded = (RAVENode) expand(selected);
            Color winner = simulate(expanded); // remplit lastSimMoves/lastSimColors RENOMMER SIMULATE SINON
            backpropagateRAVE(expanded, winner);             // utilise lastSimMoves/lastSimColors
        }
        return bestMove(r);
        
    }

    /**
     * Override : sélection basée sur le score RAVE au lieu de UCT pur.
     * Le score RAVE combine les stats MCTS classiques et les stats AMAF pour chaque enfant, en utilisant une formule de pondération.
     * @param node le nœud dont on veut trouver le meilleur enfant
     * @return le meilleur enfant selon le score RAVE
     */
    // passer child en argument a getrave pour calculer sur les enfants et pas les parents
    @Override
    public MCTSNode getBestChild(MCTSNode node) {
        MCTSNode best = null;
        double bestScore = -Double.MAX_VALUE;
        
        for (MCTSNode child : node.getChildren()) {
            int[] move = child.getMove(); 
            double score;
            
            if (node instanceof RAVENode && move != null) {

                score = ((RAVENode) node).getRAVEScore(child, move[0], move[1]); 
            } else { 
                score = child.getUCTValue();
            }
            
            if (score > bestScore) {
                bestScore = score;
                best = child;
            }
        }
        return best;
    }

        /**
         * Override : expansion qui crée un RAVENode au lieu d'un MCTSNode, avec les stats AMAF initialisées.
         * @param node le nœud à partir duquel on veut créer un enfant
         * @return le nœud enfant créé, ou le nœud lui-même s'il est terminal ou s'il n'y a plus de coups à essayer
         */
    //suppression ? utilité ?
        @Override
        public MCTSNode expand(MCTSNode node) {
            if (node.isTerminal()) {
                return node;
            }
            int[] untriedMove = node.getUntriedMove();
            if (untriedMove == null) {
                return node;
            }
     
            Board newBoard = node.getBoard().copy();
            newBoard.executeMove(untriedMove[0], untriedMove[1], node.getCurrentPlayerColor());
            Color nextColor = node.getCurrentPlayerColor().opponentColor();
     
            // RAVENode au lieu de MCTSNode
            RAVENode child = new RAVENode(newBoard, node, untriedMove, nextColor);
            node.addChild(child);
            return child;
        }

    /***
     * Simulation avec trace : joue aléatoirement jusqu'à la fin et stocke
     * tous les coups joués dans lastSimMoves et lastSimColors.
     * @param node le nœud à partir duquel on simule
     * @return la couleur du gagnant de la simulation
     */ 
    //renommmé simulate rave et override pour utiliser la fonction
    @Override
    public Color simulate(MCTSNode node) {
        Board stateBoard  = node.getBoard().copy();
        Color currentPlayer = node.getCurrentPlayerColor();
        lastSimMoves.clear();
        lastSimColors.clear();

        while (stateBoard.getWinner() == Color.EMPTY && !stateBoard.isBoardFull()) {
            List<int[]> available = stateBoard.getAvailableMoves();
            if (available.isEmpty()) break;
 
            int[] randomMove = available.get((int) (Math.random() * available.size()));
            stateBoard.executeMove(randomMove[0], randomMove[1], currentPlayer);
 
            lastSimMoves.add(randomMove);
            lastSimColors.add(currentPlayer);
 
            currentPlayer = currentPlayer.opponentColor();
        }
        
        return stateBoard.getWinner();
    }


    /**
     * Backpropagation RAVE : remonte les stats MCTS classiques ET met à jour
     * les stats AMAF pour tous les coups joués durant la simulation.
     *
     * @param node   le nœud depuis lequel on remonte
     * @param winner la couleur du gagnant
     */
    public void backpropagateRAVE(MCTSNode node, Color winner) {
        MCTSNode current = node;
 
        while (current != null) {
            current.incrVisits();
 
            // Stats MCTS classiques
            Color previousPlayer = current.getCurrentPlayerColor().opponentColor();
            if (previousPlayer == winner) {
                current.incrWins();
            }
 
            // Stats AMAF pour chaque coup joué dans la simulation
            if (current instanceof RAVENode) {
                RAVENode raveNode = (RAVENode) current;
                // On met à jour les stats AMAF pour tous les coups joués lors de la simulation
                for (int i = 0; i < lastSimMoves.size(); i++) {
                    int[] simMove  = lastSimMoves.get(i); // coup joué lors de la simulation
                    Color simColor = lastSimColors.get(i); // couleur du joueur qui a joué ce coup
                    boolean isWin  = (simColor == winner); // ce coup a-t-il mené à la victoire du joueur qui l'a joué ?
                    raveNode.updateAMAF(simMove[0], simMove[1], isWin); // met à jour les stats AMAF pour ce coup
                }
            }
 
            current = current.getParent(); // remonte au parent
        }
    }
}
