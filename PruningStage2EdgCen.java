package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;

public final class PruningStage2EdgCen extends PruningFull {

	void init (){
		int i;
		fname = new File( datafiles_path, "stage2_edgcen_stm_prune.rbk" );

		// Definition of the allowed moves.
		num_moves = N_STAGE2_SLICE_MOVES;

		// Creation of the pruning table.
		num_positions = N_SYMCENTER_COMBO4*N_STAGE2_EDGE_CONFIGS;
		n_ptable = num_positions/2 + 1;
		ptable = new byte[n_ptable];
		for (i = 0; i < n_ptable; ++i) {
			ptable[i] = (byte)0xFF;
		}

		// Fill the (almost) solved states.
		for (i=0; i < STAGE2_NUM_SOLVED_SYMCENTER_CONFIGS; i++){
			set_dist_4bit(stage2_solved_symcenters[i]*N_STAGE2_EDGE_CONFIGS + 414, 0, ptable);
			count++;
			set_dist_4bit(stage2_solved_symcenters[i]*N_STAGE2_EDGE_CONFIGS + 0  , 0, ptable);
			count++;
			unique_count++;
		}
		back_dist = 9;
	}

	int do_move (int idx, int move){
		short edge = (short)(idx % N_STAGE2_EDGE_CONFIGS);
		short cen = (short)(idx / N_STAGE2_EDGE_CONFIGS);

		short newCen = Tables.move_table_symCenterSTAGE2[cen][move];
		int sym = newCen & 0xF;
		int cenRep = newCen >> 4;

		edge = Tables.move_table_edgeSTAGE2[edge][move];
		edge = Tables.move_table_edge_conjSTAGE2[edge][sym];
		return cenRep*N_STAGE2_EDGE_CONFIGS + edge;
	}

	void saveIdxAndSyms (int idx, int dist){
		set_dist_4bit (idx, dist, ptable);
		count++;
		short edge = (short)(idx % N_STAGE2_EDGE_CONFIGS);
		short cen = (short)(idx / N_STAGE2_EDGE_CONFIGS);
		int symI = 0;
		int syms = Tables.hasSymCenterSTAGE2[cen];
		while (syms != 0){
			if(( syms & 0x1 ) == 1 ){
				short edge2 = Tables.move_table_edge_conjSTAGE2[edge][symI];
				set_dist_4bit (cen*N_STAGE2_EDGE_CONFIGS + edge2, dist, ptable);
				count++;
			}
			symI++;
			syms >>= 1;
		}
	}
}
