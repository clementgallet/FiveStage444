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
		back_dist = 11;
	}

	int do_move (int idx, int move){
		int cor = idx % N_STAGE5_CORNER_PERM;
		int edge = idx / N_STAGE5_CORNER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int allsym = newEdge & 0xFF;
		int edgeRep = newEdge >> 8;

		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][allsym];
		return edgeRep*N_STAGE5_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (int idx, int dist){
		ptable[idx] = (byte)dist;
		count++;

		int cor = idx % N_STAGE5_CORNER_PERM;
		int edge = idx / N_STAGE5_CORNER_PERM;
		for( int i=0; i < 4; i++){
			int symI = 0;
			long syms = Tables.hasSymEdgeSTAGE5[edge][i];
			while (syms != 0){
				if(( syms & 0x1L ) == 1 ){
					byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][(symI<<2)+i];
					ptable[edge*N_STAGE5_CORNER_PERM + cor2] = (byte)dist;
					count++;
				}
				symI++;
				syms >>= 1;
			}
		}
	}
}
