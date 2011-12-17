package fivestage444;

import java.io.File;
//import java.io.FileOutputStream;
//import java.io.BufferedOutputStream;
//import java.io.FileInputStream;
//import java.io.BufferedInputStream;

public final class PruningStage4 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage4_" + metric_names[metric] + "_prune.rbk" );

		// Definition of the allowed moves.
		switch (metric) {
		case 0:
			num_moves = Constants.N_STAGE4_SLICE_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = i;
				move_list[3*i+1] = -1;
				move_list[3*i+2] = -1;
			}
			break;
		case 1:
			num_moves = Constants.N_STAGE4_TWIST_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = Constants.stage4_twist_moves[i][0];
				move_list[3*i+1] = Constants.stage4_twist_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		case 2:
			num_moves = Constants.N_STAGE4_BLOCK_MOVES;
			move_list = new int[3*num_moves];
			for (i = 0; i < num_moves; ++i) {
				move_list[3*i] = Constants.stage4_block_moves[i][0];
				move_list[3*i+1] = Constants.stage4_block_moves[i][1];
				move_list[3*i+2] = -1;
			}
			break;
		}

		// Creation of the pruning table.
		num_positions = Constants.N_STAGE4_SYMEDGE_CONFIGS*Constants.N_STAGE4_CORNER_CONFIGS*Constants.N_STAGE4_CENTER_CONFIGS;
		int n = num_positions/4 + 1;
		CubeStage4.prune_table = new byte[n];
		ptable = CubeStage4.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		for (i = 0; i < Constants.STAGE4_NUM_SOLVED_CENTER_CONFIGS; ++i) {
			set_dist( Tables.bm4of8_to_70[Constants.stage4_solved_centers_bm[i]], 3);
		}
	}

	int do_move (int idx, int move){
		byte cen = (byte)(idx % Constants.N_STAGE4_CENTER_CONFIGS);
		int rest = idx / Constants.N_STAGE4_CENTER_CONFIGS;
		short cor = (short) (rest % Constants.N_STAGE4_CORNER_CONFIGS);
		int edge = rest / Constants.N_STAGE4_CORNER_CONFIGS;
	
		int newEdge = Tables.move_table_symEdgeSTAGE4[edge][move];
		int sym = newEdge & 0xF;
		int edgeRep = newEdge >> 4;

		cen = Tables.move_table_cenSTAGE4[cen][move];
		cen = Tables.move_table_cen_conjSTAGE4[cen][sym];

		cor = Tables.move_table_cornerSTAGE4[cor][move];
		cor = Tables.move_table_corner_conjSTAGE4[cor][sym];

		return (edgeRep*Constants.N_STAGE4_CORNER_CONFIGS + cor) * Constants.N_STAGE4_CENTER_CONFIGS + cen;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist (idx, dist);

		byte cen = (byte)(idx % Constants.N_STAGE4_CENTER_CONFIGS);
		int rest = idx / Constants.N_STAGE4_CENTER_CONFIGS;
		short cor = (short) (rest % Constants.N_STAGE4_CORNER_CONFIGS);
		int edge = rest / Constants.N_STAGE4_CORNER_CONFIGS;

		int symI = 0;
		int syms = Tables.hasSymEdgeSTAGE4[edge];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				byte cen2 = Tables.move_table_cen_conjSTAGE4[cen][symI];
				short cor2 = Tables.move_table_corner_conjSTAGE4[cor][symI];
				set_dist ((edge*Constants.N_STAGE4_CORNER_CONFIGS + cor2) * Constants.N_STAGE4_CENTER_CONFIGS + cen2, dist);
			}
			symI++;
			syms >>= 1;
		}
	}

}
