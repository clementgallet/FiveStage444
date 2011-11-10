#ifndef CONSTANTS_H
#define CONSTANTS_H

#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <signal.h>

typedef unsigned int UINT;
typedef unsigned short USHORT;
typedef unsigned char UBYTE;
typedef UINT (*FUNC_PTR)(UINT,int);

const char default_datafile_path[] = "";

char datafiles_path[256];

typedef char Face;

const UINT N_CORNER_PERM = 40320;
const UINT N_CORNER_ORIENT = 2187;
const UINT N_EDGE_PERM_12_1 = 479001600;

const int N_CORNER_BM = 256;	//2**8

const int N_CUBESYM = 48;
const int N_ESYM = 16;
const int N_ANTISYM = 96;
const int N_SYMX = /* N_ANTISYM; // or */ N_CUBESYM;

const int N_CORNER_GROUPS = 984;
const int N_CGG_SIZE = 12;

const UINT N_SQS_EDGE_PERM = 96*96*96;
const UINT N_SQS_CENTER_PERM = 12*12*12;
const UINT N_SQS_CORNER_PERM = 96;
const UINT N_SQS_EDGE_SYMCOUNT = 21908;
const UINT N_SQS_EDGE_ANTISYM_COUNT = 12331; //not valid
//Antisymmetry appears to be invalid with indistinguishable centers

const UINT N_SYMCOUNT = (N_SYMX == N_ANTISYM ? N_SQS_EDGE_ANTISYM_COUNT : N_SQS_EDGE_SYMCOUNT);

const UINT N_SQS = N_SQS_EDGE_SYMCOUNT*N_SQS_CORNER_PERM*N_SQS_CENTER_PERM;
const UINT N_SQS_TABLE_SIZE = N_SQS/16u;

const UINT N_EDGE_COMBO8 = 735471;	// 24!/(16!*8!)
const UINT N_STAGE1 = N_EDGE_COMBO8*N_CORNER_ORIENT;
const UINT N_STAGE1_TABLE_SIZE = (N_STAGE1 + 15)/16;

const UINT N_SYM_STAGE2 = 8;
const UINT N_STAGE2_EDGE_CONFIGS = 420;
const UINT N_STAGE2_CENTER_CONFIGS = 51482970;	// 24!/(16!*24*24)
const UINT N_STAGE2_EDGE_SYMCONFIGS = 98;
const UINT N_STAGE2_CENTER_SYMCONFIGS = 6440445;
const UINT N_STAGE2_CEN_TABLE_SIZE = (N_STAGE2_CENTER_CONFIGS+31)/32;
const UINT N_STAGE2_TABLE_SIZE = N_STAGE2_EDGE_SYMCONFIGS*N_STAGE2_CEN_TABLE_SIZE;	//1 bit per position!
const UINT N_CENTER_COMBO4 = 10626;

const UINT N_SYM_STAGE3 = 8;
const UINT N_STAGE3_CENTER_CONFIGS = 900900;	//(16*15*14*13/24)*(12*11*10*9/24)
const UINT N_COMBO_16_8 = 12870;				//16!/(8!*8!)
const UINT N_STAGE3_EDGE_PAR = 2;
const UINT N_STAGE3_EDGE_CONFIGS = N_COMBO_16_8;	//16!/(8!*8!), does not include parity info
const UINT N_STAGE3_EDGE_SYMCONFIGS = 1763;
const UINT N_STAGE3 = N_STAGE3_EDGE_PAR*N_STAGE3_CENTER_CONFIGS * N_STAGE3_EDGE_SYMCONFIGS; //3,176,573,400
const UINT N_STAGE3_TABLE_SIZE = (N_STAGE3 + 15)/16;

const UINT N_SYM_STAGE4 = 16;
const UINT N_STAGE4_CENTER_CONFIGS = 70;	//8!/(4!*4!)
const UINT N_STAGE4_RAW_EDGE_CONFIGS = 40320*40320;		//176400;	//420*420 ?
const UINT N_STAGE4_EDGE_CONFIGS = 88200;	//420*420/2
const UINT N_STAGE4_RAW_EDGE_SOLVED_CONFIGS = 96*96;		//9216
const UINT N_STAGE4_EDGE_TABLE_SIZE = N_STAGE4_RAW_EDGE_CONFIGS/16;
const UINT N_STAGE4_CORNER_CONFIGS = 420;	//8!/96
const UINT N_STAGE4 = N_STAGE4_EDGE_CONFIGS * N_STAGE4_CENTER_CONFIGS * N_STAGE4_CORNER_CONFIGS; //2,593,080,000
const UINT N_STAGE4_TABLE_SIZE = N_STAGE4/16; //N_STAGE4_EDGE_CONFIGS_8 * N_STAGE4_CENTER_CONFIGS_2 * N_STAGE4_CORNER_CONFIGS;	//162,067,500
const UINT N_STAGE4_EDGE_HASH_TABLE = 200383;	//100153;
const UINT N_STAGE4_EDGE_HASH_DIVISOR = N_STAGE4_EDGE_HASH_TABLE - 2;	//also prime
const UINT N_STAGE4_EDGE_SYMCONFIGS = 5968;
const UINT N_STAGE4_SYM_REDUCED = N_STAGE4_EDGE_SYMCONFIGS * N_STAGE4_CENTER_CONFIGS * N_STAGE4_CORNER_CONFIGS;
const UINT N_STAGE4_SYM_TABLE_SIZE = (N_STAGE4_SYM_REDUCED + 15)/16;

const Face UP = 0;
const Face DOWN = 1;
const Face LEFT = 2;
const Face RIGHT = 3;
const Face FRONT = 4;
const Face BACK = 5;

char* face_names[6] = { "UP", "DOWN", "LEFT", "RIGHT", "FRONT", "BACK" };

//These are borrowed from an older program, different numbering scheme.
const Face FaceU = 0;
const Face FaceF = 1;
const Face FaceL = 2;
const Face FaceD = 3;
const Face FaceB = 4;
const Face FaceR = 5;

UINT byte_clr_masks[4] = { 0xFFFFFF00, 0xFFFF00FF, 0xFF00FFFF, 0x00FFFFFF };
UINT pow3tab[5] = { 1, 3, 9, 27, 81 };

//slice rotate codes
const int Uf  = 0;	//Up "face" (top slice) clockwise wrt top
const int Uf3 = 1;	//Up "face" counter-clockwise
const int Uf2 = 2;	//Up "face" 180 degrees

const int Us  = 3;	//Up "slice" (upper inner slice) clockwise wrt top
const int Us3 = 4;	//Up "slice" counter-clockwise
const int Us2 = 5;	//Up "slice" 180 degrees

const int Df  = 6;	//Down "face" (bottom slice) clockwise wrt bottom
const int Df3 = 7;	//Down "face" counter-clockwise
const int Df2 = 8;	//Down "face" 180 degrees

const int Ds  = 9;	//Down "slice" (lower inner slice) clockwise wrt bottom
const int Ds3 = 10;	//Down "slice" counter-clockwise
const int Ds2 = 11;	//Down "slice" 180 degrees

const int Lf  = 12;	//Left "face" (left-hand outer slice) clockwise wrt left side
const int Lf3 = 13;	//Left "face" counter-clockwise
const int Lf2 = 14;	//Left "face" 180 degrees

const int Ls  = 15;	//Left "slice" (left-hand inner slice) clockwise wrt left side
const int Ls3 = 16;	//Left "slice" counter-clockwise
const int Ls2 = 17;	//Left "slice" 180 degrees

const int Rf  = 18;	//Right "face" (right-hand outer slice) clockwise wrt right side
const int Rf3 = 19;	//Right "face" counter-clockwise
const int Rf2 = 20;	//Right "face" 180 degrees

const int Rs  = 21;	//Right "slice" (right-hand inner slice) clockwise wrt right side
const int Rs3 = 22;	//Right "slice" counter-clockwise
const int Rs2 = 23;	//Right "slice" 180 degrees

const int Ff  = 24;	//Front "face" (front outer slice) clockwise wrt front
const int Ff3 = 25;	//Front "face" counter-clockwise
const int Ff2 = 26;	//Front "face" 180 degrees

const int Fs  = 27;	//Front "slice" (front inner slice) clockwise wrt front
const int Fs3 = 28;	//Front "slice" counter-clockwise
const int Fs2 = 29;	//Front "slice" 180 degrees

const int Bf  = 30;	//Back "face" (rear outer slice) clockwise wrt back side
const int Bf3 = 31;	//Back "face" counter-clockwise
const int Bf2 = 32;	//Back "face" 180 degrees

const int Bs  = 33;	//Back "slice" (rear inner slice) clockwise wrt back side
const int Bs3 = 34;	//Back "slice" counter-clockwise
const int Bs2 = 35;	//Back "slice" 180 degrees

const int N_BASIC_MOVES  = Bs2 + 1;	//last rotate code plus one

//Twist moves (that are not also slice moves)
const int Ufs = 36;		//(Uu)
const int Ufs3 = 37;	//(Uu)'
const int Ufs2 = 38;	//(Uu)2
const int Dfs = 39;		//(Dd)
const int Dfs3 = 40;	//(Dd)'
const int Dfs2 = 41;	//(Dd)2
const int Lfs = 42;		//(Ll)
const int Lfs3 = 43;	//(Ll)'
const int Lfs2 = 44;	//(Ll)2
const int Rfs = 45;		//(Rr)
const int Rfs3 = 46;	//(Rr)'
const int Rfs2 = 47;	//(Rr)2
const int Ffs = 48;		//(Ff)
const int Ffs3 = 49;	//(Ff)'
const int Ffs2 = 50;	//(Ff)2
const int Bfs = 51;		//(Bb)
const int Bfs3 = 52;	//(Bb)'
const int Bfs2 = 53;	//(Bb)2

//Block moves (that are not also slice or twist moves)
const int UsDs3 = 54;
const int Us3Ds = 55;
const int Us2Ds2 = 56;
const int LsRs3 = 57;
const int Ls3Rs = 58;
const int Ls2Rs2 = 59;
const int FsBs3 = 60;
const int Fs3Bs = 61;
const int Fs2Bs2 = 62;

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

const int N_QTMOVES = 24;
int qt_moves[N_QTMOVES] = {
	Uf, Uf3, Us, Us3, Df, Df3, Ds, Ds3,
	Lf, Lf3, Ls, Ls3, Rf, Rf3, Rs, Rs3,
    Ff, Ff3, Fs, Fs3, Bf, Bf3, Bs, Bs3
};

const int N_SQMOVES = 12;
int sq_moves[N_SQMOVES] = { Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2 };
int sq_fc_moves[N_SQMOVES][4] = {	//Squares, fixed DRB corner moves
	{ 1, Uf2/3, 0, 0 },
	{ 1, Us2/3, 0, 0 },
	{ 1, Ds2/3, 0, 0 },
	{ 3, Uf2/3, Us2/3, Ds2/3 },
	{ 1, Lf2/3, 0, 0 },
	{ 1, Ls2/3, 0, 0 },
	{ 1, Rs2/3, 0, 0 },
	{ 3, Lf2/3, Ls2/3, Rs2/3 },
	{ 1, Ff2/3, 0, 0 },
	{ 1, Fs2/3, 0, 0 },
	{ 1, Bs2/3, 0, 0 },
	{ 3, Ff2/3, Fs2/3, Bs2/3 }
};

const int N_FACE_MOVES = 18;
int face_moves[N_FACE_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2,
    Ff, Ff3, Ff2, Bf, Bf3, Bf2
};

int basic_to_face[N_BASIC_MOVES] = {
	 0,  1,  2, -1, -1, -1,  3,  4,  5, -1, -1, -1,
	 6,  7,  8, -1, -1, -1,  9, 10, 11, -1, -1, -1,
	12, 13, 14, -1, -1, -1, 15, 16, 17, -1, -1, -1
};

const int N_SLICE_MOVES = 18;
int slice_moves[N_SLICE_MOVES] = {
	Us, Us3, Us2, Ds, Ds3, Ds2,
	Ls, Ls3, Ls2, Rs, Rs3, Rs2,
    Fs, Fs3, Fs2, Bs, Bs3, Bs2
};

int ident_table[36] = {
	 0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11,
	12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
    24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35
};


const int N_SQ_TWIST_MOVES = 12;
int sq_twist_moves[N_SQ_TWIST_MOVES][2] = {
	{ Uf2/3,    -1 },
	{ Uf2/3, Us2/3 },
	{ Df2/3, Ds2/3 },
	{ Df2/3,    -1 },
	{ Lf2/3,    -1 },
	{ Lf2/3, Ls2/3 },
	{ Rf2/3, Rs2/3 },
	{ Rf2/3,    -1 },
	{ Ff2/3,    -1 },
	{ Ff2/3, Fs2/3 },
	{ Bf2/3, Bs2/3 },
	{ Bf2/3,    -1 }
};

int sq_twist_map1[N_SQ_TWIST_MOVES] = {
	Uf2, Ufs2, Dfs2, Df2,
	Lf2, Lfs2, Rfs2, Rf2,
	Ff2, Ffs2, Bfs2, Bf2
};

const int N_SQ_BLOCK_MOVES = 21;
int sq_block_moves[N_SQ_BLOCK_MOVES][3] = {
	{ Uf2/3,    -1, -1 },
	{ Us2/3,    -1, -1 },
	{ Uf2/3, Us2/3, -1 },
	{ Df2/3, Ds2/3, -1 },
	{ Ds2/3,    -1, -1 },
	{ Df2/3,    -1, -1 },
	{ Lf2/3,    -1, -1 },
	{ Ls2/3,    -1, -1 },
	{ Lf2/3, Ls2/3, -1 },
	{ Rf2/3, Rs2/3, -1 },
	{ Rs2/3,    -1, -1 },
	{ Rf2/3,    -1, -1 },
	{ Ff2/3,    -1, -1 },
	{ Fs2/3,    -1, -1 },
	{ Ff2/3, Fs2/3, -1 },
	{ Bf2/3, Bs2/3, -1 },
	{ Bs2/3,    -1, -1 },
	{ Bf2/3,    -1, -1 },
	{ Us2/3, Ds2/3, -1 },
	{ Ls2/3, Rs2/3, -1 },
	{ Fs2/3, Bs2/3, -1 }
};

int sq_block_map[N_SQ_BLOCK_MOVES] = {
	Uf2, Us2, Ufs2, Dfs2, Ds2, Df2,
	Lf2, Ls2, Lfs2, Rfs2, Rs2, Rf2,
	Ff2, Fs2, Ffs2, Bfs2, Bs2, Bf2,
	Us2Ds2, Ls2Rs2, Fs2Bs2
};

const int N_MINIMAL_SQS_MOVES = 7;
int sq_minimal_sqs_moves[N_SQ_BLOCK_MOVES] = {
	Uf2/3, Us2/3, Df2/3, Lf2/3, Ls2/3, Ff2/3, Fs2/3
};

int n_moves_metric_stg5[3] = { N_SQMOVES, N_SQ_TWIST_MOVES, N_SQ_BLOCK_MOVES};

const int N_STAGE1_TWIST_MOVES_ALL = 54;
const int N_STAGE1_TWIST_MOVES = 36;
int stage1_twist_moves[N_STAGE1_TWIST_MOVES_ALL][4] = {
	{ Uf,   -1, -1, -1 },
	{ Uf3,  -1, -1, -1 },
	{ Uf2,  -1, -1, -1 },
	{ Df,   -1, -1, -1 },
	{ Df3,  -1, -1, -1 },
	{ Df2,  -1, -1, -1 },
	{ Uf,   Us, -1, -1 },
	{ Uf3, Us3, -1, -1 },
	{ Uf2, Us2, -1, -1 },
	{ Df,   Ds, -1, -1 },
	{ Df3, Ds3, -1, -1 },
	{ Df2, Ds2, -1, -1 },

	{ Lf,    -1, -1, -1 },
	{ Lf3,   -1, -1, -1 },
	{ Lf2,   -1, -1, -1 },
	{ Rf,    -1, -1, -1 },
	{ Rf3,   -1, -1, -1 },
	{ Rf2,   -1, -1, -1 },
	{ Lf,    Ls, -1, -1 },
	{ Lf3,  Ls3, -1, -1 },
	{ Lf2,  Ls2, -1, -1 },
	{ Rf,    Rs, -1, -1 },
	{ Rf3,  Rs3, -1, -1 },
	{ Rf2,  Rs2, -1, -1 },

	{ Ff,    -1, -1, -1 },
	{ Ff3,   -1, -1, -1 },
	{ Ff2,   -1, -1, -1 },
	{ Bf,    -1, -1, -1 },
	{ Bf3,   -1, -1, -1 },
	{ Bf2,   -1, -1, -1 },
	{ Ff,    Fs, -1, -1 },
	{ Ff3,  Fs3, -1, -1 },
	{ Ff2,  Fs2, -1, -1 },
	{ Bf,    Bs, -1, -1 },
	{ Bf3,  Bs3, -1, -1 },
	{ Bf2,  Bs2, -1, -1 },

	{ Uf,   Us, Ds3, -1 },
	{ Uf3, Us3, Ds, -1 },
	{ Uf2, Us2, Ds2, -1 },
	{ Df,   Ds, Us3, -1 },
	{ Df3, Ds3, Us, -1 },
	{ Df2, Ds2, Us2, -1 },

	{ Lf,    Ls, Rs3, -1 },
	{ Lf3,  Ls3, Rs, -1 },
	{ Lf2,  Ls2, Rs2, -1 },
	{ Rf,    Rs, Ls3, -1 },
	{ Rf3,  Rs3, Ls, -1 },
	{ Rf2,  Rs2, Ls2, -1 },

	{ Ff,    Fs, Bs3, -1 },
	{ Ff3,  Fs3, Bs, -1 },
	{ Ff2,  Fs2, Bs2, -1 },
	{ Bf,    Bs, Fs3, -1 },
	{ Bf3,  Bs3, Fs, -1 },
	{ Bf2,  Bs2, Fs2, -1 }
};

int stage1_twist_list[N_STAGE1_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
	Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
};

const int N_STAGE1_BLOCK_MOVES = 63;
const int N_STAGE1_BLOCK_MOVES_ALL = 81;
int stage1_block_moves[N_STAGE1_BLOCK_MOVES_ALL][4] = {
	{ Uf,   -1, -1, -1 },
	{ Uf3,  -1, -1, -1 },
	{ Uf2,  -1, -1, -1 },
	{ Us,   -1, -1, -1 },
	{ Us3,  -1, -1, -1 },
	{ Us2,  -1, -1, -1 },
	{ Df,   -1, -1, -1 },
	{ Df3,  -1, -1, -1 },
	{ Df2,  -1, -1, -1 },
	{ Ds,   -1, -1, -1 },
	{ Ds3,  -1, -1, -1 },
	{ Ds2,  -1, -1, -1 },
	{ Uf,   Us, -1, -1 },
	{ Uf3, Us3, -1, -1 },
	{ Uf2, Us2, -1, -1 },
	{ Df,   Ds, -1, -1 },
	{ Df3, Ds3, -1, -1 },
	{ Df2, Ds2, -1, -1 },
	{ Us,  Ds3, -1, -1 },
	{ Us3,  Ds, -1, -1 },
	{ Us2, Ds2, -1, -1 },

	{ Lf,   -1, -1, -1 },
	{ Lf3,  -1, -1, -1 },
	{ Lf2,  -1, -1, -1 },
	{ Ls,   -1, -1, -1 },
	{ Ls3,  -1, -1, -1 },
	{ Ls2,  -1, -1, -1 },
	{ Rf,   -1, -1, -1 },
	{ Rf3,  -1, -1, -1 },
	{ Rf2,  -1, -1, -1 },
	{ Rs,   -1, -1, -1 },
	{ Rs3,  -1, -1, -1 },
	{ Rs2,  -1, -1, -1 },
	{ Lf,   Ls, -1, -1 },
	{ Lf3, Ls3, -1, -1 },
	{ Lf2, Ls2, -1, -1 },
	{ Rf,   Rs, -1, -1 },
	{ Rf3, Rs3, -1, -1 },
	{ Rf2, Rs2, -1, -1 },
	{ Ls,  Rs3, -1, -1 },
	{ Ls3,  Rs, -1, -1 },
	{ Ls2, Rs2, -1, -1 },

	{ Ff,   -1, -1, -1 },
	{ Ff3,  -1, -1, -1 },
	{ Ff2,  -1, -1, -1 },
	{ Fs,   -1, -1, -1 },
	{ Fs3,  -1, -1, -1 },
	{ Fs2,  -1, -1, -1 },
	{ Bf,   -1, -1, -1 },
	{ Bf3,  -1, -1, -1 },
	{ Bf2,  -1, -1, -1 },
	{ Bs,   -1, -1, -1 },
	{ Bs3,  -1, -1, -1 },
	{ Bs2,  -1, -1, -1 },
	{ Ff,   Fs, -1, -1 },
	{ Ff3, Fs3, -1, -1 },
	{ Ff2, Fs2, -1, -1 },
	{ Bf,   Bs, -1, -1 },
	{ Bf3, Bs3, -1, -1 },
	{ Bf2, Bs2, -1, -1 },
	{ Fs,  Bs3, -1, -1 },
	{ Fs3,  Bs, -1, -1 },
	{ Fs2, Bs2, -1, -1 },

	{ Uf,   Us, Ds3, -1 },
	{ Uf3, Us3, Ds, -1 },
	{ Uf2, Us2, Ds2, -1 },
	{ Df,   Ds, Us3, -1 },
	{ Df3, Ds3, Us, -1 },
	{ Df2, Ds2, Us2, -1 },

	{ Lf,   Ls, Rs3, -1 },
	{ Lf3, Ls3, Rs, -1 },
	{ Lf2, Ls2, Rs2, -1 },
	{ Rf,   Rs, Ls3, -1 },
	{ Rf3, Rs3, Ls, -1 },
	{ Rf2, Rs2, Ls2, -1 },

	{ Ff,   Fs, Bs3, -1 },
	{ Ff3, Fs3, Bs, -1 },
	{ Ff2, Fs2, Bs2, -1 },
	{ Bf,   Bs, Fs3, -1 },
	{ Bf3, Bs3, Fs, -1 },

	{ Bf2, Bs2, Fs2, -1 }
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

const int N_STAGE2_SLICE_MOVES = 28;
int stage2_slice_moves[N_STAGE2_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2,
	Df, Df3, Df2, Ds, Ds3, Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
};

enum STAGE2_SLICE_LIST {
	stg2_Uf, stg2_Uf3, stg2_Uf2, stg2_Us, stg2_Us3, stg2_Us2,
	stg2_Df, stg2_Df3, stg2_Df2, stg2_Ds, stg2_Ds3, stg2_Ds2,
	stg2_Lf2, stg2_Ls, stg2_Ls3, stg2_Ls2, stg2_Rf2, stg2_Rs, stg2_Rs3, stg2_Rs2,
	stg2_Ff2, stg2_Fs, stg2_Fs3, stg2_Fs2, stg2_Bf2, stg2_Bs, stg2_Bs3, stg2_Bs2
};

const int N_STAGE2_TWIST_MOVES = 20;	//30???
int stage2_twist_moves[30 /* *0 + N_STAGE2_TWIST_MOVES */ ][4] = {
	{ stg2_Uf,        -1,       -1, -1 },
	{ stg2_Uf3,       -1,       -1, -1 },
	{ stg2_Uf2,       -1,       -1, -1 },
	{ stg2_Df,        -1,       -1, -1 },
	{ stg2_Df3,       -1,       -1, -1 },
	{ stg2_Df2,       -1,       -1, -1 },
	{ stg2_Uf,   stg2_Us,       -1, -1 },
	{ stg2_Uf3, stg2_Us3,       -1, -1 },
	{ stg2_Uf2, stg2_Us2,       -1, -1 },
	{ stg2_Df,   stg2_Ds,       -1, -1 },
	{ stg2_Df3, stg2_Ds3,       -1, -1 },
	{ stg2_Df2, stg2_Ds2,       -1, -1 },

	{ stg2_Lf2,       -1,       -1, -1 },
	{ stg2_Rf2,       -1,       -1, -1 },
	{ stg2_Lf2, stg2_Ls2,       -1, -1 },
	{ stg2_Rf2, stg2_Rs2,       -1, -1 },

	{ stg2_Ff2,       -1,       -1, -1 },
	{ stg2_Bf2,       -1,       -1, -1 },
	{ stg2_Ff2, stg2_Fs2,       -1, -1 },
	{ stg2_Bf2, stg2_Bs2,       -1, -1 },

	{ stg2_Uf,   stg2_Us, stg2_Ds3, -1 },
	{ stg2_Uf3, stg2_Us3,  stg2_Ds, -1 },
	{ stg2_Uf2, stg2_Us2, stg2_Ds2, -1 },
	{ stg2_Df,   stg2_Ds, stg2_Us3, -1 },
	{ stg2_Df3, stg2_Ds3,  stg2_Us, -1 },
	{ stg2_Df2, stg2_Ds2, stg2_Us2, -1 },

	{ stg2_Lf2, stg2_Ls2, stg2_Rs2, -1 },
	{ stg2_Rf2, stg2_Rs2, stg2_Ls2, -1 },

	{ stg2_Ff2, stg2_Fs2, stg2_Bs2, -1 },
	{ stg2_Bf2, stg2_Bs2, stg2_Fs2, -1 }
};

//Also allow 2-twist moves such as (Ff) F which is the same as F2 f.
const int N_STAGE2_2TWIST_MOVES = 16;	// number of slice moves that can not be done by two allowed twist moves
int stage2_2twist_moves[N_STAGE2_2TWIST_MOVES][3] = {
	{ stg2_Fs, -1, -1 },
	{ stg2_Fs3, -1, -1 },
	{ stg2_Bs, -1, -1 },
	{ stg2_Bs3, -1, -1 },
	{ stg2_Ff2, stg2_Fs, -1 },
	{ stg2_Ff2, stg2_Fs3, -1 },
	{ stg2_Bf2, stg2_Bs, -1 },
	{ stg2_Bf2, stg2_Bs3, -1 },
	{ stg2_Ls, -1, -1 },
	{ stg2_Ls3, -1, -1 },
	{ stg2_Rs, -1, -1 },
	{ stg2_Rs3, -1, -1 },
	{ stg2_Lf2, stg2_Ls, -1 },
	{ stg2_Lf2, stg2_Ls3, -1 },
	{ stg2_Rf2, stg2_Rs, -1 },
	{ stg2_Rf2, stg2_Rs3, -1 }
};

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

const int N_STAGE2_BLOCK_MOVES = 47;	//formerly 41
int stage2_block_moves[N_STAGE2_BLOCK_MOVES][4] = {
	{ stg2_Uf,        -1, -1, -1 },
	{ stg2_Uf3,       -1, -1, -1 },
	{ stg2_Uf2,       -1, -1, -1 },
	{ stg2_Us,        -1, -1, -1 },
	{ stg2_Us3,       -1, -1, -1 },
	{ stg2_Us2,       -1, -1, -1 },
	{ stg2_Df,        -1, -1, -1 },
	{ stg2_Df3,       -1, -1, -1 },
	{ stg2_Df2,       -1, -1, -1 },
	{ stg2_Ds,        -1, -1, -1 },
	{ stg2_Ds3,       -1, -1, -1 },
	{ stg2_Ds2,       -1, -1, -1 },
	{ stg2_Uf,   stg2_Us, -1, -1 },
	{ stg2_Uf3, stg2_Us3, -1, -1 },
	{ stg2_Uf2, stg2_Us2, -1, -1 },
	{ stg2_Df,   stg2_Ds, -1, -1 },
	{ stg2_Df3, stg2_Ds3, -1, -1 },
	{ stg2_Df2, stg2_Ds2, -1, -1 },
	{ stg2_Us,  stg2_Ds3, -1, -1 },
	{ stg2_Us3,  stg2_Ds, -1, -1 },
	{ stg2_Us2, stg2_Ds2, -1, -1 },

	{ stg2_Lf2,       -1, -1, -1 },
	{ stg2_Ls,        -1, -1, -1 },
	{ stg2_Ls3,       -1, -1, -1 },
	{ stg2_Ls2,       -1, -1, -1 },
	{ stg2_Rf2,       -1, -1, -1 },
	{ stg2_Rs,        -1, -1, -1 },
	{ stg2_Rs3,       -1, -1, -1 },
	{ stg2_Rs2,       -1, -1, -1 },
	{ stg2_Lf2, stg2_Ls2, -1, -1 },
	{ stg2_Rf2, stg2_Rs2, -1, -1 },
	{ stg2_Ls,  stg2_Rs3, -1, -1 },
	{ stg2_Ls3,  stg2_Rs, -1, -1 },
	{ stg2_Ls2, stg2_Rs2, -1, -1 },

	{ stg2_Ff2,       -1, -1, -1 },
	{ stg2_Fs,        -1, -1, -1 },
	{ stg2_Fs3,       -1, -1, -1 },
	{ stg2_Fs2,       -1, -1, -1 },
	{ stg2_Bf2,       -1, -1, -1 },
	{ stg2_Bs,        -1, -1, -1 },
	{ stg2_Bs3,       -1, -1, -1 },
	{ stg2_Bs2,       -1, -1, -1 },
	{ stg2_Ff2, stg2_Fs2, -1, -1 },
	{ stg2_Bf2, stg2_Bs2, -1, -1 },
	{ stg2_Fs,  stg2_Bs3, -1, -1 },
	{ stg2_Fs3,  stg2_Bs, -1, -1 },
	{ stg2_Fs2, stg2_Bs2, -1, -1 }
};

int stage2_block_map[N_STAGE2_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
	Lfs2, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg2[3] = {
	N_STAGE2_SLICE_MOVES, N_STAGE2_TWIST_MOVES, N_STAGE2_BLOCK_MOVES
};

const int N_STAGE3_SLICE_MOVES = 20;
int stage3_slice_moves[N_STAGE3_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
};

enum STAGE3_SLICE_LIST {
	stg3_Uf, stg3_Uf3, stg3_Uf2, stg3_Us2,
	stg3_Df, stg3_Df3, stg3_Df2, stg3_Ds2,
	stg3_Lf2, stg3_Ls2, stg3_Rf2, stg3_Rs2,
	stg3_Ff2, stg3_Fs, stg3_Fs3, stg3_Fs2,
	stg3_Bf2, stg3_Bs, stg3_Bs3, stg3_Bs2
};

bool stage3_move_parity[N_STAGE3_SLICE_MOVES] = {
	false, false, false, false,
	false, false, false, false,
	false, false, false, false,
	false, true, true, false, false, true, true, false
};

const int N_STAGE3_TWIST_MOVES = 16;
int stage3_twist_moves[N_STAGE3_TWIST_MOVES][4] = {
	{ stg3_Uf,        -1,       -1, -1 },
	{ stg3_Uf3,       -1,       -1, -1 },
	{ stg3_Uf2,       -1,       -1, -1 },
	{ stg3_Df,        -1,       -1, -1 },
	{ stg3_Df3,       -1,       -1, -1 },
	{ stg3_Df2,       -1,       -1, -1 },
	{ stg3_Uf2, stg3_Us2,       -1, -1 },
	{ stg3_Df2, stg3_Ds2,       -1, -1 },

	{ stg3_Lf2,       -1,       -1, -1 },
	{ stg3_Rf2,       -1,       -1, -1 },
	{ stg3_Lf2, stg3_Ls2,       -1, -1 },
	{ stg3_Rf2, stg3_Rs2,       -1, -1 },

	{ stg3_Ff2,       -1,       -1, -1 },
	{ stg3_Bf2,       -1,       -1, -1 },
	{ stg3_Ff2, stg3_Fs2,       -1, -1 },
	{ stg3_Bf2, stg3_Bs2,       -1, -1 }

};

const int N_STAGE3_2TWIST_MOVES_X = 4;	// number of slice moves that can not be done by two allowed twist moves
int stage3_2twist_moves_x[N_STAGE3_2TWIST_MOVES_X] = {
	stg3_Fs, stg3_Fs3, stg3_Bs, stg3_Bs3
};

const int N_STAGE3_2TWIST_MOVES = 8;	// number of slice or slice+half-turn-face moves that can not be done by two allowed twist moves
int stage3_2twist_moves[N_STAGE3_2TWIST_MOVES][2] = {
	{ stg3_Fs, -1 },
	{ stg3_Fs3, -1 },
	{ stg3_Bs, -1 },
	{ stg3_Bs3, -1 },
	{ stg3_Ff2, stg3_Fs },
	{ stg3_Ff2, stg3_Fs3 },
	{ stg3_Bf2, stg3_Bs },
	{ stg3_Bf2, stg3_Bs3 },
};

int stage3_twist_map1[N_STAGE3_TWIST_MOVES + N_STAGE3_2TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2,
	Ffs, Ffs3, Bfs, Bfs3, Ffs, Ffs3, Bfs, Bfs3
};

int stage3_twist_map2[N_STAGE3_TWIST_MOVES + N_STAGE3_2TWIST_MOVES] = {
	-1, -1, -1, -1, -1, -1, -1, -1,
	-1, -1, -1, -1, -1, -1, -1, -1,
	Ff3, Ff, Bf3, Bf, Ff, Ff3, Bf, Bf3
};

const int N_STAGE3_BLOCK_MOVES = 31;
int stage3_block_moves[N_STAGE3_BLOCK_MOVES][4] = {
	{ stg3_Uf,        -1, -1, -1 },
	{ stg3_Uf3,       -1, -1, -1 },
	{ stg3_Uf2,       -1, -1, -1 },
	{ stg3_Us2,       -1, -1, -1 },
	{ stg3_Df,        -1, -1, -1 },
	{ stg3_Df3,       -1, -1, -1 },
	{ stg3_Df2,       -1, -1, -1 },
	{ stg3_Ds2,       -1, -1, -1 },
	{ stg3_Uf2, stg3_Us2, -1, -1 },
	{ stg3_Df2, stg3_Ds2, -1, -1 },
	{ stg3_Us2, stg3_Ds2, -1, -1 },

	{ stg3_Lf2,       -1, -1, -1 },
	{ stg3_Ls2,       -1, -1, -1 },
	{ stg3_Rf2,       -1, -1, -1 },
	{ stg3_Rs2,       -1, -1, -1 },
	{ stg3_Lf2, stg3_Ls2, -1, -1 },
	{ stg3_Rf2, stg3_Rs2, -1, -1 },
	{ stg3_Ls2, stg3_Rs2, -1, -1 },

	{ stg3_Ff2,       -1, -1, -1 },
	{ stg3_Fs,        -1, -1, -1 },
	{ stg3_Fs3,       -1, -1, -1 },
	{ stg3_Fs2,       -1, -1, -1 },
	{ stg3_Bf2,       -1, -1, -1 },
	{ stg3_Bs,        -1, -1, -1 },
	{ stg3_Bs3,       -1, -1, -1 },
	{ stg3_Bs2,       -1, -1, -1 },
	{ stg3_Ff2, stg3_Fs2, -1, -1 },
	{ stg3_Bf2, stg3_Bs2, -1, -1 },
	{ stg3_Fs,  stg3_Bs3, -1, -1 },
	{ stg3_Fs3,  stg3_Bs, -1, -1 },
	{ stg3_Fs2, stg3_Bs2, -1, -1 }

};

int stage3_block_map[N_STAGE3_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
};

int n_moves_metric_stg3[3] = { N_STAGE3_SLICE_MOVES, N_STAGE3_TWIST_MOVES, N_STAGE3_BLOCK_MOVES};

const int N_STAGE4_SLICE_MOVES = 16;
int stage4_slice_moves[N_STAGE4_SLICE_MOVES] = {
	Uf, Uf3, Uf2, Us2,
	Df, Df3, Df2, Ds2,
	Lf2, Ls2, Rf2, Rs2,
	Ff2, Fs2, Bf2, Bs2
};

enum STAGE4_SLICE_LIST {
	stg4_Uf, stg4_Uf3, stg4_Uf2, stg4_Us2,
	stg4_Df, stg4_Df3, stg4_Df2, stg4_Ds2,
	stg4_Lf2, stg4_Ls2, stg4_Rf2, stg4_Rs2,
	stg4_Ff2, stg4_Fs2,	stg4_Bf2, stg4_Bs2
};

const int N_STAGE4_TWIST_MOVES = 16;
int stage4_twist_moves[N_STAGE3_TWIST_MOVES][4] = {
	{ stg4_Uf,        -1,       -1, -1 },
	{ stg4_Uf3,       -1,       -1, -1 },
	{ stg4_Uf2,       -1,       -1, -1 },
	{ stg4_Df,        -1,       -1, -1 },
	{ stg4_Df3,       -1,       -1, -1 },
	{ stg4_Df2,       -1,       -1, -1 },
	{ stg4_Uf2, stg4_Us2,       -1, -1 },
	{ stg4_Df2, stg4_Ds2,       -1, -1 },

	{ stg4_Lf2,       -1,       -1, -1 },
	{ stg4_Rf2,       -1,       -1, -1 },
	{ stg4_Lf2, stg4_Ls2,       -1, -1 },
	{ stg4_Rf2, stg4_Rs2,       -1, -1 },

	{ stg4_Ff2,       -1,       -1, -1 },
	{ stg4_Bf2,       -1,       -1, -1 },
	{ stg4_Ff2, stg4_Fs2,       -1, -1 },
	{ stg4_Bf2, stg4_Bs2,       -1, -1 }

};

int stage4_twist_map1[N_STAGE4_TWIST_MOVES] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2
};

const int N_STAGE4_BLOCK_MOVES = 25;
int stage4_block_moves[N_STAGE4_BLOCK_MOVES][4] = {
	{ stg4_Uf,        -1, -1, -1 },
	{ stg4_Uf3,       -1, -1, -1 },
	{ stg4_Uf2,       -1, -1, -1 },
	{ stg4_Us2,       -1, -1, -1 },
	{ stg4_Df,        -1, -1, -1 },
	{ stg4_Df3,       -1, -1, -1 },
	{ stg4_Df2,       -1, -1, -1 },
	{ stg4_Ds2,       -1, -1, -1 },
	{ stg4_Uf2, stg4_Us2, -1, -1 },
	{ stg4_Df2, stg4_Ds2, -1, -1 },
	{ stg4_Us2, stg4_Ds2, -1, -1 },

	{ stg4_Lf2,       -1, -1, -1 },
	{ stg4_Ls2,       -1, -1, -1 },
	{ stg4_Rf2,       -1, -1, -1 },
	{ stg4_Rs2,       -1, -1, -1 },
	{ stg4_Lf2, stg4_Ls2, -1, -1 },
	{ stg4_Rf2, stg4_Rs2, -1, -1 },
	{ stg4_Ls2, stg4_Rs2, -1, -1 },

	{ stg4_Ff2,       -1, -1, -1 },
	{ stg4_Fs2,       -1, -1, -1 },
	{ stg4_Bf2,       -1, -1, -1 },
	{ stg4_Bs2,       -1, -1, -1 },
	{ stg4_Ff2, stg4_Fs2, -1, -1 },
	{ stg4_Bf2, stg4_Bs2, -1, -1 },
	{ stg4_Fs2, stg4_Bs2, -1, -1 }

};

int stage4_block_map[N_STAGE4_BLOCK_MOVES] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs2, Bf2, Bs2,	Ffs2, Bfs2, Fs2Bs2
};

int n_moves_metric_stg4[3] = { N_STAGE4_SLICE_MOVES, N_STAGE4_TWIST_MOVES, N_STAGE4_BLOCK_MOVES };

const char* metric_names [3] = { "stm", "ttm", "btm" };
const char* metric_long_names[3] = { "slice", "twist", "block" };
static int sqs_perm_to_rep[24] = {
	0, 1, 2, 3, 4, 5,
	1, 0, 4, 5, 2, 3,
	3, 2, 5, 4, 0, 1,
	5, 4, 3, 2, 1, 0
};

static int sqs_rep_to_perm[6][4] = {
	{  0,  7, 16, 23 },
	{  1,  6, 17, 22 },
	{  2, 10, 13, 21 },
	{  3, 11, 12, 20 },
	{  4,  8, 15, 19 },
	{  5,  9, 14, 18 }
};

//map a "squares" move code to one of six "canonical" move codes,
//or -1 for moves that don't affect the corresponding pieces.
int squares_map[7][N_SQMOVES] = {
	{  0, -1,  1, -1, -1,  2, -1,  3,  4, -1,  5, -1 },		//LR edges
	{  4, -1,  5, -1,  0, -1,  1, -1, -1,  2, -1,  3 },		//FB edges
	{ -1,  2, -1,  3,  4, -1,  5, -1,  0, -1,  1, -1 },		//UD edges
	{  0, -1,  1, -1,  2, -1,  3, -1,  4, -1,  5, -1 },		//corners
	{  0, -1,  1, -1, -1,  2, -1,  3, -1,  4, -1,  5 },		//UD centers
	{ -1,  4, -1,  5,  0, -1,  1, -1, -1,  2, -1,  3 },		//LR centers
	{ -1,  2, -1,  3, -1,  4, -1,  5,  0, -1,  1, -1 }		//FB centers
};

UBYTE squares_cen_map[12] = { 0x0F, 0x33, 0x3C, 0x55, 0x5A, 0x66, 0x99, 0xA5, 0xAA, 0xC3, 0xCC, 0xF0 };

int rotateEDGE_fidx[18*3] = {
	 0,  1,  0,  6,  7,  6, 12, 13, 12, 18, 19, 18, 24, 25, 24, 30, 31, 30,
	36, 37, 36, 42, 43, 42, 48, 49, 48, 54, 55, 54, 60, 61, 60, 66, 67, 66,
	72, 73, 72, 78, 79, 78, 84, 85, 84, 90, 91, 90, 96, 97, 96,102,103,102
};
int rotateEDGE_tidx[18*3] = {
	 1,  0,  2,  7,  6,  8, 13, 12, 14, 19, 18, 20, 25, 24, 26, 31, 30, 32,
    37, 36, 38, 43, 42, 44, 49, 48, 50, 55, 54, 56, 61, 60, 62, 67, 66, 68,
	73, 72, 74, 79, 78, 80, 85, 84, 86, 91, 90, 92, 97, 96, 98,103,102,104
};

int rotateEDGE_ft[18*6] = {
	 0, 12,  1, 14,  0, 12, //up face, set 1
     4,  8,  5, 10,  4,  8, //up face, set 2
	16, 22, 19, 21, 16, 22, //up slice

	 2, 15,  3, 13,  2, 15, //down face, set 1
     6, 11,  7,  9,  6, 11, //down face, set 2
    17, 23, 18, 20, 17, 23, //down slice

	 8, 20,  9, 22,  8, 20, //left face, set 1
	12, 16, 13, 18, 12, 16, //left face, set 2
	 0,  6,  3,  5,  0,  6, //left slice

	10, 23, 11, 21, 10, 23, //right face, set 1
	14, 19, 15, 17, 14, 19, //right face, set 2
	 1,  7,  2,  4,  1,  7, //right slice

	 0, 21,  2, 20,  0, 21, //front face, set 1
	 4, 17,  6, 16,  4, 17, //front face, set 2
	 8, 14, 11, 13,  8, 14, //front slice

	 1, 22,  3, 23,  1, 22, //back face, set 1
	 5, 18,  7, 19,  5, 18, //back face, set 2
	 9, 15, 10, 12,  9, 15  //back slice
};

int rotateCOR_ft[6*6] = {
	 0,  3,  2,  1,  0,  3,	//U face
	 4,  5,  6,  7,  4,  5,	//D face
     3,  0,  4,  7,  3,  0,	//L face
     1,  2,  6,  5,  1,  2,	//R face
     0,  1,  5,  4,  0,  1,	//F face
     2,  3,  7,  6,  2,  3	//B face
};

int rotateCOR_ori[4] = { 1, 2, 1, 2 };
int rotateCOR_fidx[18] = {  0,  2,  0,  6,  8,  6, 12, 14, 12, 18, 20, 18, 24, 26, 24, 30, 32, 30 };
int rotateCOR_tidx[18] = {  1,  1,  2,  7,  7,  8, 13, 13, 14, 19, 19, 20, 25, 25, 26, 31, 31, 32 };

int reorientoc_vCOR[8] = { 2, 1, 2, 1, 1, 2, 1, 2 };

#define rotateCEN_fidx	rotateEDGE_fidx
#define rotateCEN_tidx	rotateEDGE_tidx

int rotateCEN_ft[18*6] = {
	 0,  3,  1,  2,  0,  3, //up face
    16, 10, 21, 14, 16, 10, //up slice, set1
	19,  8, 22, 12, 19,  8, //up slice, set2

	 4,  7,  5,  6,  4,  7, //down face
    18, 13, 23,  9, 18, 13, //down slice, set 1
    17, 15, 20, 11, 17, 15, //down slice, set 2

	 8, 11,  9, 10,  8, 11, //left face
	16,  6, 20,  3, 16,  6, //left slice, set 1
	18,  5, 22,  0, 18,  5, //left slice, set 2

	12, 15, 13, 14, 12, 15, //right face
	19,  1, 23,  4, 19,  1, //right slice, set 1
	17,  2, 21,  7, 17,  2, //right slice, set 2

	16, 19, 17, 18, 16, 19, //front face
	 0, 14,  4, 11,  0, 14, //front slice, set 1
	 2, 13,  6,  8,  2, 13, //front slice, set 2

	20, 23, 21, 22, 20, 23, //back face
	 1, 10,  5, 15,  1, 10, //back slice, set 1
	 3,  9,  7, 12,  3,  9  //back slice, set 2
};

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

inline int
get_dist_4bit (UINT x, const UBYTE* p)
{
	UINT x2 = x >> 1;
	UINT j = x & 0x1;
	if (j == 0) {
		return p[x2] & 0xF;
	}
	return (p[x2] >> 4) & 0xF;
}

inline UINT
perm_n_pack (UINT n, const Face* array_in)
{
	UINT idx;
	UINT i, j;

	idx = 0;

	for (i = 0; i < n; ++i) {
		idx *= (n - i);

		for (j = i + 1; j < n; ++j) {
			if (array_in[j] < array_in[i]) {
				++idx;
			}
		}
	}
	return idx;
}

inline void
perm_n_unpack (UINT n, UINT idx, Face* array_out)
{
	int i, j;
	int nn = static_cast<int>(n);

	for (i = nn - 1; i >= 0; --i) {
		array_out[i] = idx % (nn - i);
		idx /= (nn - i);

		for (j = i + 1; j < nn; ++j) {
			if (array_out[j] >= array_out[i]) {
				array_out[j]++;
			}
		}
	}
}

UBYTE prune_table_cor1[(N_CORNER_ORIENT+1)/2];
UBYTE prune_table_edg1[(N_EDGE_COMBO8+1)/2];

UBYTE prune_table_cen2[N_STAGE2_CENTER_CONFIGS/2];
UBYTE prune_table_edg2[N_STAGE2_EDGE_CONFIGS/2];
UBYTE prune_table_edgcen2[N_CENTER_COMBO4*N_STAGE2_EDGE_CONFIGS/2];

UBYTE prune_table_cen3[N_STAGE3_CENTER_CONFIGS/2];
UBYTE prune_table_edg3[N_STAGE3_EDGE_CONFIGS*N_STAGE3_EDGE_PAR/2];

UBYTE prune_table_cencor4[N_STAGE4_CORNER_CONFIGS*N_STAGE4_CENTER_CONFIGS/2];
UBYTE prune_table_edg4[N_STAGE4_EDGE_CONFIGS/2];
UBYTE prune_table_edgcen4[N_STAGE4_EDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS/2];

UBYTE prune_table_cencor5[N_SQS_CENTER_PERM*N_SQS_CORNER_PERM/2];
UBYTE prune_table_edg5[N_SQS_EDGE_PERM/2];
UBYTE prune_table_edgcor5[N_SQS_EDGE_PERM*N_SQS_CORNER_PERM/2];

const UINT STAGE3_NUM_SOLVED_CENTER_CONFIGS = 12;
UINT stage3_solved_centers[STAGE3_NUM_SOLVED_CENTER_CONFIGS] = {
	900830, 900844,	900850, 900853, 900857, 900858,
	900871, 900872, 900876,	900879, 900885, 900899
};

const UINT STAGE4_NUM_SOLVED_CENTER_CONFIGS = 12;
USHORT stage4_solved_centers_bm[STAGE4_NUM_SOLVED_CENTER_CONFIGS] = {
	0x0F, 0xF0, 0x55, 0xAA, 0x5A, 0xA5, 0x69, 0x96, 0x66, 0x99, 0x3C, 0xC3
};

#endif
