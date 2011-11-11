#include "CubeConverter.h"

void
convert_stage1_to_std_cube (const CubeStage1& init_cube, CubeState* result_cube)
{
	int i;
	UINT ebm = eloc2ebm[init_cube.m_edge_ud_combo8];
	int lrfb = 0;
	int ud = 16;
	for (i = 0; i < 24; ++i) {
		if ((ebm & (1 << i)) == 0) {
			result_cube->m_edge[i] = lrfb++;
		} else {
			result_cube->m_edge[i] = ud++;
		}
		result_cube->m_cen[i] = i/4;
	}
	UINT orientc = init_cube.m_co;
	UINT orientcmod3 = 0;
	for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
		Face fo = orientc % 3;
		result_cube->m_cor[i] = i + (fo << 3);
		orientcmod3 += fo;
		orientc /= 3;
	}
	result_cube->m_cor[7] = 7 + (((24 - orientcmod3) % 3) << 3);
}

void
convert_std_cube_to_stage1 (const CubeState& init_cube, CubeStage1* result_cube)
{
	int i;
	UINT ebm = 0;
	for (i = 0; i < 24; ++i) {
		if (init_cube.m_edge[i] >= 16) {
			ebm |= (1 << i);
		}
	}
	result_cube->m_edge_ud_combo8 = ebm2eloc[ebm];
	UINT orientc = 0;
	for (i = 0; i < 7; ++i) {	//don't want 8th edge orientation
		orientc = 3*orientc + (init_cube.m_cor[i] >> 3);
	}
	result_cube->m_co = orientc;
}

void
convert_stage2_to_std_cube (const CubeStage2& init_cube, CubeState* result_cube)
{
	int i;
	Face t6[4];
	UINT cenbm = eloc2ebm[init_cube.m_centerFB/70];
	UINT cenbm4of8 = bm4of8[init_cube.m_centerFB % 70];
	int udlr = 0;
	int pos4of8 = 0;
	for (i = 0; i < 24; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = udlr++/4;
		} else {
			if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
				result_cube->m_cen[i] = 5;
			} else {
				result_cube->m_cen[i] = 4;
			}
		}
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = i;
	}
	UINT edgeFbm = bm4of8[init_cube.m_edge / 6];
	perm_n_unpack (4, init_cube.m_edge % 6, &t6[0]);
	for (i = 0; i < 16; ++i) {
		result_cube->m_edge[i] = i;
	}
	int f = 16;
	int b = 0;
	for (i = 0; i < 8; ++i) {
		if ((edgeFbm & (1 << i)) == 0) {
			result_cube->m_edge[16 + i] = 20 + t6[b++];
		} else {
			result_cube->m_edge[16 + i] = f++;
		}
	}
}

void
convert_std_cube_to_stage2 (const CubeState& init_cube, CubeStage2* result_cube)
{
	int i;
	UINT cenbm = 0;
	UINT cenbm4of8 = 0;
	int j = 0;
	for (i = 0; i < 24; ++i) {
		if (init_cube.m_cen[i] >= 4) {
			cenbm |= (1 << i);
			if (init_cube.m_cen[i] == 4) {
				cenbm4of8 |= (1 << j);
			}
			++j;
		}
	}
	result_cube->m_centerFB = 70*ebm2eloc[cenbm] + bm4of8_to_70[cenbm4of8];
	UINT u = perm_n_pack (8, &init_cube.m_edge[16]);
	result_cube->m_edge = perm_to_420[u];
}

void
convert_stage3_to_std_cube (const CubeStage3& init_cube, CubeState* result_cube)
{
	int i;
	UINT cenbm = eloc2e16bm[init_cube.m_centerLR/70];
	UINT cenbm4of8 = bm4of8[init_cube.m_centerLR % 70];
	int ud = 0;
	int pos4of8 = 0;
	for (i = 0; i < 16; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = ud++/4;
		} else {
			if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
				result_cube->m_cen[i] = 3;
			} else {
				result_cube->m_cen[i] = 2;
			}
		}
	}
	for (i = 16; i < 24; ++i) {
		result_cube->m_cen[i] = i/4;
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[i] = i;
	}
	UINT edge_bm = eloc2e16bm[init_cube.m_edge];

	UINT e0 = 0;
	UINT e1 = 4;
	for (i = 0; i < 16; ++i) {
		if ((edge_bm & (1 << i)) != 0) {
			result_cube->m_edge[i] = e0++;
			if (e0 == 4) {
				e0 = 12;		//skip numbers 4..11; those are used for e1
			}
		} else {
			result_cube->m_edge[i] = e1++;
		}
	}
	for (i = 16; i < 24; ++i) {
		result_cube->m_edge[i] = i;
	}
}

void
convert_std_cube_to_stage3 (const CubeState& init_cube, CubeStage3* result_cube)
{
	int i;
	UINT cenbm = 0;
	UINT cenbm4of8 = 0;
	int j = 0;
	for (i = 0; i < 16; ++i) {
		if (init_cube.m_cen[i] >= 4) {
			printf ("error: cube state not a stage3 position\n");
			exit (1);
		}
		if (init_cube.m_cen[i] >= 2) {
			cenbm |= (1 << i);
			if (init_cube.m_cen[i] == 2) {
				cenbm4of8 |= (1 << j);
			}
			++j;
		}
	}
	result_cube->m_centerLR = 70*e16bm2eloc[cenbm] + bm4of8_to_70[cenbm4of8];
	UINT edge_bm = 0;
	for (i = 0; i < 16; ++i) {
		if (init_cube.m_edge[i] >= 16) {
			printf ("error: cube state not a stage3 position\n");
			exit (1);
		}
		if (init_cube.m_edge[i] < 4 || init_cube.m_edge[i] >= 12) {
			edge_bm |= (1 << i);
		}
	}
	result_cube->m_edge = e16bm2eloc[edge_bm];
}

void
convert_stage4_to_std_cube (const CubeStage4& init_cube, CubeState* result_cube)
{
	int i;
	Face t6[4], t8[8];
	//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
	//But the do_move function for std_cube assumes "standard" mapping.
	//Therefore the m_cor array must be converted accordingly using this conversion array.
	static Face sqs_to_std[8] = { 0, 2, 5, 7, 1, 3, 4, 6 };

	UINT edge = stage4_edge_rep_table[init_cube.m_edge];
	lrfb_to_cube_state (edge, result_cube);	//note: initializes result_cube! so we do this first
	UINT cor_bm = bm4of8[init_cube.m_corner / 6];
	perm_n_unpack (4, init_cube.m_corner % 6, &t6[0]);
	int a = 0;
	int b = 0;
	for (i = 0; i < 8; ++i) {
		if ((cor_bm & (1 << i)) == 0) {
			t8[i] = 4 + t6[b++];
		} else {
			t8[i] = a++;
		}
	}
	for (i = 0; i < 8; ++i) {
		result_cube->m_cor[sqs_to_std[i]] = sqs_to_std[t8[i]];
	}
	UINT cenbm = bm4of8[init_cube.m_centerUD];
	for (i = 0; i < 8; ++i) {
		if ((cenbm & (1 << i)) == 0) {
			result_cube->m_cen[i] = 1;
		} else {
			result_cube->m_cen[i] = 0;
		}
	}
	for (i = 8; i < 24; ++i) {
		result_cube->m_cen[i] = i/4;
	}
}

void
convert_std_cube_to_stage4 (const CubeState& init_cube, CubeStage4* result_cube)
{
	int i;
	Face t6[8];
	//Note: for corners, use of perm_to_420 array requires "squares" style mapping.
	//But the do_move function for std_cube assumes "standard" mapping.
	//Therefore the m_cor array must be converted accordingly using this conversion array.
	static Face std_to_sqs[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	UINT edge = cube_state_to_lrfb (init_cube);
	UINT edgerep = lrfb_get_edge_rep (edge);
	UINT hash_idx;
	if (stage4_edge_table_lookup (edgerep, &hash_idx)) {
		result_cube->m_edge = stage4_edge_hash_table_idx[hash_idx];
	} else {
		printf ("stage4 edge value error\n");
		exit (1);
	}
	for (i = 0; i < 8; ++i) {
		t6[std_to_sqs[i]] = std_to_sqs[init_cube.m_cor[i]];
	}
	UINT u = perm_n_pack (8, &t6[0]);
	result_cube->m_corner = perm_to_420[u];
	UINT cenbm4of8 = 0;
	for (i = 0; i < 8; ++i) {
		if (init_cube.m_cen[i] >= 2) {
			printf ("error: cube state not a stage4 position\n");
			exit (1);
		}
		if (init_cube.m_cen[i] == 0) {
			cenbm4of8 |= (1 << i);
		}
	}
	result_cube->m_centerUD = bm4of8_to_70[cenbm4of8];
}

void
convert_std_cube_to_squares (const CubeState& init_cube, CubeSqsCoord* result_cube)
{
	int i;
	CubeState cs_sqs;
	//We must convert between "squares"-style cubie numbering and the "standard"-style
	//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
	static Face std_to_sqs_cor[8] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	static Face std_to_sqs_cen[24] = {
		0,  3,  1,  2,  5,  6,  4,  7,
		8, 11,  9, 10, 13, 14, 12, 15,
	   16, 19, 17, 18, 21, 22, 20, 23
	};
	cs_sqs = init_cube;
	for (i = 0; i < 8; ++i) {
		cs_sqs.m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[init_cube.m_cor[i]];
	}
	for (i = 0; i < 24; ++i) {
		cs_sqs.m_cen[std_to_sqs_cen[i]] = std_to_sqs_cen[4*init_cube.m_cen[i]]/4;
	}
	pack_cubeSQS (cs_sqs, result_cube);
}

void
pack_cubeSQS (const CubeState& cube1, CubeSqsCoord* result_cube)
{
	UINT ep1 = perm_n_pack (4, &cube1.m_edge[0]);
	UINT ep2 = perm_n_pack (4, &cube1.m_edge[8]);
	UINT ep3 = perm_n_pack (4, &cube1.m_edge[16]);
	result_cube->m_ep96x96x96 = 96*96*(4*ep3 + (cube1.m_edge[20] - 20)) + 96*(4*ep2 + (cube1.m_edge[12] - 12)) +
		4*ep1 + (cube1.m_edge[4] - 4);
	result_cube->m_cp96 = 4*perm_n_pack (4, &cube1.m_cor[0]) + (cube1.m_cor[4] - 4);
	result_cube->m_cen12x12x12 = squares_pack_centers (&cube1.m_cen[0]);
}

UINT
squares_pack_centers (const Face* arr)
{
	int i;
	UINT x = 0;
	UINT b = 0x800000;
	for (i = 0; i < 24; ++i) {
		if ((arr[i] & 0x1) != 0) {
			x |= b;
		}
		b >>= 1;
	}
	UINT cen1 = squares_cen_revmap[(x >> 16) & 0xFF];
	UINT cen2 = squares_cen_revmap[(x >> 8) & 0xFF];
	UINT cen3 = squares_cen_revmap[x & 0xFF];
	return cen1 + 12*cen2 + 12*12*cen3;
}

