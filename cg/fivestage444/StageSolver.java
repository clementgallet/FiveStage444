package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

import java.util.logging.Logger;

class StageSolver {
	private static final Logger l = Logger.getLogger(StageSolver.class.getName());

	private final Strategy strategy; /* what to do when we find a solution */
	private final CubeAndSolution cas; /* the starting position */
	private final Stage[] stage_list = new Stage[20]; /* a set of stages to use during the search, to avoid creating objects */
	private final byte[] move_list = new byte[100]; /* the current stage move list */
	private final int n_moves; /* the number of moves in this stage. Set to this_stage.getMovesNumber() */
	public boolean solution_found = false; /* did a solution have been found? */

	public StageSolver(CubeAndSolution cas, Strategy strategy){
		this.strategy = strategy;
		this.cas = cas;
		stage_list[0] = cas.toCurrentStage();
		try {
			for (int s=1; s<20; s++)
				stage_list[s] = stage_list[0].getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		n_moves = stage_list[0].getMovesNumber();
		l.fine("Initialise with class "+stage_list[0].getClass().getName());
	}

	/**
	 * Search for solutions using move sequences of at most length moves.
	 * Each time a solution is found, the push method is called.
	 * The search can be stopped at any time depending on what the push function returns.
	 * @param length the maximum length of the solution
	 * @return false: continue to search, true: don't go further.
	 */
	public boolean search(int length){
		/* Add the special case of length 1 here */
		if ((length == 1) && (stage_list[0].isSolved()))
			return false;
		long mask; // Filter certain moves
/*		if (cas.current_stage == 1)
			mask = 0b001000101001100100000000000011001010L;
		else*/
			mask = Moves.moves_mask[Moves.N_STAGE_MOVES];
		return search(length, 0, mask);
	}

	/**
	 * The main recursive search function.
	 * @param depth the current number of moves we can still apply.
	 * @param moves_done the current number of moves we have already applied.
	 * @param mask which moves are allowed.
	 * @return false: continue to search, true: don't go further.
	 */
	private boolean search(int depth, int moves_done, long mask){
		if (depth == 0){
			if( stage_list[moves_done].isSolved() ) {
				return push(moves_done);
			}
			return false;
		}
		for (int move = 0; mask != 0 && move < n_moves; move++, mask >>>= 1) {
			if (( mask & 1L ) == 0) /* not necessary to try this move */
				continue;
			stage_list[moves_done].moveTo( move, stage_list[moves_done+1] ); /* moving */
			int nd = stage_list[moves_done+1].pruning(); /* distance of new position to solved */
			if (nd > depth-1) continue; /* no need to go further, */
			if ((nd != depth-1) && (nd + depth - 1 < 5)) continue; /* When we are too close, we must not solved too early */
			move_list[moves_done] = (byte)move; /* append the move to the move list */
			//min1_list = Math.min( min1_list, moves_done );
			if (search (depth - 1, moves_done + 1, Moves.moves_mask[move])) return true; /* recursive call */
		}
		return false;
	}

	/**
	 * This function is called for every solution found.
	 * From the starting cube, we apply the move sequence which was found to be a solution.
	 * Then we convert to the next stage structure to peek at the pruning value,
	 * which is a lower bound of the best solution of the next stage.
	 * We use the current solution length + this lower bound to sort the current solutions we found.
	 * Then, we decide if we should insert that solution into the priority queue.
	 * @param solution_length the length of the solution.
	 * @return if we want to stop the search.
	 */
	boolean push(int solution_length){
		l.finer("Found a solution.");
		solution_found = true;
		CubeAndSolution newCas = null;
		try {
			newCas = (CubeAndSolution) cas.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		for (int move = 0; move < solution_length; move++ ){
			newCas.move(Moves.stage2moves[move_list[move]]);
		}
		l.finest("Solution is "+newCas.debugOutputMoves());
		newCas.rotate();
		boolean doWeStop = strategy.processSolution(newCas);
		return doWeStop;
	}
}
