package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage4Solver extends StageSolver{

	private static int stage4_stm_next_ms[] = 	{ SL_MS_U,SL_MS_U,SL_MS_U,SL_MS_u,SL_MS_D,SL_MS_D,SL_MS_D,SL_MS_d,SL_MS_L,SL_MS_l,SL_MS_R,SL_MS_r,SL_MS_F,SL_MS_f,SL_MS_B,SL_MS_b };

	private static int stage4_slice_moves_to_try [] = {
	0xFFFF,
	0xFFF8, 0xFF30, 0xFF30, 0xFF00,
	0xFEFF, 0xF0FF, 0xF0FF, 0xF0FF,
	0xEFFF, 0x0FFF, 0x0FFF, 0x0FFF
	};

	private CubeStage4 cube = new CubeStage4();

	Stage4Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );

		stage_slice_list = new byte[] {
		Uf, Uf3, Uf2, Us2,
		Df, Df3, Df2, Ds2,
		Lf2, Ls2, Rf2, Rs2,
		Ff2, Fs2, Bf2, Bs2
		};
	}

	void importState(){
		ss.cube.convert_to_stage4 (cube);
	}

	int id;
	int best;

	public void run (){
		while (pullState()) {
			if( id != ss.id ){
				id = ss.id;
				best = 100;
			}

			treeSearch (cube, 0, 0);
		}

		pushStopSignal();
		closePipes();
	}

	public boolean treeSearch (CubeStage4 cube1, int moves_done, int move_state){
		//Statistics.addNode(4, depth);
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, mc, j;
		int dist = cube1.get_dist();
		int next_ms = 0;
		if (dist == 3) {
			if (cube1.is_solved ()) {
				goal = moves_done;
				best = ss.move_count + goal;
				pushState();
				Statistics.addLeaf(4, goal);
				return true; // true: take the first solution, false: take all solutions.
			}
		}
		if (moves_done >= best - ss.move_count - 1) return false;

		for (mov_idx = 0; mov_idx < N_STAGE4_SLICE_MOVES; ++mov_idx) {
			//cube2.m_edge = cube1.m_edge;
			cube2.m_sym_edge = cube1.m_sym_edge;
			cube2.m_corner = cube1.m_corner;
			cube2.m_centerUD = cube1.m_centerUD; // TODO: use a copy method
			if ((stage4_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.do_move (mov_idx);
				next_ms = stage4_stm_next_ms[mov_idx];
				if ((cube2.get_dist() % 3) != (dist - 1)) continue; // TODO: computing get_dist twice !
				//if (cube2.prune_funcCENCOR_STAGE4() > depth-1) continue;
				//if (cube2.prune_funcEDGCEN_STAGE4() > depth-1) continue;
				mc = mov_idx;
				move_list[moves_done] = (byte)mc;
				if (treeSearch (cube2, moves_done + 1, next_ms)) return true;
			}
		}
		return false;
	}

	int rotateCube(CubeState cube, byte[] sol_move_list){
		int i;
		for (i = 0; i < goal; ++i) {
			sol_move_list[i] = xlate_r6[sol_move_list[i]][ss.rotate];
		}
		return ss.rotate;
	}

}
