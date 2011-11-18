package fivestage444;

public final class CubeStage4 {

	public byte m_centerUD; //center coordinate (70)
	public short m_corner; //corner coordinate	(420)
	public int m_edge; //edge coordinate (420*420)

	public static byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };

	public static byte[] prune_table_cencor4 = new byte[Constants.N_STAGE4_CORNER_CONFIGS*Constants.N_STAGE4_CENTER_CONFIGS/2];
	public static byte[] prune_table_edgcen4 = new byte[Constants.N_STAGE4_EDGE_CONFIGS*Constants.N_STAGE4_CENTER_CONFIGS/2];

	public void init (){
		m_edge = 0;
		m_corner = 0;
		m_centerUD = 0;
	}

	public void do_move (int move_code){
		m_centerUD = Tables.move_table_cenSTAGE4[m_centerUD][move_code];
		m_corner = Tables.move_table_cornerSTAGE4[m_corner][move_code];
		m_edge = Tables.move_table_edgeSTAGE4[m_edge][move_code];
	}

	public boolean is_solved (){
		int i;

		if (m_corner != 0) {
			return false;	//not solved if wrong corner value
		}
		if (m_edge != 0) {
			return false;	//not solved if wrong edge value
		}
		for (i = 0; i < Constants.STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i)
			if (m_centerUD == Tables.bm4of8_to_70[Constants.stage4_solved_centers_bm[i]])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	public void convert_to_std_cube (CubeState result_cube){
		int i;
		byte[] t6 = new byte[4];
		byte[] t8 = new byte[8];
		//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.

		int edge = Tables.stage4_edge_rep_table[m_edge];
		Tables.lrfb_to_cube_state (edge, result_cube);	//note: initializes result_cube! so we do this first
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

	public int prune_funcCENCOR_STAGE4 (){
		int idx = Constants.N_STAGE4_CENTER_CONFIGS*m_corner + m_centerUD;
		return Constants.get_dist_4bit (idx, prune_table_cencor4);
	}

	public int prune_funcEDGCEN_STAGE4 (){
		int idx = Constants.N_STAGE4_CENTER_CONFIGS*m_edge + m_centerUD;
		return Constants.get_dist_4bit (idx, prune_table_edgcen4);
	}

}
