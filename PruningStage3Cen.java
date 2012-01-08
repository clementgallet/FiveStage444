package fivestage444;

import java.io.File;

public final class PruningStage3Cen extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage3_cen_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = Constants.N_STAGE3_SYMCENTER_CONFIGS;
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i) {
			set_dist( Constants.stage3_solved_sym_centers[i], 3);
		}
		back_dist = 7;
	}

	long do_move (long idx, int move){
		int newCen = Tables.move_table_symCenterSTAGE3[(int)idx][move];
		int cenRep = newCen >> 3;

		return cenRep;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);
	}

}
