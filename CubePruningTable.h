#ifndef CUBEPRUNINGTABLE_H
#define CUBEPRUNINGTABLE_H

#pragma once

#include "Constants.h"
#include "Tables.h"

class CubePruningTable {
private:
	UINT m_num_positions;
	UBYTE* m_ptable;
	void* m_do_move_func;
	int m_num_moves;
	int* m_move_list;
	int m_num_moves2;
	int* m_move_list2;
	int m_num_solved;
	int* m_psolved;
	int m_stage;
	int m_metric;
	UINT m_cencoredg;
	UINT m_count;
public:
	CubePruningTable (UINT num_positions, UBYTE* ptable, void* move_func, int stage, int metric, UINT cencoredg);
	~CubePruningTable ();
	void init_move_list (int dim2, int num_moves, int* move_list);
	void init_move_list2 (int dim2, int num_moves, int* move_list);
	void init_solved_list (int count, int* solved_list);
	void init ();
	void analyze ();
	void generate1 (UINT idx, int dist);
	void generate2 (UINT idx, int dist);
	void add_to_table (UINT idx, int dist);
};

UINT callfunc (void* pfunc, UINT idx, int move_code);

inline void
set_dist_4bit (UINT x, UINT dist, UBYTE* p)
{
	UINT x2 = x >> 1;
	UINT j = x & 0x1;
	if (j == 0) {
		p[x2] &= 0xF0;
		p[x2] |= dist & 0xF;
		return;
	}
	p[x2] &= 0x0F;
	p[x2] |= (dist & 0xF) << 4;
}

#endif
