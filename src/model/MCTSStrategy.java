package model;

import algo.mcts.MCTS;

/**
 * classe qui implémente la stratégie de jeu basée sur l'algorithme MCTS (Monte Carlo Tree Search).
 */
public class MCTSStrategy implements MoveStrategy{
	/**
	 * représente l'instance de l'algorithme MCTS utilisé pour choisir les coups.
	 */
	private MCTS mcts;
	/**
	 * représente la couleur du joueur pour lequel la stratégie est utilisée.
	 */
	private Color player;

	/**
	 * constructeur de la classe MCTSStrategy qui initialise l'instance de MCTS avec un budget donné et la couleur du joueur.
	 * @param budget le nombre de simulations que l'algorithme MCTS doit effectuer pour choisir un coup.
	 * @param player la couleur du joueur pour lequel la stratégie est utilisée.
	 */
	public MCTSStrategy(int budget, Color player){

		this.mcts = new MCTS(budget);
		this.player = player;


	}

	/**
	 * méthode qui utilise l'algorithme MCTS pour choisir le meilleur coup à jouer sur le plateau de jeu donné.
	 */
	@Override
	public int[] chooseMove(Board board){

		return mcts.search(board, player);
	}



}
