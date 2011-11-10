#include "Tables.h"

void
init_4of8 ()
{
	int a1, a2, a3, a4;
	int i;
	int count = 0;
	for (i = 0; i < 256; ++i) {
		bm4of8_to_70[i] = 99;
	}
	for (a1 = 0; a1 < 8-3; ++a1) {
		for (a2 = a1+1; a2 < 8-2; ++a2) {
			for (a3 = a2+1; a3 < 8-1; ++a3) {
				for (a4 = a3+1; a4 < 8; ++a4) {
					bm4of8[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4);
					bm4of8_to_70[bm4of8[count]] = count;
					++count;
				}
			}
		}
	}
}

void
init_parity_table ()
{
	UINT x;

	for (x = 0; x < 40320; ++x) {
		parity_perm8_table[x] = (get_parity8 (x) != 0);
	}
}

int
get_parity8 (UINT x)
{
	int i, j;
	int parity = 0;
	Face t[8];
	perm_n_unpack (8, x, &t[0]);
	for (i = 0; i < 7; ++i) {
		if (t[i] == i) {
			continue;
		}
		for (j = i + 1; j < 8; ++j) {
			if (t[j] == i) {
				//"swap" the i & j elements, but don't bother updating the "i"-element
				//as it isn't needed anymore.
				t[j] = t[i];
			}
		}
		parity ^= 1;
	}
	return parity;
}

void
init_eloc ()
{
	const UINT POW2_24 = 4096*4096;
	int a1, a2, a3, a4, a5, a6, a7, a8;
	int i;
	USHORT u;
	Face t[8];
	Face f;
	int count = 0;
	for (a1 = 0; a1 < POW2_24; ++a1) {
		ebm2eloc[a1] = 999999;
	}
	for (a1 = 0; a1 < 24-7; ++a1) {
	 for (a2 = a1 + 1; a2 < 24-6; ++a2) {
	  for (a3 = a2 + 1; a3 < 24-5; ++a3) {
	   for (a4 = a3 + 1; a4 < 24-4; ++a4) {
	    for (a5 = a4 + 1; a5 < 24-3; ++a5) {
	     for (a6 = a5 + 1; a6 < 24-2; ++a6) {
	      for (a7 = a6 + 1; a7 < 24-1; ++a7) {
	       for (a8 = a7 + 1; a8 < 24; ++a8) {
				eloc2ebm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
					(1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
				ebm2eloc[eloc2ebm[count]] = count++;
	       }
	      }
	     }
	    }
	   }
	  }
	 }
	}
	for (a1 = 0; a1 < 24; ++a1) {
		perm_n_unpack (4, a1, &t[0]);
		for (i = 0; i < 4; ++i) {
			t[i+4] = t[i] + 4;
		}
		for (i = 0; i < 8; ++i) {
			map96[4*a1][i] = t[i];
		}
		f = t[4]; t[4]= t[5]; t[5] = f;
		f = t[6]; t[6]= t[7]; t[7] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 1][i] = t[i];
		}
		f = t[4]; t[4]= t[7]; t[7] = f;
		f = t[5]; t[5]= t[6]; t[6] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 2][i] = t[i];
		}
		f = t[4]; t[4]= t[5]; t[5] = f;
		f = t[6]; t[6]= t[7]; t[7] = f;
		for (i = 0; i < 8; ++i) {
			map96[4*a1 + 3][i] = t[i];
		}
	}
//optimization in progress...
	for (u = 0; u < 4096; ++u) {
		UINT u1;
		for (u1 = 0; u1 < 70; ++u1) {
			UINT u12 = u;
			UINT bbbb = bm4of8[u1];
			int j1 = 0;
			int j2;
			for (i = 0; u12 != 0; ++i) {
				if ((u12 & 0x1) != 0) {
					if ((bbbb & 0x1) == 0x1) {
						t[j1++] = i;
					}
					bbbb >>= 1;
				}
				u12 >>= 1;
			}
			a1 = 0;
			a2 = 24*24*24;
			for (j2 = 0; j2 < j1; ++j2) {
				a1 += a2*t[j2];
				a2 /= 24;
			}
			bm12_4of8_to_low_idx[u][u1] = a1;
			u12 = u;
			bbbb = bm4of8[u1];
			j1 = 0;
			for (i = 24 - 1; u12 != 0; --i) {
				if ((u12 & 0x800) != 0) {
					if ((bbbb & 0x80) != 0) {
						t[j1++] = i;
					}
					bbbb <<= 1;
				}
				u12 <<= 1;
				u12 &= 0xFFF;		//need this to become 0 after no more than 12 iterations
			}
			a1 = 0;
			a2 = 1;
			for (j2 = 0; j2 < j1; ++j2) {
				a1 += a2*t[j2];
				a2 *= 24;
			}
			bm12_4of8_to_high_idx[u][u1] = a1;
		}
	}
	for (u = 0; u < 256; ++u) {
		UINT u1;
		int bc = countbits (u);
		bitcount8[u] = bc;
		for (u1 = 0; u1 < 256; ++u1) {
			UINT u0 = u;
			UINT u3 = 0;
			UINT b = 0x1;
			for (i = 0; i < 8 && u0 != 0; ++i) {
				if ((u0 & 0x1) != 0) {
					if ((u1 & (1 << i)) != 0) {
						u3 |= b;
					}
					b <<= 1;
				}
				u0 >>= 1;
			}
			gen_MofN8[u][u1] = u3;
		}
	}
}

int
countbits (UINT x)
{
	UINT x2 = ((x >> 1) & 0x55555555) + (x & 0x55555555);
	UINT x4 = ((x2 >> 2) & 0x33333333) + (x2 & 0x33333333);
	UINT x8 = ((x4 >> 4) & 0x0F0F0F0F) + (x4 & 0x0F0F0F0F);
	return static_cast<int>(x8) % 255;
}

void
init_cloc ()
{
	int a1, a2, a3, a4; //, a5, a6, a7, a8;
	int count = 0;

	count = 0;
	for (a1 = 0; a1 < 24-3; ++a1) {
	 for (a2 = a1 + 1; a2 < 24-2; ++a2) {
	  for (a3 = a2 + 1; a3 < 24-1; ++a3) {
	   for (a4 = a3 + 1; a4 < 24; ++a4) {
		cloc_to_bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4);
		c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a3 + a4] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a4 + a3] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a2 + a4] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a4 + a2] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a2 + a3] = count;
		c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a3 + a2] = count;

		c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a3 + a4] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a4 + a3] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a1 + a4] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a4 + a1] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a1 + a3] = count;
		c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a3 + a1] = count;

		c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a2 + a4] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a4 + a2] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a1 + a4] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a4 + a1] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a1 + a2] = count;
		c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a2 + a1] = count;

		c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a2 + a3] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a3 + a2] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a1 + a3] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a3 + a1] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a1 + a2] = count;
		c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a2 + a1] = count;
		++count;
	   }
	  }
	 }
	}
}

void
init_perm_to_420 ()
{
	int i;
	UINT u, v, w, u2;
	Face t[8];
	Face t2[8];
	Face t3[8];

	for (u = 0; u < 40320; ++u) {
		perm_to_420[u] = 999;
	}
	for (u = 0; u < 70; ++u) {
		UINT bm = bm4of8[u];
		for (v = 0; v < 6; ++v) {
			perm_n_unpack (8, v, &t[0]);
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i) {
					t2[i] = map96[w][t[i]];
				}
				int f = 0;
				int b = 4;
				for (i = 0; i < 8; ++i) {
					if ((bm & (1 << i)) == 0) {
						t3[i] = t2[b++];
					} else {
						t3[i] = t2[f++];
					}
				}
				u2 = perm_n_pack (8, &t3[0]);
				perm_to_420[u2] = 6*u + v;
			}
		}
	}
}

void
init_move_tablesSTAGE1 ()
{
	int i, mc, lrfb, ud;
	UINT u;
	CubeState cube1, cube2;
	CubeStage1 s1, s2;
	s1.init ();
	s2.init ();
	cube1.init ();
	cube2.init ();
	for (u = 0; u < N_EDGE_COMBO8; ++u) {
		UINT ebm = eloc2ebm[u];
		lrfb = 0;
		ud = 16;
		for (i = 0; i < 24; ++i) {
			if ((ebm & (1 << i)) == 0) {
				cube1.m_edge[i] = lrfb++;
			} else {
				cube1.m_edge[i] = ud++;
			}
		}
		for (mc = 0; mc < N_BASIC_MOVES; ++mc) {
			cube2 = cube1;
			cube2.do_move (mc);
			ebm = 0;
			for (i = 0; i < 24; ++i) {
				if (cube2.m_edge[i] >= 16) {
					ebm |= (1 << i);
				}
			}
			move_table_edgeSTAGE1[u][mc] = ebm2eloc[ebm];
		}
	}
	cube1.init ();
	for (u = 0; u < N_CORNER_ORIENT; ++u) {
		s1.m_co = u;
		convert_stage1_to_std_cube (s1, &cube1);
		for (mc = 0; mc < N_BASIC_MOVES; ++mc) {
			int fmc = basic_to_face[mc];
			if (fmc >= 0) {
				if (fmc >= N_FACE_MOVES) {
					printf ("do_move: face move code error\n");
					exit (1);
				}
				cube2 = cube1;
				rotate_sliceCORNER (mc, cube1, &cube2);
				convert_std_cube_to_stage1 (cube2, &s2);
				move_table_co[u][fmc] = s2.m_co;
			}
		}
	}
}

void
rotate_sliceCORNER (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	//Caller must initialize result cube m_cor[] array!
	int i;
	if (move_code % 6 >= 3) {
		return;		//inner slice turn, no corners affected
	}
	int mc6 = move_code/6;
	int mc = 3*mc6 + move_code % 3;
	int fidx = rotateCOR_fidx[mc];
	int tidx = rotateCOR_tidx[mc];
	if (mc % 3 != 2) {	//avoid doing "if" inside loop, for speed
		for (i = 0; i < 4; ++i) {
			Face tmpface = init_cube.m_cor[rotateCOR_ft[fidx + i]];
			if (mc >= 6) {	//L,R,F,B face turns
				Face new_ori = (tmpface >> 3) + rotateCOR_ori[i];
				new_ori %= 3;
				tmpface = (tmpface & 0x7) + (new_ori << 3);
			}
			result_cube->m_cor[rotateCOR_ft[tidx + i]] = tmpface;
		}
	} else {
		for (i = 0; i < 4; ++i) {
			result_cube->m_cor[rotateCOR_ft[tidx + i]] = init_cube.m_cor[rotateCOR_ft[fidx + i]];
		}
	}
}

void
init_stage2 ()
{
	int i, j;
	UINT u;
	Face t[8];
	int mc, udlrf;
	CubeState cube1, cube2;
	CubeStage2 s1, s2;
	s1.init ();
	s2.init ();
	cube1.init ();
	cube2.init ();
	for (u = 0; u < N_CENTER_COMBO4; ++u) {
		UINT cbm = cloc_to_bm[u];
		udlrf = 0;
		for (i = 0; i < 24; ++i) {
			if ((cbm & (1 << i)) == 0) {
				cube1.m_cen[i] = udlrf++/4;
			} else {
				cube1.m_cen[i] = 5;
			}
		}
		for (mc = 0; mc < N_STAGE2_SLICE_MOVES; ++mc) {
			cube2 = cube1;
			cube2.do_move (stage2_slice_moves[mc]);
			j = 0;
			for (i = 0; i < 24; ++i) {
				if (cube2.m_cen[i] == 5) {
					t[j++] = i;
				}
			}
			int idx = 24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3];
			move_table_cenSTAGE2[u][mc] = c4_to_cloc[idx];
		}
	}
	for (u = 0; u < 420; ++u) {
		s1.m_centerFB = 0;
		s1.m_edge = u;
		for (mc = 0; mc < N_STAGE2_SLICE_MOVES; ++mc) {
			s2 = s1;
			s2.do_move_slow (mc);
			move_table_edgeSTAGE2[u][mc] = s2.m_edge;
		}
	}
}

void
stage2_cen_to_cloc4s (UINT cen, UINT *pclocf, UINT* pclocb)
{
	UINT cenbm = eloc2ebm[cen / 70];
	UINT cenbm4of8 = bm4of8[cen % 70];
	int idx1 = bm12_4of8_to_high_idx[cenbm >> 12][cen % 70];
	idx1 += bm12_4of8_to_low_idx[cenbm & 0xFFF][cen % 70];
	UINT comp_70 = bm4of8_to_70[(~cenbm4of8) & 0xFF];	//could be a direct lookup
	int idx2 = bm12_4of8_to_high_idx[cenbm >> 12][comp_70];
	idx2 += bm12_4of8_to_low_idx[cenbm & 0xFFF][comp_70];
	*pclocf = c4_to_cloc[idx1];
	*pclocb = c4_to_cloc[idx2];
}

void
init_stage3 ()
{
	const UINT POW2_16 = 256*256;
	int a1, a2, a3, a4, a5, a6, a7, a8;

	int count = 0;
	for (a1 = 0; a1 < POW2_16; ++a1) {
		e16bm2eloc[a1] = 999999;
	}
	for (a1 = 0; a1 < 16-7; ++a1) {
	 for (a2 = a1 + 1; a2 < 16-6; ++a2) {
	  for (a3 = a2 + 1; a3 < 16-5; ++a3) {
	   for (a4 = a3 + 1; a4 < 16-4; ++a4) {
	    for (a5 = a4 + 1; a5 < 16-3; ++a5) {
	     for (a6 = a5 + 1; a6 < 16-2; ++a6) {
	      for (a7 = a6 + 1; a7 < 16-1; ++a7) {
	       for (a8 = a7 + 1; a8 < 16; ++a8) {
				eloc2e16bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
					(1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
				e16bm2eloc[eloc2e16bm[count]] = count++;
	       }
	      }
	     }
	    }
	   }
	  }
	 }
	}
	init_move_tablesSTAGE3 ();
}

void
init_move_tablesSTAGE3 ()
{
	int mc;
	UINT u;
	CubeStage3 s3;
	s3.init ();
	for (u = 0; u < N_STAGE3_CENTER_CONFIGS; ++u) {
		s3.m_edge = 0;
		for (mc = 0; mc < N_STAGE3_SLICE_MOVES; ++mc) {
			s3.m_centerLR = u;
			s3.do_move_slow (mc);
			move_table_cenSTAGE3[u][mc] = s3.m_centerLR;
		}
	}
	for (u = 0; u < N_STAGE3_EDGE_CONFIGS; ++u) {
		s3.m_centerLR = 0;
		for (mc = 0; mc < N_STAGE3_SLICE_MOVES; ++mc) {
			s3.m_edge = u;
			s3.do_move_slow (mc);
			move_table_edgeSTAGE3[u][mc] = s3.m_edge;
		}
	}
}

void
array8_to_set_a (const Face* t, CubeState* result_cube)
{
	int i;
	int j = 0;
	for (i = 0; i < 8; ++i) {
		if (i >= 4) {
			j = i + 8;
		} else {
			j = i;
		}
		Face t1 = t[i];
		if (t1 >= 4) {
			t1 += 8;
		}
		result_cube->m_edge[j] = t1;
	}
}

void
set_a_to_array8 (const CubeState& init_cube, Face* t)
{
	int i;
	int j = 0;
	for (i = 0; i < 8; ++i) {
		if (i >= 4) {
			j = i + 8;
		} else {
			j = i;
		}
		Face t1 = init_cube.m_edge[j];
		if (t1 >= 4) {
			if (t1 >= 12) {
				t1 -= 8;
			} else {
				printf ("error: set_a_to_packed8\n");
				exit (1);
			}
		}
		t[i] = t1;
	}
}

void
array8_to_set_b (const Face* t, CubeState* result_cube)
{
	int i;
	for (i = 0; i < 8; ++i) {
		result_cube->m_edge[4 + i] = t[i] + 4;
	}
}

void
set_b_to_array8 (const CubeState& init_cube, Face* t)
{
	int i;
	for (i = 0; i < 8; ++i) {
		t[i] = init_cube.m_edge[4 + i] - 4;
	}
}

UINT
cube_state_to_lrfb (const CubeState& init_cube)
{
	Face t[8];
	set_a_to_array8 (init_cube, &t[0]);
	UINT u1 = perm_n_pack (8, &t[0]);
	set_b_to_array8 (init_cube, &t[0]);
	UINT u2 = perm_n_pack (8, &t[0]);
	return 40320*u2 + u1;
}

void
lrfb_to_cube_state (UINT u, CubeState* result_cube)
{
	Face t[8];
	result_cube->init ();
	perm_n_unpack (8, u % 40320, &t[0]);
	array8_to_set_a (&t[0], result_cube);
	perm_n_unpack (8, u / 40320, &t[0]);
	array8_to_set_b (&t[0], result_cube);
}

UINT
lrfb_get_edge_rep (UINT u)
{
	UINT rep = stage4_edge_hB[u/40320];	//65000*40320;
	UINT reph = stage4_edge_hgB[u/40320];	//65000;
	UINT Blr, Bfb;
	Blr = rep / 24;
	Bfb = rep % 24;
	UINT repBlr = sqs_perm_to_rep[Blr];
	UINT repBfb = sqs_perm_to_rep[Bfb];
	UINT repl = stage4_edge_hgA[u % 40320][6*repBlr + repBfb];
	return 40320*reph + repl;
}

void
init_stage4_edge_tables ()
{
	int i;
	UINT u, h1, h2;
	CubeState cs1, cs2, cs3;
	cs1.init ();
	cs2.init ();
	for (u = 0; u < 40320; ++u) {
		cs2.init ();
		lrfb_to_cube_state (40320*u, &cs2);
		UINT rep = 999;
		UINT reph = 65000;
		UINT Blr, Bfb;
		for (h1 = 0; h1 < 576; ++h1) {
			Blr = h1 / 24;
			Bfb = h1 % 24;
			perm_n_unpack (4, Blr, &cs1.m_edge[4]);
			perm_n_unpack (4, Bfb, &cs1.m_edge[8]);
			for (i = 4; i < 8; ++i) {
				cs1.m_edge[i] += 4;
			}
			for (i = 8; i < 12; ++i) {
				cs1.m_edge[i] += 8;
			}
			cs3.compose_edge (cs1, cs2);
			UINT u3 = cube_state_to_lrfb (cs3);
			UINT u3h = u3/40320;
			if (u3h < reph) {
				reph = u3h;
				rep = h1;
			}
		}
		stage4_edge_hB[u] = rep;
		stage4_edge_hgB[u] = reph;
	}
	cs1.init ();
	for (u = 0; u < 40320; ++u) {
		cs2.init ();
		lrfb_to_cube_state (u, &cs2);
		for (h1 = 0; h1 < 36; ++h1) {
			UINT repl = 65000;
			UINT replr = h1 / 6;
			UINT repfb = h1 % 6;
			for (h2 = 0; h2 < 16; ++h2) {
				perm_n_unpack (4, sqs_rep_to_perm[replr][h2/4], &cs1.m_edge[0]);
				perm_n_unpack (4, sqs_rep_to_perm[repfb][h2%4], &cs1.m_edge[12]);
				for (i = 12; i < 16; ++i) {
					cs1.m_edge[i] += 12;
				}
				cs3.compose_edge (cs1, cs2);
				UINT u3 = cube_state_to_lrfb (cs3);
				UINT u3l = u3 % 40320;
				if (u3l < repl) {
					repl = u3l;
				}
			}
			stage4_edge_hgA[u][h1] = repl;
		}
	}
}



void
lrfb_check ()
{
	UINT u1;
	CubeState cs1, cs2;
	cs1.init ();
	cs2.init ();

	stage4_edge_table_init ();
	UINT repcount = 0;
	const UINT n = 40320u*40320u;
	for (u1 = 0; u1 < n; ++u1) {
		if (u1 % 1000 == 0) {
			if (repcount == 44100 && u1 < 200000000) {
				u1 = 40320u*20160u;
			}
			if (repcount == 88200) {
				break;
			}
		}
		UINT uH = u1 / 40320;
		UINT uL = u1 % 40320;
		if (parity_perm8_table[uH] != parity_perm8_table[uL]) {
			continue;
		}
		UINT myrep = lrfb_get_edge_rep (u1);
		if (myrep == u1) {
			add_to_stage4_edge_table (myrep, repcount++);
		}
	}
}

void
stage4_edge_table_init ()
{
	UINT i;
	for (i = 0; i < N_STAGE4_EDGE_HASH_TABLE; ++i) {
		stage4_edge_hash_table_val[i] = 40320u*40320u;	//an "invalid" value
		stage4_edge_hash_table_idx[i] = 88200;	//also "invalid" but shouldn't matter
	}
}

bool
stage4_edge_table_lookup (UINT val, UINT* hash_loc)
{
	UINT hash = val % N_STAGE4_EDGE_HASH_DIVISOR;
	UINT i = hash + 1;
	while (stage4_edge_hash_table_val[i] < 40320u*40320u) {
		if (stage4_edge_hash_table_val[i] == val) {
			*hash_loc = i;
			return true;
		}
		i += hash;
		i %= N_STAGE4_EDGE_HASH_TABLE;
		if (i == 0) {	//relies on table being a prime number in size
			*hash_loc = 0;
			return false;	//not found, table full, return false
		}
	}
	*hash_loc = i;
	return false;	//new position, it was not found in the table
}

void
add_to_stage4_edge_table (UINT val, UINT idx)
{
	UINT hash_idx;
	if (stage4_edge_table_lookup (val, &hash_idx)) {
		printf ("edge hash table: duplicate value!\n");
	} else {
		if (hash_idx == 0) {
			printf ("Stage4 edge hash table full!\n");
			exit (0);
		}
		stage4_edge_hash_table_val[hash_idx] = val;
		stage4_edge_hash_table_idx[hash_idx] = idx;
		stage4_edge_rep_table[idx] = val;
	}
}

void
init_move_tablesSTAGE4 ()
{
	int mc;
	UINT u;
	CubeStage4 s4, s4a;
	s4.init ();
	s4a.init ();
	CubeState cs1;
	cs1.init ();
	for (u = 0; u < 40320; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			lrfb_to_cube_state (u, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			UINT u2 = cube_state_to_lrfb (cs1);
			move_table_AedgeSTAGE4[u][mc] = u2 % 40320;
		}
	}
	for (u = 0; u < 40320; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			lrfb_to_cube_state (40320*u, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			UINT u2 = cube_state_to_lrfb (cs1);
			move_table_BedgeSTAGE4[u][mc] = u2 / 40320;
		}
	}
	s4.m_edge = 0;
	s4.m_centerUD = 0;
	for (u = 0; u < N_STAGE4_CORNER_CONFIGS; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			s4.m_corner = u;
			convert_stage4_to_std_cube (s4, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			convert_std_cube_to_stage4 (cs1, &s4a);
			move_table_cornerSTAGE4[u][mc] = s4a.m_corner;
		}
	}
	s4.m_edge = 0;
	s4.m_corner = 0;
	for (u = 0; u < N_STAGE4_CENTER_CONFIGS; ++u) {
		for (mc = 0; mc < N_STAGE4_SLICE_MOVES; ++mc) {
			s4.m_centerUD = u;
			convert_stage4_to_std_cube (s4, &cs1);
			cs1.do_move (stage4_slice_moves[mc]);
			convert_std_cube_to_stage4 (cs1, &s4a);
			move_table_cenSTAGE4[u][mc] = s4a.m_centerUD;
		}
	}
}

void
init_squares ()
{
	static int mov_lst[6] = { Uf2, Df2, Ls2, Rs2, Ff2, Bf2 };
	static UBYTE cen_swapbits_map[6*2] = {
		0x90, 0x60, //Uf2
		0x09, 0x06, //Df2
		0x82, 0x28, //Ls2
		0x41, 0x14, //Rs2
		0x84, 0x48, //Fs2
		0x21, 0x12  //Bs2
	};
	UINT i, j;
	CubeState cube1, cube2;
	for (i = 0; i < 24; ++i) {
		switch (sqs_perm_to_rep[i]) {
		case 0:
			squares_2nd_perm[i][0] = 0;
			squares_2nd_perm[i][1] = 7;
			squares_2nd_perm[i][2] = 16;
			squares_2nd_perm[i][3] = 23;
			break;
		case 1:
			squares_2nd_perm[i][0] = 1;
			squares_2nd_perm[i][1] = 6;
			squares_2nd_perm[i][2] = 17;
			squares_2nd_perm[i][3] = 22;
			break;
		case 2:
			squares_2nd_perm[i][0] = 2;
			squares_2nd_perm[i][1] = 10;
			squares_2nd_perm[i][2] = 13;
			squares_2nd_perm[i][3] = 21;
			break;
		case 3:
			squares_2nd_perm[i][0] = 3;
			squares_2nd_perm[i][1] = 11;
			squares_2nd_perm[i][2] = 12;
			squares_2nd_perm[i][3] = 20;
			break;
		case 4:
			squares_2nd_perm[i][0] = 4;
			squares_2nd_perm[i][1] = 8;
			squares_2nd_perm[i][2] = 15;
			squares_2nd_perm[i][3] = 19;
			break;
		case 5:
			squares_2nd_perm[i][0] = 5;
			squares_2nd_perm[i][1] = 9;
			squares_2nd_perm[i][2] = 14;
			squares_2nd_perm[i][3] = 18;
			break;
		}
	}
	cube2.init ();
	for (i = 0; i < 96; ++i) {
		cube1.init ();
		UINT first_perm = i / 4;
		UINT second_perm = squares_2nd_perm[first_perm][i % 4];
		perm_n_unpack (4, first_perm, &cube1.m_edge[0]);
		perm_n_unpack (4, second_perm, &cube1.m_edge[4]);
		for (j = 4; j < 8; ++j) {
			cube1.m_edge[j] += 4;
		}
		for (j = 0; j < 6; ++j) {
			cube2 = cube1;
			rotate_sliceEDGE (mov_lst[j], cube1, &cube2);
			UINT x1 = perm_n_pack (4, &cube2.m_edge[0]);
			UINT x2 = cube2.m_edge[4] - 4;
			if (x2 >= 4) {
				printf ("unexpected cube state\n");
				squares_movemap[i][j] = 0;
				continue;
			}
			squares_movemap[i][j] = 4*x1 + x2;
			x2 = perm_n_pack (4, &cube2.m_edge[4]);
			if (sqs_perm_to_rep[x1] != sqs_perm_to_rep[x2]) {
				printf ("perm1,perm2 inconsistency! %u %u\n", i, j);
			}
		}
	}
	for (i = 0; i < 256; ++i) {
		squares_cen_revmap[i] = 0;
	}
	for (i = 0; i < 12; ++i) {
		squares_cen_revmap[squares_cen_map[i]] = i;
	}
	for (i = 0; i < 12; ++i) {
		UINT x = squares_cen_map[i];
		for (j = 0; j < 6; ++j) {
			UINT x2 = swapbits (x, cen_swapbits_map[2*j]);
			x2 = swapbits (x2, cen_swapbits_map[2*j + 1]);
			squares_cen_movemap[i][j] = squares_cen_revmap[x2];
			if (x2 == 0) {
				printf ("Unexpected value for squares_cen_movemap[%d][%d]!\n", i, j);
			}
		}
	}
}

//swapbits () - return a value with two bits interchanged.
//x = input value
//b = value (bit mask) specifying the two bits to be swapped (must have countbits (b) == 2)
UINT
swapbits (UINT x, UINT b)
{
	UINT x2 = x & b;
	if (x2 == 0 || x2 == b) {
		return x;
	}
	return x ^ b;
}


void
rotate_sliceEDGE (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	//Caller must initialize result cube m_edge[] array!
	int i;
	int mc3 = move_code/3;
	int movdir = move_code % 3;
	int mcx = 3*(mc3/2);
	if ((mc3 & 0x1) != 0) {	//slice move?
		mcx += 2;
	}
	int fidx = rotateEDGE_fidx[3*mcx + movdir];
	int tidx = rotateEDGE_tidx[3*mcx + movdir];
	for (i = 0; i < 4; ++i) {
		result_cube->m_edge[rotateEDGE_ft[tidx + i]] = init_cube.m_edge[rotateEDGE_ft[fidx + i]];
	}
	if ((mc3 & 0x1) == 0) {	//face move? have a 2nd set of edges to cycle
		fidx = rotateEDGE_fidx[3*(mcx+1) + movdir];
		tidx = rotateEDGE_tidx[3*(mcx+1) + movdir];
		for (i = 0; i < 4; ++i) {
			result_cube->m_edge[rotateEDGE_ft[tidx + i]] = init_cube.m_edge[rotateEDGE_ft[fidx + i]];
		}
	}
}

UINT
squares_move (UINT pos96, int move_code6)
{
	if (move_code6 < 0) {
		return pos96;
	}
	return squares_movemap[pos96][move_code6];
}

UINT
squares_move_corners (UINT pos96, int sqs_move_code)
{
	return squares_move (pos96, squares_map[3][sqs_move_code]);
}

UINT
squares_move_edges (UINT pos96, int sqs_move_code, int edge_group)
{
	return squares_move (pos96, squares_map[edge_group][sqs_move_code]);
}

UINT
squares_move_centers (UINT pos12, int sqs_move_code, int cen_group)
{
	int move_code6 = squares_map[4+cen_group][sqs_move_code];
	if (move_code6 < 0) {
		return pos12;
	}
	return squares_cen_movemap[pos12][move_code6];
}

void
rotate_sliceCENTER (int move_code, const CubeState& init_cube, CubeState* result_cube)
{
	int i;
	int mc3 = move_code/3;
	int movdir = move_code % 3;
	int mcx = 3*(mc3/2) + (mc3 & 0x1);
	int fidx = rotateCEN_fidx[3*mcx + movdir];
	int tidx = rotateCEN_tidx[3*mcx + movdir];
	for (i = 0; i < 4; ++i) {
		result_cube->m_cen[rotateCEN_ft[tidx + i]] = init_cube.m_cen[rotateCEN_ft[fidx + i]];
	}
	if ((mc3 & 0x1) == 1) {	//slice move? have a 2nd set of centers to cycle
		fidx = rotateCEN_fidx[3*(mcx+1) + movdir];
		tidx = rotateCEN_tidx[3*(mcx+1) + movdir];
		for (i = 0; i < 4; ++i) {
			result_cube->m_cen[rotateCEN_ft[tidx + i]] = init_cube.m_cen[rotateCEN_ft[fidx + i]];
		}
	}
}
