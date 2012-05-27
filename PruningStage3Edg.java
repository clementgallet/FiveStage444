package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;

public final class PruningStage3Edg extends PruningFull {

	void init (){
		int i;
		fname = new File( datafiles_path, "stage3_edg_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = N_STAGE3_EDGE_CONFIGS*N_STAGE3_EDGE_PAR;
		n_ptable = num_positions/2 + 1;
		ptable = new byte[n_ptable];
		for (i = 0; i < n_ptable; ++i) {
			ptable[i] = (byte)0xFF;
		}

		// Fill the solved states.
		set_dist_4bit( 494 << 1, 0, ptable);
		count++;
		back_dist = 7;
	}

	int do_move (int idx, int move){
		int edge = idx >> 1;
		int par = idx & 0x1;

		int newEdge = Tables.move_table_edgeSTAGE3[edge][move];
		if( Constants.stage3_move_parity[move] )
			par = 1 - par;
		return (newEdge << 1) + par;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist_4bit (idx, dist, ptable);
	}
}
