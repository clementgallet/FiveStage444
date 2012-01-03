package fivestage444;

import java.io.File;

public final class PruningStage1 extends Pruning {

	void init (){
		int i;
		fname = new File( Constants.datafiles_path, "stage1_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = Constants.N_BASIC_MOVES;

		// Creation of the pruning table.
		num_positions = Constants.N_CORNER_ORIENT*Constants.N_SYMEDGE_COMBO8;
		int n = (int)(num_positions/4 + 1);
		CubeStage1.prune_table = new byte[n];
		ptable = CubeStage1.prune_table;
		for (i = 0; i < n; ++i) {
			ptable[i] = 0;
		}

		// Fill the solved states.
		set_dist( 0*Constants.N_CORNER_ORIENT + 1906, 3);
	}

	long do_move (long idx, int move){
		short co = (short)(idx % Constants.N_CORNER_ORIENT);
		int edge = (int)(idx / Constants.N_CORNER_ORIENT);
		
		int newEdge = Tables.move_table_symEdgeSTAGE1[edge][move];
		int sym = newEdge & 0x3F;
		int edgeRep = newEdge >> 6;

		int fmc = Constants.basic_to_face[move];
		if (fmc >= 0)
			co = Tables.move_table_co[co][fmc];
		co = Tables.move_table_co_conj[co][sym];

		return (long)(edgeRep*Constants.N_CORNER_ORIENT + co);
	}

	void saveIdxAndSyms (long idx, int dist){
		set_dist (idx, dist);

		short co = (short)(idx % Constants.N_CORNER_ORIENT);
		int edge = (int)(idx / Constants.N_CORNER_ORIENT);
		int symI = 0;
		long syms = Tables.hasSymEdgeSTAGE1[edge];
		while (syms != 0){
			if(( syms & 0x1L ) == 1 ){
				short co2 = Tables.move_table_co_conj[co][symI];
				set_dist (edge*Constants.N_CORNER_ORIENT + co2, dist);
			}
			symI++;
			syms >>= 1;
		}

	}
}
