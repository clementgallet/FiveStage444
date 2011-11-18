package fivestage444;

public final class DoMoveCC4STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		int centerUD = Tables.move_table_cenSTAGE4[idx % Constants.N_STAGE4_CENTER_CONFIGS][move_code];
		int corner = Tables.move_table_cornerSTAGE4[idx / Constants.N_STAGE4_CENTER_CONFIGS][move_code];
		return Constants.N_STAGE4_CENTER_CONFIGS*corner + centerUD;
	}
}
