package fivestage444;

import java.io.File;

public final class PruningStage5EdgCor extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage5_edgcor_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_STAGE5_MOVES;

		// Creation of the pruning table.
		num_positions = (long)(Constants.N_STAGE5_SYMEDGE_PERM*Constants.N_STAGE5_CORNER_PERM);
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		set_dist(21616*Constants.N_STAGE5_CORNER_PERM+66, 3);
		back_dist = 11;
	}

	long do_move (long idx, int move){
		byte cor = (byte)(idx % Constants.N_STAGE5_CORNER_PERM);
		int edge = (int)(idx / Constants.N_STAGE5_CORNER_PERM);

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge % 48;
		int edgeRep = newEdge / 48;

		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][sym];
		return edgeRep*Constants.N_STAGE5_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		byte cor = (byte)(idx % Constants.N_STAGE5_CORNER_PERM);
		int edge = (int)(idx / Constants.N_STAGE5_CORNER_PERM);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][symI];
				set_dist (edge*Constants.N_STAGE5_CORNER_PERM + cor2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
