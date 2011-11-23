package fivestage444;

import static fivestage444.Constants.*;

public final class Stage3Solver extends Thread{

	private static int stage3_block_map[] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2,
	Ffs2, Bfs2, FsBs3, Fs3Bs, Fs2Bs2
	};

	private static int n_moves_metric_stg3[] = { N_STAGE3_SLICE_MOVES, N_STAGE3_TWIST_MOVES, N_STAGE3_BLOCK_MOVES};

	private CubeStage3 cube;
	public int[] move_list = new int[30];
	private int metric;
	public int goal;

	Stage3Solver( CubeStage3 cube, int metric ){
		this.cube = cube;
		this.metric = metric;
	}

	public void run (){
		for (int i = 0; i < 30; ++i) move_list[i] = 0;
		for (goal = 0; goal <= 30; ++goal) {
			if (treeSearch (cube, goal, 0)) {
				formatMoves();
				return;
			}
		}
	}

	public boolean treeSearch (CubeStage3 cube1, int depth, int moves_done){
	CubeStage3 cube2 = new CubeStage3();
	int mov_idx, mc, j;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = cube1.prune_funcCEN_STAGE3 ();
	if (dist <= depth) {
		dist = cube1.prune_funcEDGE_STAGE3 ();
	}
	if (dist <= depth) {
		if (metric == 1 && depth >= 2) {
			for (mov_idx = 0; mov_idx < Constants.N_STAGE3_2TWIST_MOVES; ++mov_idx) {
				cube2.m_centerLR = cube1.m_centerLR; // TODO: Add a method copy.
				cube2.m_edge = cube1.m_edge;
				cube2.m_edge_odd = cube1.m_edge_odd;

				mc = stage3_2twist_moves[mov_idx][0];		//!!! metric dependency
				cube2.do_move (mc);
				mc = stage3_2twist_moves[mov_idx][1];
				if (mc >= 0) {
					cube2.do_move (mc);
				}
				move_list[moves_done] = stage3_twist_map1[Constants.N_STAGE3_TWIST_MOVES + mov_idx];
				move_list[moves_done + 1] = stage3_twist_map2[Constants.N_STAGE3_TWIST_MOVES + mov_idx];
				if (treeSearch (cube2, depth - 2, moves_done + 2))
				{
					return true;
				}
			}
		}
		for (mov_idx = 0; mov_idx < n_moves_metric_stg3[metric]; ++mov_idx) {
			cube2.m_centerLR = cube1.m_centerLR; // TODO: Add a method copy.
			cube2.m_edge = cube1.m_edge;
			cube2.m_edge_odd = cube1.m_edge_odd;
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				//old TODO: This not finished.
				for (j = 0; stage3_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				//old TODO: This not finished.
				for (j = 0; stage3_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage3_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage3_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage3_block_map[mov_idx];
				break;
			}
			move_list[moves_done] = mc;
			if (treeSearch (cube2, depth - 1, moves_done + 1))
			{
				return true;
			}
		}
	}
	return false;
}

	private void formatMoves(){
		int i;
		if (metric == 0) {
			for (i = 0; i < goal; ++i) {
				move_list[i] = stage3_slice_moves[move_list[i]];
			}
		}
	}
}
