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

	public static void main(String[] args){

		int random_count = 100;
		int metric = 0;

		new Tables().init_all ();
		CubePruningTableMgr.init_pruning_tables (metric);
		do_random_cubes (metric, random_count);
	}

	public static void do_random_cubes (int metric, int count){
	int i, i1;
	//Random r = new Random();
	Random r = new Random(42);
	int[] random_list = new int[160];	//must be >= scramble_len
	CubeState solveme = new CubeState();
	CubeState solved = new CubeState();
	int scramble_len = 100;
	int success_count = 0;
	int[] solveme_moves = new int[100];

	solved.init ();
	for (i = 1; i <= count; ++i) {
		int j;
		solveme.init ();
		for (j = 0; j < scramble_len; ++j) {
			random_list[j] = r.nextInt(36);
		}
		solveme.scramble(scramble_len, random_list);
		System.out.println ("scramble: ");
		print_move_list (scramble_len, random_list);
		int solveme_count = solveit4x4x4IDA (solveme, solveme_moves, metric);
		print_move_list (solveme_count, solveme_moves);
	}
	System.out.println( "Stage1 has taken " + tStage1 + " ms." );
	System.out.println( "Stage2 has taken " + tStage2 + " ms." );
	System.out.println( "Stage3 has taken " + tStage3 + " ms." );
	System.out.println( "Stage4 has taken " + tStage4 + " ms." );
	System.out.println( "Stage5 has taken " + tStage5 + " ms." );
}

	public static long tStage1, tStage2, tStage3, tStage4, tStage5 = 0;
	public static Timer t = new Timer();

	public static int solveit4x4x4IDA (CubeState cube, int[] move_list, int metric){

	int i;


	try{
	PipedOutputStream pipeStage01out = new PipedOutputStream ();
	PipedInputStream pipeStage01in = new PipedInputStream (pipeStage01out);
	PipedOutputStream pipeStage12out = new PipedOutputStream ();
	PipedInputStream pipeStage12in = new PipedInputStream (pipeStage12out);
	PipedOutputStream pipeStage23out = new PipedOutputStream ();
	PipedInputStream pipeStage23in = new PipedInputStream (pipeStage23out);
	PipedOutputStream pipeStage34out = new PipedOutputStream ();
	PipedInputStream pipeStage34in = new PipedInputStream (pipeStage34out);
	PipedOutputStream pipeStage45out = new PipedOutputStream ();
	PipedInputStream pipeStage45in = new PipedInputStream (pipeStage45out);
	PipedOutputStream pipeStage50out = new PipedOutputStream ();
	PipedInputStream pipeStage50in = new PipedInputStream (pipeStage50out);

	Stage1Solver stage1Solver = new Stage1Solver(pipeStage01in, pipeStage12out);
	Stage2Solver stage2Solver = new Stage2Solver(pipeStage12in, pipeStage23out);
	Stage3Solver stage3Solver = new Stage3Solver(pipeStage23in, pipeStage34out);
	Stage4Solver stage4Solver = new Stage4Solver(pipeStage34in, pipeStage45out);
	Stage5Solver stage5Solver = new Stage5Solver(pipeStage45in, pipeStage50out);

	stage1Solver.run();
	stage2Solver.run();
	stage3Solver.run();
	stage4Solver.run();
	stage5Solver.run();

	ObjectOutputStream myPipeOut = new ObjectOutputStream(pipeStage01out);
	
	myPipeOut.writeObject(new SolverState(cube, metric, move_list, 0, 0));

	ObjectInputStream myPipeIn = new ObjectInputStream(pipeStage50in);

	SolverState solution = null;
	while (solution == null) {
		try{
			solution = (SolverState) myPipeIn.readObject();
			Thread.currentThread().sleep(100);
		}
		catch(java.lang.ClassNotFoundException e) {}
		catch(java.lang.InterruptedException e) {}


	}

	print_move_list (solution.move_count, solution.move_list);

	return solution.move_count;
	}
	catch(java.io.IOException ioe)
	{
	}
	return -1;
}

}
