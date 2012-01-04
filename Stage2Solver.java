package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage2Solver extends StageSolver{

	private static int stage2_slice_moves_to_try [] = {
	0xFFFFFFF,
	0xFFFFFF8, 0xFFFFFC0, 0xFFFF1C0, 0xFFFF000,
	0xFF60FFF, 0xFFF1FFF, 0xFF10FFF, 0xFF00FFF,
	0x60FFFFF, 0xF1FFFFF, 0x10FFFFF, 0x00FFFFF
	};

	private static int stage2_stm_next_ms[] = 	{ SL_MS_U,SL_MS_U,SL_MS_U,SL_MS_u,SL_MS_u,SL_MS_u,SL_MS_D,SL_MS_D,SL_MS_D,SL_MS_d,SL_MS_d,SL_MS_d,SL_MS_L,SL_MS_l,SL_MS_l,SL_MS_l,SL_MS_R,SL_MS_r,SL_MS_r,SL_MS_r,SL_MS_F,SL_MS_f,SL_MS_f,SL_MS_f,SL_MS_B,SL_MS_b,SL_MS_b,SL_MS_b };

	private CubeStage2 cube = new CubeStage2();

	Stage2Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );

		stage_slice_list = new byte[]{
		Uf, Uf3, Uf2, Us, Us3, Us2,
		Df, Df3, Df2, Ds, Ds3, Ds2,
		Lf2, Ls, Ls3, Ls2, Rf2, Rs, Rs3, Rs2,
		Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
		};
	}

	void importState(){
		ss.cube.convert_to_stage2 (cube);
	}

	public void run (){
		while(pullState()) {
			foundSol = false;
			for (goal = cube.prune_funcEDGCEN_STAGE2(); goal < 30; ++goal) {
				treeSearch (cube, goal, 0, 0);
				if (foundSol){
					break;
				}
			}
		}

		pushStopSignal();
		closePipes();
	}

	public boolean treeSearch (CubeStage2 cube1, int depth, int moves_done, int move_state){
		//Statistics.addNode(2, depth);
		CubeStage2 cube2 = new CubeStage2();
		int mov_idx, mc, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			pushState();
			Statistics.addLeaf(2, goal);
			return true; // true: take the first solution, false: take all solutions.
		}
		for (mov_idx = 0; mov_idx < N_STAGE2_SLICE_MOVES; ++mov_idx) {
			if ((stage2_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.m_edge = cube1.m_edge;
				cube2.m_centerF = cube1.m_centerF;
				cube2.m_centerB = cube1.m_centerB;
				cube2.do_move (mov_idx);
				next_ms = stage2_stm_next_ms[mov_idx];

				if (cube2.prune_funcEDGCEN_STAGE2() > depth-1) continue;
				move_list[moves_done] = (byte)mov_idx;
				if (treeSearch (cube2, depth - 1, moves_done + 1, next_ms)) return true;
			}
		}
		return false;
	}

	int rotateCube(CubeState cube, byte[] sol_move_list){
		int i;
		for (i = 0; i < goal; ++i) {
			sol_move_list[i] = xlate_r6[sol_move_list[i]][ss.rotate];
		}
		int r6 = ss.rotate;
		if (cube.m_cen[16] < 4) {
			cube.do_move (Uf);
			cube.do_move (Us);
			cube.do_move (Ds3);
			cube.do_move (Df3);
			r6 += 3;
		}
		return r6;
	}

}
