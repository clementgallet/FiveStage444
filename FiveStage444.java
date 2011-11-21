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

	private static int sq_moves[] = { Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2 };

	private static int sq_twist_map1[] = {
	Uf2, Ufs2, Dfs2, Df2,
	Lf2, Lfs2, Rfs2, Rf2,
	Ff2, Ffs2, Bfs2, Bf2
	};

	private static int sq_block_map[] = {
	Uf2, Us2, Ufs2, Dfs2, Ds2, Df2,
	Lf2, Ls2, Lfs2, Rfs2, Rs2, Rf2,
	Ff2, Fs2, Ffs2, Bfs2, Bs2, Bf2,
	Us2Ds2, Ls2Rs2, Fs2Bs2
	};

	private static int n_moves_metric_stg5[] = { N_SQMOVES, N_SQ_TWIST_MOVES, N_SQ_BLOCK_MOVES};

	private static int stage1_twist_list[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
	Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
	};

	private static int stage1_block_list[] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf, Lf3, Lf2, Ls, Ls3, Ls2, Rf, Rf3, Rf2, Rs, Rs3, Rs2,
	Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff, Ff3, Ff2, Fs, Fs3, Fs2, Bf, Bf3, Bf2, Bs, Bs3, Bs2,
	Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
	};

	private static int n_moves_metric_stg1[] = { N_BASIC_MOVES, N_STAGE1_TWIST_MOVES, N_STAGE1_BLOCK_MOVES};

	private static int stage2_twist_map1[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3,
	Lfs, Lfs3, Rfs, Rfs3, Lfs, Lfs3, Rfs, Rfs3
	};

	private static int stage2_twist_map2[] = {
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3,
	Lf3, Lf, Rf3, Rf, Lf, Lf3, Rf, Rf3
	};

	private static int stage2_block_map[] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Lfs2, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
	};

	private static int n_moves_metric_stg2[] = { N_STAGE2_SLICE_MOVES, N_STAGE2_TWIST_MOVES, N_STAGE2_BLOCK_MOVES};

	private static int stage3_block_map[] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
	};

	private static int n_moves_metric_stg3[] = { N_STAGE3_SLICE_MOVES, N_STAGE3_TWIST_MOVES, N_STAGE3_BLOCK_MOVES};

	private static int stage4_twist_map1[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2
	};

	private static int stage4_block_map[] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs2, Bf2, Bs2,	Ffs2, Bfs2, Fs2Bs2
	};

	private static int n_moves_metric_stg4[] = { N_STAGE4_SLICE_MOVES, N_STAGE4_TWIST_MOVES, N_STAGE4_BLOCK_MOVES };

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

	/*** For stage 2 ***/

	private static int STG2_SL_MS_X = 0;
	private static int STG2_SL_MS_U = 1;
	private static int STG2_SL_MS_u = 2;
	private static int STG2_SL_MS_d = 3;
	private static int STG2_SL_MS_D = 4;
	private static int STG2_SL_MS_L = 5;
	private static int STG2_SL_MS_l = 6;
	private static int STG2_SL_MS_r = 7;
	private static int STG2_SL_MS_R = 8;
	private static int STG2_SL_MS_F = 9;
	private static int STG2_SL_MS_f = 10;
	private static int STG2_SL_MS_b = 11;
	private static int STG2_SL_MS_B = 12;

	private static int STG2_TW_MS_X = 0;
	private static int STG2_TW_MS_u = 1;
	private static int STG2_TW_MS_U = 2;
	private static int STG2_TW_MS_d = 3;
	private static int STG2_TW_MS_D = 4;
	private static int STG2_TW_MS_l = 5;
	private static int STG2_TW_MS_L = 6;
	private static int STG2_TW_MS_r = 7;
	private static int STG2_TW_MS_R = 8;
	private static int STG2_TW_MS_f = 9;
	private static int STG2_TW_MS_F = 10;
	private static int STG2_TW_MS_b = 11;
	private static int STG2_TW_MS_B = 12;

	private static int stage2_slice_moves_to_try [] = {
	0xFFFFFFF,
	0xFFFFFF8, 0xFFFFFC0, 0xFFFF1C0, 0xFFFF000,
	0xFFFEFFF, 0xFFF0FFF, 0xFF10FFF, 0xFF00FFF,
	0xFEFFFFF, 0xF0FFFFF, 0x10FFFFF, 0x00FFFFF
	};

	private static int stage2_stm_next_ms[][] = {
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_L,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_L,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_R,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_r,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_R,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_F,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_f,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_B,STG2_SL_MS_b,STG2_SL_MS_b,STG2_SL_MS_b },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_B,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X },
	{	STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_U,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_u,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_D,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_d,STG2_SL_MS_L,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_l,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X,STG2_SL_MS_X }
};

	private static int stage2_2twist_types[] = {
	22, 22, 23, 23, 22, 22, 23, 23,
	20, 20, 21, 21, 20, 20, 21, 21
};

	private static int stage2_twist_moves_to_try [] = {
	0xFFFFFF,
	0xFFFE3F, 0xFFFE38, 0xFFF038, 0xFFF000,
	0xEFBFFF, 0xEFAFFF, 0xCF2FFF, 0xCF0FFF,
	0xBBFFFF, 0xBAFFFF, 0x32FFFF, 0x30FFFF
};

	private static int stage2_twist_next_ms[][] = {
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },

	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },

	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_X,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_f,STG2_TW_MS_b,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_F,STG2_TW_MS_B },

	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_F,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_B },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_b,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_B },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_X,STG2_TW_MS_B,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X },
	{	STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_U,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_D,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_u,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_d,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_l,STG2_TW_MS_r,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_X,STG2_TW_MS_L,STG2_TW_MS_R,STG2_TW_MS_X,STG2_TW_MS_X }
};

	private static byte S2BMTT_X = 0;		//moves not used for IDA* search
	private static byte S2BMTT_UGEN = 1;	//U-axis general moves
	private static byte S2BMTT_u = 2;
	private static byte S2BMTT_u3 = 3;
	private static byte S2BMTT_u2 = 4;
	private static byte S2BMTT_D = 5;
	private static byte S2BMTT_D3 = 6;
	private static byte S2BMTT_D2 = 7;
	private static byte S2BMTT_d = 8;
	private static byte S2BMTT_d3 = 9;
	private static byte S2BMTT_d2 = 10;
	private static byte S2BMTT_u2d2 = 11;
	private static byte S2BMTT_ud3 = 12;	//and u3d
	private static byte S2BMTT_LGEN = 13;	//L-Axis general moves
	private static byte S2BMTT_l = 14;	//and l'
	private static byte S2BMTT_r = 15;
	private static byte S2BMTT_r3 = 16;
	private static byte S2BMTT_r2 = 17;
	private static byte S2BMTT_lr3 = 18;	//and l'r
	private static byte S2BMTT_FGEN = 19;	//F-Axis general moves
	private static byte S2BMTT_f = 20;	//and f'
	private static byte S2BMTT_b = 21;
	private static byte S2BMTT_b3 = 22;
	private static byte S2BMTT_b2 = 23;
	private static byte S2BMTT_fb3 = 24;	//and f'b

	private static byte stage2_btm_mtt_idx[] = {
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

	private static int stage2_block_moves_to_try [] = {
	(0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_u3) | (1 << S2BMTT_u2) | (1 << S2BMTT_D) | (1 << S2BMTT_D2) | (1 << S2BMTT_d) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_u) | (1 << S2BMTT_u2) | (1 << S2BMTT_D3) | (1 << S2BMTT_D2) | (1 << S2BMTT_d) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_u) | (1 << S2BMTT_u3) | (1 << S2BMTT_D) | (1 << S2BMTT_D3) | (1 << S2BMTT_d) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_D) | (1 << S2BMTT_D2) | (1 << S2BMTT_d) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_D3) | (1 << S2BMTT_D2) | (1 << S2BMTT_d3) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_D) | (1 << S2BMTT_D3) | (1 << S2BMTT_d) | (1 << S2BMTT_d3)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_d3) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_d) | (1 << S2BMTT_d2)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_d) | (1 << S2BMTT_d3)) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	(1 << S2BMTT_u2d2) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	(1 << S2BMTT_ud3) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	(1 << S2BMTT_D3) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	(1 << S2BMTT_D) | (0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	(0x3F << S2BMTT_LGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_l) | ((1 << S2BMTT_r) | (1 << S2BMTT_r3)) | (1 << S2BMTT_r2) | ((1 << S2BMTT_lr3))) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_l) | ((1 << S2BMTT_r) | (1 << S2BMTT_r3))) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_r) | (1 << S2BMTT_r2)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_r3) | (1 << S2BMTT_r2)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_r) | (1 << S2BMTT_r3)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	(1 << S2BMTT_lr3) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	(0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_FGEN),
	((1 << S2BMTT_f) | ((1 << S2BMTT_b) | (1 << S2BMTT_b3)) | (1 << S2BMTT_b2) | ((1 << S2BMTT_fb3))) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	((1 << S2BMTT_f) | ((1 << S2BMTT_b) | (1 << S2BMTT_b3))) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	((1 << S2BMTT_b) | (1 << S2BMTT_b2)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	((1 << S2BMTT_b3) | (1 << S2BMTT_b2)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	((1 << S2BMTT_b) | (1 << S2BMTT_b3)) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	(1 << S2BMTT_fb3) | (0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN),
	(0xFFF << S2BMTT_UGEN) | (0x3F << S2BMTT_LGEN)
};

	private static int stage2_btm_next_ms[][] = {
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,13,12,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 21,21,21,21,21,21,21,21,21,0,21,21,21, 22,24,25,26,23,28,28,28,27,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 },
	{ 1,2,3,4,5,6,7,8,9,14,14,14,10,10,11,0,0,0,14,14,14, 15,17,18,19,16,21,21,21,20,0,21,21,21, 28,28,28,28,28,28,28,28,28,0,28,28,28 }
};

	/*** Stage 5 ***/

	private static int sqs_slice_moves_to_try [] = {
	0xFFE, 0xFFC, 0xFF8, 0xFF0,
	0xFEF, 0xFCF, 0xF8F, 0xF0F,
	0xEFF, 0xCFF, 0x8FF, 0x0FF,
	0xFFF
};

	private static int sqs_stm_next_ms[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	private static int SQS_TW_MS_U = 0;
	private static int SQS_TW_MS_D = 1;
	private static int SQS_TW_MS_Uu = 2;
	private static int SQS_TW_MS_u = 3;
	private static int SQS_TW_MS_d = 4;
	private static int SQS_TW_MS_UD = 5;
	private static int SQS_TW_MS_Ud = 6;

	private static int SQS_TW_MS_L = 8;
	private static int SQS_TW_MS_R = 9;
	private static int SQS_TW_MS_Ll = 10;
	private static int SQS_TW_MS_l = 11;
	private static int SQS_TW_MS_r = 12;
	private static int SQS_TW_MS_LR = 13;
	private static int SQS_TW_MS_Lr = 14;

	private static int SQS_TW_MS_F = 16;
	private static int SQS_TW_MS_B = 17;
	private static int SQS_TW_MS_Ff = 18;
	private static int SQS_TW_MS_f = 19;
	private static int SQS_TW_MS_b = 20;
	private static int SQS_TW_MS_FB = 21;
	private static int SQS_TW_MS_Fb = 22;

	private static int SQS_TW_MS_X = 23;

	private static int sqs_twist_moves_to_try[] = {
	0xBBA, 0xBB4, 0xBB0, 0xBB0, 0xBB0, 0xBB4, 0xBB0, 0xBB0,
	0xBAB, 0xB4B, 0xB0B, 0xB0B, 0xB0B, 0xB4B, 0xB0B, 0xB0B,
	0xABB, 0x4BB, 0x0BB, 0x0BB, 0x0BB, 0x4BB, 0x0BB, 0xBBB
	};

	private static int sqs_twist_next_ms[][] = {
	{ SQS_TW_MS_X,SQS_TW_MS_u,SQS_TW_MS_X,SQS_TW_MS_UD, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_d,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Ud,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_l,SQS_TW_MS_X,SQS_TW_MS_LR, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_r,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Lr,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_f,SQS_TW_MS_X,SQS_TW_MS_FB },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_b,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Fb,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B }
};

	private static int SQS_BL_MS_U = 0;
	private static int SQS_BL_MS_XU = 1;
	private static int SQS_BL_MS_L = 2;
	private static int SQS_BL_MS_XL = 3;
	private static int SQS_BL_MS_F = 4;
	private static int SQS_BL_MS_XF = 5;
	private static int SQS_BL_MS_X = 6;

	private static int sqs_block_moves_to_try[] = {
	0x1B7DD0, 0x1B7DC0, 0x177437, 0x177037, 0x0D0DF7, 0x0C0DF7, 0x1F7DF7
};

	private static int sqs_block_next_ms[][] = {
	{ SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_X, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_X, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_X, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_XF }
};



	public static void main(String[] args){

		//C++: strcpy (&datafiles_path[0], &default_datafile_path[0]);

		int random_count = 100;
		int metric = 0;

		// Parse command-line arguments. Will do it later...
		/*
		int i2;
		for (i2 = 1; i2 < args.length; ++i2) {
			switch (args[i2]) {
				case "-s": case "-S":
					metric = 0;
					break;
				case "-t": case "-T":
					metric = 1;
					break;
				case "-b": case "-B":
					metric = 2;
					break;
			}
		}
		*/

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

	public static int solveitIDA_STAGE1 (CubeStage1 init_cube, int[] move_list, int index, int metric){
		int g1;
		for (g1 = 0; g1 <= 30; ++g1) {
			if (treesearchSTAGE1 (init_cube, g1, 0, g1, metric, move_list, index)) {
				return g1;
			}
		}
		return 999;
	}

	public static boolean treesearchSTAGE1 (CubeStage1 cube1, int depth, int moves_done, int goal, int metric, int[] move_list, int index){
	CubeStage1 cube2 = new CubeStage1();
	int mov_idx, mc, j;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = cube1.prune_funcCOR_STAGE1();
	if (dist <= depth) {
		dist = cube1.prune_funcEDGE_STAGE1();
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg1[metric]; ++mov_idx) {
			cube2.m_co = cube1.m_co;
			cube2.m_edge_ud_combo8 = cube1.m_edge_ud_combo8;
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
			move_list[moves_done+index] = mov_idx;
			if (treesearchSTAGE1 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, index))
			{
				return true;
			}
		}
	}
	return false;
}

	public static int solveitIDA_STAGE2 (CubeStage2 init_cube, int[] move_list, int index, int metric){
		int g1;
		for (g1 = 0; g1 <= 30; ++g1) {
			if (treesearchSTAGE2 (init_cube, g1, 0, 0, g1, metric, move_list, index)) {
				return g1;
			}
		}
		return 999;
	}

	public static boolean treesearchSTAGE2 (CubeStage2 cube1, int depth, int moves_done, int move_state, int goal, int metric, int[] move_list, int index){
	CubeStage2 cube2 = new CubeStage2();
	int mov_idx, mc, j;
	int next_ms = 0;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = 0;
	if (dist <= depth) {
		dist = cube1.prune_funcEDGCEN_STAGE2 ();
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < N_STAGE2_2TWIST_MOVES; ++mov_idx) {
				int mtype = stage2_2twist_types[mov_idx];
				if ((stage2_twist_moves_to_try[move_state] & (1 << mtype)) != 0) {
					cube2.m_edge = cube1.m_edge;
					cube2.m_centerFB = cube1.m_centerFB;

					for (j = 0; stage2_2twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage2_2twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = stage2_twist_next_ms[move_state][mtype];
					move_list[moves_done + index] = stage2_twist_map1[Constants.N_STAGE2_TWIST_MOVES + mov_idx];
					move_list[moves_done + 1 + index] = stage2_twist_map2[Constants.N_STAGE2_TWIST_MOVES + mov_idx];
					if (treesearchSTAGE2 (cube2, depth - 2, moves_done + 2, next_ms, goal, metric, move_list, index))
					{
						return true;
					}
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg2[metric]; ++mov_idx) {
			boolean did_move = false;
			switch (metric) {
			case 0:
				if ((stage2_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2.m_edge = cube1.m_edge;
					cube2.m_centerFB = cube1.m_centerFB;
					cube2.do_move (mov_idx);
					next_ms = stage2_stm_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((stage2_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2.m_edge = cube1.m_edge;
					cube2.m_centerFB = cube1.m_centerFB;
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
					cube2.m_edge = cube1.m_edge;
					cube2.m_centerFB = cube1.m_centerFB;
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
				move_list[moves_done + index] = mc;
				if (treesearchSTAGE2 (cube2, depth - 1, moves_done + 1,	next_ms, goal, metric, move_list, index))
				{
					return true;
				}
			}
		}
	}
	return false;
}

	public static int solveitIDA_STAGE3 (CubeStage3 init_cube, int[] move_list, int index, int metric){
	int g1;
	for (g1 = 0; g1 <= 30; ++g1) {
		if (treesearchSTAGE3 (init_cube, g1, 0, g1, metric, move_list, index)) {
			return g1;
		}
	}
	return 999;
}

	public static boolean treesearchSTAGE3 (CubeStage3 cube1, int depth, int moves_done, int goal, int metric, int[] move_list, int index){
	CubeStage3 cube2 = new CubeStage3();
	int mov_idx, mc, j;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = 0;
	if ((0x3 & 0x1) != 0) {
		dist = cube1.prune_funcCEN_STAGE3 ();
	}
	if ((0x3 & 0x2) != 0 && dist <= depth) {
		dist = cube1.prune_funcEDGE_STAGE3 ();
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < Constants.N_STAGE3_2TWIST_MOVES; ++mov_idx) {
				cube2.m_centerLR = cube1.m_centerLR; // TODO: Add a method copy.
				cube2.m_edge = cube1.m_edge;
				cube2.m_edge_odd = cube1.m_edge_odd;

				mc = stage3_2twist_moves[mov_idx][0];		//!!! metric dependency
				cube2.do_move (mc);
				mc = stage3_2twist_moves[mov_idx][1];
				if (mc >= 0) {
					cube2.do_move (mc);
				}
				move_list[moves_done + index] = stage3_twist_map1[Constants.N_STAGE3_TWIST_MOVES + mov_idx];
				move_list[moves_done + 1 + index] = stage3_twist_map2[Constants.N_STAGE3_TWIST_MOVES + mov_idx];
				if (treesearchSTAGE3 (cube2, depth - 2, moves_done + 2, goal, metric, move_list, index))
				{
					return true;
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg3[metric]; ++mov_idx) {
			cube2.m_centerLR = cube1.m_centerLR; // TODO: Add a method copy.
			cube2.m_edge = cube1.m_edge;
			cube2.m_edge_odd = cube1.m_edge_odd;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				//old TODO: This not finished.
				for (j = 0; stage3_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				//old TODO: This not finished.
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
			move_list[moves_done + index] = mc;
			if (treesearchSTAGE3 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, index)) // TODO: Create a new object for every call ? Maybe no.
			{
				return true;
			}
		}
	}
	return false;
}

	public static int solveitIDA_STAGE4 (CubeStage4 init_cube, int[] move_list, int index, int metric){
		int g1;
		for (g1 = 0; g1 <= 30; ++g1) {
			if (treesearchSTAGE4 (init_cube, g1, 0, g1, metric, move_list, index)) {
				return g1;
			}
		}
		return 999;
	}

	public static boolean treesearchSTAGE4 (CubeStage4 cube1, int depth, int moves_done, int goal, int metric, int[] move_list, int index){
	CubeStage4 cube2 = new CubeStage4();
	int mov_idx, mc, j;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = 0;
	if ((0x3 & 0x1) != 0) {
		dist = cube1.prune_funcCENCOR_STAGE4 ();
	}
	if (dist <= depth && (0x3 & 0x2) != 0) {
		dist = cube1.prune_funcEDGCEN_STAGE4 ();
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg4[metric]; ++mov_idx) {
			cube2.m_edge = cube1.m_edge;
			cube2.m_corner = cube1.m_corner;
			cube2.m_centerUD = cube1.m_centerUD;
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
			move_list[moves_done + index] = mc;
			if (treesearchSTAGE4 (cube2, depth - 1, moves_done + 1, goal, metric, move_list, index))
			{
				return true;
			}
		}
	}
	return false;
}

	public static int solveitIDA_SQS (CubeSqsCoord init_cube, int[] move_list, int index, int metric){
		int init_move_state[] = { 12, 23, 6 };
		int g1;
		for (g1 = 0; g1 <= 30; ++g1) {
			if (treesearchSQS (init_cube, g1, 0, init_move_state[metric], g1, metric, move_list, index)) {
				return g1;
			}
		}
		return 999;
	}

	public static boolean treesearchSQS (CubeSqsCoord cube1, int depth, int moves_done, int move_state, int goal, int metric, int[] move_list, int index){
	CubeSqsCoord cube2 = new CubeSqsCoord();
	int mov_idx, mc, j;
	int next_ms = 0;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = cube1.prune_funcEDGCOR_STAGE5 ();
	if (dist <= depth) {
		dist = cube1.prune_funcCENCOR_STAGE5 ();
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg5[metric]; ++mov_idx) {
			boolean did_move = false;
			cube2.m_cen12x12x12 = cube1.m_cen12x12x12;
			cube2.m_cp96 = cube1.m_cp96;
			cube2.m_ep96x96x96 = cube1.m_ep96x96x96;
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
				move_list[moves_done + index] = mc;
				if (treesearchSQS (cube2, depth - 1, moves_done + 1, next_ms, goal, metric, move_list, index))
				{
					return true;
				}
			}
		}
	}
	return false;
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
	int count1 = solveitIDA_STAGE1 (s1, move_list, 0, metric);
	tStage1 += t.elapsed();
	if (count1 < 0 || count1 > 90) {
		System.out.println ("Solve failure!\n");
		return -1;
	}
	int count = count1;
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
	int count2 = solveitIDA_STAGE2 (s2, move_list, count, metric);
	tStage2 += t.elapsed();
	if (count2 < 0 || count2 > 90) {
		System.out.println ("Solve failure!\n");
		return -1;
	}
	//translate from stage2 move indices to standard move codes
	if (metric == 0) {
		for (i = count; i < count + count2; ++i) {
			move_list[i] = stage2_slice_moves[move_list[i]];
		}
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
	int count3 = solveitIDA_STAGE3 (s3, move_list, count, metric);
	tStage3 += t.elapsed();
	if (count3 < 0 || count3 > 90) {
		System.out.println ("Solve failure!");
		return -1;
	}
	if (metric == 0) {
		for (i = count; i < count + count3; ++i) {
			move_list[i] = stage3_slice_moves[move_list[i]];
		}
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
	int count4 = solveitIDA_STAGE4 (s4, move_list, count, metric);
	tStage4 += t.elapsed();
	if (count4 < 0 || count4 > 90) {
		System.out.println ("Solve failure!");
		return -1;
	}
	if (metric == 0) {
		for (i = count; i < count + count4; ++i) {
			move_list[i] = stage4_slice_moves[move_list[i]];
		}
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
	int count5 = solveitIDA_SQS (s5, move_list, count, metric);
	tStage5 += t.elapsed();
	if (count5 < 0 || count5 > 90) {
		System.out.println ("Solve failure!");
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
