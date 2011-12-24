package fivestage444;

import java.io.File;

public final class PruningStage3 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage3_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = (long)(Constants.N_STAGE3_SYMCENTER_CONFIGS*Constants.N_STAGE3_EDGE_CONFIGS)*Constants.N_STAGE3_EDGE_PAR;
		int n = (int)(num_positions/4 + 1);
		CubeStage3.prune_table = new byte[n];
		ptable = CubeStage3.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i) {
			set_dist( (long)(Constants.stage3_solved_sym_centers[i]*Constants.N_STAGE3_EDGE_CONFIGS + 494L)*Constants.N_STAGE3_EDGE_PAR + 0, 3);
		}
	}

	long do_move (long idx, int move){
		int par = (int)(idx & 0x1);
		long rest = idx >> 1;
		int cen = (int)(rest / Constants.N_STAGE3_EDGE_CONFIGS);
		short edge = (short)(rest % Constants.N_STAGE3_EDGE_CONFIGS);

		int newCen = Tables.move_table_symCenterSTAGE3[cen][move];
		int cenRep = newCen >> 3;
		int sym = newCen & 0x7;

		edge = Tables.move_table_edgeSTAGE3[edge][move];
		edge = Tables.move_table_edge_conjSTAGE3[edge][sym];
		if( Constants.stage3_move_parity[move] )
			par = 1 - par;

		return ((long)(cenRep*Constants.N_STAGE3_EDGE_CONFIGS + edge))*Constants.N_STAGE3_EDGE_PAR + par;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int par = (int)(idx & 0x1);
		long rest = idx >> 1;
		int cen = (int)(rest / Constants.N_STAGE3_EDGE_CONFIGS);
		short edge = (short)(rest % Constants.N_STAGE3_EDGE_CONFIGS);

		int symI = 0;
		int syms = Tables.hasSymCenterSTAGE3[cen];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short edge2 = Tables.move_table_edge_conjSTAGE3[edge][symI];
				set_dist (((long)(cen*Constants.N_STAGE3_EDGE_CONFIGS + edge2))*Constants.N_STAGE3_EDGE_PAR + par, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
