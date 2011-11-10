#include "CubeStage1.h"
#include "Tables.h"

void
CubeStage1::init ()
{
	m_co = 0;
	m_edge_ud_combo8 = N_EDGE_COMBO8 - 1;
	m_distance = 255;
}

void
CubeStage1::do_move (int move_code)
{

	m_edge_ud_combo8 = move_table_edgeSTAGE1[m_edge_ud_combo8][move_code];
	int fmc = basic_to_face[move_code];
	if (fmc >= 0) {
		m_co = move_table_co[m_co][fmc];
	}
}

void
CubeStage1::do_whole_cube_move (int whole_cube_move)
{
	switch (whole_cube_move) {
	case 1:
		do_move (Uf);
		do_move (Us);
		do_move (Df3);
		do_move (Ds3);
		break;
	case 2:
		do_move (Ff);
		do_move (Fs);
		do_move (Bf3);
		do_move (Bs3);
		break;
	case 3:
		do_move (Lf);
		do_move (Ls);
		do_move (Rf3);
		do_move (Rs3);
		break;
	default: //case 0
		break;
	}
}

bool
CubeStage1::is_solved () const
{
	if (m_co == 0 && m_edge_ud_combo8 == 735470) {
		return true;
	}
	if (m_co == 1373 && m_edge_ud_combo8 == 722601) {
		return true;
	}
	if (m_co == 1906 && m_edge_ud_combo8 == 0) {
		return true;
	}
	return false;
}
