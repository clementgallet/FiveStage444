package fivestage444;

import java.io.File;
//import java.io.FileOutputStream;
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.BufferedInputStream;

public final class PruningStage1 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage1_" + metric_names[metric] + "_prune.rbk" );

		// Definition of the allowed moves.
		switch (metric) {
		case 0:
			num_moves = Constants.N_BASIC_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_BASIC_MOVES; ++i) {
				move_list[3*i] = i;
				move_list[3*i+1] = -1;
				move_list[3*i+2] = -1;
			}
			break;
		case 1:
			num_moves = Constants.N_STAGE1_TWIST_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_STAGE1_TWIST_MOVES; ++i) {
				move_list[3*i] = Constants.stage1_twist_moves[i][0];
				move_list[3*i+1] = Constants.stage1_twist_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		case 2:
			num_moves = Constants.N_STAGE1_BLOCK_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < Constants.N_STAGE1_BLOCK_MOVES; ++i) {
				move_list[3*i] = Constants.stage1_block_moves[i][0];
				move_list[3*i+1] = Constants.stage1_block_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		}

		// Creation of the pruning table.
		num_positions = Constants.N_CORNER_ORIENT*Constants.N_SYMEDGE_COMBO8;
		int n = num_positions/4 + 1;
		CubeStage1.prune_table = new byte[n];
		ptable = CubeStage1.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist( 46370*Constants.N_CORNER_ORIENT + 0   , 3);
		set_dist( 0    *Constants.N_CORNER_ORIENT + 1906, 3);

		count = 2;
	}

	int do_move (int idx, int move){
		short co = (short)(idx % Constants.N_CORNER_ORIENT);
		int edge = (idx / Constants.N_CORNER_ORIENT);
		
		int newEdge = Tables.move_table_symEdgeSTAGE1[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		int fmc = Constants.basic_to_face[move];
		if (fmc >= 0)
			co = Tables.move_table_co[co][fmc];
		co = Tables.move_table_co_conj[co][sym];

		return edgeRep*Constants.N_CORNER_ORIENT + co;
	}
}
