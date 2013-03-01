package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

public class StageSolver {

	Stage[] stage_list = new Stage[20];
	byte[] move_list = new byte[100];
	int n_moves;

	public StageSolver(Stage s){
		stage_list[0] = s;
	}

	public boolean search(int depth){
		return search(depth, 0, Moves.N_STAGE_MOVES);
	}

	private boolean search(int depth, int moves_done, int last_move){
		if( stage_list[moves_done].isSolved() ){
			if( depth == 0 && push() );
		}
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < n_moves; move++, mask >>>= 1) {
			if (( mask & 1L ) == 0)
				continue;
			stage_list[moves_done].moveTo( move, stage_list[moves_done+1] );
			if (stage_list[moves_done+1].pruning() > depth-1) continue;
			move_list[moves_done] = (byte)move;
			//min1_list = Math.min( min1_list, moves_done );
			if (search (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	boolean push(){
		return true;
	}
}
