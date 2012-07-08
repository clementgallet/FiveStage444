package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage4EdgCor extends Pruning {

	PruningStage4EdgCor(){

		num_positions = (long)(N_STAGE4_SYMEDGES*N_STAGE4_CORNERS);
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
		set_dist(0, 3);
		back_dist = 11;
	}

	long do_move (long idx, int move){
		int cor = (int)(idx % N_STAGE4_CORNERS);
		int edge = (int)(idx / N_STAGE4_CORNERS);

		int newEdge = Tables.moveEdge4[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		cor = Tables.moveCorner4[cor][move];
		cor = Tables.conjCorner4[cor][sym];
		return edgeRep*N_STAGE4_CORNERS + cor;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int edge = (int)(idx / N_STAGE4_CORNERS);
		int cor = (int)(idx % N_STAGE4_CORNERS);
		int symI = 0;
		int syms = Tables.hasSymEdgeSTAGE4[edge];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short cor2 = Tables.conjCorner4[cor][symI];
				set_dist (edge*N_STAGE4_CORNERS + cor2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
