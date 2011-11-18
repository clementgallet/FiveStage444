package fivestage444;

public final class CubeStage1 {

	public short m_co; // corner orientation (2187)
	public int m_edge_ud_combo8; // (735471)

	public static byte[] prune_table_cor1 = new byte[(Constants.N_CORNER_ORIENT+1)/2];
	public static byte[] prune_table_edg1 = new byte[(Constants.N_EDGE_COMBO8+1)/2];

	public void init (){
		m_co = 0;
		m_edge_ud_combo8 = Constants.N_EDGE_COMBO8 - 1;
	}

	public void do_move (int move_code){
		m_edge_ud_combo8 = Tables.move_table_edgeSTAGE1[m_edge_ud_combo8][move_code];
		int fmc = Constants.basic_to_face[move_code];
		if (fmc >= 0)
			m_co = Tables.move_table_co[m_co][fmc];
	}

	public void do_whole_cube_move (int whole_cube_move){
		switch (whole_cube_move) {
			case 1:
				do_move (Constants.Uf);
				do_move (Constants.Us);
				do_move (Constants.Df3);
				do_move (Constants.Ds3);
			break;
			case 2:
				do_move (Constants.Ff);
				do_move (Constants.Fs);
				do_move (Constants.Bf3);
				do_move (Constants.Bs3);
				break;
			case 3:
				do_move (Constants.Lf);
				do_move (Constants.Ls);
				do_move (Constants.Rf3);
				do_move (Constants.Rs3);
				break;
			default: //case 0
				break;
		}
	}

	public boolean is_solved (){
		if (m_co == 0 && m_edge_ud_combo8 == 735470)
			return true;
		if (m_co == 1373 && m_edge_ud_combo8 == 722601)
			return true;
		if (m_co == 1906 && m_edge_ud_combo8 == 0)
			return true;
		return false;
	}

	public void convert_to_std_cube (CubeState result_cube)
	{
		int i;
		int ebm = Tables.eloc2ebm[m_edge_ud_combo8];
		byte lrfb = 0;
		byte ud = 16;
		for (i = 0; i < 24; ++i) {
			if ((ebm & (1 << i)) == 0) {
				result_cube.m_edge[i] = lrfb++;
			} else {
				result_cube.m_edge[i] = ud++;
			}
			result_cube.m_cen[i] = (byte)(i/4);
		}
		int orientc = m_co;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
			byte fo = (byte)(orientc % 3);
			result_cube.m_cor[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		result_cube.m_cor[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	public int prune_funcCOR_STAGE1 (){
		return Constants.get_dist_4bit (m_co, prune_table_cor1);
	}

	public int prune_funcEDGE_STAGE1 (){
		return Constants.get_dist_4bit (m_edge_ud_combo8, prune_table_edg1);
	}


}
