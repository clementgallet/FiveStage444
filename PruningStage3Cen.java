package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage3Cen extends PruningFull {

	PruningStage3Cen(){

		// Creation of the pruning table.
		num_positions = N_STAGE3_SYMCENTERS<<1;
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
			ptable[stage3_solved_sym_centers[i]<<1] = 0;
			count++;
		}
		back_dist = 7;
	}

	int do_move (int idx, int move){
		int newCen = Tables.moveCenter3[idx>>1][move];
		int cenRep = newCen >>> 3;
                int par = (idx&1) ^ (( Constants.stage3_move_parity >>> move ) & 1 );

		return cenRep<<1 | par;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;
	}

}
