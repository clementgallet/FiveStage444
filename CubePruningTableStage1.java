package fivestage444;

public final class CubePruningTableStage1{

	public static int num_positions;
	public static byte[] ptable;
	public static int num_moves;
	public static int[] move_list;
	public static int num_solved;
	public static int[] psolved;
	public static int count;
	private static int metric = 0;

	public static void init (){
		int i;

		// Definition of the allowed moves.
		switch (metric) {
		case 0:
			num_moves = Constants.N_BASIC_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_BASIC_MOVES; ++i) {
				move_list[3*i] = i;
				move_list[3*i+1] = -1;
				move_list[3*i+2] = -1;
			}
			break;
		case 1:
			num_moves = Constants.N_STAGE1_TWIST_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_STAGE1_TWIST_MOVES; ++i) {
				move_list[3*i] = Constants.stage1_twist_moves[i][0];
				move_list[3*i+1] = Constants.stage1_twist_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		case 2:
			num_moves = Constants.N_STAGE1_BLOCK_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_STAGE1_BLOCK_MOVES; ++i) {
				move_list[3*i] = Constants.stage1_block_moves[i][0];
				move_list[3*i+1] = Constants.stage1_block_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		}

		// Creation of the pruning table.
		num_positions = 2187*46371;
		int n = num_positions/4 + (num_positions & 0x3);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist( 46370*2187+0, 3);
		set_dist( 0*2187+1906, 3);

		count = 2;
	}

	public static void set_metric(int m) {
		metric = m;
	}

	public static int get_dist (int idx){
		return (ptable[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	public static void set_dist (int idx, int value){
		ptable[idx>>2] |= (byte)(value << ((idx & 0x3) << 1));
	}

	public static int do_move (int idx, int move){
		short co = (short)(idx % 2187);
		int edge = (idx / 2187);
		
		int newEdge = Tables.move_table_symEdgeSTAGE1[edge][move];
		int sym = newEdge % 16;
		int edgeRep = newEdge / 16;

		int fmc = Constants.basic_to_face[move];
		if (fmc >= 0)
			co = Tables.move_table_co[co][fmc];
		co = Tables.move_table_co_conj[co][sym];

		return edgeRep*2187 + co;
	}

	public static void analyse (){
		System.out.println("Start stage 1 pruning table");
		int i;
		int idx;
		int dist;
		int max_dist = 20;	//MAX_DISTANCE;
		init ();
		int new_count = count;
		for (dist = 0; dist < max_dist && new_count > 0; ++dist) {
			System.out.println(" dist "+dist+": "+new_count+" positions.");
			int old_count = count;
			for (idx = 0; idx < num_positions; ++idx) {
				if (get_dist(idx) == (((dist + 2) % 3) + 1)){
					generate (idx, (dist % 3) + 1);
				}
			}
			new_count = count - old_count;
		}
		System.out.println("Finished stage 1 pruning table");
	}

	public static void generate (int idx, int dist)
	{
		int i, j;

		for (i = 0; i < num_moves; ++i) {
			int idx2 = do_move (idx, move_list[3*i]);
			for (j = 1; j < 3 && move_list[3*i + j] >= 0; ++j) {
				idx2 = do_move (idx2, move_list[3*i + j]);
			}
			if (get_dist(idx2) == 0){
				set_dist (idx2, dist);
				count++;
			}
		}
	}
}
