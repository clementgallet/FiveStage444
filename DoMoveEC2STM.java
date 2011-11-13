package fivestage444;

public class DoMoveEC2STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		int edg = idx % Constants.N_STAGE2_EDGE_CONFIGS;
		int cen = idx / Constants.N_STAGE2_EDGE_CONFIGS;
		return Constants.N_STAGE2_EDGE_CONFIGS*Tables.move_table_cenSTAGE2[cen][move_code] + Tables.move_table_edgeSTAGE2[edg][move_code];
	}
}
