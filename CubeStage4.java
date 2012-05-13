package fivestage444;

public final class CubeStage4 {

	public byte m_centerUD; //center coordinate (70)
	public short m_corner; //corner coordinate	(420)
	public int m_sym_edge; //sym edge coordinate (5968*16)

	public static byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };

	public static PruningStage4 prune_table;

	public int get_idx (){
		return (((( m_sym_edge >> 4 ) * Constants.N_STAGE4_CORNER_CONFIGS ) + Tables.move_table_corner_conjSTAGE4[m_corner][m_sym_edge & 0xF] ) * Constants.N_STAGE4_CENTER_CONFIGS ) + Tables.move_table_cen_conjSTAGE4[m_centerUD][m_sym_edge & 0xF];
	}

	public int get_dist (){
		return prune_table.get_dist_packed(get_idx());
	}

	public int new_dist (int dist){
		return prune_table.new_dist(get_idx(), dist);
	}

	public void do_move (int move_code){
		m_centerUD = Tables.move_table_cenSTAGE4[m_centerUD][move_code];
		m_corner = Tables.move_table_cornerSTAGE4[m_corner][move_code];

		int sym = m_sym_edge & 0xF;
		int rep = m_sym_edge >> 4;

		int moveConj = Constants.stage4_inv_slice_moves[Symmetry.moveConjugate[Constants.stage4_slice_moves[move_code]][sym]];
		int newEdge = Tables.move_table_symEdgeSTAGE4[rep][moveConj];

		int newSym = newEdge & 0xF;
		int newRep = newEdge >> 4;

		m_sym_edge = ( newRep << 4 ) + Symmetry.symIdxMultiply[newSym][sym];
	}

	public boolean is_solved (){
		int i;

		if (m_corner != 0) {
			return false;	//not solved if wrong corner value
		}
		if ((m_sym_edge >> 4) != 0) {
			return false;	//not solved if wrong edge value
		}
		for (i = 0; i < Constants.STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i)
			if (m_centerUD == Tables.bm4of8_to_70[Constants.stage4_solved_centers_bm[i]])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	public void convert_corners_to_std_cube (CubeState result_cube){
		int i;
		byte[] t6 = new byte[4];
		byte[] t8 = new byte[8];
		//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		int cor_bm = Tables.bm4of8[m_corner / 6];
		Constants.perm_n_unpack (4, m_corner % 6, t6, 0);
		int a = 0;
		int b = 0;
		for (i = 0; i < 8; ++i) {
			if ((cor_bm & (1 << i)) == 0) {
				t8[i] = (byte)(4 + t6[b++]);
			} else {
				t8[i] = (byte)a++;
			}
		}
		for (i = 0; i < 8; ++i) {
			result_cube.m_cor[sqs_to_std[i]] = sqs_to_std[t8[i]];
		}
	}

	public void convert_centers_to_std_cube (CubeState result_cube){
		int i;
		int cenbm = Tables.bm4of8[m_centerUD];
		for (i = 0; i < 8; ++i) {
			if ((cenbm & (1 << i)) == 0) {
				result_cube.m_cen[i] = 1;
			} else {
				result_cube.m_cen[i] = 0;
			}
		}
		for (i = 8; i < 24; ++i) {
			result_cube.m_cen[i] = (byte)(i/4);
		}
	}
}
