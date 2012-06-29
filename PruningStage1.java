package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage1 extends Pruning {

	PruningStage1(){

		num_positions = N_CORNER_ORIENT*N_SYMEDGE_COMBO8;
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
		set_dist( 0*N_CORNER_ORIENT + 1906, 3);
		back_dist = ( METRIC == STM ) ? 8 : 9;
	}

	final long do_move (long idx, int move){
		short co = (short)(idx % N_CORNER_ORIENT);
		int edge = (int)(idx / N_CORNER_ORIENT);
		
		int newEdge = Tables.move_table_symEdgeSTAGE1[edge][move];
		int sym = newEdge & 0x3F;
		int edgeRep = newEdge >> 6;

		if (( METRIC == FTM ) || (( stage1_inv_slice_moves[move] % 6 ) < 3 ))
			co = Tables.move_table_co[co][basic_to_face[move]];
		co = Tables.move_table_co_conj[co][sym];

		return (long)(edgeRep*N_CORNER_ORIENT + co);
	}

	final void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		short co = (short)(idx % N_CORNER_ORIENT);
		int edge = (int)(idx / N_CORNER_ORIENT);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE1[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				short co2 = Tables.move_table_co_conj[co][symI];
				set_dist (edge*N_CORNER_ORIENT + co2, dist);
			}
			symI++;
			syms >>= 1;
		}

	}
}
