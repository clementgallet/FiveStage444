package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage1 extends Pruning {

	PruningStage1(){

		num_positions = N_STAGE1_CORNERS*N_STAGE1_SYMEDGES;
		n_packed = (int)(num_positions/5 + 1);
		ptable_packed = new byte[n_packed];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE1_MOVES;

		// Creation of the pruning table.
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist( 0*N_STAGE1_CORNERS + 1906, 3);
		back_dist = ( METRIC == STM ) ? 8 : 9;
	}

	final long do_move (long idx, int move){
		short co = (short)(idx % N_STAGE1_CORNERS);
		int edge = (int)(idx / N_STAGE1_CORNERS);
		
		int newEdge = Tables.moveEdge1[edge][move];
		int sym = newEdge & 0x3F;
		int edgeRep = newEdge >> 6;

		if (( stage1_slice_moves[move]/3)%3 != 1 )
			co = Tables.moveCorner1[co][basic_to_face[move]];
		co = Tables.conjCorner1[co][sym];

		return (long)(edgeRep*N_STAGE1_CORNERS + co);
	}

	final void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);
		short co = (short)(idx % N_STAGE1_CORNERS);
		int edge = (int)(idx / N_STAGE1_CORNERS);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE1[edge][0];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				short co2 = Tables.conjCorner1[co][symI];
				long idxx = edge*N_STAGE1_CORNERS + co2;
				if( get_dist(idxx) == 0 ){
					set_dist (idxx, dist);
				}
			}
			symI++;
			syms >>= 1;
		}

	}
}
