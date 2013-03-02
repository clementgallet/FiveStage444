package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

import java.util.PriorityQueue;

public class StageSolver {

	PriorityQueue<CubeAndSolution> queue; /* the queue to push solutions of this stage to */
	CubeAndSolution cas; /* the starting position */
	Stage[] stage_list = new Stage[20]; /* a set of stages to use during the search, to avoid creating objects */
	byte[] move_list = new byte[100]; /* the current stage move list */
	int n_moves; /* the number of moves in this stage. Set to this_stage.getMovesNumber() */

	public StageSolver(CubeAndSolution cas, PriorityQueue<CubeAndSolution> queue){
		this.queue = queue;
		this.cas = cas;
		stage_list[0] = cas.toCurrentStage();
		n_moves = stage_list[0].getMovesNumber();
	}

	/**
	 * Search for solutions using move sequences of at most length moves.
	 * Each time a solution is found, the push method is called.
	 * The search can be stopped at any time depending on what the push function returns.
	 * @param length the maximum length of the solution
	 */
	public void search(int length){
		search(length, 0, Moves.N_STAGE_MOVES);
	}

	/**
	 * The main recursive search function.
	 * @param depth the current number of moves we can still apply.
	 * @param moves_done the current number of moves we have already applied.
	 * @param last_move what was the last move applied
	 * @return false: continue to search, true: don't go further.
	 */
	private boolean search(int depth, int moves_done, int last_move){
		if( stage_list[moves_done].isSolved() ){
			if( depth == 0 && push() );
		}
		long mask = Moves.moves_mask[last_move]; /* we use a mask to filter certain combinasion of moves. */
		for (int move = 0; mask != 0 && move < n_moves; move++, mask >>>= 1) {
			if (( mask & 1L ) == 0) /* not necessary to try this move */
				continue;
			stage_list[moves_done].moveTo( move, stage_list[moves_done+1] ); /* moving */
			if (stage_list[moves_done+1].pruning() > depth-1) continue; /* no need to go further, */
			move_list[moves_done] = (byte)move; /* append the move to the move list */
			//min1_list = Math.min( min1_list, moves_done );
			if (search (depth - 1, moves_done + 1, move)) return true; /* recursive call */
		}
		return false;
	}

	/**
	 * This function is called for every solution found.
	 * From the starting cube, we apply the move sequence which was found to be a solution.
	 * Then we convert to the next stage structure to peek at the pruning value,
	 * which is a lower bound of the best solution of the next stage.
	 * We use the current solution length + this lower bound to sort the current solutions we found.
	 * @return if we want to stop the search.
	 */
	boolean push(){
		CubeAndSolution newCas = null;
		try {
			newCas = (CubeAndSolution) cas.clone();
		} catch (CloneNotSupportedException e) {
		}
		for (int move = 0; move < n_moves; move++ ){
			newCas.move(Moves.stage2moves[move]);
		}
		newCas.rotate();
		Stage s = newCas.toNextStage();
		if(s != null)
			newCas.comparator = newCas.move_length + s.pruning();
		else
			newCas.comparator = newCas.move_length;

		queue.add(newCas);
		return true;
	}
}
