package fivestage444;

public class CubeStage2 {

	public int m_centerFB; // (51482970)
	public short m_edge; //edge coordinate (420)

	public static byte prune_table_edgcen2[Constants.N_CENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS/2];

	public void init (){
		m_edge = 0;
		m_centerFB = Constants.N_STAGE2_CENTER_CONFIGS - 70;
	}

	public boolean is_solved ()
	{
		if (m_edge == 0) {
			if (m_centerFB < 51482900 || m_centerFB > 51482969)
				return false;
			if (m_centerFB == 51482900 || m_centerFB == 51482914 || m_centerFB == 51482920 ||
			    m_centerFB == 51482923 || m_centerFB == 51482927 || m_centerFB == 51482928 || 
			    m_centerFB == 51482941 || m_centerFB == 51482942 || m_centerFB == 51482946 ||
			    m_centerFB == 51482949 || m_centerFB == 51482955 ||  m_centerFB == 51482969)
				return true;
		} else if (m_edge == 414) {
			if (m_centerFB < 50582070 || m_centerFB > 50582139)
				return false;
			if (m_centerFB == 50582070 || m_centerFB == 50582084 || m_centerFB == 50582090 ||
			    m_centerFB == 50582093 || m_centerFB == 50582097 || m_centerFB == 50582098 ||
			    m_centerFB == 50582111 || m_centerFB == 50582112 || m_centerFB == 50582116 ||
			    m_centerFB == 50582119 || m_centerFB == 50582125 || m_centerFB == 50582139)
				return true;
		}
		return false;
	}

	public void do_move_slow (int move_code)
	{
		CubeState cube1;
		convert_to_std_cube(cube1);
		cube1.do_move (Constants.stage2_slice_moves[move_code]);
		cube1.convert_to_stage2 (this);
	}

	public void do_move (int move_code){
		int i;
		Face t1[4];
		Face t2[4];
		int cenbm = Tables.eloc2ebm[m_centerFB / 70];
		int cenbm4of8 = Tables.bm4of8[m_centerFB % 70];
		int j1 = 0;
		int j2 = 0;
		for (i = 0; cenbm != 0; ++i) {
			if ((cenbm & 0x1) != 0) {
				if ((cenbm4of8 & 0x1) == 0) {
					t2[j2++] = i;
				} else {
					t1[j1++] = i;
				}
				cenbm4of8 >>= 1;
			}
			cenbm >>= 1;
		}
		int idx1 = 24*24*24*t1[0] + 24*24*t1[1] + 24*t1[2] + t1[3];
		int idx2 = 24*24*24*t2[0] + 24*24*t2[1] + 24*t2[2] + t2[3];

		int cloc1 = Tables.c4_to_cloc[idx1];
		int cloc2 = Tables.c4_to_cloc[idx2];
		int cloc1b = Tables.move_table_cenSTAGE2[cloc1][move_code];
		int cloc2b = Tables.move_table_cenSTAGE2[cloc2][move_code];
		int cbm1b = Tables.cloc_to_bm[cloc1b];
		int cbm2b = Tables.cloc_to_bm[cloc2b];
		int cenbm2 = cbm1b | cbm2b;
		j1 = 0;
		int bm4of8b = 0;
		for (i = 0; i < 24; ++i) {
			if ((cenbm2 & (1 << i)) != 0) {
				if ((cbm1b & (1 << i)) != 0) {
					bm4of8b |= (1 << j1);
				}
				++j1;
			}
		}

		int cen1 = Tables.bm4of8_to_70[bm4of8b];
		int cen2 = Tables.ebm2eloc[cenbm2];
		m_centerFB = 70*cen2 + cen1;
		m_edge = Tables.move_table_edgeSTAGE2[m_edge][move_code];
	}

	public void do_whole_cube_move (int whole_cube_move){
		switch (whole_cube_move) {
		case 1:	//U u d' D'
			do_move (0);
			do_move (3);
			do_move (7);
			do_move (10);
			break;
		case 2:	//F2 f2 b2 B2
			do_move (16);
			do_move (19);
			do_move (20);
			do_move (23);
			break;
		case 3:	//L2 l2 r2 R2
			do_move (12);
			do_move (13);
			do_move (14);
			do_move (15);
			break;
		default: //case 0
			break;
		}
	}

	public void convert_to_std_cube (CubeState result_cube){
		int i;
		Face t6[4];
		int cenbm = Tables.eloc2ebm[m_centerFB/70];
		int cenbm4of8 = Tables.bm4of8[m_centerFB % 70];
		int udlr = 0;
		int pos4of8 = 0;
		for (i = 0; i < 24; ++i) {
			if ((cenbm & (1 << i)) == 0) {
				result_cube.m_cen[i] = udlr++/4;
			} else {
				if ((cenbm4of8 & (1 << pos4of8++)) == 0)
					result_cube.m_cen[i] = 5;
				else
					result_cube.m_cen[i] = 4;
			}
		}
		for (i = 0; i < 8; ++i)
			result_cube.m_cor[i] = i;

		int edgeFbm = bm4of8[init_cube.m_edge / 6];
		Constants.perm_n_unpack (4, m_edge % 6, t6, 0);
		for (i = 0; i < 16; ++i)
			result_cube.m_edge[i] = i;

		int f = 16;
		int b = 0;
		for (i = 0; i < 8; ++i) {
			if ((edgeFbm & (1 << i)) == 0) {
				result_cube.m_edge[16 + i] = 20 + t6[b++];
			} else {
				result_cube.m_edge[16 + i] = f++;
			}
		}
	}

	public int prune_funcEDGCEN_STAGE2 (){
		int clocf = Tables.stage2_cen_to_cloc4sf (cube2.m_centerFB);
		int clocb = Tables.stage2_cen_to_cloc4sb (cube2.m_centerFB);
		int d1 = Tables.get_dist_4bit (Constants.N_STAGE2_EDGE_CONFIGS*clocf + m_edge, prune_table_edgcen2);
		int d2 = Tables.get_dist_4bit (Constants.N_STAGE2_EDGE_CONFIGS*clocb + m_edge, prune_table_edgcen2);
		if (d2 >= d1) {
			return d2;
		}
		return d1;
	}

}
