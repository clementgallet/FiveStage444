package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;

public final class PruningStage3 extends Pruning {

	void init (){
		int i;
		fname = new File( tables_path, "stage3_"+METRIC_STR+"_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = N_STAGE3_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = (long)(N_STAGE3_SYMCENTER_CONFIGS*N_STAGE3_EDGE_CONFIGS)*N_STAGE3_EDGE_PAR;
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i) {
			set_dist( (long)(stage3_solved_sym_centers[i]*N_STAGE3_EDGE_CONFIGS + 494L)*N_STAGE3_EDGE_PAR + 0, 3);
		}
		back_dist = 14;
	}

	long do_move (long idx, int move){
		int par = (int)(idx & 0x1);
		long rest = idx >> 1;
		int cen = (int)(rest / N_STAGE3_EDGE_CONFIGS);
		short edge = (short)(rest % N_STAGE3_EDGE_CONFIGS);

		int newCen = Tables.move_table_symCenterSTAGE3[cen][move];
		int cenRep = newCen >> 3;
		int sym = newCen & 0x7;

		edge = Tables.move_table_edgeSTAGE3[edge][move];
		edge = Tables.move_table_edge_conjSTAGE3[edge][sym];
		if( stage3_move_parity[move] )
			par = 1 - par;

		return ((long)(cenRep*N_STAGE3_EDGE_CONFIGS + edge))*N_STAGE3_EDGE_PAR + par;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int par = (int)(idx & 0x1);
		long rest = idx >> 1;
		int cen = (int)(rest / N_STAGE3_EDGE_CONFIGS);
		short edge = (short)(rest % N_STAGE3_EDGE_CONFIGS);

		int symI = 0;
		int syms = Tables.hasSymCenterSTAGE3[cen];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short edge2 = Tables.move_table_edge_conjSTAGE3[edge][symI];
				set_dist (((long)(cen*N_STAGE3_EDGE_CONFIGS + edge2))*N_STAGE3_EDGE_PAR + par, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
