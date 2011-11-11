#ifndef CUBESTAGE1_H
#define CUBESTAGE1_H

#pragma once

#include "Constants.h"

struct CubeStage1 {
	USHORT m_co;	//corner orientation
	UINT m_edge_ud_combo8;
	void init ();
	void do_move (int move_code);
	void do_whole_cube_move (int whole_cube_move);
	bool is_solved () const;
};

#endif
