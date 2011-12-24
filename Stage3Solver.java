package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage3Solver extends StageSolver{

	private static int stage3_stm_next_ms[] = 	{ SL_MS_U,SL_MS_U,SL_MS_U,SL_MS_u,SL_MS_D,SL_MS_D,SL_MS_D,SL_MS_d,SL_MS_L,SL_MS_l,SL_MS_R,SL_MS_r,SL_MS_F,SL_MS_f,SL_MS_f,SL_MS_f,SL_MS_B,SL_MS_b,SL_MS_b,SL_MS_b };

	private static int stage3_slice_moves_to_try [] = {
	0xFFFFF,
	0xFFFF8, 0xFFF30, 0xFFF30, 0xFFF00,
	0xFFEFF, 0xFF0FF, 0xFF0FF, 0xFF0FF,
	0x60FFF, 0xF1FFF, 0x10FFF, 0x00FFF
	};

	private CubeStage3 cube = new CubeStage3();

	Stage3Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );

		stage_slice_list = new byte[] {
		Uf, Uf3, Uf2, Us2,
		Df, Df3, Df2, Ds2,
		Lf2, Ls2, Rf2, Rs2,
		Ff2, Fs, Fs3, Fs2, Bf2, Bs, Bs3, Bs2
		};
	}

	void importState(){
		ss.cube.convert_to_stage3 (cube);
		cube.m_edge_odd = ss.cube.edgeUD_parity_odd ();
	}

	public void run (){
		while(pullState()) {

			int cubeDistCen = getDistanceCen();
			foundSol = false;
			for (goal = cubeDistCen; goal < 30; ++goal) {
				treeSearch (cube, goal, 0, 0, cubeDistCen);
				if (foundSol){
					break;
				}
			}
		}

		pushStopSignal();
		closePipes();
	}

	public int getDistanceCen (){
		CubeStage3 cube1 = new CubeStage3();
		CubeStage3 cube2 = new CubeStage3();
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;
		
		cube1.m_sym_centerLR = cube.m_sym_centerLR;

		dist1 = cube1.get_dist_cen();

		while (! cube1.centers_solved()) {

			boolean noMoves=true;

			for (mov_idx = 0; mov_idx < N_STAGE3_SLICE_MOVES; ++mov_idx) {
				cube2.m_sym_centerLR = cube1.m_sym_centerLR;
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_cen();
				if ((dist2 % 3) != (dist1 - 1)) continue;
				cube1.m_sym_centerLR = cube2.m_sym_centerLR;
				nDist++;
				dist1 = dist2;
				noMoves=false;
				break;
			}
			if( noMoves){
				System.out.println("Could not find a move that lowers the distance !!");
				break;
			}
		}
		return nDist;
	}


	public boolean treeSearch (CubeStage3 cube1, int depth, int moves_done, int move_state, int distCen){
		//Statistics.addNode(3, depth);
		CubeStage3 cube2 = new CubeStage3();
		int mov_idx, mc, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			pushState();
			Statistics.addLeaf(3, goal);
			return true; // true: take the first solution, false: take all solutions
		}
		for (mov_idx = 0; mov_idx < N_STAGE3_SLICE_MOVES; ++mov_idx) {
			boolean did_move = false;
			//cube2.m_centerLR = cube1.m_centerLR; // TODO: Add a method copy.
			cube2.m_sym_centerLR = cube1.m_sym_centerLR;
			cube2.m_edge = cube1.m_edge;
			cube2.m_edge_odd = cube1.m_edge_odd;
			if ((stage3_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.do_move (mov_idx);
				next_ms = stage3_stm_next_ms[mov_idx];
				int newDistCen = ((cube2.get_dist_cen() - (distCen%3) + 4) % 3 ) + distCen - 1; // TODO: Could make a better formula...
				if (newDistCen > depth-1) continue;
				//if (cube2.prune_funcCEN_STAGE3() > depth-1) continue;
				if (cube2.prune_funcEDGE_STAGE3() > depth-1) continue;
				mc = mov_idx;
				move_list[moves_done] = (byte)mc;
				if (treeSearch (cube2, depth - 1, moves_done + 1, next_ms, newDistCen)) return true;
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
