// FiveStage444.exe.cpp : Defines the entry point for the console application.
//

#include "Constants.h"
#include "CubePruningTable.h"
#include "CubePruningTableMgr.h"
#include "CubeSqsCoord.h"
#include "CubeStage1.h"
#include "CubeStage2.h"
#include "CubeStage3.h"
#include "CubeStage4.h"
#include "CubeState.h"
#include "Tables.h"
#include "CubeConverter.h"

//bool got_interrupt = false;

//void SignalHandler(int signal)
//{
//	if (signal == SIGINT) {
//		got_interrupt = true;
//	}
//}

// Revenge.cpp : Program to generate a table of distances for various subsets of states
// of the 4x4x4 Rubik's Revenge cube. The "squares group" is the first goal of the program.
//

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

CubePruningTableMgr cpt_mgr;

int prune_funcCOR_STAGE1 (const CubeStage1& cube1);
int prune_funcEDGE_STAGE1 (const CubeStage1& cube1);
int prune_funcEDGCEN_STAGE2 (const CubeStage2& cube2);
int prune_funcCEN_STAGE3 (const CubeStage3& cube1);
int prune_funcEDGE_STAGE3 (const CubeStage3& cube1);
int prune_funcCENCOR_STAGE4 (const CubeStage4& cube1);
int prune_funcEDGCEN_STAGE4 (const CubeStage4& cube1);
int prune_funcCENCOR_STAGE5 (const CubeSqsCoord& cube1);
int prune_funcEDGCOR_STAGE5 (const CubeSqsCoord& cube1);

void do_random_cubes (int metric, int count);

int solveitIDA_STAGE1 (const CubeStage1& init_cube, int* move_list, int metric);
bool treesearchSTAGE1 (const CubeStage1& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count);

int solveitIDA_STAGE2 (const CubeStage2& init_cube, int* move_list, int metric);
bool treesearchSTAGE2 (const CubeStage2& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);

int solveitIDA_STAGE3 (const CubeStage3& init_cube, int* move_list, int metric);
bool treesearchSTAGE3 (const CubeStage3& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count);

int solveitIDA_STAGE4 (const CubeStage4& init_cube, int* move_list, int metric);
bool treesearchSTAGE4 (const CubeStage4& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count);

int solveitIDA_SQS (const CubeSqsCoord& init_cube, int* move_list, int metric);
bool treesearchSQS (const CubeSqsCoord& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count);

int solveit4x4x4IDA (const CubeState& init_cube, int* move_list, int metric);

void scrambleCUBE (CubeState* pcube, int move_count, const int* move_arr);

void print_move_list (int count, const int* move_list);
int random (int n);

int main (int argc, char* argv[])
{
	srand( (unsigned)time( NULL ) );
	strcpy (&datafiles_path[0], &default_datafile_path[0]);

	int random_count = 100;
	int metric = 0;
	int i2;
	for (i2 = 1; i2 < argc; ++i2) {
		if (strncmp (argv[i2], "-", 1) == 0) {
			switch (argv[i2][1]) {
			case 's': case 'S':
				metric = 0;
				break;
			case 't': case 'T':
				metric = 1;
				break;
			case 'b': case 'B':
				metric = 2;
				break;
			}
		}
	}
	printf ("Performing misc. initializations...\n");
	init_4of8 ();

	init_parity_table ();

	init_eloc ();
	init_cloc ();

	printf ("Performing stage 1 initializations...\n");
	init_move_tablesSTAGE1 ();

	printf ("Performing stage 2 initializations...\n");
	init_stage2 ();

	printf ("Performing stage 3 initializations...\n");
	init_stage3 ();

	printf ("Performing stage 4 initializations...\n");
	init_stage4_edge_tables ();
	lrfb_check ();
	init_move_tablesSTAGE4 ();

	printf ("Performing stage 5 initializations...\n");
	init_squares ();

	cpt_mgr.init_pruning_tables (metric);

	do_random_cubes (metric, random_count);
	return 0;
}


void
do_random_cubes (int metric, int count)
{
	int i, i1;
	static int random_list[160];	//must be >= scramble_len
	CubeState solveme;
	CubeState solved;
	const int scramble_len = 100;	//const for now
	int success_count = 0;
	static int solveme_moves[100];

	solved.init ();

	for (i = 1; i <= count; ++i) {
		int j;
		solveme.init ();
		for (j = 0; j < scramble_len; ++j) {
			random_list[j] = random (36);
		}
		scrambleCUBE (&solveme, scramble_len, &random_list[0]);
		printf ("scramble: ");
		print_move_list (scramble_len, &random_list[0]);
		printf ("\n");
		int solveme_count = solveit4x4x4IDA (solveme, &solveme_moves[0], metric);
		print_move_list (solveme_count, &solveme_moves[0]);
		printf ("\n");
		CubeState ycube = solveme;
		scrambleCUBE (&ycube, solveme_count, &solveme_moves[0]);
		CubeState zcube = ycube;
		Face f1 = 6;
		for (i1 = 0; i1 < 6; ++i1) {
			if (zcube.m_cen[4*i1] == 0) {
				f1 = i1;
				break;
			}
		}
		switch (f1) {
		case 0:
			break;
		case 1:
			zcube.do_move (Lf2);
			zcube.do_move (Ls2);
			zcube.do_move (Rf2);
			zcube.do_move (Rs2);
			break;
		case 2:
			zcube.do_move (Ff);
			zcube.do_move (Fs);
			zcube.do_move (Bf3);
			zcube.do_move (Bs3);
			break;
		case 3:
			zcube.do_move (Ff3);
			zcube.do_move (Fs3);
			zcube.do_move (Bf);
			zcube.do_move (Bs);
			break;
		case 4:
			zcube.do_move (Lf3);
			zcube.do_move (Ls3);
			zcube.do_move (Rf);
			zcube.do_move (Rs);
			break;
		case 5:
			zcube.do_move (Lf);
			zcube.do_move (Ls);
			zcube.do_move (Rf3);
			zcube.do_move (Rs3);
			break;
		}
		switch (zcube.m_cen[8]) {
		case 2:
			break;
		case 3:
			zcube.do_move (Uf2);
			zcube.do_move (Us2);
			zcube.do_move (Df2);
			zcube.do_move (Ds2);
			break;
		case 4:
			zcube.do_move (Uf3);
			zcube.do_move (Us3);
			zcube.do_move (Df);
			zcube.do_move (Ds);
			break;
		case 5:
			zcube.do_move (Uf);
			zcube.do_move (Us);
			zcube.do_move (Df3);
			zcube.do_move (Ds3);
			break;
		}
	}
	printf ("Successful solves: %d\n", success_count);
}





int
solveitIDA_SQS (const CubeSqsCoord& init_cube, int* move_list, int metric)
{
	static UINT init_move_state[3] = { 12, 23, 6 };
	int move_count;
	int g1;
	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSQS (init_cube, g1, 0, init_move_state[metric], g1, metric,
				move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

UINT sqs_slice_moves_to_try [13] = {
	0xFFE, 0xFFC, 0xFF8, 0xFF0,
	0xFEF, 0xFCF, 0xF8F, 0xF0F,
	0xEFF, 0xCFF, 0x8FF, 0x0FF,
	0xFFF
};

UINT sqs_stm_next_ms[N_SQMOVES] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

const UINT SQS_TW_MS_U = 0;
const UINT SQS_TW_MS_D = 1;
const UINT SQS_TW_MS_Uu = 2;
const UINT SQS_TW_MS_u = 3;
const UINT SQS_TW_MS_d = 4;
const UINT SQS_TW_MS_UD = 5;
const UINT SQS_TW_MS_Ud = 6;

const UINT SQS_TW_MS_L = 8;
const UINT SQS_TW_MS_R = 9;
const UINT SQS_TW_MS_Ll = 10;
const UINT SQS_TW_MS_l = 11;
const UINT SQS_TW_MS_r = 12;
const UINT SQS_TW_MS_LR = 13;
const UINT SQS_TW_MS_Lr = 14;

const UINT SQS_TW_MS_F = 16;
const UINT SQS_TW_MS_B = 17;
const UINT SQS_TW_MS_Ff = 18;
const UINT SQS_TW_MS_f = 19;
const UINT SQS_TW_MS_b = 20;
const UINT SQS_TW_MS_FB = 21;
const UINT SQS_TW_MS_Fb = 22;

const UINT SQS_TW_MS_X = 23;

UINT sqs_twist_moves_to_try[24] = {
	0xBBA, 0xBB4, 0xBB0, 0xBB0, 0xBB0, 0xBB4, 0xBB0, 0xBB0,
	0xBAB, 0xB4B, 0xB0B, 0xB0B, 0xB0B, 0xB4B, 0xB0B, 0xB0B,
	0xABB, 0x4BB, 0x0BB, 0x0BB, 0x0BB, 0x4BB, 0x0BB, 0xBBB
};

#define	SQST_XX			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X
#define	SQST_XU			SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D
#define	SQST_XL			SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R
#define	SQST_XF			SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B
#define SQST_U0			SQS_TW_MS_X,SQS_TW_MS_u,SQS_TW_MS_X,SQS_TW_MS_UD
#define SQST_U1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_d,SQS_TW_MS_X
#define SQST_U5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Ud,SQS_TW_MS_X
#define SQST_L0			SQS_TW_MS_X,SQS_TW_MS_l,SQS_TW_MS_X,SQS_TW_MS_LR
#define SQST_L1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_r,SQS_TW_MS_X
#define SQST_L5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Lr,SQS_TW_MS_X
#define SQST_F0			SQS_TW_MS_X,SQS_TW_MS_f,SQS_TW_MS_X,SQS_TW_MS_FB
#define SQST_F1			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_b,SQS_TW_MS_X
#define SQST_F5			SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Fb,SQS_TW_MS_X

UINT sqs_twist_next_ms[24][12] = {
	{ SQST_U0, SQST_XL, SQST_XF },
	{ SQST_U1, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_U5, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XL, SQST_XF },
	{ SQST_XX, SQST_XX, SQST_XX },

	{ SQST_XU, SQST_L0, SQST_XF },
	{ SQST_XU, SQST_L1, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XU, SQST_L5, SQST_XF },
	{ SQST_XU, SQST_XX, SQST_XF },
	{ SQST_XX, SQST_XX, SQST_XX },

	{ SQST_XU, SQST_XL, SQST_F0 },
	{ SQST_XU, SQST_XL, SQST_F1 },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_XX },
	{ SQST_XU, SQST_XL, SQST_F5 },
	{ SQST_XU, SQST_XL, SQST_XF },

	{ SQST_XU, SQST_XL, SQST_XF }
};

const UINT SQS_BL_MS_U = 0;
const UINT SQS_BL_MS_XU = 1;
const UINT SQS_BL_MS_L = 2;
const UINT SQS_BL_MS_XL = 3;
const UINT SQS_BL_MS_F = 4;
const UINT SQS_BL_MS_XF = 5;
const UINT SQS_BL_MS_X = 6;

#define	SQSB_XX			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X
#define	SQSB_XU			SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU
#define SQSB_U0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_X
#define	SQSB_XL			SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL
#define SQSB_L0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_X
#define	SQSB_XF			SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF
#define SQSB_F0			SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_X

UINT sqs_block_moves_to_try[7] = {
	0x1B7DD0, 0x1B7DC0, 0x177437, 0x177037, 0x0D0DF7, 0x0C0DF7, 0x1F7DF7
};

UINT sqs_block_next_ms[7][21] = {
	{ SQSB_U0, SQSB_XL, SQSB_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQSB_XX, SQSB_XL, SQSB_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_L0, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_XX, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQSB_XU, SQSB_XL, SQSB_F0, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQSB_XU, SQSB_XL, SQSB_XX, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQSB_XU, SQSB_XL, SQSB_XF, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_XF }
};

bool
treesearchSQS (const CubeSqsCoord& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeSqsCoord cube2;
	int mov_idx, mc, j;
	UINT next_ms = 0;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			//if (got_interrupt) {
			//	throw 1;
			//}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = prune_funcEDGCOR_STAGE5 (cube1);
	if (dist <= depth) {
		dist = prune_funcCENCOR_STAGE5 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg5[metric]; ++mov_idx) {
			bool did_move = false;
			cube2 = cube1;
			switch (metric) {
			case 0:
				if ((sqs_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2.do_move (mov_idx);
					next_ms = sqs_stm_next_ms[mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((sqs_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; j < 2 && sq_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = sqs_twist_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 2:
				if ((sqs_block_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; sq_block_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_block_moves[mov_idx][j];
						cube2.do_move (mc);
					}
					next_ms = sqs_block_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			}
			if (did_move) {
				mc = mov_idx;
				switch (metric) {
				case 1:
					mc = sq_twist_map1[mov_idx];
					break;
				case 2:
					mc = sq_block_map[mov_idx];
					break;
				}
				move_list[moves_done] = mc;
				if (treesearchSQS (cube2, depth - 1, moves_done + 1,
						next_ms, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
	}
	return false;
}

int
solveitIDA_STAGE1 (const CubeStage1& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE1 (init_cube, g1, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE1 (const CubeStage1& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage1 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			//if (got_interrupt) {
			//	throw 1;
			//}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = prune_funcCOR_STAGE1 (cube1);
	if (dist <= depth) {
		dist = prune_funcEDGE_STAGE1 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg1[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				for (j = 0; stage1_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				for (j = 0; stage1_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			move_list[moves_done] = mov_idx;
			if (treesearchSTAGE1 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}

const UINT STG2_SL_MS_X = 0;
const UINT STG2_SL_MS_U = 1;
const UINT STG2_SL_MS_u = 2;
const UINT STG2_SL_MS_d = 3;
const UINT STG2_SL_MS_D = 4;
const UINT STG2_SL_MS_L = 5;
const UINT STG2_SL_MS_l = 6;
const UINT STG2_SL_MS_r = 7;
const UINT STG2_SL_MS_R = 8;
const UINT STG2_SL_MS_F = 9;
const UINT STG2_SL_MS_f = 10;
const UINT STG2_SL_MS_b = 11;
const UINT STG2_SL_MS_B = 12;

const UINT STG2_TW_MS_X = 0;
const UINT STG2_TW_MS_u = 1;
const UINT STG2_TW_MS_U = 2;
const UINT STG2_TW_MS_d = 3;
const UINT STG2_TW_MS_D = 4;
const UINT STG2_TW_MS_l = 5;
const UINT STG2_TW_MS_L = 6;
const UINT STG2_TW_MS_r = 7;
const UINT STG2_TW_MS_R = 8;
const UINT STG2_TW_MS_f = 9;
const UINT STG2_TW_MS_F = 10;
const UINT STG2_TW_MS_b = 11;
const UINT STG2_TW_MS_B = 12;

#define	STG2S_X		STG2_SL_MS_X
#define	STG2S_Xx3	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X
#define	STG2S_U0x3	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U
#define STG2S_U1x3	STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u
#define	STG2S_U2x3	STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d
#define	STG2S_U3x3	STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D
#define	STG2S_L0	STG2_SL_MS_L
#define	STG2S_L1x3	STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l
#define	STG2S_L1x3_OLD	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l
#define	STG2S_L2x3	STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r
#define	STG2S_L3	STG2_SL_MS_R
#define STG2S_F0	STG2_SL_MS_F
#define STG2S_F1x3	STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f
#define STG2S_F2x3	STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b
#define	STG2S_F3	STG2_SL_MS_B

#define	STG2T_Xx3	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_Xx4	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_U0x3	STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u
#define STG2T_U1x3	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U
#define	STG2T_D0x3	STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d
#define	STG2T_D1x3	STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D
#define	STG2T_LRlr	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r
#define	STG2T_LRXr	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r
#define	STG2T_XRXr	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r
#define	STG2T_XRXX	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_FBfb	STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b
#define	STG2T_FBXb	STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b
#define	STG2T_XBXb	STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b
#define	STG2T_XBXX	STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_X
#define	STG2T_LRFB	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_XRFB	STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_XXFB	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B
#define	STG2T_LRXB	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_B
#define	STG2T_LRXX	STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X

UINT stage2_slice_moves_to_try [13] = {
	0xFFFFFFF,
	0xFFFFFF8, 0xFFFFFC0, 0xFFFF1C0, 0xFFFF000,
	0xFFFEFFF, 0xFFF0FFF, 0xFF10FFF, 0xFF00FFF,
	0xFEFFFFF, 0xF0FFFFF, 0x10FFFFF, 0x00FFFFF
};

UINT stage2_stm_next_ms[13][N_STAGE2_SLICE_MOVES] = {
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_U3x3,STG2S_Xx3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_Xx3,STG2S_L0,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_L1x3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_L3,STG2S_L2x3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_L3,STG2S_Xx3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3,STG2S_F0,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_F1x3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_F2x3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_F3,STG2S_Xx3 },
	{	STG2S_U0x3,STG2S_U1x3,STG2S_U3x3,STG2S_U2x3,STG2S_L0,STG2S_L1x3_OLD,STG2S_X,STG2S_Xx3,STG2S_X,STG2S_Xx3 }
};

UINT stage2_2twist_types[N_STAGE2_2TWIST_MOVES] = {
	22, 22, 23, 23, 22, 22, 23, 23,
	20, 20, 21, 21, 20, 20, 21, 21
};

UINT stage2_twist_moves_to_try [13] = {
	0xFFFFFF,
	0xFFFE3F, 0xFFFE38, 0xFFF038, 0xFFF000,
	0xEFBFFF, 0xEFAFFF, 0xCF2FFF, 0xCF0FFF,
	0xBBFFFF, 0xBAFFFF, 0x32FFFF, 0x30FFFF
};

UINT stage2_twist_next_ms[13][24] = {
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_Xx3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_D1x3,STG2T_Xx3,STG2T_D0x3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_D1x3,STG2T_Xx3,STG2T_Xx3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },
	{	STG2T_Xx3,STG2T_Xx3,STG2T_Xx3,STG2T_Xx3,STG2T_LRlr,STG2T_FBfb,STG2T_LRFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRXr,STG2T_FBfb,STG2T_XRFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_XRXr,STG2T_FBfb,STG2T_XRFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_XRXX,STG2T_FBfb,STG2T_XXFB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_Xx4,STG2T_FBfb,STG2T_XXFB },

	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_FBXb,STG2T_LRXB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_XBXb,STG2T_LRXB },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_XBXX,STG2T_LRXX },
	{	STG2T_U1x3,STG2T_D1x3,STG2T_U0x3,STG2T_D0x3,STG2T_LRlr,STG2T_Xx4,STG2T_LRXX }
};

const int S2BMTT_X = 0;		//moves not used for IDA* search
const int S2BMTT_UGEN = 1;	//U-axis general moves
const int S2BMTT_u = 2;
const int S2BMTT_u3 = 3;
const int S2BMTT_u2 = 4;
const int S2BMTT_D = 5;
const int S2BMTT_D3 = 6;
const int S2BMTT_D2 = 7;
const int S2BMTT_d = 8;
const int S2BMTT_d3 = 9;
const int S2BMTT_d2 = 10;
const int S2BMTT_u2d2 = 11;
const int S2BMTT_ud3 = 12;	//and u3d
const int S2BMTT_LGEN = 13;	//L-Axis general moves
const int S2BMTT_l = 14;	//and l'
const int S2BMTT_r = 15;
const int S2BMTT_r3 = 16;
const int S2BMTT_r2 = 17;
const int S2BMTT_lr3 = 18;	//and l'r
const int S2BMTT_FGEN = 19;	//F-Axis general moves
const int S2BMTT_f = 20;	//and f'
const int S2BMTT_b = 21;
const int S2BMTT_b3 = 22;
const int S2BMTT_b2 = 23;
const int S2BMTT_fb3 = 24;	//and f'b

UBYTE stage2_btm_mtt_idx[N_STAGE2_BLOCK_MOVES] = {
	S2BMTT_UGEN, S2BMTT_UGEN, S2BMTT_UGEN,		//U, U', U2
	S2BMTT_u, S2BMTT_u3, S2BMTT_u2,				//u, u', u2
	S2BMTT_D, S2BMTT_D3, S2BMTT_D2,				//D, D', D2
	S2BMTT_d, S2BMTT_d3, S2BMTT_d2,				//d, d', d2
	S2BMTT_UGEN, S2BMTT_UGEN, S2BMTT_UGEN,		//(Uu), (Uu)', (Uu)2
	S2BMTT_X, S2BMTT_X, S2BMTT_X,				//(Dd), (Dd)', (Dd)2
	S2BMTT_ud3, S2BMTT_ud3, S2BMTT_u2d2,		//(ud'), (u'd), (ud')2
	S2BMTT_LGEN,								//L2
	S2BMTT_l, S2BMTT_l, S2BMTT_LGEN,			//l, l', l2
	S2BMTT_LGEN,								//R2
	S2BMTT_r, S2BMTT_r3, S2BMTT_r2,				//r, r', r2
	S2BMTT_LGEN, S2BMTT_X,						//(Ll)2, (Rr)2
	S2BMTT_lr3, S2BMTT_lr3, S2BMTT_LGEN,		//(lr'), (l'r), (lr')2
	S2BMTT_FGEN,								//F2
	S2BMTT_f, S2BMTT_f, S2BMTT_FGEN,			//f, f', f2
	S2BMTT_FGEN,								//B2
	S2BMTT_b, S2BMTT_b3, S2BMTT_b2,				//b, b', b2
	S2BMTT_FGEN, S2BMTT_X,						//(Ff)2, (Bb)2
	S2BMTT_fb3, S2BMTT_fb3, S2BMTT_FGEN			//(fb'), (f'b), (fb')2
};

#define S2BMTT_MK_U_ALL (0xFFF << S2BMTT_UGEN)
#define S2BMTT_MK_L_ALL (0x3F << S2BMTT_LGEN)
#define S2BMTT_MK_F_ALL (0x3F << S2BMTT_FGEN)
#define S2BMTT_MK_ALL_d (1 << S2BMTT_d) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)
#define S2BMTT_MK_U1 ((1 << S2BMTT_u3) | (1 << S2BMTT_u2) | (1 << S2BMTT_D) | (1 << S2BMTT_D2) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_U3 ((1 << S2BMTT_u) | (1 << S2BMTT_u2) | (1 << S2BMTT_D3) | (1 << S2BMTT_D2) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_U2 ((1 << S2BMTT_u) | (1 << S2BMTT_u3) | (1 << S2BMTT_D) | (1 << S2BMTT_D3) | S2BMTT_MK_ALL_d)
#define S2BMTT_MK_u1 ((1 << S2BMTT_D) | (1 << S2BMTT_D2) | (1 << S2BMTT_d) | (1 << S2BMTT_d2))
#define S2BMTT_MK_u3 ((1 << S2BMTT_D3) | (1 << S2BMTT_D2) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2))
#define S2BMTT_MK_u2 ((1 << S2BMTT_D) | (1 << S2BMTT_D3) | (1 << S2BMTT_d) | (1 << S2BMTT_d3))
#define S2BMTT_MK_D1 ((1 << S2BMTT_d3) | (1 << S2BMTT_d2))
#define S2BMTT_MK_D3 ((1 << S2BMTT_d) | (1 << S2BMTT_d2))
#define S2BMTT_MK_D2 ((1 << S2BMTT_d) | (1 << S2BMTT_d3))
#define S2BMTT_MK_xll3 (1 << S2BMTT_l)
#define S2BMTT_MK_xrr3 ((1 << S2BMTT_r) | (1 << S2BMTT_r3))
#define S2BMTT_MK_xlr3 (1 << S2BMTT_lr3)
#define S2BMTT_MK_L2 (S2BMTT_MK_xll3 | S2BMTT_MK_xrr3 | (1 << S2BMTT_r2) | (S2BMTT_MK_xlr3))
#define S2BMTT_MK_R2 (S2BMTT_MK_xll3 | S2BMTT_MK_xrr3)
#define S2BMTT_MK_l ((1 << S2BMTT_r) | (1 << S2BMTT_r2))
#define S2BMTT_MK_l3 ((1 << S2BMTT_r3) | (1 << S2BMTT_r2))
#define S2BMTT_MK_xff3 (1 << S2BMTT_f)
#define S2BMTT_MK_xbb3 ((1 << S2BMTT_b) | (1 << S2BMTT_b3))
#define S2BMTT_MK_xfb3 (1 << S2BMTT_fb3)
#define S2BMTT_MK_F2 (S2BMTT_MK_xff3 | S2BMTT_MK_xbb3 | (1 << S2BMTT_b2) | (S2BMTT_MK_xfb3))
#define S2BMTT_MK_B2 (S2BMTT_MK_xff3 | S2BMTT_MK_xbb3)
#define S2BMTT_MK_f ((1 << S2BMTT_b) | (1 << S2BMTT_b2))
#define S2BMTT_MK_f3 ((1 << S2BMTT_b3) | (1 << S2BMTT_b2))

UINT stage2_block_moves_to_try [29] = {
	S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_u2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D1 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D3 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_D2 | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_u2d2) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_ud3) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_D3) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	(1 << S2BMTT_D) | S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_L_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_L2 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_R2 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_l | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_l3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_xrr3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_xlr3 | S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_U_ALL | S2BMTT_MK_F_ALL,
	S2BMTT_MK_F2 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_B2 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_f | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_f3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_xbb3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_xfb3 | S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL,
	S2BMTT_MK_U_ALL | S2BMTT_MK_L_ALL
};

#define	STG2B_U_ANY	1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14
#define	STG2B_U_GEN	14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14
#define	STG2B_U2	14,14,14,13,12,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14
#define	STG2B_L_ANY	15,17,18,19,16,21,21,21,20,0,21,21,21
#define STG2B_L_GEN	21,21,21,21,21,21,21,21,21,0,21,21,21
#define	STG2B_F_ANY	22,24,25,26,23,28,28,28,27,0,28,28,28
#define STG2B_F_GEN	28,28,28,28,28,28,28,28,28,0,28,28,28

UINT stage2_btm_next_ms[29][47] = {
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U2, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_GEN, STG2B_L_ANY, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_GEN, STG2B_F_ANY },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN },
	{ STG2B_U_ANY, STG2B_L_ANY, STG2B_F_GEN }
};

int
solveitIDA_STAGE2 (const CubeStage2& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE2 (init_cube, g1, 0, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE2 (const CubeStage2& cube1, int depth, int moves_done, UINT move_state, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage2 cube2;
	int mov_idx, mc, j;
	UINT next_ms = 0;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			//if (got_interrupt) {
			//	throw 1;
			//}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0;
	if (dist <= depth) {
		dist = prune_funcEDGCEN_STAGE2 (cube1);
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < N_STAGE2_2TWIST_MOVES; ++mov_idx) {
				int mtype = stage2_2twist_types[mov_idx];
				if ((stage2_twist_moves_to_try[move_state] & (1 << mtype)) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_2twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_2twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_twist_next_ms[move_state][mtype];
					move_list[moves_done] = stage2_twist_map1[N_STAGE2_TWIST_MOVES + mov_idx];
					move_list[moves_done + 1] = stage2_twist_map2[N_STAGE2_TWIST_MOVES + mov_idx];
					if (treesearchSTAGE2 (cube2, depth - 2, moves_done + 2,
							next_ms, goal, metric, move_list, pmove_count))
					{
						return true;
					}
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg2[metric]; ++mov_idx) {
			bool did_move = false;
			switch (metric) {
			case 0:
				if ((stage2_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2 = cube1;
					cube2.do_move (mov_idx);
					next_ms = stage2_stm_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((stage2_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_twist_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 2:
				if ((stage2_block_moves_to_try[move_state] & (1 << stage2_btm_mtt_idx[mov_idx])) != 0) {
					cube2 = cube1;
					for (j = 0; stage2_block_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_block_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_btm_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			}
			if (did_move) {
				mc = mov_idx;
				switch (metric) {
				case 1:
					mc = stage2_twist_map1[mov_idx];
					break;
				case 2:
					mc = stage2_block_map[mov_idx];
					break;
				}
				move_list[moves_done] = mc;
				if (treesearchSTAGE2 (cube2, depth - 1, moves_done + 1,
							next_ms, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
	}
	return false;
}

int
solveitIDA_STAGE3 (const CubeStage3& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE3 (init_cube, g1, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE3 (const CubeStage3& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage3 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			//if (got_interrupt) {
			//	throw 1;
			//}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0;
	if ((0x3 & 0x1) != 0) {
		dist = prune_funcCEN_STAGE3 (cube1);
	}
	if ((0x3 & 0x2) != 0 && dist <= depth) {
		dist = prune_funcEDGE_STAGE3 (cube1);
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < N_STAGE3_2TWIST_MOVES; ++mov_idx) {
				cube2 = cube1;
				mc = stage3_2twist_moves[mov_idx][0];		//!!! metric dependency
				cube2.do_move (mc);
				mc = stage3_2twist_moves[mov_idx][1];
				if (mc >= 0) {
					cube2.do_move (mc);
				}
				move_list[moves_done] = stage3_twist_map1[N_STAGE3_TWIST_MOVES + mov_idx];
				move_list[moves_done + 1] = stage3_twist_map2[N_STAGE3_TWIST_MOVES + mov_idx];
				if (treesearchSTAGE3 (cube2, depth - 2, moves_done + 2, goal, metric, move_list, pmove_count))
				{
					return true;
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg3[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				//TODO: This not finished.
				for (j = 0; stage3_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				//TODO: This not finished.
				for (j = 0; stage3_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage3_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage3_block_map[mov_idx];
				break;
			}
			move_list[moves_done] = mc;
			if (treesearchSTAGE3 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}

int
solveitIDA_STAGE4 (const CubeStage4& init_cube, int* move_list, int metric)
{
	int move_count;
	int g1;

	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE4 (init_cube, g1, 0, g1, metric, move_list, &move_count)) {
			return g1;
		}
	}
	return 999;
}

bool
treesearchSTAGE4 (const CubeStage4& cube1, int depth, int moves_done, int goal, int metric, int* move_list, int* pmove_count)
{
	CubeStage4 cube2;
	int mov_idx, mc, j;
	if (move_list == NULL || pmove_count == NULL || depth < 0) {
		printf ("arg error\n");
		exit (1);
	}
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			//if (got_interrupt) {
			//	throw 1;
			//}
			return false;
		}
		*pmove_count = moves_done;
		return true;
	}
	int dist = 0;
	if ((0x3 & 0x1) != 0) {
		dist = prune_funcCENCOR_STAGE4 (cube1);
	}
	if (dist <= depth && (0x3 & 0x2) != 0) {
		dist = prune_funcEDGCEN_STAGE4 (cube1);
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg4[metric]; ++mov_idx) {
			cube2 = cube1;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				for (j = 0; stage4_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				for (j = 0; stage4_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage4_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage4_block_map[mov_idx];
				break;
			}
			move_list[moves_done] = mc;
			if (treesearchSTAGE4 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, pmove_count))
			{
				return true;
			}
		}
	}
	return false;
}

int
solveit4x4x4IDA (const CubeState& init_cube, int* move_list, int metric)
{

	int i;
	CubeStage1 s1;
	CubeStage2 s2;
	CubeStage3 s3;
	CubeStage4 s4;
	CubeSqsCoord s5;
	s1.init ();
	s2.init ();
	s3.init ();
	s4.init ();
	s5.init ();
	convert_std_cube_to_stage1 (init_cube, &s1);
	int count1 = solveitIDA_STAGE1 (s1, move_list, metric);
	if (count1 < 0 || count1 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	int count = count1;
	CubeState cube1 = init_cube;
	switch (metric) {
	case 0:
		break;
	case 1:
		for (i = 0; i < count1; ++i) {
			move_list[i] = stage1_twist_list[move_list[i]];
		}
		break;
	case 2:
		for (i = 0; i < count1; ++i) {
			move_list[i] = stage1_block_list[move_list[i]];
		}
		break;
	}
	printf ("Stage1: ");
	print_move_list (count1, move_list);
	printf ("\n");

	scrambleCUBE (&cube1, count, move_list);
	int r3 = cube1.m_cor[0] >> 3;
	switch (r3) {
	case 0:
		break;	//no whole cube rotation
	case 1:
		cube1.do_move (Lf3);
		cube1.do_move (Ls3);
		cube1.do_move (Rs);
		cube1.do_move (Rf);
		cube1.do_move (Uf3);
		cube1.do_move (Us3);
		cube1.do_move (Ds);
		cube1.do_move (Df);
		break;
	case 2:
		cube1.do_move (Ff);
		cube1.do_move (Fs);
		cube1.do_move (Bs3);
		cube1.do_move (Bf3);
		cube1.do_move (Uf);
		cube1.do_move (Us);
		cube1.do_move (Ds3);
		cube1.do_move (Df3);
		break;
	default:
		printf ("Invalid cube rotation state.\n");
		exit (1);
	}

	convert_std_cube_to_stage2 (cube1, &s2);
	int count2 = solveitIDA_STAGE2 (s2, &move_list[count], metric);
	if (count2 < 0 || count2 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube2 = cube1;
	//translate from stage2 move indices to standard move codes
	if (metric == 0) {
		for (i = count; i < count + count2; ++i) {
			move_list[i] = stage2_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube2, count2, &move_list[count]);

	//translate move codes for whole cube rotations performed
	for (i = count; i < count + count2; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r3];
	}
	printf ("Stage2: ");
	print_move_list (count2, &move_list[count]);
	printf ("\n");

	count += count2;

	int r6 = r3;
	if (cube2.m_cen[16] < 4) {
		cube2.do_move (Uf);
		cube2.do_move (Us);
		cube2.do_move (Ds3);
		cube2.do_move (Df3);
		r6 += 3;
	}
	convert_std_cube_to_stage3 (cube2, &s3);
	s3.m_edge_odd = cube2.edgeUD_parity_odd ();
	int count3 = solveitIDA_STAGE3 (s3, &move_list[count], metric);
	if (count3 < 0 || count3 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube3 = cube2;
	if (metric == 0) {
		for (i = count; i < count + count3; ++i) {
			move_list[i] = stage3_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube3, count3, &move_list[count]);
	for (i = count; i < count + count3; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	printf ("Stage3: ");
	print_move_list (count3, &move_list[count]);
	printf ("\n");

	count += count3;

	convert_std_cube_to_stage4 (cube3, &s4);
	int count4 = solveitIDA_STAGE4 (s4, &move_list[count], metric);
	if (count4 < 0 || count4 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	CubeState cube4 = cube3;
	if (metric == 0) {
		for (i = count; i < count + count4; ++i) {
			move_list[i] = stage4_slice_moves[move_list[i]];
		}
	}
	scrambleCUBE (&cube4, count4, &move_list[count]);
	for (i = count; i < count + count4; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	printf ("Stage4: ");
	print_move_list (count4, &move_list[count]);
	printf ("\n");

	count += count4;

	convert_std_cube_to_squares (cube4, &s5);
	int count5 = solveitIDA_SQS (s5, &move_list[count], metric);
	if (count5 < 0 || count5 > 90) {
		printf ("Solve failure!\n");
		return -1;
	}
	if (metric == 0) {
		for (i = count; i < count + count5; ++i) {
			move_list[i] = sq_moves[move_list[i]];
		}
	}
	for (i = count; i < count + count5; ++i) {
		move_list[i] = xlate_r6[move_list[i]][r6];
	}
	printf ("Stage5: ");
	print_move_list (count5, &move_list[count]);
	printf ("\n");

	count += count5;
	return count;
}


void
scrambleCUBE (CubeState* pcube, int move_count, const int* move_arr)
{
	//Now supports double-layer twist turns: Ufs (36) .. Bfs2 (53)
	int i;
	for (i = 0; i < move_count; ++i) {
		int mc = move_arr[i];
		if (mc >= Ufs) {
			pcube->do_move (dbltwists[mc - Ufs][0]);
			pcube->do_move (dbltwists[mc - Ufs][1]);
		} else {
			pcube->do_move (mc);
		}
	}
}

int
prune_funcCOR_STAGE1 (const CubeStage1& cube1)
{
	return get_dist_4bit (cube1.m_co, &prune_table_cor1[0]);
}

int
prune_funcEDGE_STAGE1 (const CubeStage1& cube1)
{
	return get_dist_4bit (cube1.m_edge_ud_combo8, &prune_table_edg1[0]);
}

int
prune_funcEDGCEN_STAGE2 (const CubeStage2& cube2)
{
	UINT clocf = 0;

	UINT clocb = 0;
	stage2_cen_to_cloc4s (cube2.m_centerFB, &clocf, &clocb);
	UINT d1 = get_dist_4bit (N_STAGE2_EDGE_CONFIGS*clocf + cube2.m_edge, &prune_table_edgcen2[0]);
	UINT d2 = get_dist_4bit (N_STAGE2_EDGE_CONFIGS*clocb + cube2.m_edge, &prune_table_edgcen2[0]);
	if (d2 >= d1) {
		return d2;
	}
	return d1;
}

int
prune_funcCEN_STAGE3 (const CubeStage3& cube1)
{
	return get_dist_4bit (cube1.m_centerLR, &prune_table_cen3[0]);
}

int
prune_funcEDGE_STAGE3 (const CubeStage3& cube1)
{
	UINT idx = cube1.m_edge;
	if (cube1.m_edge_odd) {
		idx += N_STAGE3_EDGE_CONFIGS;
	}
	return get_dist_4bit (idx, &prune_table_edg3[0]);
}

int
prune_funcCENCOR_STAGE4 (const CubeStage4& cube1)
{
	UINT idx = N_STAGE4_CENTER_CONFIGS*cube1.m_corner + cube1.m_centerUD;
	return get_dist_4bit (idx, &prune_table_cencor4[0]);
}

int
prune_funcEDGCEN_STAGE4 (const CubeStage4& cube1)
{
	UINT idx = N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
	return get_dist_4bit (idx, &prune_table_edgcen4[0]);
}

int
prune_funcCENCOR_STAGE5 (const CubeSqsCoord& cube1)
{
	UINT idx = N_SQS_CORNER_PERM*cube1.m_cen12x12x12 + cube1.m_cp96;
	return get_dist_4bit (idx, &prune_table_cencor5[0]);
}

int
prune_funcEDGCOR_STAGE5 (const CubeSqsCoord& cube1)
{
	UINT idx = N_SQS_CORNER_PERM*cube1.m_ep96x96x96 + cube1.m_cp96;
	return get_dist_4bit (idx, &prune_table_edgcor5[0]);
}


void
print_move_list (int count, const int* move_list)
{
	int j;
	if (count >= 0) {
		printf ("[%2d] ", count);
		for (j = 0; j < count; ++j) {
			printf (" %s", move_strings[move_list[j]]);
		}
	} else {
		printf ("[Did not solve]");
	}
}

int
random (int n)
{
	return rand() % n;
}
