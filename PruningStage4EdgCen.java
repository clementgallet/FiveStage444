package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage4EdgCen extends PruningFull {

	PruningStage4EdgCen(){

		// Creation of the pruning table.
		num_positions = N_STAGE4_SYMEDGES*N_STAGE4_CENTERS;
		ptable = new byte[num_positions];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE4_MOVES;

		for (i = 0; i < num_positions; ++i) {
			ptable[i] = -1;
		}

		// Fill the solved states.
		for (i = 0; i < stage4_solved_centers_bm.length; ++i)
			ptable[stage4_solved_centers_bm[i]] = 0;

		count++;
		back_dist = 11;
	}

	int do_move (int idx, int move){
		int cen = idx % N_STAGE4_CENTERS;
		int edge = idx / N_STAGE4_CENTERS;

		int newEdge = Tables.moveEdge4[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		cen = Tables.moveCenter4[cen][move];
		cen = Tables.conjCenter4[cen][sym];
		return edgeRep*N_STAGE4_CENTERS + cen;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;

		int cen = idx % N_STAGE4_CENTERS;
		int edge = idx / N_STAGE4_CENTERS;
		int symI = 0;
		int syms = Tables.hasSymEdgeSTAGE4[edge];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short cen2 = Tables.conjCenter4[cen][symI];
				ptable[edge*N_STAGE4_CENTERS + cen2] = (byte)dist;
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
