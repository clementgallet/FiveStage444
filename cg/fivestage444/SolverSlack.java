package cg.fivestage444;

import java.util.logging.Logger;

public class SolverSlack {
	private static final Logger l = Logger.getLogger(SolverSlack.class.getName());
	private int max_slack = 5;

	public SolverSlack(int max_slack){
		this.max_slack = max_slack;
	}

	public String solve (CubeState cube, boolean inverse) {
		StrategySlack strategy;

		CubeAndSolution[] solutionArray = new CubeAndSolution[12];
		Thread[] threads = new Thread[12];

		for (int slack=0; slack<=max_slack; slack++){
			System.out.println("Testing slack "+slack);
			for(int j=0; j<12; j++){
				CubeState startingCube = new CubeState();
				cube.copyTo(startingCube);
				if ((j/3) > 0)
					startingCube.move((j/3)-1);
				startingCube.leftMult((j%3) * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
				Worker wk = new Worker();
				wk.st = new StrategySlack(slack);
				wk.cas = new CubeAndSolution(startingCube);
				wk.cas.current_stage = 0;
				threads[j] = new Thread(wk);
			}
			for (int j=0; j<12; j=j+3) {
				threads[j].start();
			}
			for (int j=0; j<12; j=j+3) {
				try {
					threads[j].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
