package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage5EdgCor extends PruningFull {

	PruningStage5EdgCor(){

		// Creation of the pruning table.
		num_positions = N_STAGE5_SYMEDGE_PERM*N_STAGE5_CORNER_PERM;
		ptable = new byte[num_positions];

	}

	void init (){
		int i;

		// Definition of the allowed moves.
		num_moves = N_STAGE5_MOVES;

		for (i = 0; i < num_positions; ++i) {
			ptable[i] = -1;
		}

		// Fill the solved states.
		ptable[0] = 0;
		count++;
		ptable[21616*Constants.N_STAGE5_CORNER_PERM+66] = 0;
		count++;
		back_dist = 11;
	}

	int do_move (int idx, int move){
		int cor = idx % N_STAGE5_CORNER_PERM;
		int edge = idx / N_STAGE5_CORNER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge & 0x3F;
		int edgeRep = newEdge >> 6;

		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][sym];
		return edgeRep*N_STAGE5_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;

		int cor = idx % N_STAGE5_CORNER_PERM;
		int edge = idx / N_STAGE5_CORNER_PERM;
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][symI];
				ptable[edge*N_STAGE5_CORNER_PERM + cor2] = (byte)dist;
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
