package fivestage444;

public class CubeSqsCoord {

	public int m_ep96x96x96; // (884736)
	public short m_cen12x12x12; // (1728)
	public byte m_cp96; // (96)

	public static byte[] prune_table_cencor5 = new byte[Constants.N_SQS_CENTER_PERM*Constants.N_SQS_CORNER_PERM/2];
	public static byte[] prune_table_edgcor5 = new byte[Constants.N_SQS_EDGE_PERM*Constants.N_SQS_CORNER_PERM/2];

	public void init (){
		m_cen12x12x12 = 0;
		m_cp96 = 0;
		m_ep96x96x96 = 0;
	}

	public void do_move (int sqs_move_code){
		int cen = m_cen12x12x12;
		int ep = m_ep96x96x96;
		int ep0 = ep%96;
		int ep1 = (ep/96) % 96;
		int ep2 = ep/(96*96);
		m_ep96x96x96 = Tables.squares_move_edges (ep0, sqs_move_code, 0) +
			96*Tables.squares_move_edges (ep1, sqs_move_code, 1) +
			96*96*Tables.squares_move_edges (ep2, sqs_move_code, 2);
		m_cp96 = (byte)(Tables.squares_move_corners (m_cp96, sqs_move_code));
		int cen0 = cen % 12;
		int cen1 = (cen/12) % 12;
		int cen2 = cen/(12*12); 
		m_cen12x12x12 = (short)(Tables.squares_move_centers (cen0, sqs_move_code, 0) +
			12*Tables.squares_move_centers (cen1, sqs_move_code, 1) +
			12*12*Tables.squares_move_centers (cen2, sqs_move_code, 2));
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

		if (m_cen12x12x12 == 0 && m_cp96 == 0 && m_ep96x96x96 == 0) {
			return true;
		}
		if (m_cen12x12x12 == 1716 && m_cp96 == 29 && m_ep96x96x96 == 881885) {
			return true;
		}
		if (m_cen12x12x12 == 143 && m_cp96 == 66 && m_ep96x96x96 == 276450) {
			return true;
		}
		if (m_cen12x12x12 == 1595 && m_cp96 == 95 && m_ep96x96x96 == 611135) {
			return true;
		}
		return false;
	}

	public int prune_funcCENCOR_STAGE5 (){
		int idx = Constants.N_SQS_CORNER_PERM*m_cen12x12x12 + m_cp96;
		return Constants.get_dist_4bit (idx, prune_table_cencor5);
	}

	public int prune_funcEDGCOR_STAGE5 (){
		int idx = Constants.N_SQS_CORNER_PERM*m_ep96x96x96 + m_cp96;
		return Constants.get_dist_4bit (idx, prune_table_edgcor5);
	}

}
