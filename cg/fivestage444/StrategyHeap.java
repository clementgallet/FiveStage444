package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

import java.util.PriorityQueue;

public class StrategyHeap extends Strategy {
	private final PriorityQueue<CubeAndSolution> heap; /* the heap to push solutions of this stage to */
	public int n_solutions; /* the number of solutions found */

	/* We are searching for a limited number of solutions, to store the best ones into our heap. */
	public int[] max_solutions = {100, 100, 100, 100, 1};

	/* We keep a limited number of solutions of this stage for the next stage search.
	 * It is usually lower than the result of howManySolutions().
	 */
	public int[] max_attempts = {50, 50, 50, 50, 1};

	public StrategyHeap(PriorityQueue<CubeAndSolution> heap){
		this.heap = heap;
	}

	@Override
	public boolean processSolution(CubeAndSolution solution){
		Stage s = solution.toNextStage();
		if(s != null)
			solution.comparator = solution.move_length + s.pruning();
		else
			solution.comparator = solution.move_length;

		/* We have to determine if we need to insert this solution into the heap. */
		if(heap.size() < max_attempts[solution.current_stage-2]) /* We still have room for this solution */
			heap.add(solution);
		else{
			/* We don't have room anymore. To know if we need to insert this solution,
			 * we peek at the worst solution of the heap, and compare against this solution.
			 * If this solution is better, we replace the worst solution with this one.
			 * If not, we do nothing.
			 */
			CubeAndSolution worseCubeYet = heap.peek();
			if(worseCubeYet.comparator > solution.comparator){
				heap.poll();
				heap.add(solution);
			}
		}
		n_solutions++;
		return n_solutions >= max_solutions[solution.current_stage-2];
	}
}
