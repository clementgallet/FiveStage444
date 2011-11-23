package fivestage444;

import static fivestage444.Constants.*;

public final class Stage4Solver extends Thread{

	private static int stage4_twist_map1[] = {
	Uf, Uf3, Uf2, Df, Df3, Df2, Ufs2, Dfs2,
	Lf2, Rf2, Lfs2, Rfs2, Ff2, Bf2, Ffs2, Bfs2
	};

	private static int stage4_block_map[] = {
	Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
	Ufs2, Dfs2, Us2Ds2,
	Lf2, Ls2, Rf2, Rs2, Lfs2, Rfs2, Ls2Rs2,
	Ff2, Fs2, Bf2, Bs2,	Ffs2, Bfs2, Fs2Bs2
	};

	private static int n_moves_metric_stg4[] = { N_STAGE4_SLICE_MOVES, N_STAGE4_TWIST_MOVES, N_STAGE4_BLOCK_MOVES };

	private CubeStage4 cube;
	public int[] move_list = new int[30];
	private int metric;
	public int goal;

	Stage4Solver( CubeStage4 cube, int metric ){
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

	public boolean treeSearch (CubeStage4 cube1, int depth, int moves_done){
	CubeStage4 cube2 = new CubeStage4();
	int mov_idx, mc, j;
	if (depth == 0) {
		if (! cube1.is_solved ()) {
			return false;
		}
		return true;
	}
	int dist = cube1.prune_funcCENCOR_STAGE4 ();
	if (dist <= depth) {
		dist = cube1.prune_funcEDGCEN_STAGE4 ();
	}
	if (dist <= depth) {
		for (mov_idx = 0; mov_idx < n_moves_metric_stg4[metric]; ++mov_idx) {
			cube2.m_edge = cube1.m_edge;
			cube2.m_corner = cube1.m_corner;
			cube2.m_centerUD = cube1.m_centerUD; // TODO: use a copy method
			switch (metric) {
			case 0:
				cube2.do_move (mov_idx);
				break;
			case 1:
				for (j = 0; stage4_twist_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_twist_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			case 2:
				for (j = 0; stage4_block_moves[mov_idx][j] >= 0; ++j) {
					mc = stage4_block_moves[mov_idx][j];		//!!! metric dependency
					cube2.do_move (mc);		//!!! metric dependency
				}
				break;
			}
			mc = mov_idx;
			switch (metric) {
			case 1:
				mc = stage4_twist_map1[mov_idx];
				break;
			case 2:
				mc = stage4_block_map[mov_idx];
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
				move_list[i] = stage4_slice_moves[move_list[i]];
			}
		}
	}
}
