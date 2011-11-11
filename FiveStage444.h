#ifndef FIVESTAGE444_H
#define FIVESTAGE444_H

const char default_datafile_path[] = "";

int dbltwists[27][2] = {
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

const char* move_strings[63] = {
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

int sq_moves[N_SQMOVES] = { Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2 };

int sq_twist_map1[N_SQ_TWIST_MOVES] = {
	Uf2, Ufs2, Dfs2, Df2,
	Lf2, Lfs2, Rfs2, Rf2,
	Ff2, Ffs2, Bfs2, Bf2
};

int sq_block_map[N_SQ_BLOCK_MOVES] = {
	Uf2, Us2, Ufs2, Dfs2, Ds2, Df2,
	Lf2, Ls2, Lfs2, Rfs2, Rs2, Rf2,
	Ff2, Fs2, Ffs2, Bfs2, Bs2, Bf2,
	Us2Ds2, Ls2Rs2, Fs2Bs2
};

int n_moves_metric_stg5[3] = { N_SQMOVES, N_SQ_TWIST_MOVES, N_SQ_BLOCK_MOVES};

int stage1_twist_list[N_STAGE1_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
	Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
};

int stage1_block_list[N_STAGE1_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf, Lf3, Lf2, Ls, Ls3, Ls2, Rf, Rf3, Rf2, Rs, Rs3, Rs2,
	Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff, Ff3, Ff2, Fs, Fs3, Fs2, Bf, Bf3, Bf2, Bs, Bs3, Bs2,
	Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg1[3] = { N_BASIC_MOVES, N_STAGE1_TWIST_MOVES, N_STAGE1_BLOCK_MOVES};

int stage2_twist_map1[N_STAGE2_TWIST_MOVES + N_STAGE2_2TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3,
	Lfs, Lfs3, Rfs, Rfs3, Lfs, Lfs3, Rfs, Rfs3
};

int stage2_twist_map2[N_STAGE2_TWIST_MOVES + N_STAGE2_2TWIST_MOVES] = {
	-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3,
	Lf3, Lf, Rf3, Rf, Lf, Lf3, Rf, Rf3
};

int stage2_block_map[N_STAGE2_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Lfs2, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg2[3] = { N_STAGE2_SLICE_MOVES, N_STAGE2_TWIST_MOVES, N_STAGE2_BLOCK_MOVES};

int stage3_block_map[N_STAGE3_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg3[3] = { N_STAGE3_SLICE_MOVES, N_STAGE3_TWIST_MOVES, N_STAGE3_BLOCK_MOVES};

int stage4_twist_map1[N_STAGE4_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2
};

int stage4_block_map[N_STAGE4_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs2, Bf2, Bs2,	Ffs2, Bfs2, Fs2Bs2
};

int n_moves_metric_stg4[3] = { N_STAGE4_SLICE_MOVES, N_STAGE4_TWIST_MOVES, N_STAGE4_BLOCK_MOVES };

int xlate_r6[63][6] = {
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

#endif
