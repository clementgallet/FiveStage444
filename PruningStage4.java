package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage4 extends Pruning {

	PruningStage4(){

		num_positions = N_STAGE4_SYMEDGES*N_STAGE4_CORNERS*N_STAGE4_CENTERS;
		n_packed = (int)(num_positions/5 + 1);
		ptable_packed = new byte[n_packed];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE4_MOVES;

		// Creation of the pruning table.
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < stage4_solved_centers_bm.length; ++i) {
			set_dist( stage4_solved_centers_bm[i], 3);
		}
		unique_count = 5;
		back_dist = ( METRIC == STM ) ? 12 : 13;
	}

	final long do_move (long idx, int move){
		int cen = (int)(idx % N_STAGE4_CENTERS);
		int rest = (int)(idx / N_STAGE4_CENTERS);
		int cor = rest % N_STAGE4_CORNERS;
		int edge = rest / N_STAGE4_CORNERS;
	
		int newEdge = Tables.moveEdge4[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		cen = Tables.moveCenter4[cen][move];
		cen = Tables.conjCenter4[cen][sym];

		cor = Tables.moveCorner4[cor][move];
		cor = Tables.conjCorner4[cor][sym];

		return (edgeRep*N_STAGE4_CORNERS + cor) * N_STAGE4_CENTERS + cen;
	}

	final void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int edge = (int) ( idx / ( N_STAGE4_CORNERS * N_STAGE4_CENTERS ));
		int syms = (int)Tables.hasSymEdgeSTAGE4[edge][0];
		if( syms == 0 ) return;

		int cen = (int)(idx % N_STAGE4_CENTERS);
		int rest = (int)(idx / N_STAGE4_CENTERS);
		int cor = rest % N_STAGE4_CORNERS;

		int symI = 0;
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				byte cen2 = Tables.conjCenter4[cen][symI];
				short cor2 = Tables.conjCorner4[cor][symI];
				set_dist ((edge*N_STAGE4_CORNERS + cor2) * N_STAGE4_CENTERS + cen2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}

}
