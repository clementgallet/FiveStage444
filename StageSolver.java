package fivestage444;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

abstract class StageSolver extends Thread{

	byte[] stage_slice_list;
	byte[] stage_twist_list;
	byte[] stage_block_list;

	protected SolverState ss;
	protected byte[] move_list = new byte[30];
	protected int metric;
	protected int goal;
	protected PipedOutputStream pipeOut;
	protected PipedInputStream pipeIn;
	protected boolean foundSol;

	StageSolver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		this.pipeIn = pipeIn;
		this.pipeOut = pipeOut;

		stage_slice_list = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
		stage_twist_list = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};
		stage_block_list = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};

	}

	protected void pushState(){
		foundSol = true;
		byte[] sol_move_list = new byte[30];
		System.arraycopy(move_list, 0, sol_move_list, 0, goal);
		formatMoves(sol_move_list);

		CubeState cs = new CubeState();
		cs.init();
		ss.cube.copyTo( cs );
		cs.scramble( goal, sol_move_list );

		int r = rotateCube(cs, sol_move_list);

		byte[] move_list_all = new byte[100];
		if( ss.move_list != null )
			System.arraycopy(ss.move_list, 0, move_list_all, 0, ss.move_count);
		System.arraycopy(sol_move_list, 0, move_list_all, ss.move_count, goal);
		//print_move_list( goal, sol_move_list );

		SolverState nss = new SolverState(ss.id, cs, ss.metric, move_list_all, ss.move_count + goal, r);
		writeState( nss );
	}

	protected void writeState( SolverState nss ){

		ObjectOutputStream stateOut = null;
		try{
			stateOut = new ObjectOutputStream (pipeOut);
			stateOut.writeObject(nss);
		}
		catch (java.io.IOException ioe) {}

	}

	protected void pushStopSignal(){
		ObjectOutputStream stateOut = null;
		try{
			stateOut = new ObjectOutputStream (pipeOut);
			stateOut.writeObject(new SolverState(-1, null, 0, null, 0, 0)); // id = -1 -> stop
		}
		catch (java.io.IOException ioe) { ioe.getMessage(); }
	}

	protected void closePipes(){
		try{
			pipeIn.close();
			pipeOut.close();
		}
		catch (java.io.IOException ioe) { ioe.getMessage(); }
	}

	protected boolean pullState(){

		ss = null;
		while (ss == null) {
			try{
				ObjectInputStream stateIn = new ObjectInputStream( pipeIn );
				ss = (SolverState) stateIn.readObject();
				if (ss == null) yield();
			}
			catch (java.io.IOException ioe) { ioe.printStackTrace(); }
			catch (java.lang.ClassNotFoundException e) { e.printStackTrace(); }
		}

		metric = ss.metric;
		if( ss.id == -1 ) return false;
		importState();
		return true;
	}

	abstract void importState();

	protected void formatMoves( byte[] sol_move_list){
		int i;
		switch (metric) {
		case 0:
			for (i = 0; i < goal; ++i) {
				sol_move_list[i] = stage_slice_list[sol_move_list[i]];
			}
			break;
		case 1:
			for (i = 0; i < goal; ++i) {
				sol_move_list[i] = stage_twist_list[sol_move_list[i]];
			}
			break;
		case 2:
			for (i = 0; i < goal; ++i) {
				sol_move_list[i] = stage_block_list[sol_move_list[i]];
			}
			break;
		}
	}

	abstract int rotateCube(CubeState cs, byte[] sol_move_list);
}
