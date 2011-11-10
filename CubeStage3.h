#ifndef CUBESTAGE3_H
#define CUBESTAGE3_H

#pragma once

#include "Constants.h"

struct CubeStage3 {
	UINT m_centerLR;
	USHORT m_edge;	//edge coordinate
	bool m_edge_odd;	//odd parity of edges?
	UBYTE m_distance;
	void init ();
	void do_move_slow (int move_code);
	void do_move (int move_code);
	bool is_solved () const;
};

#endif
