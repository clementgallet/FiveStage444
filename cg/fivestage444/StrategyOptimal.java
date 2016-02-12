package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

import java.util.logging.Logger;

public class StrategyOptimal extends Strategy{
	private static final Logger l = Logger.getLogger(StrategyOptimal.class.getName());

	final int olength;

	StrategyOptimal(int olength){
		this.olength = olength;
	}

	@Override
	public boolean processSolution(CubeAndSolution solution){
		Stage s = solution.toNextStage();
		l.info("Found a solution on stage "+(solution.current_stage-1));
		//l.info(solution.debugOutputMoves());
		if (s == null){ // Last stage, we are done.
			if(solution.move_length == olength) {
				if (!solution.isSolved())
					l.severe("Not a solution!");
				System.out.println(Moves.print_metrics(solution.move_length, solution.move_list) + " " + solution.outputSolution());
				//System.out.println(solution.move_length + " " + solution.outputSolutionSep());
			}
			return false;
		}
		StageSolver ss = new StageSolver(solution, this);

		int length;
		for (length=0; length<=(olength-solution.move_length); length++){
			ss.search(length);
		}
		return false;
	}
}
