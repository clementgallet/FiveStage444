package fivestage444;

import static fivestage444.Constants.*;

import java.util.Random;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;

public final class FiveStage444 {

// EDGE CONVENTION:

// There are 24 "edge" cubies, numbered 0 to 23.
// The home positions of these cubies are labeled in the diagram below.
// Each edge cubie has two exposed faces, so there are two faces labelled with
// each number.

//             -------------
//             |    5  1   |
//             |12   UP  10|
//             | 8       14|
//             |    0  4   |
// -------------------------------------------------
// |   12  8   |    0  4   |   14 10   |    1  5   |
// |22  LHS  16|16  FRT  21|21  RHS  19|19  BAK  22|
// |18       20|20       17|17       23|23       18|
// |    9 13   |    6  2   |   11 15   |    7  3   |
// -------------------------------------------------
//             |    6  2   |
//             |13  DWN  11|
//             | 9       15|
//             |    3  7   |
//             -------------


// There are 8 "corner" cubies, numbered 0 to 7.
// The home positions of these cubies are labeled in the diagram below.
// Each corner cubie has three exposed faces, so there are three faces labelled
// with each number. Asterisks mark the primary facelet position. Orientation
// will be the number of clockwise rotations the primary facelet is from the
// primary facelet position where it is located.

//            +----------+
//            |*3*    *2*|
//            |    UP    |
//            |*0*    *1*|
// +----------+----------+----------+----------+
// | 7      0 | 0      1 | 1      2 | 2      3 |
// |   LEFT   |  FRONT   |  RIGHT   |  BACK    |
// | 7      4 | 4      5 | 5      6 | 6      7 |
// +----------+----------+----------+----------+
//            |*4*    *5*|
//            |   DOWN   |
//            |*7*    *6*|
//            +----------+

//For squares calculation, corners are numbered as given below.
//This makes the corners look much like a set of 8 edges of a
//given pair of inner slices.
//            +----------+
//            | 5      1 |
//            |    UP    |
//            | 0      4 |
// +----------+----------+----------+----------+
// | 5      0 | 0      4 | 4      1 | 1      5 |
// |   LEFT   |  FRONT   |  RIGHT   |  BACK    |
// | 3      6 | 6      2 | 2      7 | 7      3 |
// +----------+----------+----------+----------+
//            | 6      2 |
//            |   DOWN   |
//            | 3      7 |
//            +----------+

// There are 24 "center" cubies. For the squares analysis, they are numbered 0 to 23 as shown.
//             -------------
//             |           |
//             |    2  3   |
//             |    0  1   |
//             |           |
// -------------------------------------------------
// |           |           |           |           |
// |    9  8   |   16 18   |   12 13   |   22 20   |
// |   11 10   |   17 19   |   14 15   |   23 21   |
// |           |           |           |           |
// -------------------------------------------------
//             |           |
//             |    4  5   |
//             |    6  7   |
//             |           |
//             -------------

// For the other analyses, they are numbered 0 to 23 as shown.
//             -------------
//             |           |
//             |    3  1   |
//             |    0  2   |
//             |           |
// -------------------------------------------------
// |           |           |           |           |
// |   10  8   |   16 19   |   14 12   |   21 22   |
// |    9 11   |   18 17   |   13 15   |   23 20   |
// |           |           |           |           |
// -------------------------------------------------
//             |           |
//             |    6  4   |
//             |    5  7   |
//             |           |
//             -------------


	private static String default_datafile_path = "";
	public static Thread getS;

	public static void main(String[] args){

		int random_count = 100;
		int metric = 0;

		Symmetry.initSymTables();
		Symmetry.initInvSymIdx();
		Symmetry.initSymIdxMultiply();
		Symmetry.initMoveConjugate();

		new Tables().init_all ();
		CubePruningTableMgr.init_pruning_tables (metric);
		CubePruningTableStage1.analyse();
		try{
			initPipes();
		} catch(java.io.IOException e) { e.printStackTrace(); }

		/* Get solutions */
		getS = new Thread(new Runnable() {
			public void run() {
				getSolutions();
			}
		});

		/* Start getting the solutions */
		getS.start();

		do_random_cubes (metric, random_count);

		/* Tells the threads that there are no more scrambles */
		stopThreads();

		/* Wait for the end of every thread */
		try{
			getS.join();
		} catch (java.lang.InterruptedException ie) { ie.getMessage(); }

		/* Print statistics */
		Statistics.print();

		/* Closing the last pipes */
		try{
			pipeStage01out.close();
			pipeStage50in.close();
		}
		catch (java.io.IOException ioe) { ioe.getMessage(); }
	}

	public static void do_random_cubes (int metric, int count) {
	int i, i1;
	//Random r = new Random();
	Random r = new Random(42);
	byte[] random_list = new byte[160];	//must be >= scramble_len
	CubeState solveme = new CubeState();
	int scramble_len = 100;

	for (i = 1; i <= count; ++i) {
		int j;
		solveme.init ();
		for (j = 0; j < scramble_len; ++j) {
			random_list[j] = (byte)r.nextInt(36);
		}
		solveme.scramble(scramble_len, random_list);
		System.out.println ("scramble: ");
		print_move_list (scramble_len, random_list);
		solveit4x4x4IDA (solveme, metric);
	}

}

	public static PipedOutputStream pipeStage01out = null;
	public static PipedInputStream pipeStage01in = null;
	public static PipedOutputStream pipeStage12out = null;
	public static PipedInputStream pipeStage12in = null;
	public static PipedOutputStream pipeStage23out = null;
	public static PipedInputStream pipeStage23in = null;
	public static PipedOutputStream pipeStage34out = null;
	public static PipedInputStream pipeStage34in = null;
	public static PipedOutputStream pipeStage45out = null;
	public static PipedInputStream pipeStage45in = null;
	public static PipedOutputStream pipeStage50out = null;
	public static PipedInputStream pipeStage50in = null;

	public static Stage1Solver stage1Solver;
	public static Stage2Solver stage2Solver;
	public static Stage3Solver stage3Solver;
	public static Stage4Solver stage4Solver;
	public static Stage5Solver stage5Solver;

	public static void initPipes () throws java.io.IOException {

		System.out.println("Init pipes");

		int PIPE_SIZE = 4096;

		pipeStage01out = new PipedOutputStream ();
		pipeStage01in = new PipedInputStream (pipeStage01out, PIPE_SIZE);
		pipeStage12out = new PipedOutputStream ();
		pipeStage12in = new PipedInputStream (pipeStage12out, PIPE_SIZE);
		pipeStage23out = new PipedOutputStream ();
		pipeStage23in = new PipedInputStream (pipeStage23out, PIPE_SIZE);
		pipeStage34out = new PipedOutputStream ();
		pipeStage34in = new PipedInputStream (pipeStage34out, PIPE_SIZE);
		pipeStage45out = new PipedOutputStream ();
		pipeStage45in = new PipedInputStream (pipeStage45out, PIPE_SIZE);
		pipeStage50out = new PipedOutputStream ();
		pipeStage50in = new PipedInputStream (pipeStage50out, PIPE_SIZE);

		System.out.println("Share pipes");

		stage1Solver = new Stage1Solver(pipeStage01in, pipeStage12out);
		stage2Solver = new Stage2Solver(pipeStage12in, pipeStage23out);
		stage3Solver = new Stage3Solver(pipeStage23in, pipeStage34out);
		stage4Solver = new Stage4Solver(pipeStage34in, pipeStage45out);
		stage5Solver = new Stage5Solver(pipeStage45in, pipeStage50out);
		stage5Solver.setPriority(Thread.NORM_PRIORITY+1);

		stage1Solver.start();
		stage2Solver.start();
		stage3Solver.start();
		stage4Solver.start();
		stage5Solver.start();
	}

	public static void solveit4x4x4IDA (CubeState cube, int metric) {

		ObjectOutputStream myPipeOut = null;
		try{
			myPipeOut = new ObjectOutputStream (pipeStage01out);
			myPipeOut.writeObject(new SolverState(cube, metric, null, 0, 0));
		}
		catch (java.io.IOException ioe) { ioe.getMessage(); }
	}

	public static void getSolutions () {

		ObjectInputStream myPipeIn = null;
		SolverState solution = new SolverState(null, 0, null, 0, 0);
		while ( solution.metric != -1 ) {
			solution = null;
			while (solution == null) {
				try{
					Thread.currentThread().sleep(10);
					myPipeIn = new ObjectInputStream(pipeStage50in);
					solution = (SolverState) myPipeIn.readObject();
				}
				catch(java.io.IOException e) {
					e.printStackTrace();
				}
				catch(java.lang.ClassNotFoundException e) {
					e.printStackTrace();
				}
				catch(java.lang.InterruptedException e) {
					e.printStackTrace();
				}
			}
			print_move_list (solution.move_count, solution.move_list);
		}
	}

	public static void stopThreads () {
		CubeState cc = new CubeState();
		ObjectOutputStream myPipeOut = null;
		try{
			myPipeOut = new ObjectOutputStream (pipeStage01out);
			myPipeOut.writeObject(new SolverState(null, -1, null, 0, 0)); // metric = -1 -> stop
		}
		catch (java.io.IOException ioe) { ioe.getMessage(); }
	}
}
