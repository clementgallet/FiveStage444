package fivestage444;

public final class CubeStage1 {

	public short m_co; // corner orientation (2187)
	//public int m_edge_ud_combo8; // (735471)
	public int m_sym_edge_ud_combo8; // (46371)

	//public static byte[] prune_table_cor1 = new byte[(Constants.N_CORNER_ORIENT+1)/2];
	//public static byte[] prune_table_edg1 = new byte[(Constants.N_EDGE_COMBO8+1)/2];
	public static byte[] prune_table;

	public void init (){
		m_co = 0;
		//m_edge_ud_combo8 = Constants.N_EDGE_COMBO8 - 1;
		m_sym_edge_ud_combo8 = 741931;
	}

	public int get_dist (){
		int idx = Constants.N_CORNER_ORIENT * (m_sym_edge_ud_combo8 >> 4 ) + Tables.move_table_co_conj[m_co][m_sym_edge_ud_combo8 & 0xF];
		return (prune_table[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	/*
	public void computeSymEdge (){
		CubeState cube = new CubeState();
		int minEdge = 99999999;
		int minSym = 0;
		for (int sym=0; sym < Constants.N_SYM_STAGE1; sym++ ){
			convert_to_std_cube (cube);
			cube.conjugate(sym);
			int ebm = 0;
			for (int i = 0; i < 24; ++i) {
				if (cube.m_edge[i] >= 16) {
					ebm |= (1 << i);
				}
			}
			if( Tables.ebm2eloc[ebm] < minEdge){
				minEdge = Tables.ebm2eloc[ebm];
				minSym = sym;
			}
		}
		m_sym_edge_ud_combo8 = Symmetry.getRep(Tables.symEdgeToEdgeSTAGE1, minEdge)*Constants.N_SYM_STAGE1 + minSym;
	}
	*/

	public void do_move (int move_code){
		//m_edge_ud_combo8 = Tables.move_table_edgeSTAGE1[m_edge_ud_combo8][move_code];
		int fmc = Constants.basic_to_face[move_code];
		if (fmc >= 0)
			m_co = Tables.move_table_co[m_co][fmc];

		int sym = m_sym_edge_ud_combo8 & 0xF;
		int rep = m_sym_edge_ud_combo8 >> 4;

		int moveConj = Symmetry.moveConjugate[move_code][sym];
		int newEdge = Tables.move_table_symEdgeSTAGE1[rep][moveConj];

		int newSym = newEdge & 0xF;
		int newRep = newEdge >> 4;

		m_sym_edge_ud_combo8 = ( newRep << 4 ) + Symmetry.symIdxMultiply[newSym][sym];

		/*
		int bakSym = m_sym_edge_ud_combo8;
		computeSymEdge ();
		if( (bakSym) != (m_sym_edge_ud_combo8) )
			System.out.println("Error sym edge1. symTable:"+bakSym+"-symComp:"+m_sym_edge_ud_combo8);
		*/
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

	/*public boolean is_solved (){
		if (m_co == 0 && m_edge_ud_combo8 == 735470)
			return true;
		if (m_co == 1373 && m_edge_ud_combo8 == 722601)
			return true;
		if (m_co == 1906 && m_edge_ud_combo8 == 0)
			return true;
		return false;
	}*/
	
	public boolean is_solved (){
		if (m_co == 0 && (m_sym_edge_ud_combo8 >> 4) == 46370)
			return true;
		if (( m_sym_edge_ud_combo8 >> 4 ) == 0 && Tables.move_table_co_conj[m_co][m_sym_edge_ud_combo8 & 0xF] == 1906)
			return true;
		/*
		if (m_co == 1373 && (m_sym_edge_ud_combo8 & 0xFFFFFF2) == 2)
			return true;
		if (m_co == 1906 && (m_sym_edge_ud_combo8 & 0xFFFFFF2) == 0)
			return true;*/
		return false;
	}

	public void convert_to_std_cube (CubeState result_cube)
	{
		int i;

		/*
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
		*/

		
		int ebm = Tables.eloc2ebm[Tables.symEdgeToEdgeSTAGE1[m_sym_edge_ud_combo8>>4]];
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
		result_cube.conjugate(m_sym_edge_ud_combo8 & 0xF);
		

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

	/*
	public int prune_funcCOR_STAGE1 (){
		return Constants.get_dist_4bit (m_co, prune_table_cor1);
	}

	public int prune_funcEDGE_STAGE1 (){
		return Constants.get_dist_4bit (m_edge_ud_combo8, prune_table_edg1);
	}
	*/

}
