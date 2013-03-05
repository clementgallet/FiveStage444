package cg.fivestage444.Stages;

abstract public class Stage {

	/**
	 * Check if the state is solved.
	 * @return if solved.
	 */
	abstract public boolean isSolved();

	/**
	 * We apply a move on this stage, and storing the result on another stage.
	 * @param m the move to apply. It belongs to the own stage domain (0..this.getMovesNumber())
	 * @param s the stage to store the result of the move.
	 */
	abstract public void moveTo( int m, Stage s );

	/**
	 * We want to know given a certain state a lower bound of the minimum number of moves to solved that state.
	 * @return a lower bound of the distance to the closest solved state.
	 */
	abstract public int pruning();

	/**
	 * Returns what are the moves allowed in this stage.
	 * Because the moves of the different stages are all included in each other (building a chain),
	 * the move array Moves.stage2moves is structured so that the set of moves in a particular stage is always of the
	 * form 0..N, so that this function only have to return the integer N.
	 * @return how many moves are allowed in this stage.
	 */
	abstract public int getMovesNumber();

	/**
	 * We are searching for a limited number of solutions, to store the best ones into our priority queue.
	 * @return how many solutions do we have to find during this stage search. Not all solutions will be kept.
	 */
	abstract public int howManySolutions();

	/**
	 * We keep a limited number of solutions of this stage for the next stage search.
	 * It is usually lower than the result of howManySolutions().
	 * @return how many solutions are we keeping for the next search.
	 */
	abstract public int howManyAttempts();
}
