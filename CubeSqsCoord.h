#ifndef CUBESQSCOORD_H
#define CUBESQSCOORD_H

#pragma once

#include "Constants.h"

struct CubeSqsCoord {
	UINT m_ep96x96x96;
	USHORT m_cen12x12x12;
	UBYTE m_cp96;
	void init ();
	void do_move (int sqs_move_code);
	void do_whole_cube_move (int sqs_whole_cube_move);
	bool is_solved () const;
};

#endif
