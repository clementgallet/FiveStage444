package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage1Solver extends StageSolver{

	private static int n_moves_metric_stg1[] = { N_BASIC_MOVES, N_STAGE1_TWIST_MOVES, N_STAGE1_BLOCK_MOVES};

	private static long stage1_slice_moves_to_try [] = {
	0xFFFFFFFF8L, 0xFFFFFFFF8L, 0xFFFFFFFF8L, 0xFFFFFFB40L, 0xFFFFFFD80L, 0xFFFFFF6C0L, 0xFFFFFFC00L, 0xFFFFFFA00L, 0xFFFFFF600L, 0xFFFFFF000L, 0xFFFFFF000L, 0xFFFFFF000L,
	0xFFFFF8FFFL, 0xFFFFF8FFFL, 0xFFFFF8FFFL, 0xFFFB40FFFL, 0xFFFD80FFFL, 0xFFF6C0FFFL, 0xFFFC00FFFL, 0xFFFA00FFFL, 0xFFF600FFFL, 0xFFF000FFFL, 0xFFF000FFFL, 0xFFF000FFFL,
	0xFF8FFFFFFL, 0xFF8FFFFFFL, 0xFF8FFFFFFL, 0xB40FFFFFFL, 0xD80FFFFFFL, 0x6C0FFFFFFL, 0xC00FFFFFFL, 0xA00FFFFFFL, 0x600FFFFFFL, 0x000FFFFFFL, 0x000FFFFFFL, 0x000FFFFFFL,
	0xFFFFFFFFFL};

	private CubeStage1 cube = new CubeStage1();

	Stage1Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );

		stage_twist_list = new byte[] {
		Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
		Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
		Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
		};

		stage_block_list = new byte[] {
		Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
		Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
		Lf, Lf3, Lf2, Ls, Ls3, Ls2, Rf, Rf3, Rf2, Rs, Rs3, Rs2,
		Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
		Ff, Ff3, Ff2, Fs, Fs3, Fs2, Bf, Bf3, Bf2, Bs, Bs3, Bs2,
		Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
		};
	}

	void importState(){
		ss.cube.convert_to_stage1 (cube);
	}

	public void run (){
		while (pullState()) {
			treeSearch (cube, 0, N_BASIC_MOVES);
		}

		pushStopSignal();
		closePipes();
	}

	private boolean treeSearch (CubeStage1 cube1, int moves_done, int move_state){
		//Statistics.addNode(1, depth);
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, mc, j;
		int dist = cube1.get_dist();
		if (dist == 3){
			if (cube1.is_solved ()) {
				goal = moves_done;
				pushState();
				Statistics.addLeaf(1, goal);
				return true; // true: take the first solution, false: take all solutions
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg1[metric]; ++mov_idx) {
			boolean did_move = false;
			cube2.m_co = cube1.m_co;
			//cube2.m_edge_ud_combo8 = cube1.m_edge_ud_combo8;
			cube2.m_sym_edge_ud_combo8 = cube1.m_sym_edge_ud_combo8;
			switch (metric) {
			case 0:
				if ((stage1_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) { // TODO: make this for the other metrics.
					cube2.do_move (mov_idx);
					did_move = true;
				}
				break;
			case 1:
				for (j = 0; stage1_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				did_move = true;
				break;
			case 2:
				for (j = 0; stage1_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage1_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				did_move = true;
				break;
			}
			if (did_move) {
				if ((cube2.get_dist() % 3) != (dist - 1)) continue;
				move_list[moves_done] = (byte)mov_idx;
				if (treeSearch (cube2, moves_done + 1, mov_idx)) return true;
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
