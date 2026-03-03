package model;

<<<<<<< HEAD
import algo.MCTS;


public class MCTSStrategy implements MoveStrategy{
=======
import algo.mcts.MCTS;


public class MCTSStrategy implements MoveStrategy{

>>>>>>> 6759424f768f3947f9e88ab7e8a2b20a7c3ab9f2
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
