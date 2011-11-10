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
void rotate_sliceCORNER (int move_code, const CubeState& init_cube, CubeState* result_cube);
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

void rotate_sliceCENTER (int move_code, const CubeState& init_cube, CubeState* result_cube);

#endif
