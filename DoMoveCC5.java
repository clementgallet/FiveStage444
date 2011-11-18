package fivestage444;

public final class DoMoveCC5 implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		int cen = idx/Constants.N_SQS_CORNER_PERM;
		int cp = idx % Constants.N_SQS_CORNER_PERM;
		int cp96 = Tables.squares_move_corners (cp, move_code);
		int cen0 = cen % 12;
		int cen1 = (cen/12) % 12;
		int cen2 = cen/(12*12); 
		int cen12x12x12 = Tables.squares_move_centers (cen0, move_code, 0) +
			12*Tables.squares_move_centers (cen1, move_code, 1) +
			12*12*Tables.squares_move_centers (cen2, move_code, 2);
		return Constants.N_SQS_CORNER_PERM*cen12x12x12 + cp96;
	}
}
