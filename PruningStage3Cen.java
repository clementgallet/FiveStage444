package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage3Cen extends PruningFull {

	PruningStage3Cen(){

		// Creation of the pruning table.
		num_positions = N_STAGE3_SYMCENTERS;
		ptable = new byte[num_positions];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE3_MOVES;

		for (i = 0; i < num_positions; ++i) {
			ptable[i] = -1;
		}

		// Fill the solved states.
		for (i = 0; i < stage3_solved_sym_centers.length; ++i) {
			ptable[stage3_solved_sym_centers[i]] = 0;
			count++;
		}
		back_dist = 7;
	}

	int do_move (int idx, int move){
		int newCen = Tables.moveCenter3[idx][move];
		int cenRep = newCen >> 4;

		return cenRep;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;
	}

}
