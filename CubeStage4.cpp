#include "CubeStage4.h"
#include "Tables.h"
#include "CubeConverter.h"

void
CubeStage4::init ()
{
	m_edge = 0;	//?
	m_corner = 0;	//?
	m_centerUD = 0;	//?
}

void
CubeStage4::do_move (int move_code)
{
	UINT hash_idx;
	m_centerUD = move_table_cenSTAGE4[m_centerUD][move_code];
	m_corner = move_table_cornerSTAGE4[m_corner][move_code];
	UINT edge_lrfb = stage4_edge_rep_table[m_edge];
	UINT lrfbA = edge_lrfb % 40320;
	UINT lrfbB = edge_lrfb / 40320;
	UINT result_lrfb = 40320*move_table_BedgeSTAGE4[lrfbB][move_code] + move_table_AedgeSTAGE4[lrfbA][move_code];
	UINT result_edgerep = lrfb_get_edge_rep (result_lrfb);
	if (stage4_edge_table_lookup (result_edgerep, &hash_idx)) {
		m_edge = stage4_edge_hash_table_idx[hash_idx];
	} else {
		printf ("edge representative not found in hash table!\n");
		exit (1);
	}
}

bool
CubeStage4::is_solved () const
{
	int i;

	if (m_corner != 0) {
		return false;	//not solved if wrong corner value
	}
	if (m_edge != 0) {
		return false;	//not solved if wrong edge value
	}
	bool found = false;
	for (i = 0; i < STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i) {
		if (m_centerUD == bm4of8_to_70[stage4_solved_centers_bm[i]]) {
			found = true;
			break;
		}
	}
	return found;	//If we found a matching center value, then it is solved.
}
