package cg.fivestage444;

import java.util.logging.Logger;

public class SolverOptimal {
	private static final Logger l = Logger.getLogger(SolverOptimal.class.getName());
	private int max_depth = 20;

	public SolverOptimal(int max_depth){
		this.max_depth = max_depth;
	}

	public String solve (CubeState cube, boolean inverse) {
		Thread[] threads = new Thread[4];

		for (int length=0; length<=max_depth; length++){
			System.out.println("Testing length "+length);
			int ori = 1;
			for(int j=0; j<4; j++){
				CubeState startingCube = new CubeState();
				cube.copyTo(startingCube);
				if (j > 0)
					startingCube.move(j-1);
				startingCube.leftMult(ori * 16); /* Repaint colors using the rotation along the UFL-DBR axis j times */
				Worker wk = new Worker();
				wk.st = new StrategyOptimal(length);
				wk.cas = new CubeAndSolution(startingCube);
				wk.cas.current_stage = 0;
				threads[j] = new Thread(wk);
			}
			for (int j=0; j<1; j++) {
				threads[j].start();
			}
			for (int j=0; j<1; j++) {
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
