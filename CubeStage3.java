package fivestage444;

public final class CubeStage3 {

	public static boolean stage3_move_parity[] = {
		false, false, false, false,
		false, false, false, false,
		false, false, false, false,
		false, true,  true,  false,
		false, true,  true,  false
	};

	public int m_centerLR; // (900900)
	public int m_sym_centerLR; // (113330)
	public short m_edge; //edge coordinate (12870)
	public boolean m_edge_odd; //odd parity of edges

	public static byte[] prune_table_cen3 = new byte[Constants.N_STAGE3_CENTER_CONFIGS/2];
	public static byte[] prune_table_edg3 = new byte[Constants.N_STAGE3_EDGE_CONFIGS*Constants.N_STAGE3_EDGE_PAR/2];


	public void init (){
		m_edge = 494;
		m_centerLR = 900830;
		m_sym_centerLR = 906488;
		m_edge_odd = false;	
	}
/*
	public void do_move_slow (int move_code){
		boolean par = m_edge_odd;
		if (stage3_move_parity[move_code]) {
			par = ! par;
		}
		CubeState cube1 = new CubeState();
		convert_to_std_cube(cube1);
		cube1.do_move (Constants.stage3_slice_moves[move_code]);
		cube1.convert_to_stage3 (this);
		m_edge_odd = par;
	}
*/
	public void do_move (int move_code){
		m_centerLR = Tables.move_table_cenSTAGE3[m_centerLR][move_code];
		m_edge = Tables.move_table_edgeSTAGE3[m_edge][move_code];
		if (stage3_move_parity[move_code]) {
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

	public boolean is_solved ()
	{
		int i;	
		if (m_edge_odd)
			return false;	//not solved if odd edge parity

		if (m_edge != 494)
			return false;	//not solved if wrong edge value

		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_CENTER_CONFIGS; ++i)
			if (m_centerLR == Constants.stage3_solved_centers[i])
				return true;	//If we found a matching center value, then it is solved.

		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i)
			if (( m_sym_centerLR >> 3 ) == Constants.stage3_solved_sym_centers[i])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	public void convert_centers_to_std_cube (CubeState result_cube){
		int i;
		int cenbm = Tables.eloc2e16bm[m_centerLR/70];
		int cenbm4of8 = Tables.bm4of8[m_centerLR % 70];
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

	public void convert_to_std_cube (CubeState result_cube){
		int i;
		for (i = 0; i < 8; ++i) {
			result_cube.m_cor[i] = (byte)i;
		}
		convert_centers_to_std_cube( result_cube );
		convert_edges_to_std_cube( result_cube );
	}

	public int prune_funcCEN_STAGE3 (){
		return Constants.get_dist_4bit (m_centerLR, prune_table_cen3);
	}

	public int prune_funcEDGE_STAGE3 (){
		int idx = m_edge;
		if (m_edge_odd) {
			idx += Constants.N_STAGE3_EDGE_CONFIGS;
		}
		return Constants.get_dist_4bit (idx, prune_table_edg3);
	}


}

