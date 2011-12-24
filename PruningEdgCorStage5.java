package fivestage444;

import java.io.File;

public final class PruningEdgCorStage5 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage5_edgcor_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_SQMOVES;

		// Creation of the pruning table.
		num_positions = Constants.N_SQS_SYMEDGE_PERM*Constants.N_SQS_CORNER_PERM;
		int n = num_positions/4 + 1;
		CubeSqsCoord.prune_table_edgcor = new byte[n];
		ptable = CubeSqsCoord.prune_table_edgcor;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		set_dist(21616*Constants.N_SQS_CORNER_PERM+66, 3);
	}

	int do_move (int idx, int move){
		byte cor = (byte)(idx % Constants.N_SQS_CORNER_PERM);
		int edge = idx / Constants.N_SQS_CORNER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge % 48;
		int edgeRep = newEdge / 48;

		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][sym];
		return edgeRep*Constants.N_SQS_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist (idx, dist);

		int edge = idx / Constants.N_SQS_CORNER_PERM;
		byte cor = (byte)(idx % Constants.N_SQS_CORNER_PERM);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][symI];
				set_dist (edge*Constants.N_SQS_CORNER_PERM + cor2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
