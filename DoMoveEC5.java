package fivestage444;

public class DoMoveEC5 implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		int ep96x96x96 = idx/Constants.N_SQS_CORNER_PERM;
		int cp = idx % Constants.N_SQS_CORNER_PERM;
		int cp96 = Tables.squares_move_corners (cp, move_code);
		int ep0 = ep96x96x96%96;
		int ep1 = (ep96x96x96/96) % 96;
		int ep2 = ep96x96x96/(96*96);
		ep96x96x96 = Tables.squares_move_edges (ep0, move_code, 0) +
			96*Tables.squares_move_edges (ep1, move_code, 1) +
			96*96*Tables.squares_move_edges (ep2, move_code, 2);
		return Constants.N_SQS_CORNER_PERM*ep96x96x96 + cp96;
	}
}
