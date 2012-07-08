package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage5EdgCen extends Pruning {

	PruningStage5EdgCen(){

		num_positions = (long)(N_STAGE5_SYMEDGES*N_STAGE5_CENTERS);
		n_packed = (int)(num_positions/5 + 1);
		ptable_packed = new byte[n_packed];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE5_MOVES;

		// Creation of the pruning table.
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		back_dist = 11;
	}

	long do_move (long idx, int move){
		int cen = (int)(idx % N_STAGE5_CENTERS);
		int edge = (int)(idx / N_STAGE5_CENTERS);

		int newEdge = Tables.moveEdge5[edge][move];
		int sym = newEdge & 0xFF;
		int edgeRep = newEdge >> 8;

		cen = Tables.moveCenter5[cen][move];
		cen = Tables.conjCenter5[cen][sym];
		return edgeRep*N_STAGE5_CENTERS + cen;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int edge = (int)(idx / N_STAGE5_CENTERS);
		int cen = (int)(idx % N_STAGE5_CENTERS);
		for (int i=0; i < 4; i++){
			int symI = 0;
			long syms = Tables.hasSymEdgeSTAGE5[edge][i];
			while (syms != 0){
				if(( syms & 0x1L ) == 1 ){
					short cen2 = Tables.conjCenter5[cen][(symI<<2)+i];
					set_dist (edge*N_STAGE5_CENTERS + cen2, dist);
				}
				symI++;
				syms >>= 1;
			}
		}
	}
}
