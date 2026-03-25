package model;
import algo.rave.RAVE;

/**
 * RAVEStrategy est une implémentation de MoveStrategy qui utilise l'algorithme RAVE pour choisir le meilleur coup à jouer.
 */
public class RAVEStrategy implements MoveStrategy {
    /**
     * l'instance de l'algorithme RAVE utilisé pour la recherche de coup.
     */
    private RAVE rave;
    /**
     * le joueur pour lequel la stratégie est utilisé
     */
    private Color player;

    /**
     * Constructeur de RAVEStrategy
     * @param budget le nombre de simulations
     * @param player le joueur pour lequel la stratégie est utilisée
     */
    public RAVEStrategy(int budget, Color player) {
        this.rave = new RAVE(budget);
        this.player = player;
    }

    /**
     * Choisit le meilleur coup à jouer en utilisant l'algorithme RAVE.
     */
    @Override
    public int[] chooseMove(Board board) {
        return rave.search(board, player);
    }

    
    @Override
    public boolean decideSwap(Board board, int[] firstMove) {
        return false;
    }
}
