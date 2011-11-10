#include "CubePruningTable.h"

CubePruningTable::CubePruningTable (UINT num_positions, UBYTE* ptable, void* move_func, int stage, int metric, UINT cencoredg) :
	m_num_positions (num_positions),
	m_ptable (ptable),
	m_do_move_func (move_func),
	m_num_moves (0),
	m_move_list (NULL),
	m_num_moves2 (0),
	m_move_list2 (NULL),
	m_num_solved (0),
	m_psolved (NULL),
	m_stage (stage),
	m_metric (metric),
	m_cencoredg (cencoredg),
	m_count (0)
{
}

CubePruningTable::~CubePruningTable ()
{
	if (m_move_list != NULL) {
		delete [] m_move_list;
	}
	if (m_move_list2 != NULL) {
		delete [] m_move_list2;
	}
}

void
CubePruningTable::init_move_list (int dim2, int num_moves, int* move_list)
{
	int i;
	m_num_moves = num_moves;
	m_move_list = new int[3*m_num_moves];
	switch (dim2) {
	case 0:
		for (i = 0; i < m_num_moves; ++i) {
			m_move_list[3*i] = i;
			m_move_list[3*i+1] = -1;
			m_move_list[3*i+2] = -1;
		}
		break;
	case 1:
		for (i = 0; i < m_num_moves; ++i) {
			m_move_list[3*i] = move_list[i];
			m_move_list[3*i+1] = -1;
			m_move_list[3*i+2] = -1;
		}
		break;
	case 2:
		{
			int* p = move_list;
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = *p++;
				m_move_list[3*i+1] = *p++;
				m_move_list[3*i+2] = -1;
			}
		}
		break;
	case 3:
		{
			int* p = move_list;
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = *p++;
				m_move_list[3*i+1] = *p++;
				m_move_list[3*i+2] = *p++;
			}
		}
		break;
	default:
		printf ("CubePruningTable::init_move_list call ignored\n");
	}
}

void
CubePruningTable::init_move_list2 (int dim2, int num_moves, int* move_list)
{
	int i;
	m_num_moves2 = num_moves;
	m_move_list2 = new int[3*m_num_moves];
	switch (dim2) {
	case 0:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = i;
			m_move_list2[3*i+1] = -1;
			m_move_list2[3*i+2] = -1;
		}
		break;
	case 1:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = move_list[i];
			m_move_list2[3*i+1] = -1;
			m_move_list2[3*i+2] = -1;
		}
		break;
	case 2:
		for (i = 0; i < m_num_moves2; ++i) {
			m_move_list2[3*i] = move_list[2*i];
			m_move_list2[3*i+1] = move_list[2*i + 1];
			m_move_list2[3*i+2] = -1;
		}
		break;
	default:
		printf ("CubePruningTable::init_move_list2 call ignored\n");
	}
}

void
CubePruningTable::init_solved_list (int count, int* solved_list)
{
	m_num_solved = count;
	m_psolved = solved_list;	//Assume points to statically allocated array
}

void
CubePruningTable::init ()
{
	UINT i;
	UINT n = m_num_positions/2 + (m_num_positions & 0x1);
	m_count = 0;
	for (i = 0; i < n; ++i) {
		m_ptable[i] = 0xFF;
	}
}

void
CubePruningTable::analyze ()
{
	int i;
	UINT idx;
	int dist;
	int max_dist = 14;	//MAX_DISTANCE;
	init ();
	for (i = 0; i < m_num_solved; ++i) {
		add_to_table (m_psolved[i], 0);
	}
	UINT new_count = m_count;
	for (dist = 1; dist <= max_dist && new_count > 0; ++dist) {
		UINT old_count = m_count;
		for (idx = 0; idx < m_num_positions; ++idx) {
			//UINT idx2 = idx/2;
			//UINT j = idx & 0x1;
			int dx = get_dist_4bit (idx, m_ptable);
			if (m_num_moves2 > 0 && dist >= 2 && dx == dist - 2) {
				generate2 (idx, dist);
			}
			if (dx == dist - 1) {
				generate1 (idx, dist);
			}
		}
		new_count = m_count - old_count;
		//special case: distance 1 could have 0 positions when there are "moves" that count as 2 moves.
		if (new_count == 0 && m_num_moves2 > 0 && dist == 1) {
			new_count = 1;	//fake new count to prevent exiting loop prematurely.
		}
	}
}

void
CubePruningTable::generate1 (UINT idx, int dist)
{
	int i, j;

	for (i = 0; i < m_num_moves; ++i) {
		UINT idx2 = callfunc (m_do_move_func, idx, m_move_list[3*i]);
		for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
			idx2 = callfunc (m_do_move_func, idx2, m_move_list[3*i + j]);
		}
		add_to_table (idx2, dist);
	}
}

void
CubePruningTable::generate2 (UINT idx, int dist)
{
	int i, j;

	for (i = 0; i < m_num_moves2; ++i) {
		UINT idx2 = callfunc (m_do_move_func, idx, m_move_list2[3*i]);
		for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
			idx2 = callfunc (m_do_move_func, idx2, m_move_list2[3*i + j]);
		}
		add_to_table (idx2, dist);
	}
}

void
CubePruningTable::add_to_table (UINT idx, int dist)
{
	if (get_dist_4bit (idx, m_ptable) == 0xF) {
		set_dist_4bit (idx, dist, m_ptable);
		++m_count;
	}
}

UINT
callfunc (void* pfunc, UINT idx, int move_code)
{
	FUNC_PTR pFunc = reinterpret_cast<FUNC_PTR>(pfunc);
	return pFunc (idx, move_code);
}
