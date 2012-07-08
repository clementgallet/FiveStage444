package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage2EdgCen extends PruningFull {

	PruningStage2EdgCen(){

		// Creation of the pruning table.
		num_positions = N_STAGE2_SYMCENTER*N_STAGE2_EDGES;
		ptable = new byte[num_positions];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE2_MOVES;

		for (i = 0; i < num_positions; ++i) {
			ptable[i] = -1;
		}

		// Fill the (almost) solved states.
		for (i=0; i < stage2_solved_symcenters.length; i++){
			ptable[stage2_solved_symcenters[i]*N_STAGE2_EDGES + 414] = 0;
			count++;
			ptable[stage2_solved_symcenters[i]*N_STAGE2_EDGES + 0  ] = 0;
			count++;
			unique_count++;
		}
		back_dist = 9;
	}

	int do_move (int idx, int move){
		int edge = idx % N_STAGE2_EDGES;
		int cen = idx / N_STAGE2_EDGES;

		short newCen = Tables.moveCenter2[cen][move];
		int sym = newCen & 0xF;
		int cenRep = newCen >> 4;

		edge = Tables.moveEdge2[edge][move];
		edge = Tables.conjEdge2[edge][sym];
		return cenRep*N_STAGE2_EDGES + edge;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;
		int edge = idx % N_STAGE2_EDGES;
		int cen = idx / N_STAGE2_EDGES;
		int symI = 0;
		int syms = Tables.hasSymCenterSTAGE2[cen];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short edge2 = Tables.conjEdge2[edge][symI];
				ptable[cen*N_STAGE2_EDGES + edge2] = (byte)dist;
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
