package cg.fivestage444;

import java.util.logging.Logger;

public class SolverSlack {
	private static final Logger l = Logger.getLogger(SolverSlack.class.getName());

	public String solve (CubeState cube, boolean inverse) {
		StrategySlack strategy;

		CubeAndSolution[] solutionArray = new CubeAndSolution[3];
		for(int j=0; j<3; j++){
			CubeState startingCube = new CubeState();
			cube.copyTo(startingCube);
			startingCube.leftMult(j * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
			solutionArray[j] = new CubeAndSolution(startingCube);
			solutionArray[j].current_stage = 0;
		}

		for (int slack=0; slack<1; slack++){
			for (int i=0; i<solutionArray.length; i++){
				strategy = new StrategySlack(slack);
				strategy.processSolution(solutionArray[i]);
			}
		}

		CubeAndSolution fullSolution = StrategySlack.best_solution; /* This is *the* solution! */
		if(!fullSolution.isSolved())
			l.severe("Not a solution!");
		System.out.print(fullSolution.move_length+" ");
		if(inverse)
			return fullSolution.outputGenerator(cube);
		else
			return fullSolution.outputSolution();
	}
}
