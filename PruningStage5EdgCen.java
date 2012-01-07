package fivestage444;

import java.io.File;

public final class PruningStage5EdgCen extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage5_edgcen_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_SQMOVES;

		// Creation of the pruning table.
		num_positions = (long)(Constants.N_SQS_SYMEDGE_PERM*Constants.N_SQS_CENTER_PERM);
		int n = (int)(num_positions/4 + 1);
		ptable = new byte[n];
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		set_dist(21616*Constants.N_SQS_CENTER_PERM+143, 3);
	}

	long do_move (long idx, int move){
		short cen = (short)(idx % Constants.N_SQS_CENTER_PERM);
		int edge = (int)(idx / Constants.N_SQS_CENTER_PERM);

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge % 48;
		int edgeRep = newEdge / 48;

		cen = Tables.move_table_cenSTAGE5[cen][move];
		cen = Tables.move_table_cen_conjSTAGE5[cen][sym];
		//if( idx == 20626457 ) System.out.println("Edge:"+edgeRep+"Idx:"+(edgeRep*Constants.N_SQS_CENTER_PERM + cen)+" - move:"+move);
		return edgeRep*Constants.N_SQS_CENTER_PERM + cen;
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		int edge = (int)(idx / Constants.N_SQS_CENTER_PERM);
		short cen = (short)(idx % Constants.N_SQS_CENTER_PERM);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE5[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				short cen2 = Tables.move_table_cen_conjSTAGE5[cen][symI];
				set_dist (edge*Constants.N_SQS_CENTER_PERM + cen2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}
}
