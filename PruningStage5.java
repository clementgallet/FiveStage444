package fivestage444;

import java.io.File;

public final class PruningStage5 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage5_" + metric_names[metric] + "_prune.rbk" );

		// Definition of the allowed moves.
		switch (metric) {
		case 0:
			num_moves = Constants.N_SQMOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = i;
				move_list[3*i+1] = -1;
				move_list[3*i+2] = -1;
			}
			break;
		case 1:
			num_moves = Constants.N_SQ_TWIST_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = Constants.sq_twist_moves[i][0];
				move_list[3*i+1] = Constants.sq_twist_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		case 2:
			num_moves = Constants.N_SQ_BLOCK_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = Constants.sq_block_moves[i][0];
				move_list[3*i+1] = Constants.sq_block_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		}

		// Creation of the pruning table.
		num_positions = Constants.N_SQS_SYMEDGE_PERM*Constants.N_SQS_CENTER_PERM;
		int n = num_positions/4 + 1;
		CubeSqsCoord.prune_table = new byte[n];
		ptable = CubeSqsCoord.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist(0, 3);
		set_dist(21616*Constants.N_SQS_CENTER_PERM+143, 3);
	}

	int do_move (int idx, int move){
		short cen = (short)(idx % Constants.N_SQS_CENTER_PERM);
		int edge = idx / Constants.N_SQS_CENTER_PERM;

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][move];
		int sym = newEdge % 48;
		int edgeRep = newEdge / 48;

		cen = Tables.move_table_cenSTAGE5[cen][move];
		cen = Tables.move_table_cen_conjSTAGE5[cen][sym];
		//if( idx == 20626457 ) System.out.println("Edge:"+edgeRep+"Idx:"+(edgeRep*Constants.N_SQS_CENTER_PERM + cen)+" - move:"+move);
		return edgeRep*Constants.N_SQS_CENTER_PERM + cen;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist (idx, dist);

		int edge = idx / Constants.N_SQS_CENTER_PERM;
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
