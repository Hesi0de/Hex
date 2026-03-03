package model;

import algo.MCTS;


public class MCTSStrategy implements MoveStrategy{
	private MCTS mcts;
	private Color player;

	public MCTSStrategy(int budget, Color player){

		this.mcts = new MCTS(budget);
		this.player = player;


	}

	@Override
	public int[] chooseMove(Board board){

		return mcts.search(board, player);
	}



}
