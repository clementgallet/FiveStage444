#ifndef CUBESTAGE3_H
#define CUBESTAGE3_H

#pragma once

#include "Constants.h"

struct CubeStage3 {
	UINT m_centerLR;
	USHORT m_edge;	//edge coordinate
	bool m_edge_odd;	//odd parity of edges?
	void init ();
	void do_move_slow (int move_code);
	void do_move (int move_code);
	bool is_solved () const;
};


bool stage3_move_parity[N_STAGE3_SLICE_MOVES] = {
	false, false, false, false,
	false, false, false, false,
	false, false, false, false,
	false, true, true, false, false, true, true, false
};

#endif
