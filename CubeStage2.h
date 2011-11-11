#ifndef CUBESTAGE2_H
#define CUBESTAGE2_H

#pragma once

#include "Constants.h"

struct CubeStage2 {
	UINT m_centerFB;
	USHORT m_edge;	//edge coordinate
	void init ();
	bool is_solved () const;
	void do_move_slow (int move_code);
	void do_move (int move_code);
	void do_whole_cube_move (int whole_cube_move);
};

#endif
