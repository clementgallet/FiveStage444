
public interface DoMove{
	public int do_move(int idx, int move_code); 
}

public class DoMoveE1STM implements DoMove{
	public int do_move(int idx, int move_code){
		CubeStage1 cube1;
		cube1.m_co = 0;
		cube1.m_edge_ud_combo8 = idx;
		cube1.do_move (move_code);
		return cube1.m_edge_ud_combo8;
	}
}

public class DoMoveC1STM implements DoMove{
	public int do_move(int idx, int move_code){
		CubeStage1 cube1;
		cube1.m_co = idx;
		cube1.m_edge_ud_combo8 = 0;
		cube1.do_move (move_code);
		return cube1.m_co;
	}
}

public class DoMoveEC2STM implements DoMove{
	public int do_move(int idx, int move_code){
		int edg = idx % Constants.N_STAGE2_EDGE_CONFIGS;
		int cen = idx / Constants.N_STAGE2_EDGE_CONFIGS;
		return Constants.N_STAGE2_EDGE_CONFIGS*Tables.move_table_cenSTAGE2[cen][move_code] + Tables.move_table_edgeSTAGE2[edg][move_code];
	}
}

public class DoMoveC3STM implements DoMove{
	public int do_move(int idx, int move_code){
		CubeStage3 cube1;
		cube1.m_centerLR = idx;
		cube1.m_edge = 0;
		cube1.m_edge_odd = false;
		cube1.do_move (move_code);
		return cube1.m_centerLR;
	}
}

public class DoMoveE3STM implements DoMove{
	public int do_move(int idx, int move_code){
		CubeStage3 cube1;
		cube1.m_centerLR = 0;
		cube1.m_edge = idx % Constants.N_STAGE3_EDGE_CONFIGS;
		cube1.m_edge_odd = (idx >= Constants.N_STAGE3_EDGE_CONFIGS);
		cube1.do_move (move_code);
		int x = cube1.m_edge;
		if (cube1.m_edge_odd) {
			x += Constants.N_STAGE3_EDGE_CONFIGS;
		}
		return x;
	}
}

public class DoMoveCC4STM implements DoMove{
	public int do_move(int idx, int move_code){
		int centerUD = Tables.move_table_cenSTAGE4[idx % Constants.N_STAGE4_CENTER_CONFIGS][move_code];
		int corner = Tables.move_table_cornerSTAGE4[idx / Constants.N_STAGE4_CENTER_CONFIGS][move_code];
		return Constants.N_STAGE4_CENTER_CONFIGS*corner + centerUD;
	}
}

public class DoMoveEC4STM implements DoMove{
	public int do_move(int idx, int move_code){
		CubeStage4 cube1;
		cube1.m_centerUD = idx % Constants.N_STAGE4_CENTER_CONFIGS;
		cube1.m_corner = 0;
		cube1.m_edge = idx / Constants.N_STAGE4_CENTER_CONFIGS;
		cube1.do_move (move_code);
		return Constants.N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
	}
}

public class DoMoveCC5 implements DoMove{
	public int do_move(int idx, int move_code){
		int cen = idx/Constants.N_SQS_CORNER_PERM;
		int cp = idx % Constants.N_SQS_CORNER_PERM;
		int cp96 = Tables.squares_move_corners (cp, sqs_move_code);
		int cen0 = cen % 12;
		int cen1 = (cen/12) % 12;
		int cen2 = cen/(12*12); 
		int cen12x12x12 = Tables.squares_move_centers (cen0, sqs_move_code, 0) +
			12*Tables.squares_move_centers (cen1, sqs_move_code, 1) +
			12*12*Tables.squares_move_centers (cen2, sqs_move_code, 2);
		return Constants.N_SQS_CORNER_PERM*cen12x12x12 + cp96;
	}
}

public class DoMoveEC5 implements DoMove{
	public int do_move(int idx, int move_code){
		int ep96x96x96 = idx/Constants.N_SQS_CORNER_PERM;
		int cp = idx % Constants.N_SQS_CORNER_PERM;
		int cp96 = Tables.squares_move_corners (cp, sqs_move_code);
		int ep0 = ep96x96x96%96;
		int ep1 = (ep96x96x96/96) % 96;
		int ep2 = ep96x96x96/(96*96);
		ep96x96x96 = Tables.squares_move_edges (ep0, sqs_move_code, 0) +
			96*Tables.squares_move_edges (ep1, sqs_move_code, 1) +
			96*96*Tables.squares_move_edges (ep2, sqs_move_code, 2);
		return Constants.N_SQS_CORNER_PERM*ep96x96x96 + cp96;
	}
}
