#ifndef TABLES_H
#define TABLES_H

#pragma once

#include "Constants.h"
#include "CubeState.h"
#include "CubeConverter.h"
#include "CubeSqsCoord.h"
#include "CubeStage1.h"
#include "CubeStage2.h"
#include "CubeStage3.h"
#include "CubeStage4.h"

// init_4of8
void init_4of8 ();
UBYTE bm4of8_to_70[256];
UBYTE bm4of8[70];

// init_parity_table
void init_parity_table ();
bool parity_perm8_table[40320];
int get_parity8 (UINT x);

// init_eloc
void init_eloc ();
int countbits (UINT x);
UINT ebm2eloc[4096*4096];
UINT eloc2ebm[N_EDGE_COMBO8];
Face map96[96][8];
UINT bm12_4of8_to_high_idx[4096][70];
UINT bm12_4of8_to_low_idx[4096][70];
UBYTE bitcount8[256];
UBYTE gen_MofN8[256][256];

// init_cloc
void init_cloc ();
UINT c4_to_cloc[24*24*24*24];
UINT cloc_to_bm[N_CENTER_COMBO4];

// init_perm_to_420
USHORT perm_to_420[40320];

// init_stage1
void init_move_tablesSTAGE1 ();

UINT move_table_edgeSTAGE1[N_EDGE_COMBO8][N_BASIC_MOVES];	// > 100MB !
UINT move_table_co[N_CORNER_ORIENT][N_FACE_MOVES];

// init_stage2
void init_stage2 ();
USHORT move_table_cenSTAGE2[N_CENTER_COMBO4][N_STAGE2_SLICE_MOVES];
USHORT move_table_edgeSTAGE2[420][N_STAGE2_SLICE_MOVES];

void stage2_cen_to_cloc4s (UINT cen, UINT *pclocf, UINT* pclocb);

// init_stage3
void init_stage3 ();
UINT e16bm2eloc[256*256];
UINT eloc2e16bm[N_COMBO_16_8];

void init_move_tablesSTAGE3 ();
UINT move_table_cenSTAGE3[N_STAGE3_CENTER_CONFIGS][N_STAGE3_SLICE_MOVES];	//72MB
USHORT move_table_edgeSTAGE3[N_STAGE3_EDGE_CONFIGS][N_STAGE3_SLICE_MOVES];

// init_stage4
void array8_to_set_a (const Face* t, CubeState* result_cube);
void set_a_to_array8 (const CubeState& init_cube, Face* t);
void array8_to_set_b (const Face* t, CubeState* result_cube);
void set_b_to_array8 (const CubeState& init_cube, Face* t);
void lrfb_to_cube_state (UINT u, CubeState* result_cube);
UINT cube_state_to_lrfb (const CubeState& init_cube);
UINT lrfb_get_edge_rep (UINT u);

void init_stage4_edge_tables ();
void lrfb_check ();
void stage4_edge_table_init ();
bool stage4_edge_table_lookup (UINT val, UINT* hash_loc);
void add_to_stage4_edge_table (UINT val, UINT idx);
void init_move_tablesSTAGE4 ();

UINT move_table_cenSTAGE4[N_STAGE4_CENTER_CONFIGS][N_STAGE4_SLICE_MOVES];
USHORT move_table_cornerSTAGE4[N_STAGE4_CORNER_CONFIGS][N_STAGE4_SLICE_MOVES];
USHORT stage4_edge_hB[40320];
USHORT stage4_edge_hgB[40320];
USHORT stage4_edge_hgA[40320][36];
UINT stage4_edge_hash_table_val[N_STAGE4_EDGE_HASH_TABLE];
UINT stage4_edge_hash_table_idx[N_STAGE4_EDGE_HASH_TABLE];
UINT stage4_edge_rep_table[N_STAGE4_EDGE_CONFIGS];
USHORT move_table_AedgeSTAGE4[40320][N_STAGE4_SLICE_MOVES];
USHORT move_table_BedgeSTAGE4[40320][N_STAGE4_SLICE_MOVES];

// init_stage5
void init_squares ();
int squares_2nd_perm[24][4];
UBYTE squares_movemap[96][6];
UBYTE squares_cen_revmap[256];
UBYTE squares_cen_movemap[12][6];
UINT swapbits (UINT x, UINT b);
void rotate_sliceEDGE (int move_code, const CubeState& init_cube, CubeState* result_cube);

UINT squares_move (UINT pos96, int move_code6);
UINT squares_move_corners (UINT pos96, int sqs_move_code);
UINT squares_move_edges (UINT pos96, int sqs_move_code, int edge_group);
UINT squares_move_centers (UINT pos96, int sqs_move_code, int cen_group);

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


void rotate_sliceCENTER (int move_code, const CubeState& init_cube, CubeState* result_cube);

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


#endif
