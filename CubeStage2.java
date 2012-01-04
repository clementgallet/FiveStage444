package fivestage444;

public final class CubeStage2 {

	public short m_centerFB; //
	public short m_centerF; //
	public short m_centerB; //
	public short m_edge; //edge coordinate (420)

	public static byte[] prune_table_edgcen2 = new byte[Constants.N_CENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS/2];

	public void init (){
		m_edge = 0;
		m_centerF = 0;
		m_centerB = 0;
	}

	public boolean is_solved (){
		int i;
		if (m_edge == 0) {
			if (m_centerF < Constants.stage2_solved_fb_centers[0] || m_centerF > Constants.stage2_solved_fb_centers[Constants.STAGE2_NUM_SOLVED_FB_CENTER_CONFIGS-1]
			 || m_centerB < Constants.stage2_solved_fb_centers[0] || m_centerB > Constants.stage2_solved_fb_centers[Constants.STAGE2_NUM_SOLVED_FB_CENTER_CONFIGS-1])
				return false;
			for (i=0; i < Constants.STAGE2_NUM_SOLVED_FB_CENTER_CONFIGS; i++)
				if ((m_centerF == Constants.stage2_solved_fb_centers[i]) && (m_centerB == Constants.stage2_solved_fb_centers[Constants.STAGE2_NUM_SOLVED_FB_CENTER_CONFIGS-i-1]))
					return true;
		} else if (m_edge == 414) {
			if (m_centerF < Constants.stage2_solved_rl_centers[0] || m_centerF > Constants.stage2_solved_rl_centers[Constants.STAGE2_NUM_SOLVED_RL_CENTER_CONFIGS-1]
			 || m_centerB < Constants.stage2_solved_rl_centers[0] || m_centerB > Constants.stage2_solved_rl_centers[Constants.STAGE2_NUM_SOLVED_RL_CENTER_CONFIGS-1])
				return false;
			for (i=0; i < Constants.STAGE2_NUM_SOLVED_RL_CENTER_CONFIGS; i++)
				if ((m_centerF == Constants.stage2_solved_rl_centers[i]) && (m_centerB == Constants.stage2_solved_rl_centers[Constants.STAGE2_NUM_SOLVED_RL_CENTER_CONFIGS-i-1]))
					return true;
		}
		return false;
	}

	public void do_move (int move_code){
		m_centerF = Tables.move_table_cenSTAGE2[m_centerF][move_code];
		m_centerB = Tables.move_table_cenSTAGE2[m_centerB][move_code];
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

	public void convert_centers_to_std_cube (CubeState result_cube){
		int i;
		int cbmf = Tables.cloc_to_bm[m_centerF];
		int cbmb = Tables.cloc_to_bm[m_centerB];
		int udlrf = 0;
		for (i = 0; i < 24; ++i) {
			if ((cbmb & (1 << i)) == 0) {
				if ((cbmf & (1 << i)) == 0) {
					result_cube.m_cen[i] = (byte)(udlrf++/4);
				} else {
					result_cube.m_cen[i] = 4;
				}
			} else {
				result_cube.m_cen[i] = 5;
			}
		}
	}

	public void convert_edges_to_std_cube (CubeState result_cube){
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = Tables.bm4of8[m_edge / 6];
		Constants.perm_n_unpack (4, m_edge % 6, t6, 0);
		for (i = 0; i < 16; ++i)
			result_cube.m_edge[i] = (byte)i;

		byte f = 16;
		int b = 0;
		for (i = 0; i < 8; ++i) {
			if ((edgeFbm & (1 << i)) == 0) {
				result_cube.m_edge[16 + i] = (byte)(20 + t6[b++]);
			} else {
				result_cube.m_edge[16 + i] = f++;
			}
		}
	}

	public int prune_funcEDGCEN_STAGE2 (){
		int d1 = Constants.get_dist_4bit (Constants.N_STAGE2_EDGE_CONFIGS*m_centerF + m_edge, prune_table_edgcen2);
		int d2 = Constants.get_dist_4bit (Constants.N_STAGE2_EDGE_CONFIGS*m_centerB + m_edge, prune_table_edgcen2);
		if (d2 >= d1) {
			return d2;
		}
		return d1;
	}
}
