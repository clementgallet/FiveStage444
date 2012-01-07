package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage1Solver extends StageSolver{

	private static long stage1_slice_moves_to_try [] = {
	0xFFFFFFFF8L, 0xFFFFFFFF8L, 0xFFFFFFFF8L, 0xFFFFFFB40L, 0xFFFFFFD80L, 0xFFFFFF6C0L, 0xFFFFFFC00L, 0xFFFFFFA00L, 0xFFFFFF600L, 0xFFFFFF000L, 0xFFFFFF000L, 0xFFFFFF000L,
	0xFFFFF8FFFL, 0xFFFFF8FFFL, 0xFFFFF8FFFL, 0xFFFB40FFFL, 0xFFFD80FFFL, 0xFFF6C0FFFL, 0xFFFC00FFFL, 0xFFFA00FFFL, 0xFFF600FFFL, 0xFFF000FFFL, 0xFFF000FFFL, 0xFFF000FFFL,
	0xFF8FFFFFFL, 0xFF8FFFFFFL, 0xFF8FFFFFFL, 0xB40FFFFFFL, 0xD80FFFFFFL, 0x6C0FFFFFFL, 0xC00FFFFFFL, 0xA00FFFFFFL, 0x600FFFFFFL, 0x000FFFFFFL, 0x000FFFFFFL, 0x000FFFFFFL,
	0xFFFFFFFFFL};

	private CubeStage1 cube = new CubeStage1();

	Stage1Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );
	}

	void importState(){
		ss.cube.convert_to_stage1 (cube);
	}

	public void run (){
		while (pullState()) {
			solve (cube, 0, N_BASIC_MOVES, cube.get_dist());
		}

		pushStopSignal();
		closePipes();
	}

	private boolean solve (CubeStage1 cube1, int moves_done, int move_state, int dist){
		//Statistics.addNode(1, depth);
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, j, dist2;
		if (dist == 0){
			if (cube1.is_solved ()) {
				goal = moves_done;
				pushState();
				Statistics.addLeaf(1, goal);
				return true; // true: take the first solution, false: take all solutions
			}
		}
		for (mov_idx = 0; mov_idx < N_BASIC_MOVES; ++mov_idx) {
			cube2.m_co = cube1.m_co;
			cube2.m_sym_edge_ud_combo8 = cube1.m_sym_edge_ud_combo8;
			if ((stage1_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist();
				if (((dist2+1) % 3) != dist) continue; // If distance is not lowered by 1, continue.
				move_list[moves_done] = (byte)mov_idx;
				if (solve (cube2, moves_done + 1, mov_idx, dist2)) return true;
			}
		}
		return false;
	}

	int rotateCube(CubeState cube, byte[] sol_move_list){

		int r3 = cube.m_cor[0] >> 3;
		switch (r3) {
		case 0:
			break;	//no whole cube rotation
		case 1:
			cube.do_move (Lf3);
			cube.do_move (Ls3);
			cube.do_move (Rs);
			cube.do_move (Rf);
			cube.do_move (Uf3);
			cube.do_move (Us3);
			cube.do_move (Ds);
			cube.do_move (Df);
			break;
		case 2:
			cube.do_move (Ff);
			cube.do_move (Fs);
			cube.do_move (Bs3);
			cube.do_move (Bf3);
			cube.do_move (Uf);
			cube.do_move (Us);
			cube.do_move (Ds3);
			cube.do_move (Df3);
			break;
		default:
			System.out.println ("Invalid cube rotation state.");
		}
		return r3;
	}
}
