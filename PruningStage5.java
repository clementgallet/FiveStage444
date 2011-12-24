package fivestage444;

import java.io.File;

public final class PruningStage5 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage5_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_SQMOVES;

		// Creation of the pruning table.
		num_positions = ((long)Constants.N_SQS_SYMEDGE_PERM)*Constants.N_SQS_CENTER_PERM*Constants.N_SQS_CORNER_PERM; // To avoid overflow.
		int n = (int)(num_positions/4 + 1);
		CubeSqsCoord.prune_table = new byte[n];
		ptable = CubeSqsCoord.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		set_dist((21616L*Constants.N_SQS_CENTER_PERM+143L)*Constants.N_SQS_CORNER_PERM+66L, 3);
	}

	long do_move (long idx, int move){
		byte cor = (byte)(idx % Constants.N_SQS_CORNER_PERM);
		int rest = (int)(idx / Constants.N_SQS_CORNER_PERM);
		short cen = (short)(rest % Constants.N_SQS_CENTER_PERM);
		int edge = rest / Constants.N_SQS_CENTER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge % 48;
		long edgeRep = (long)(newEdge / 48);

		cen = Tables.move_table_cenSTAGE5[cen][move];
		cen = Tables.move_table_cen_conjSTAGE5[cen][sym];
		cor = Tables.move_table_cornerSTAGE5[cor][move];
		cor = Tables.move_table_corner_conjSTAGE5[cor][sym];
		return ((long)(edgeRep*Constants.N_SQS_CENTER_PERM + cen))*Constants.N_SQS_CORNER_PERM + cor;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		byte cor = (byte)(idx % Constants.N_SQS_CORNER_PERM);
		int rest = (int)(idx / Constants.N_SQS_CORNER_PERM);
		short cen = (short)(rest % Constants.N_SQS_CENTER_PERM);
		int edge = rest / Constants.N_SQS_CENTER_PERM;
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				short cen2 = Tables.move_table_cen_conjSTAGE5[cen][symI];
				byte cor2 = Tables.move_table_corner_conjSTAGE5[cor][symI];
				set_dist (((long)(edge*Constants.N_SQS_CENTER_PERM + cen2))*Constants.N_SQS_CORNER_PERM + cor2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
