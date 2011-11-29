package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage1Solver extends StageSolver{

	private static int stage_twist_list[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
	Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
	Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
	};

	private static int stage_block_list[] = {
	Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
	Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2, UsDs3, Us3Ds, Us2Ds2,
	Lf, Lf3, Lf2, Ls, Ls3, Ls2, Rf, Rf3, Rf2, Rs, Rs3, Rs2,
	Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2, LsRs3, Ls3Rs, Ls2Rs2,
	Ff, Ff3, Ff2, Fs, Fs3, Fs2, Bf, Bf3, Bf2, Bs, Bs3, Bs2,
	Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
	};

	private static int n_moves_metric_stg1[] = { N_BASIC_MOVES, N_STAGE1_TWIST_MOVES, N_STAGE1_BLOCK_MOVES};

	private CubeStage1 cube = new CubeStage1();

	Stage1Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );

		stage_twist_list = new int[] {
		Uf, Uf3, Uf2, Df, Df3, Df2, Ufs, Ufs3, Ufs2, Dfs, Dfs3, Dfs2,
		Lf, Lf3, Lf2, Rf, Rf3, Rf2, Lfs, Lfs3, Lfs2, Rfs, Rfs3, Rfs2,
		Ff, Ff3, Ff2, Bf, Bf3, Bf2, Ffs, Ffs3, Ffs2, Bfs, Bfs3, Bfs2
		};

		stage_block_list = new int[] {
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
			foundSol = false;
			for (goal = 0; goal <= 30; ++goal) {
				treeSearch (cube, goal, 0);
				if (foundSol)
					break;
			}
		}

		pushStopSignal();
		closePipes();
	}

	private boolean treeSearch (CubeStage1 cube1, int depth, int moves_done){
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, mc, j;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			pushState();
			return false; // true: take the first solution, false: take all solutions
		}
		int dist = cube1.prune_funcCOR_STAGE1();
		if (dist <= depth) {
			dist = cube1.prune_funcEDGE_STAGE1();
		}
		if (dist <= depth) {
			for (mov_idx = 0; mov_idx < n_moves_metric_stg1[metric]; ++mov_idx) {
				cube2.m_co = cube1.m_co;
				cube2.m_edge_ud_combo8 = cube1.m_edge_ud_combo8;
				switch (metric) {
				case 0:
					cube2.do_move (mov_idx);
					break;
				case 1:
					for (j = 0; stage1_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = stage1_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					break;
				case 2:
					for (j = 0; stage1_block_moves[mov_idx][j] >= 0; ++j) {
						mc = stage1_block_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					break;
				}
				move_list[moves_done] = mov_idx;
				if (treeSearch (cube2, depth - 1, moves_done + 1)) return true;
			}
		}
		return false;
	}

	int rotateCube(CubeState cube, int[] sol_move_list){

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
