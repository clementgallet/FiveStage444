package fivestage444;

import static fivestage444.Constants.*;

import java.util.Random;

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

	private static int dbltwists[][] = {
	{ Uf, Us }, { Uf3, Us3 }, { Uf2, Us2 },
	{ Df, Ds }, { Df3, Ds3 }, { Df2, Ds2 },
	{ Lf, Ls }, { Lf3, Ls3 }, { Lf2, Ls2 },
	{ Rf, Rs }, { Rf3, Rs3 }, { Rf2, Rs2 },
	{ Ff, Fs }, { Ff3, Fs3 }, { Ff2, Fs2 },
	{ Bf, Bs }, { Bf3, Bs3 }, { Bf2, Bs2 },
	{ Us, Ds3 }, { Us3, Ds }, { Us2, Ds2 },
	{ Ls, Rs3 }, { Ls3, Rs }, { Ls2, Rs2 },
	{ Fs, Bs3 }, { Fs3, Bs }, { Fs2, Bs2 }
	};

	private static String move_strings[] = {
	"U", "U'", "U2", "u", "u'", "u2",
	"D", "D'", "D2", "d", "d'", "d2",
	"L", "L'", "L2", "l", "l'", "l2",
	"R", "R'", "R2", "r", "r'", "r2",
	"F", "F'", "F2", "f", "f'", "f2",
	"B", "B'", "B2", "b", "b'", "b2",
	"(Uu)", "(Uu)'", "(Uu)2", "(Dd)", "(Dd)'", "(Dd)2",
	"(Ll)", "(Ll)'", "(Ll)2", "(Rr)", "(Rr)'", "(Rr)2",
	"(Ff)", "(Ff)'", "(Ff)2", "(Bb)", "(Bb)'", "(Bb)2",
	"(ud')", "(u'd)", "(ud')2",
	"(lr')", "(l'r)", "(lr')2",
	"(fb')", "(f'b)", "(fb')2"
	};

	private static int xlate_r6[][] = {
	{ 0, 24, 12,  0, 24, 12}, { 1, 25, 13,  1, 25, 13}, { 2, 26, 14,  2, 26, 14},
	{ 3, 27, 15,  3, 27, 15}, { 4, 28, 16,  4, 28, 16}, { 5, 29, 17,  5, 29, 17},
	{ 6, 30, 18,  6, 30, 18}, { 7, 31, 19,  7, 31, 19}, { 8, 32, 20,  8, 32, 20},
	{ 9, 33, 21,  9, 33, 21}, {10, 34, 22, 10, 34, 22}, {11, 35, 23, 11, 35, 23},
	{12,  0, 24, 24, 12,  0}, {13,  1, 25, 25, 13,  1}, {14,  2, 26, 26, 14,  2},
	{15,  3, 27, 27, 15,  3}, {16,  4, 28, 28, 16,  4}, {17,  5, 29, 29, 17,  5},
	{18,  6, 30, 30, 18,  6}, {19,  7, 31, 31, 19,  7}, {20,  8, 32, 32, 20,  8},
	{21,  9, 33, 33, 21,  9}, {22, 10, 34, 34, 22, 10}, {23, 11, 35, 35, 23, 11},
	{24, 12,  0, 18,  6, 30}, {25, 13,  1, 19,  7, 31}, {26, 14,  2, 20,  8, 32},
	{27, 15,  3, 21,  9, 33}, {28, 16,  4, 22, 10, 34}, {29, 17,  5, 23, 11, 35},
	{30, 18,  6, 12,  0, 24}, {31, 19,  7, 13,  1, 25}, {32, 20,  8, 14,  2, 26},
	{33, 21,  9, 15,  3, 27}, {34, 22, 10, 16,  4, 28}, {35, 23, 11, 17,  5, 29},
	{36, 48, 42, 36, 48, 42}, {37, 49, 43, 37, 49, 43}, {38, 50, 44, 38, 50, 44},
	{39, 51, 45, 39, 51, 45}, {40, 52, 46, 40, 52, 46}, {41, 53, 47, 41, 53, 47},
	{42, 36, 48, 48, 42, 36}, {43, 37, 49, 49, 43, 37}, {44, 38, 50, 50, 44, 38},
	{45, 39, 51, 51, 45, 39}, {46, 40, 52, 52, 46, 40}, {47, 41, 53, 53, 47, 41},
	{48, 42, 36, 45, 39, 51}, {49, 43, 37, 46, 40, 52}, {50, 44, 38, 47, 41, 53},
	{51, 45, 39, 42, 36, 48}, {52, 46, 40, 43, 37, 49}, {53, 47, 41, 44, 38, 50},
	{54, 60, 57, 54, 60, 57}, {55, 61, 58, 55, 61, 58}, {56, 62, 59, 56, 62, 59},
	{57, 54, 60, 60, 57, 54}, {58, 55, 61, 61, 58, 55}, {59, 56, 62, 62, 59, 56},
	{60, 57, 54, 58, 55, 61}, {61, 58, 55, 57, 54, 60}, {62, 59, 56, 59, 56, 62}
	};

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
		scrambleCUBE (solveme, scramble_len, random_list, 0);
		System.out.println ("scramble: ");
		print_move_list (scramble_len, random_list, 0);
		int solveme_count = solveit4x4x4IDA (solveme, solveme_moves, metric);
		print_move_list (solveme_count, solveme_moves, 0);
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
	CubeStage1 s1 = new CubeStage1();
	CubeStage2 s2 = new CubeStage2();
	CubeStage3 s3 = new CubeStage3();
	CubeStage4 s4 = new CubeStage4();
	CubeSqsCoord s5 = new CubeSqsCoord();
	s1.init ();
	s2.init ();
	s3.init ();
	s4.init ();
	s5.init ();

	/*** Stage1 ***/
	cube.convert_to_stage1 (s1);
	t.reset();
	Stage1Solver stage1Solver = new Stage1Solver(s1, metric);
	stage1Solver.run();
	while (stage1Solver.isAlive()){
		try{
			Thread.currentThread().sleep(100);
		}
		catch( java.lang.InterruptedException ie){
		}
	}
	int count1 = stage1Solver.goal;
	for (i=0;i<count1;i++)
		move_list[i] = stage1Solver.move_list[i];
	tStage1 += t.elapsed();
	if (count1 < 0 || count1 > 90) {
		System.out.println ("Solve failure!\n");
		return -1;
	}
	int count = count1;
	System.out.print ("Stage1: ");
	print_move_list (count1, move_list, 0);

	scrambleCUBE (cube, count, move_list, 0);
	int r3 = cube.m_cor[0] >> 3;
	switch (r3) {
	case 0:
		break;	//no whole cube rotation
	case 1:
		cube.do_move (Lf3);
		cube.do_move (Ls3);
		cube.do_move (Rs);
		cube.do_move (Rf);
		cube.do_move (Uf3);
		cube.do_move (Us3);
		cube.do_move (Ds);
		cube.do_move (Df);
		break;
	case 2:
		cube.do_move (Ff);
		cube.do_move (Fs);
		cube.do_move (Bs3);
		cube.do_move (Bf3);
		cube.do_move (Uf);
		cube.do_move (Us);
		cube.do_move (Ds3);
		cube.do_move (Df3);
		break;
	default:
		System.out.println ("Invalid cube rotation state.\n");
		//exit (1);
	}

	/*** Stage 2 ***/
	cube.convert_to_stage2 (s2);
	t.reset();
	Stage2Solver stage2Solver = new Stage2Solver(s2, metric);
	stage2Solver.run();
	while (stage2Solver.isAlive()){
		try{
			Thread.currentThread().sleep(100);
		}
		catch( java.lang.InterruptedException ie){
		}
	}
	int count2 = stage2Solver.goal;
	for (i=0;i<count2;i++)
		move_list[i+count] = stage2Solver.move_list[i];
	tStage2 += t.elapsed();
	if (count2 < 0 || count2 > 90) {
		System.out.println ("Solve failure!\n");
		return -1;
	}

	scrambleCUBE (cube, count2, move_list, count);

	//translate move codes for whole cube rotations performed
	for (i = count; i < count + count2; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r3];
	}
	System.out.print ("Stage2: ");
	print_move_list (count2, move_list, count);

	count += count2;

	int r6 = r3;
	if (cube.m_cen[16] < 4) {
		cube.do_move (Uf);
		cube.do_move (Us);
		cube.do_move (Ds3);
		cube.do_move (Df3);
		r6 += 3;
	}

	/*** Stage 3 ***/
	cube.convert_to_stage3 (s3);
	s3.m_edge_odd = cube.edgeUD_parity_odd ();
	t.reset();
	Stage3Solver stage3Solver = new Stage3Solver(s3, metric);
	stage3Solver.run();
	while (stage3Solver.isAlive()){
		try{
			Thread.currentThread().sleep(100);
		}
		catch( java.lang.InterruptedException ie){
		}
	}
	int count3 = stage3Solver.goal;
	for (i=0;i<count3;i++)
		move_list[i+count] = stage3Solver.move_list[i];
	tStage3 += t.elapsed();
	if (count3 < 0 || count3 > 90) {
		System.out.println ("Solve failure!");
		return -1;
	}
	scrambleCUBE (cube, count3, move_list, count);
	for (i = count; i < count + count3; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	System.out.print ("Stage3: ");
	print_move_list (count3, move_list, count);

	count += count3;

	/*** Stage 4 ***/
	cube.convert_to_stage4 (s4);
	t.reset();
	Stage4Solver stage4Solver = new Stage4Solver(s4, metric);
	stage4Solver.run();
	while (stage4Solver.isAlive()){
		try{
			Thread.currentThread().sleep(100);
		}
		catch( java.lang.InterruptedException ie){
		}
	}
	int count4 = stage4Solver.goal;
	for (i=0;i<count4;i++)
		move_list[i+count] = stage4Solver.move_list[i];
	tStage4 += t.elapsed();
	if (count4 < 0 || count4 > 90) {
		System.out.println ("Solve failure!");
		return -1;
	}
	scrambleCUBE (cube, count4, move_list, count);
	for (i = count; i < count + count4; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	System.out.print ("Stage4: ");
	print_move_list (count4, move_list, count);

	count += count4;

	/*** Stage 5 ***/
	cube.convert_to_squares (s5);
	t.reset();
	Stage5Solver stage5Solver = new Stage5Solver(s5, metric);
	stage5Solver.run();
	while (stage5Solver.isAlive()){
		try{
			Thread.currentThread().sleep(100);
		}
		catch( java.lang.InterruptedException ie){
		}
	}
	int count5 = stage5Solver.goal;
	for (i=0;i<count5;i++)
		move_list[i+count] = stage5Solver.move_list[i];
	tStage5 += t.elapsed();
	if (count5 < 0 || count5 > 90) {
		System.out.println ("Solve failure!");
		return -1;
	}
	for (i = count; i < count + count5; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	System.out.print ("Stage5: ");
	print_move_list (count5, move_list, count);

	count += count5;
	return count;
}

	public static void scrambleCUBE (CubeState pcube, int move_count, int[] move_arr, int index){
		int i;
		for (i = 0; i < move_count; ++i) {
			int mc = move_arr[i+index];
			if (mc >= Ufs) {
				pcube.do_move (dbltwists[mc - Ufs][0]);
				pcube.do_move (dbltwists[mc - Ufs][1]);
			} else {
				pcube.do_move (mc);
			}
		}
	}

	public static void print_move_list (int count, int[] move_list, int index){
		int j;
		if (count >= 0) {
			System.out.print ("[" + count + "] ");
			for (j = 0; j < count; ++j) {
				System.out.print (" " + move_strings[move_list[j+index]]);
			}
		} else {
			System.out.print ("[Did not solve]");
		}
		System.out.println (" ");
	}
}
