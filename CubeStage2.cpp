#include "CubeStage2.h"
#include "Tables.h"
#include "CubeConverter.h"

void
CubeStage2::init ()
{
	m_edge = 0;
	m_centerFB = N_STAGE2_CENTER_CONFIGS - 70;
	m_distance = 255;
}

bool
CubeStage2::is_solved () const
{
	if (m_edge == 0) {
		if (m_centerFB < 51482900 || m_centerFB > 51482969) {
			return false;
		}
		if (m_centerFB == 51482900 || m_centerFB == 51482914 || m_centerFB == 51482920 ||
			m_centerFB == 51482923 || m_centerFB == 51482927 || m_centerFB == 51482928 || 
			m_centerFB == 51482941 || m_centerFB == 51482942 || m_centerFB == 51482946 ||
			m_centerFB == 51482949 || m_centerFB == 51482955 ||  m_centerFB == 51482969)
		{
			return true;
		}
	} else {
		if (m_edge == 414) {
			if (m_centerFB < 50582070 || m_centerFB > 50582139) {
				return false;
			}
			if (m_centerFB == 50582070 || m_centerFB == 50582084 || m_centerFB == 50582090 ||
				m_centerFB == 50582093 || m_centerFB == 50582097 || m_centerFB == 50582098 ||
				m_centerFB == 50582111 || m_centerFB == 50582112 || m_centerFB == 50582116 ||
				m_centerFB == 50582119 || m_centerFB == 50582125 || m_centerFB == 50582139)
			{
				return true;
			}
		}
	}
	return false;
}

void
CubeStage2::do_move_slow (int move_code)
{
	CubeState cube1;
	convert_stage2_to_std_cube (*this, &cube1);
	cube1.do_move (stage2_slice_moves[move_code]);
	convert_std_cube_to_stage2 (cube1, this);
}

void
CubeStage2::do_move (int move_code)
{
	int i;
	CubeStage2 cube2 = *this;
	Face t1[4];
	Face t2[4];
	UINT cenbm = eloc2ebm[m_centerFB / 70];
	UINT cenbm4of8 = bm4of8[m_centerFB % 70];
	int j1 = 0;
	int j2 = 0;
	for (i = 0; cenbm != 0; ++i) {
		if ((cenbm & 0x1) != 0) {
			if ((cenbm4of8 & 0x1) == 0) {
				t2[j2++] = i;
			} else {
				t1[j1++] = i;
			}
			cenbm4of8 >>= 1;
		}
		cenbm >>= 1;
	}
	int idx1 = 24*24*24*t1[0] + 24*24*t1[1] + 24*t1[2] + t1[3];
	int idx2 = 24*24*24*t2[0] + 24*24*t2[1] + 24*t2[2] + t2[3];

	UINT cloc1 = c4_to_cloc[idx1];
	UINT cloc2 = c4_to_cloc[idx2];
	UINT cloc1b = move_table_cenSTAGE2[cloc1][move_code];
	UINT cloc2b = move_table_cenSTAGE2[cloc2][move_code];
	UINT cbm1b = cloc_to_bm[cloc1b];
	UINT cbm2b = cloc_to_bm[cloc2b];
	UINT cenbm2 = cbm1b | cbm2b;
	j1 = 0;
	UINT bm4of8b = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm2 & (1 << i)) != 0) {
			if ((cbm1b & (1 << i)) != 0) {
				bm4of8b |= (1 << j1);
			}
			++j1;
		}
	}

	UINT cen1 = bm4of8_to_70[bm4of8b];
	UINT cen2 = ebm2eloc[cenbm2];
	m_centerFB = 70*cen2 + cen1;
	m_edge = move_table_edgeSTAGE2[m_edge][move_code];
}

void
CubeStage2::do_whole_cube_move (int whole_cube_move)
{
	switch (whole_cube_move) {
	case 1:	//U u d' D'
		do_move (0);
		do_move (3);
		do_move (7);
		do_move (10);
		break;
	case 2:	//F2 f2 b2 B2
		do_move (16);
		do_move (19);
		do_move (20);
		do_move (23);
		break;
	case 3:	//L2 l2 r2 R2
		do_move (12);
		do_move (13);
		do_move (14);
		do_move (15);
		break;
	default: //case 0
		break;
	}
}
