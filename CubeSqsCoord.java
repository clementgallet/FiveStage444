package fivestage444;

public final class CubeSqsCoord {

	public int m_ep96x96x96; // (884736) only used during initialisation by convert_edges_to_std_cube function.
	public int m_sym_ep96x96x96; // (1051584)
	public short m_cen12x12x12; // (1728)
	public byte m_cp96; // (96)

	public static byte[] prune_table_edgcen;
	public static byte[] prune_table_edgcor;
	

	public void init (){
		m_cen12x12x12 = 0;
		m_cp96 = 0;
		//m_ep96x96x96 = 0;
		m_sym_ep96x96x96 = 0;
	}

	public int get_dist_edgcen (){
		int idx = ( m_sym_ep96x96x96 / 48 ) * Constants.N_SQS_CENTER_PERM + Tables.move_table_cen_conjSTAGE5[m_cen12x12x12][m_sym_ep96x96x96 % 48];
		return (prune_table_edgcen[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	public int get_dist_edgcor (){
		int idx = ( m_sym_ep96x96x96 / 48 ) * Constants.N_SQS_CORNER_PERM + Tables.move_table_corner_conjSTAGE5[m_cp96][m_sym_ep96x96x96 % 48];
		return (prune_table_edgcor[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	public void do_move (int sqs_move_code){
		m_cen12x12x12 = Tables.move_table_cenSTAGE5[m_cen12x12x12][sqs_move_code];
		m_cp96 = Tables.move_table_cornerSTAGE5[m_cp96][sqs_move_code];

		int sym = m_sym_ep96x96x96 % 48;
		int rep = m_sym_ep96x96x96 / 48;

		int moveConj = Constants.stage5_inv_slice_moves[Symmetry.moveConjugate[Constants.stage5_slice_moves[sqs_move_code]][sym]];
		int newEdge = Tables.move_table_symEdgeSTAGE5[rep][moveConj];

		int newSym = newEdge % 48;
		int newRep = newEdge / 48;

		m_sym_ep96x96x96 = ( newRep * 48 ) + Symmetry.symIdxMultiply[newSym][sym];
	}

	public void do_whole_cube_move (int sqs_whole_cube_move){
		switch (sqs_whole_cube_move) {
		case 1:
			do_move (Constants.Uf2/3);
			do_move (Constants.Us2/3);
			do_move (Constants.Df2/3);
			do_move (Constants.Ds2/3);
			break;
		case 2:
			do_move (Constants.Ff2/3);
			do_move (Constants.Fs2/3);
			do_move (Constants.Bf2/3);
			do_move (Constants.Bs2/3);
			break;
		case 3:
			do_move (Constants.Lf2/3);
			do_move (Constants.Ls2/3);
			do_move (Constants.Rf2/3);
			do_move (Constants.Rs2/3);
			break;
		default: //case 0
			break;
		}
	}

	public boolean is_solved (){

		if (m_cen12x12x12 == 0 && m_cp96 == 0 && ( m_sym_ep96x96x96 / 48 ) == 0) {
			return true;
		}
		if (m_sym_ep96x96x96 / 48 == 21616 && Tables.move_table_corner_conjSTAGE5[m_cp96][m_sym_ep96x96x96 % 48] == 66 && Tables.move_table_cen_conjSTAGE5[m_cen12x12x12][m_sym_ep96x96x96 % 48] == 143)
			return true;
		return false;
	}

	public boolean edges_corners_solved (){

		if (m_cp96 == 0 && ( m_sym_ep96x96x96 / 48 ) == 0) {
			return true;
		}
		if (m_sym_ep96x96x96 / 48 == 21616 && Tables.move_table_corner_conjSTAGE5[m_cp96][m_sym_ep96x96x96 % 48] == 66)
			return true;
		return false;
	}

	public boolean edges_centers_solved (){

		if (m_cen12x12x12 == 0 && ( m_sym_ep96x96x96 / 48 ) == 0) {
			return true;
		}
		if (m_sym_ep96x96x96 / 48 == 21616 && Tables.move_table_cen_conjSTAGE5[m_cen12x12x12][m_sym_ep96x96x96 % 48] == 143)
			return true;
		return false;
	}

	public void convert_edges_to_std_cube (CubeState result_cube){
		int i;

		int ep1 = m_ep96x96x96 % 96;
		int ep2 = (m_ep96x96x96/96) % 96;
		int ep3 = m_ep96x96x96/(96*96);
		int rep = Tables.sqs_perm_to_rep[ep1/4];
		Constants.perm_n_unpack (4, ep1/4, result_cube.m_edge, 0);
		Constants.perm_n_unpack (4, Tables.sqs_rep_to_perm[rep][ep1 % 4], result_cube.m_edge, 4);
		rep = Tables.sqs_perm_to_rep[ep2/4];
		Constants.perm_n_unpack (4, ep2/4, result_cube.m_edge, 8);
		Constants.perm_n_unpack (4, Tables.sqs_rep_to_perm[rep][ep2 % 4], result_cube.m_edge, 12);
		rep = Tables.sqs_perm_to_rep[ep3/4];
		Constants.perm_n_unpack (4, ep3/4, result_cube.m_edge, 16);
		Constants.perm_n_unpack (4, Tables.sqs_rep_to_perm[rep][ep3 % 4], result_cube.m_edge, 20);
		for (i = 0; i < 24; ++i) {
			result_cube.m_edge[i] += 4*(i/4);
		}
	}

	private static byte sqs_to_std_cor[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
	private static byte sqs_to_std_cen[] = {
		0,  2,  3,  1,  6,  4,  5,  7,
		8, 10, 11,  9, 14, 12, 13, 15,
		16, 18, 19, 17, 22, 20, 21, 23
	};

	public void convert_to_std_cube (CubeState result_cube){
		int i;

		convert_edges_to_std_cube (result_cube);

		int rep = Tables.sqs_perm_to_rep[m_cp96/4];
		Constants.perm_n_unpack (4, m_cp96/4, result_cube.m_cor, 0);
		Constants.perm_n_unpack (4, Tables.sqs_rep_to_perm[rep][m_cp96 % 4], result_cube.m_cor, 4);
		for (i = 0; i < 8; ++i) {
			result_cube.m_cor[i] += (byte)(4*(i/4));
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		byte[] old_m_cor = new byte[8];
		System.arraycopy(result_cube.m_cor, 0, old_m_cor, 0, 8);
		for (i = 0; i < 8; ++i) {
			result_cube.m_cor[sqs_to_std_cor[i]] = sqs_to_std_cor[old_m_cor[i]];
		}

		int cen1 = m_cen12x12x12 % 12;
		int cen2 = (m_cen12x12x12/12) % 12;
		int cen3 = m_cen12x12x12/(12*12);
		squares_unpack_centers (cen1, cen2, cen3, result_cube);

		byte[] old_m_cen = new byte[24];
		System.arraycopy(result_cube.m_cen, 0, old_m_cen, 0, 24);
		for (i = 0; i < 24; ++i) {
			result_cube.m_cen[sqs_to_std_cen[i]] = (byte)(sqs_to_std_cen[4*old_m_cen[i]]/4);
		}
	}

	public void squares_unpack_centers (int cen1, int cen2, int cen3, CubeState result_cube){
		int i;
		int x = (Tables.squares_cen_map[cen1] << 16) | (Tables.squares_cen_map[cen2] << 8) | Tables.squares_cen_map[cen3];
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			result_cube.m_cen[i] = (byte) (2*(i/8) + ((x & b) == 0 ? 0 : 1));
			b >>= 1;
		}
	}
}
