package fivestage444;

public final class CubeStage3 {

	public int m_sym_centerLR; // (113330)
	public short m_edge; //edge coordinate (12870)
	public boolean m_edge_odd; //odd parity of edges

	public static PruningStage3Cen prune_table_cen;
	public static PruningStage3Edg prune_table_edg;
	public static PruningStage3 prune_table;


	public void init (){
		m_edge = 494;
		m_sym_centerLR = 906488;
		m_edge_odd = false;	
	}

	public int get_dist_cen (){
		int idx = m_sym_centerLR >> 3;
		return prune_table_cen.get_dist(idx);
	}

	public int get_dist_edg (){
		int idx = (m_edge << 1);
		if( m_edge_odd ) idx++;
		return prune_table_edg.get_dist(idx);
	}

	public int get_dist (){
		long idx = (((long)(m_sym_centerLR >> 3))*Constants.N_STAGE3_EDGE_CONFIGS + Tables.move_table_edge_conjSTAGE3[m_edge][m_sym_centerLR & 0x7])<<1;
		if (m_edge_odd) idx++;
		return prune_table.get_dist(idx);
	}

	public void do_move (int move_code){
		m_edge = Tables.move_table_edgeSTAGE3[m_edge][move_code];
		if (Constants.stage3_move_parity[move_code]) {
			m_edge_odd = ! m_edge_odd;
		}
		int sym = m_sym_centerLR & 0x7;
		int rep = m_sym_centerLR >> 3;

		int moveConj = Constants.stage3_inv_slice_moves[Symmetry.moveConjugate[Constants.stage3_slice_moves[move_code]][sym]];
		int newCen = Tables.move_table_symCenterSTAGE3[rep][moveConj];

		int newSym = newCen & 0x7;
		int newRep = newCen >> 3;

		m_sym_centerLR = ( newRep << 3 ) + Symmetry.symIdxMultiply[newSym][sym];

	}

	public boolean centers_solved ()
	{
		for (int i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i)
			if (( m_sym_centerLR >> 3 ) == Constants.stage3_solved_sym_centers[i])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	public boolean edges_solved ()
	{
		if (m_edge_odd)
			return false;	//not solved if odd edge parity

		if (m_edge != 494)
			return false;	//not solved if wrong edge value

		return true;
	}

	public boolean is_solved ()
	{
		if (edges_solved())
			return centers_solved();
		return false;
	}

	public void convert_centers_to_std_cube (int center, CubeState result_cube){
		int i;
		int cenbm = Tables.eloc2e16bm[center/70];
		int cenbm4of8 = Tables.bm4of8[center % 70];
		int ud = 0;
		int pos4of8 = 0;
		for (i = 0; i < 16; ++i) {
			if ((cenbm & (1 << i)) == 0) {
				result_cube.m_cen[i] = (byte)(ud++/4);
			} else {
				if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
					result_cube.m_cen[i] = 3;
				} else {
					result_cube.m_cen[i] = 2;
				}
			}
		}
		for (i = 16; i < 24; ++i) {
			result_cube.m_cen[i] = (byte)(i/4);
		}
	}

	public void convert_edges_to_std_cube (CubeState result_cube){
		int i;
		int edge_bm = Tables.eloc2e16bm[m_edge];
		byte e0 = 0;
		byte e1 = 4;
		for (i = 0; i < 16; ++i) {
			if ((edge_bm & (1 << i)) != 0) {
				result_cube.m_edge[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				result_cube.m_edge[i] = e1++;
			}
		}
		for (i = 16; i < 24; ++i) {
			result_cube.m_edge[i] = (byte)i;
		}
	}
}

