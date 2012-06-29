package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage4EdgCen extends PruningFull {

	PruningStage4EdgCen(){

		// Creation of the pruning table.
		num_positions = N_STAGE4_SYMEDGE_CONFIGS*N_STAGE4_CENTER_CONFIGS;
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
		for (i = 0; i < STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i)
			ptable[Tables.bm4of8_to_70[stage4_solved_centers_bm[i]]] = 0;

		count++;
		back_dist = 11;
	}

	int do_move (int idx, int move){
		int cen = idx % N_STAGE4_CENTER_CONFIGS;
		int edge = idx / N_STAGE4_CENTER_CONFIGS;

		int newEdge = Tables.move_table_symEdgeSTAGE4[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		cen = Tables.move_table_cenSTAGE4[cen][move];
		cen = Tables.move_table_cen_conjSTAGE4[cen][sym];
		return edgeRep*N_STAGE4_CENTER_CONFIGS + cen;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;

		int cen = idx % N_STAGE4_CENTER_CONFIGS;
		int edge = idx / N_STAGE4_CENTER_CONFIGS;
		int symI = 0;
		int syms = Tables.hasSymEdgeSTAGE4[edge];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short cen2 = Tables.move_table_cen_conjSTAGE4[cen][symI];
				ptable[edge*N_STAGE4_CENTER_CONFIGS + cen2] = (byte)dist;
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
