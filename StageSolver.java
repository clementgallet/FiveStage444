package fivestage444;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

abstract class StageSolver extends Thread{

	protected static int[] stage_slice_list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
	protected static int[] stage_twist_list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
	protected static int[] stage_block_list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};

	protected SolverState ss;
	protected int[] move_list = new int[30];
	protected int metric;
	protected int goal;
	protected ObjectOutputStream pipeOut;
	protected ObjectInputStream pipeIn;

	protected void pushState(){
		formatMoves();

		CubeState cs = new CubeState();
		ss.cube.copyTo( cs );
		cs.scramble( goal, move_list );

		int r = rotateCube(cs);

		int[] move_list_all = new int[100];
		System.arraycopy(ss.move_list, 0, move_list_all, 0, ss.move_count);
		System.arraycopy(move_list, 0, move_list_all, ss.move_count, goal);
		print_move_list( goal, move_list );
		try{
			pipeOut.writeObject(new SolverState(cs, ss.metric, move_list_all, ss.move_count + goal, r));
		}
		catch (java.io.IOException ioe) {}
	}

	protected void pullState(){

		ss = null;
		while (ss == null) {
			try{
				ss = (SolverState) pipeIn.readObject();
				sleep(10);
			}
			catch (java.io.IOException ioe) {}
			catch (java.lang.ClassNotFoundException e) {}
			catch (java.lang.InterruptedException e) {}
		}

		metric = ss.metric;
		importState();
	}

	abstract void importState();


	protected void formatMoves(){
		int i;
		switch (metric) {
		case 0:
			for (i = 0; i < goal; ++i) {
				move_list[i] = stage_slice_list[move_list[i]];
			}
			break;
		case 1:
			for (i = 0; i < goal; ++i) {
				move_list[i] = stage_twist_list[move_list[i]];
			}
			break;
		case 2:
			for (i = 0; i < goal; ++i) {
				move_list[i] = stage_block_list[move_list[i]];
			}
			break;
		}
	}

	abstract int rotateCube(CubeState cs);
}
