package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;

public final class PruningStage3Cen extends PruningFull {

	void init (){
		int i;
		fname = new File( datafiles_path, "stage3_cen_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = N_STAGE3_SYMCENTER_CONFIGS;
		n_ptable = num_positions/2 + 1;
		ptable = new byte[n_ptable];
		for (i = 0; i < n_ptable; ++i) {
			ptable[i] = (byte)0xFF;
		}

		// Fill the solved states.
		for (i = 0; i < STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i) {
			set_dist_4bit( stage3_solved_sym_centers[i], 0, ptable);
			count++;
		}
		back_dist = 7;
	}

	int do_move (int idx, int move){
		int newCen = Tables.move_table_symCenterSTAGE3[idx][move];
		int cenRep = newCen >> 3;

		return cenRep;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist_4bit (idx, dist, ptable);
		count++;
	}

}
