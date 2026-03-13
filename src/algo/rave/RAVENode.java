package algo.rave;

import java.util.*;

import algo.mcts.MCTSNode;
import model.*;

/**
 * RAVENode étend MCTSNode en ajoutant les statistiques AMAF (All Moves As First) pour le RAVE .
 */
public class RAVENode  extends MCTSNode{
    
    /**
     * AMAF (All Moves As First) stats: pour chaque coup, on stocke le nombre de victoires et de visites
     */
    private Map<Integer, double[]> amafStats; // Map de coups (row*boardSize + col) à [raveWins, raveVisits]
    /**
     * boardSize est nécessaire pour calculer l'index unique d'un coup (row*boardSize + col) dans amafStats
     */
    private int boardSize;

    /**
     * Constructeur de RAVENode 
     * @param board l'état du plateau à ce noeud
     * @param parent le noeud parent, null pour la racine
     * @param move le coup qui a mené à ce noeud {row, col}
     * @param currentPlayerColor le joueur qui a joué pour arriver ici
     */
    public RAVENode(Board board, MCTSNode parent, int[] move, Color currentPlayerColor) {
        super(board, parent, move, currentPlayerColor);
        this.boardSize = board.getSize();
        this.amafStats = new HashMap<>();
    }

    /**
     * Met à jour les statistiques AMAF pour un coup en fonction de resulat de simulation
     * @param row la ligne du coup
     * @param col la colonne du coup 
     * @param isWin indique si le coup a mené à une victoire lors de la simulation
     */
    public void updateAMAF(int row, int col, boolean isWin) {
        int key = encodeMoves(row, col); 

        double[] stats = amafStats.get(key);
        if (stats == null) {
            stats = new double[]{0.0, 0.0}; // [raveWins, raveVisits]
            amafStats.put(key, stats);
        }
        stats[1] += 1; // Increment raveVisits
        if (isWin) {
            stats[0] += 1; // Increment raveWins
        }
    }

    /**
     * Encode un coup (row, col) en un index unique pour le stockage dans amafStats
     * @param row la ligne du coup
     * @param col la colonne du coup
     * @return l'index unique pour ce coup
     */
    private int encodeMoves(int row, int col) {
        return row * boardSize + col;
    }

    /**
     * Calcule le score RAVE pour un coup donné en combinant les statistiques MCTS classiques et AMAF
     * Formule de base : RAVEScore = (1 - beta) * qMCTS + beta * qRAVE
     * où qMCTS est le score MCTS classique (wins/visits) et qRAVE est le score RAVE (raveWins/raveVisits)
     * beta est sqrt(k / (3*visits + k)), k est une constante (eq à environ 300 visites)
     * 
     * si le coup n'a pas de stat amaf, retourne le score UCT classique pour ce coup
     * @param row la ligne du coup
     * @param col la colonne du coup
     * @return le score RAVE pour ce coup
     */
    public double getRAVEScore(int row, int col) {
        int key = encodeMoves(row, col);
        double[] amaf = amafStats.get(key);

        int visits = getVisits();
        
        double qMCTS = (visits > 0) ? getWins() / visits : 0.0; // Score MCTS classique
        
        if(amaf == null || amaf[1] == 0){
            // pas encore de stats rave, score UCT classique
            return getUCTValue(); 
        }

        double qRAVE = amaf[0] / amaf[1]; // Score RAVE

        // Paramètre de pondération, à ajuster selon le jeu et la taille du plateau
        double k = 300.0; 
        double beta = Math.sqrt(k / (3 * visits + k)); // ponderation entre mcts et rave
        
        MCTSNode parent = getParent();
        double exploration = 0.0;

        // UCB1 classique pour l'exploration
        if(parent != null && visits > 0){
            exploration = Math.sqrt(2) * Math.sqrt(Math.log(parent.getVisits()) / visits);
        }

        // Combinaison des scores MCTS et RAVE
        return (1 - beta) * qMCTS + beta * qRAVE + exploration;
    }

    public Map<Integer, double[]> getAmafStats() {
        return amafStats;
    }
 
    public int getBoardSize() {
        return boardSize;
    }
}
