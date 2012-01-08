package fivestage444;

import java.io.File;

public final class PruningStage3Edg extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage3_edg_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = Constants.N_STAGE3_EDGE_CONFIGS*Constants.N_STAGE3_EDGE_PAR;
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist( 494 << 1, 3);
		back_dist = 7;
	}

	long do_move (long idx, int move){
		int edge = (int)(idx >> 1);
		int par = (int)(idx & 0x1);

		int newEdge = Tables.move_table_edgeSTAGE3[edge][move];
		if( Constants.stage3_move_parity[move] )
			par = 1 - par;
		return (newEdge << 1) + par;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);
	}
}
