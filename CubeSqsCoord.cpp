#include "CubeSqsCoord.h"
#include "Tables.h"

void
CubeSqsCoord::init ()
{
	m_cen12x12x12 = 0;
	m_cp96 = 0;
	m_ep96x96x96 = 0;
}

void
CubeSqsCoord::do_move (int sqs_move_code)
{
	UINT cen = m_cen12x12x12;
	UINT cp = m_cp96;
	UINT ep = m_ep96x96x96;
	UINT ep0 = ep%96;
	UINT ep1 = (ep/96) % 96;
	UINT ep2 = ep/(96*96);
	m_ep96x96x96 = squares_move_edges (ep0, sqs_move_code, 0) +
		96*squares_move_edges (ep1, sqs_move_code, 1) +
		96*96*squares_move_edges (ep2, sqs_move_code, 2);
	m_cp96 = squares_move_corners (cp, sqs_move_code);
	UINT cen0 = cen % 12;
	UINT cen1 = (cen/12) % 12;
	UINT cen2 = cen/(12*12); 
	m_cen12x12x12 = squares_move_centers (cen0, sqs_move_code, 0) +
		12*squares_move_centers (cen1, sqs_move_code, 1) +
		12*12*squares_move_centers (cen2, sqs_move_code, 2);
}

void
CubeSqsCoord::do_whole_cube_move (int sqs_whole_cube_move)
{
	switch (sqs_whole_cube_move) {
	case 1:
		do_move (Uf2/3);
		do_move (Us2/3);
		do_move (Df2/3);
		do_move (Ds2/3);
		break;
	case 2:
		do_move (Ff2/3);
		do_move (Fs2/3);
		do_move (Bf2/3);
		do_move (Bs2/3);
		break;
	case 3:
		do_move (Lf2/3);
		do_move (Ls2/3);
		do_move (Rf2/3);
		do_move (Rs2/3);
		break;
	default: //case 0
		break;
	}
}

bool
CubeSqsCoord::is_solved () const
{
	if (m_cen12x12x12 == 0 && m_cp96 == 0 && m_ep96x96x96 == 0) {
		return true;
	}
	if (m_cen12x12x12 == 1716 && m_cp96 == 29 && m_ep96x96x96 == 881885) {
		return true;
	}
	if (m_cen12x12x12 == 143 && m_cp96 == 66 && m_ep96x96x96 == 276450) {
		return true;
	}
	if (m_cen12x12x12 == 1595 && m_cp96 == 95 && m_ep96x96x96 == 611135) {
		return true;
	}
	return false;
}
