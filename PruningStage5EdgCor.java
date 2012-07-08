package cg.fivestage444;

import static cg.fivestage444.Constants.*;

import java.io.File;

public final class PruningStage5EdgCor extends PruningFull {

	PruningStage5EdgCor(){

		// Creation of the pruning table.
		num_positions = N_STAGE5_SYMEDGES*N_STAGE5_CORNERS;
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
		back_dist = 11;
	}

	int do_move (int idx, int move){
		int cor = idx % N_STAGE5_CORNERS;
		int edge = idx / N_STAGE5_CORNERS;

		int newEdge = Tables.moveEdge5[edge][move];
		int sym = newEdge & 0xFF;
		int edgeRep = newEdge >> 8;

		cor = Tables.moveCorner5[cor][move];
		cor = Tables.conjCorner5[cor][sym];
		return edgeRep*N_STAGE5_CORNERS + cor;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;

		int cor = idx % N_STAGE5_CORNERS;
		int edge = idx / N_STAGE5_CORNERS;
		for( int i=0; i < 4; i++){
			int symI = 0;
			long syms = Tables.hasSymEdgeSTAGE5[edge][i];
			while (syms != 0){
				if(( syms & 0x1L ) == 1 ){
					byte cor2 = Tables.conjCorner5[cor][(symI<<2)+i];
					ptable[edge*N_STAGE5_CORNERS + cor2] = (byte)dist;
					count++;
				}
				symI++;
				syms >>= 1;
			}
		}
	}
}
