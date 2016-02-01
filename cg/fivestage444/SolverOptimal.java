package cg.fivestage444;

import java.util.logging.Logger;

public class SolverOptimal {
	private static final Logger l = Logger.getLogger(SolverOptimal.class.getName());

	public String solve (CubeState cube, boolean inverse) {
		StrategyOptimal strategy;

		CubeAndSolution[] solutionArray = new CubeAndSolution[3];

		for (int length=0; length<15; length++){
			System.out.println("Testing length "+length);
			for(int j=0; j<3; j++){
				CubeState startingCube = new CubeState();
				cube.copyTo(startingCube);
				startingCube.leftMult(j * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
				solutionArray[j] = new CubeAndSolution(startingCube);
				solutionArray[j].current_stage = 0;
			}
			for (CubeAndSolution aSolutionArray : solutionArray) {
				strategy = new StrategyOptimal(length);
				strategy.processSolution(aSolutionArray);
			}
		}

		return "";
	}
}
