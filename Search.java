package cg.fivestage444;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.logging.Logger;

final class Search {
	private static final Logger l = Logger.getLogger(StageSolver.class.getName());

	public String solve (CubeState cube, boolean inverse) {
		int i;

		/* We use a priority queue to store the partial solutions.
		 * The value used to order the solutions is:
		 * the length of the current solution + the lower bound of the best solution of the next stage.
		 */
		PriorityQueue<CubeAndSolution> solutionQueue = new PriorityQueue<CubeAndSolution>();

		/* We use a temporary array to export the priority queue to, before converting to... */
		CubeAndSolution[] solutionArray = new CubeAndSolution[3];

		/* ... the StageSolver objects, which prepare for the next stage search to come. */
		StageSolver[] solversSet;

		/* The initial positions to search from.
		 * We have 3 starting cubes because we can choose to either solve with
		 * white/yellow, red/orange or blue/green on front/back.
		 * We cannot use the inverse trick used on 3x3x3 solvers to add 3 more positions to solve,
		 * because we would need to inverse a position, and centers do not form a group structure,
		 * so we are not allowed to do that.
		 */
		for(int j=0; j<3; j++){
			CubeState startingCube = new CubeState();
			cube.copyTo(startingCube);
			startingCube.leftMult(j * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
			solutionArray[j] = new CubeAndSolution(startingCube);
		}

		/* We now start the main loop to find a solution, going through every stage.
		 */
		for (int s=0; s<5; s++){
			l.info("Start a new stage search");

			/* We will process better solutions earlier if we sort the array of partial solutions. */
			Arrays.sort(solutionArray);

			/* We build our StageSolver objects, one for each partial solution we found.
			 */
			solversSet = new StageSolver[solutionArray.length];
			for(int length=0; length<solutionArray.length; length++){
				solversSet[length] = new StageSolver(solutionArray[length], solutionQueue);
			}

			/* Now, we seach for a solution of incremental length on every StageSolver.
			 * If one of the StageSolvers outputs true, it means that the search must be stopped,
			 * as we reached the limit.
			 */
			OUT: for(int length=0; length<100; length++){
				for(int solver=0; solver<solversSet.length; solver++){
					if(solversSet[0].search(length))
						break OUT;
				}
			}

			/* Then we convert all our partial solutions with a new stage solved to our array,
			 * and we clear the priority queue.
			 */
			solutionArray = solutionQueue.toArray(new CubeAndSolution[0]);
			solutionQueue.clear();
		}

		CubeAndSolution fullSolution = solutionArray[0]; /* This is *the* solution! */
		if(inverse)
			return fullSolution.outputGenerator();
		else
			return fullSolution.outputSolution();
	}
}
