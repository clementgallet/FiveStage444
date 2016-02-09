package cg.fivestage444;

import java.util.logging.Logger;

public class SolverOptimal {
	private static final Logger l = Logger.getLogger(SolverOptimal.class.getName());

	public String solve (CubeState cube, boolean inverse) {
		Thread[] threads = new Thread[3];

		for (int length=0; length<12; length++){
			System.out.println("Testing length "+length);
			for(int j=0; j<3; j++){
				CubeState startingCube = new CubeState();
				cube.copyTo(startingCube);
				startingCube.leftMult(j * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
				Worker wk = new Worker();
				wk.st = new StrategyOptimal(length);
				wk.cas = new CubeAndSolution(startingCube);
				wk.cas.current_stage = 0;
				threads[j] = new Thread(wk);
			}
			for (int j=1; j<2; j++) {
				threads[j].start();
			}
			for (int j=1; j<2; j++) {
				try {
					threads[j].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return "";
	}
}
