#include "CubeStage3.h"
#include "Tables.h"
#include "CubeConverter.h"

void
CubeStage3::init ()
{
	m_edge = 494;
	m_centerLR = 900830;
	m_edge_odd = false;	
	m_distance = 255;
}

void
CubeStage3::do_move_slow (int move_code)
{
	CubeState cube1;
	bool par = m_edge_odd;
	if (stage3_move_parity[move_code]) {
		par = ! par;
	}
	convert_stage3_to_std_cube (*this, &cube1);
	cube1.do_move (stage3_slice_moves[move_code]);
	convert_std_cube_to_stage3 (cube1, this);
	m_edge_odd = par;
}

void
CubeStage3::do_move (int move_code)
{
	m_centerLR = move_table_cenSTAGE3[m_centerLR][move_code];
	m_edge = move_table_edgeSTAGE3[m_edge][move_code];
	if (stage3_move_parity[move_code]) {
		m_edge_odd = ! m_edge_odd;
	}
}

bool
CubeStage3::is_solved () const
{
	int i;

	if (m_edge_odd) {
		return false;	//not solved if odd edge parity
	}
	if (m_edge != 494) {
		return false;	//not solved if wrong edge value
	}
	bool found = false;
	for (i = 0; i < STAGE3_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		if (m_centerLR == stage3_solved_centers[i]) {
			found = true;
			break;
		}
	}
	return found;	//If we found a matching center value, then it is solved.
}
