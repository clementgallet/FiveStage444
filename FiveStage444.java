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
	static byte[] move_list_stage1 = new byte[50];
	static byte[] move_list_stage2 = new byte[50];
	static byte[] move_list_stage3 = new byte[50];
	static byte[] move_list_stage4 = new byte[50];
	static byte[] move_list_stage5 = new byte[50];
	static int length1, length2, length3, length4, length5;
	static int rotate, rotate2;

	static CubeState c = new CubeState();
	static CubeState c1 = new CubeState();
	static CubeState c2 = new CubeState();
	static CubeState c3 = new CubeState();
	static CubeState c4 = new CubeState();
	static CubeState c5 = new CubeState();

	public static void main(String[] args){

		int random_count = 5;

		Symmetry.init();

		new Tables().init_all ();

		CubeStage1.prune_table = new PruningStage1();
		CubeStage1.prune_table.analyse();

		CubeStage2.prune_table_edgcen = new PruningStage2EdgCen();
		CubeStage2.prune_table_edgcen.analyse();

		if( USE_FULL_PRUNING_STAGE3){
			CubeStage3.prune_table = new PruningStage3();
			CubeStage3.prune_table.analyse();
		}else{
			CubeStage3.prune_table_cen = new PruningStage3Cen();
			CubeStage3.prune_table_cen.analyse();
			CubeStage3.prune_table_edg = new PruningStage3Edg();
			CubeStage3.prune_table_edg.analyse();
		}

		CubeStage4.prune_table = new PruningStage4();
		CubeStage4.prune_table.analyse();

		if( USE_FULL_PRUNING_STAGE5){
			CubeStage5.prune_table = new PruningStage5();
			CubeStage5.prune_table.analyse();
		}else{
			CubeStage5.prune_table_edgcen = new PruningStage5EdgCen();
			CubeStage5.prune_table_edgcen.analyse();
			CubeStage5.prune_table_edgcor = new PruningStage5EdgCor();
			CubeStage5.prune_table_edgcor.analyse();
		}

		do_random_cubes (random_count);

		/* Print statistics */
		Statistics.print();

	}

	public static void do_random_cubes (int count) {
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
			//System.out.println ("scramble: ");
			//print_move_list (scramble_len, random_list);
			solveit4x4x4IDA (i, solveme);
		}
	}

	public static void solveit4x4x4IDA (int id, CubeState cube) {

		cube.copyTo (c);
		CubeStage1 s1 = new CubeStage1();
		/*
		CubeStage1 s2 = new CubeStage1();
		CubeStage1 s3 = new CubeStage1();
		*/
		c.convert_to_stage1 (s1);

		int d1 = s1.getDistance();

		for (length1 = d1; length1 < 100; ++length1) {
			if( search_stage1 (s1, length1, 0, N_BASIC_MOVES, d1 ))
				break;
		}


		/*
		cube.do_move (Lf3);
		cube.do_move (Ls3);
		cube.do_move (Rs);
		cube.do_move (Rf);
		cube.do_move (Uf3);
		cube.do_move (Us3);
		cube.do_move (Ds);
		cube.do_move (Df);

		cube.convert_to_stage1 (s2);		

		cube.do_move (Lf3);
		cube.do_move (Ls3);
		cube.do_move (Rs);
		cube.do_move (Rf);
		cube.do_move (Uf3);
		cube.do_move (Us3);
		cube.do_move (Ds);
		cube.do_move (Df);

		cube.convert_to_stage1 (s3);
		*/
	}

	public static boolean search_stage1 (CubeStage1 cube1, int depth, int moves_done, int move_state, int dist){
		Statistics.addNode(1, depth);
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, j;
		if (depth == 0){
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(1, length1);
			return init_stage2();
		}
		for (mov_idx = 0; mov_idx < N_BASIC_MOVES; ++mov_idx) {
			if (((stage1_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) || ( depth == 1 )) {
				cube1.copyTo(cube2);
				cube2.do_move (mov_idx);
				int newDist = cube2.new_dist(dist);
				if (newDist > depth-1) continue;
				move_list_stage1[moves_done] = (byte)mov_idx;
				if (search_stage1 (cube2, depth - 1, moves_done + 1, mov_idx, newDist)) return true;
			}
		}
		return false;
	}

	public static boolean init_stage2 (){

		System.out.print ("Stage 1: ");
		print_move_list (length1, move_list_stage1);

		int i;
		c.copyTo(c1);
		c1.scramble( length1, move_list_stage1 );

		int rotate = c1.m_cor[0] >> 3;
		switch (rotate) {
		case 0:
			break;	//no whole cube rotation
		case 1:
			c1.do_move (Lf3);
			c1.do_move (Ls3);
			c1.do_move (Rs);
			c1.do_move (Rf);
			c1.do_move (Uf3);
			c1.do_move (Us3);
			c1.do_move (Ds);
			c1.do_move (Df);
			break;
		case 2:
			c1.do_move (Ff);
			c1.do_move (Fs);
			c1.do_move (Bs3);
			c1.do_move (Bf3);
			c1.do_move (Uf);
			c1.do_move (Us);
			c1.do_move (Ds3);
			c1.do_move (Df3);
			break;
		default:
			System.out.println ("Invalid cube rotation state.");
		}

		CubeStage2 s1 = new CubeStage2();
		c1.convert_to_stage2 (s1);

		int cubeDistCenF = s1.getDistanceEdgCen(true);
		int cubeDistCenB = s1.getDistanceEdgCen(false);
		int d2 = Math.max(cubeDistCenF, cubeDistCenB);

		for (length2 = d2; length2 < 100; ++length2) {
			if( search_stage2 (s1, length2, 0, 0, cubeDistCenF, cubeDistCenB ))
				return false;
		}
		return false;
	}

	public static boolean search_stage2 (CubeStage2 cube1, int depth, int moves_done, int move_state, int distCenF, int distCenB){
		Statistics.addNode(2, depth);
		CubeStage2 cube2 = new CubeStage2();
		int mov_idx, mc, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(2, length2);
			return init_stage3();
		}
		for (mov_idx = 0; mov_idx < N_STAGE2_SLICE_MOVES; ++mov_idx) {
			if ((stage2_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				next_ms = stage2_stm_next_ms[mov_idx];

				int newDistCenF = cube2.new_dist_edgcen(true, distCenF);
				int newDistCenB = cube2.new_dist_edgcen(false, distCenB);
				if (newDistCenF > depth-1) continue;
				if (newDistCenB > depth-1) continue;
				move_list_stage2[moves_done] = (byte)mov_idx;
				if (search_stage2 (cube2, depth - 1, moves_done + 1, next_ms, newDistCenF, newDistCenB)) return true;
			}
		}
		return false;
	}

	public static boolean init_stage3 (){
		System.out.print ("Stage 2: ");
		print_move_list (length2, move_list_stage2);

		int i;
		for (i = 0; i < length2; ++i) {
			move_list_stage2[i] = stage2_slice_moves[move_list_stage2[i]];
		}

		c1.copyTo(c2);
		c2.scramble( length2, move_list_stage2 );

		for (i = 0; i < length2; ++i) {
			move_list_stage2[i] = xlate_r6[move_list_stage2[i]][rotate];
		}

		rotate2 = rotate;

		if (c2.m_cen[16] < 4) {
			c2.do_move (Uf);
			c2.do_move (Us);
			c2.do_move (Ds3);
			c2.do_move (Df3);
			rotate2 += 3;
		}

		CubeStage3 s1 = new CubeStage3();
		c2.convert_to_stage3 (s1);

		int cubeDistCen = s1.getDistanceCen();
		int cubeDistEdg = s1.getDistanceEdg();
		int d3 = Math.max(cubeDistCen, cubeDistEdg);

		for (length3 = d3; length3 < 100; ++length3) {
			if( search_stage3 (s1, length3, 0, 0, cubeDistCen, cubeDistEdg ))
				return false;
		}
		return false;
	}

	public static boolean search_stage3 (CubeStage3 cube1, int depth, int moves_done, int move_state, int distCen, int distEdg){
		Statistics.addNode(3, depth);
		CubeStage3 cube2 = new CubeStage3();
		int mov_idx, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(3, length3);
			return init_stage4();
		}
		for (mov_idx = 0; mov_idx < N_STAGE3_SLICE_MOVES; ++mov_idx) {
			if ((stage3_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				next_ms = stage3_stm_next_ms[mov_idx];
				int newDistCen = cube2.new_dist_cen(distCen);
				int newDistEdg = cube2.new_dist_edg(distEdg);
				if (newDistCen > depth-1) continue;
				if (newDistEdg > depth-1) continue;
				move_list_stage3[moves_done] = (byte)mov_idx;
				if (search_stage3 (cube2, depth - 1, moves_done + 1, next_ms, newDistCen, newDistEdg)) return true;
			}
		}
		return false;
	}

	public static boolean init_stage4 (){
		System.out.print ("Stage 3: ");
		print_move_list (length3, move_list_stage3);

		int i;
		for (i = 0; i < length3; ++i) {
			move_list_stage3[i] = stage3_slice_moves[move_list_stage3[i]];
		}

		c2.copyTo(c3);
		c3.scramble( length3, move_list_stage3 );

		for (i = 0; i < length3; ++i) {
			move_list_stage3[i] = xlate_r6[move_list_stage3[i]][rotate2];
		}

		CubeStage4 s1 = new CubeStage4();
		c3.convert_to_stage4 (s1);

		System.out.println("m_sym_edge"+s1.m_sym_edge);
		System.out.println("m_centerUD"+s1.m_centerUD);
		System.out.println("m_corner"+s1.m_corner);

		int d4 = s1.getDistance();

		for (length4 = d4; length4 < 100; ++length4) {
			if( search_stage4 (s1, length4, 0, 0, d4 ))
				return false;
		}
		return false;
	}

	public static boolean search_stage4 (CubeStage4 cube1, int depth, int moves_done, int move_state, int dist){
		Statistics.addNode(4, depth);
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(4, length4);
			init_stage5();
		}
		for (mov_idx = 0; mov_idx < N_STAGE4_SLICE_MOVES; ++mov_idx) {
			if ((stage4_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				next_ms = stage4_stm_next_ms[mov_idx];
				int newDist = cube2.new_dist(dist);
				if (newDist > depth-1) continue;
				move_list_stage4[moves_done] = (byte)mov_idx;
				if (search_stage4 (cube2, depth - 1, moves_done + 1, next_ms, newDist)) return true;
			}
		}
		return false;
	}

	public static boolean init_stage5 (){
		int i;

		for (i = 0; i < length4; ++i) {
			move_list_stage4[i] = stage4_slice_moves[move_list_stage4[i]];
		}

		c3.copyTo(c4);
		c4.scramble( length4, move_list_stage4 );

		for (i = 0; i < length4; ++i) {
			move_list_stage4[i] = xlate_r6[move_list_stage4[i]][rotate2];
		}

		CubeStage5 s1 = new CubeStage5();
		c4.convert_to_stage5 (s1);

		int cubeDistEdgCen = s1.getDistanceEdgCen();
		int cubeDistEdgCor = s1.getDistanceEdgCor();
		int d5 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

		length5 = -1;
		for (i = d5; i < 100; ++i) {
			if( search_stage5 (s1, length5, 0, 12, cubeDistEdgCen, cubeDistEdgCor )){
				length5 = i;
				return false;
			}
		}

		if (length5 != -1){
			/* Print solution */
			System.out.print ("Stage 1: ");
			print_move_list (length1, move_list_stage1);
			System.out.print ("Stage 2: ");
			print_move_list (length2, move_list_stage2);
			System.out.print ("Stage 3: ");
			print_move_list (length3, move_list_stage3);
			System.out.print ("Stage 4: ");
			print_move_list (length4, move_list_stage4);
			System.out.print ("Stage 5: ");
			print_move_list (length5, move_list_stage5);

			return true;
		}
		return false;
	}

	public static boolean search_stage5 (CubeStage5 cube1, int depth, int moves_done, int move_state, int distEdgCen, int distEdgCor){
		Statistics.addNode(5, depth);
		CubeStage5 cube2 = new CubeStage5();
		int mov_idx, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(5, length5);
			return true;
		}
		for (mov_idx = 0; mov_idx < N_STAGE5_MOVES; ++mov_idx) {
			cube1.copyTo (cube2);
			if ((sqs_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.do_move (mov_idx);
				next_ms = sqs_stm_next_ms[mov_idx];
				int newDistEdgCen = cube2.new_dist_edgcen(distEdgCen);
				int newDistEdgCor = cube2.new_dist_edgcor(distEdgCor);
				if (newDistEdgCen > depth-1) continue;
				if (newDistEdgCor > depth-1) continue;
				move_list_stage5[moves_done] = (byte)mov_idx;
				if (search_stage5 (cube2, depth - 1, moves_done + 1, next_ms, newDistEdgCen, newDistEdgCor)) return true;
			}
		}
		return false;
	}
}
