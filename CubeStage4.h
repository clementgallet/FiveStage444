#ifndef CUBESTAGE4_H
#define CUBESTAGE4_H

#pragma once

#include "Constants.h"

struct CubeStage4 {
	USHORT m_centerUD;	//center coordinate (70)
	USHORT m_corner;	//corner coordinate	(420)
	UINT m_edge;	//edge coordinate (420*420)
	UBYTE m_distance;
	void init ();
	void do_move (int move_code);
	bool is_solved () const;
};

#endif
