package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;

public final class PruningStage5EdgCor extends PruningFull {

	void init (){
		int i;
		fname = new File( datafiles_path, "stage5_edgcor_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = N_STAGE5_MOVES;

		// Creation of the pruning table.
		num_positions = N_STAGE5_SYMEDGE_PERM*N_STAGE5_CORNER_PERM;
		n_ptable = num_positions/2 + 1;
		ptable = new byte[n_ptable];
		for (i = 0; i < n_ptable; ++i) {
			ptable[i] = (byte)0xFF;
		}

		// Fill the solved states.
		set_dist_4bit(0, 0, ptable);
		count++;
		set_dist_4bit(21616*Constants.N_STAGE5_CORNER_PERM+66, 0, ptable);
		count++;
		back_dist = 11;
	}

	int do_move (int idx, int move){
		byte cor = (byte)(idx % N_STAGE5_CORNER_PERM);
		int edge = idx / N_STAGE5_CORNER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge & 0x3F;
		int edgeRep = newEdge >> 6;

		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][sym];
		return edgeRep*N_STAGE5_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist_4bit (idx, dist, ptable);
		count++;

		byte cor = (byte)(idx % N_STAGE5_CORNER_PERM);
		int edge = idx / N_STAGE5_CORNER_PERM;
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][symI];
				set_dist_4bit (edge*N_STAGE5_CORNER_PERM + cor2, dist, ptable);
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
