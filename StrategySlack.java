package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

import java.util.logging.Logger;

public class StrategySlack extends Strategy{
	private static final Logger l = Logger.getLogger(StrategySlack.class.getName());

	int slack;
	static CubeAndSolution best_solution;

	StrategySlack(int slack){
		this.slack = slack;
	}

	@Override
	public boolean processSolution(CubeAndSolution solution){
		Stage s = solution.toNextStage();
		l.info("Found a solution on stage "+(solution.current_stage-1));
		l.info(solution.debugOutputMoves());
		if (s == null){ // Last stage, we are done.
			if((best_solution == null) || (solution.move_length < best_solution.move_length))
				best_solution = solution;
			return true;
		}
		StageSolver ss = new StageSolver(solution, this);

		int length;
		int best_solution_length = (best_solution == null) ? 1000 : best_solution.move_length;
		for (length=0; length<best_solution_length-solution.move_length; length++){
			ss.search(length);
			if(ss.solution_found) break;
		}
		for (int extra=1; extra<=slack; extra++){
			ss = new StageSolver(solution, new StrategySlack(slack-extra));
			ss.search(length+extra);
		}
		return false;
	}
}
