package fivestage444;

import java.io.File;

public final class PruningStage3 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage3_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE3_SLICE_MOVES;
		move_list = new int[3*num_moves];
		for (i = 0; i < num_moves; ++i) {
			move_list[3*i] = i;
			move_list[3*i+1] = -1;
			move_list[3*i+2] = -1;
		}

		// Creation of the pruning table.
		num_positions = Constants.N_STAGE3_SYMCENTER_CONFIGS;
		int n = num_positions/4 + 1;
		CubeStage3.prune_table = new byte[n];
		ptable = CubeStage3.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i) {
			set_dist( Constants.stage3_solved_sym_centers[i], 3);
		}
	}

	int do_move (int idx, int move){
		int newCen = Tables.move_table_symCenterSTAGE3[idx][move];
		int cenRep = newCen >> 3;

		return cenRep;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist (idx, dist);
	}

}
