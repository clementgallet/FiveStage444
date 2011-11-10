#ifndef CUBESTATE_H
#define CUBESTATE_H

#pragma once

#include "Constants.h"

//CubeState structure: a cubie-level representation of the cube.
struct CubeState {
	int m_distance;		//distance from solved state
	Face m_edge[24];	//what's at each edge position
	Face m_cor[8];		//what's at each corner position (3*cubie + orientation)
	Face m_cen[24];		//what's at each center position
	void init ();
	void do_move (int move_code);
	void do_sqs_move (int sqs_move_code);
	void compose_edge (const CubeState& cs1, const CubeState& cs2);
	void invert_fbcen ();
	bool edgeUD_parity_odd () const;	//compute edge parity for stage 3 purposes.
};

#endif
