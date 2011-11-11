#include "CubeState.h"
#include "Tables.h"

void
CubeState::init ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		m_edge[i] = i;
	}
	for (i = 0; i < 8; ++i) {
		m_cor[i] = i;
	}
	for (i = 0; i < 24; ++i) {
		m_cen[i] = i/4;
	}
}

void
CubeState::do_move (int move_code)
{
	CubeState result_cube = *this;	//fast copy to initialize result cube
	rotate_sliceEDGE (move_code, *this, &result_cube);
	rotate_sliceCORNER (move_code, *this, &result_cube);
	rotate_sliceCENTER (move_code, *this, &result_cube);
	*this = result_cube;
}

void

CubeState::compose_edge (const CubeState& cs1, const CubeState& cs2)
{
	int i;
	*this = cs1;
	for (i = 0; i < 24; ++i) {
		int j = cs2.m_edge[i];
		if (j >= 24) {
			printf ("compose_edge: CubeState error\n");
			exit (1);
		}
		m_edge[i] = cs1.m_edge[j];
	}
}

void
CubeState::invert_fbcen ()
{
	int i;
	for (i = 0; i < 24; ++i) {
		if (m_cen[i] >= 4) {
			m_cen[i] ^= 1;
		}
	}
}

bool
CubeState::edgeUD_parity_odd () const
{
	int i, j;
	int parity = 0;
	Face t[16];

	for (i = 0; i < 16; ++i) {
		t[i] = m_edge[i];
	}
	for (i = 0; i < 15; ++i) {
		if (t[i] == i) {
			continue;
		}
		for (j = i + 1; j < 16; ++j) {
			if (t[j] == i) {
				//"swap" the i & j elements, but don't bother updating the "i"-element
				//as it isn't needed anymore.
				t[j] = t[i];
			}
		}
		parity ^= 1;
	}
	return parity != 0;
}

