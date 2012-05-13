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

	public void run (){
		while (pullState()) {

			if( StageController.currentStage != 34 ) continue;

			/* Stage 3-4 */

			int cubeDist = getDistance();

			if( cubeDist + ss.move_count > StageController.currentBest ) continue;

			foundSol = false;
			for (goal = cubeDist; goal < StageController.currentBest - ss.move_count; ++goal) {
				if( treeSearch (cube, goal, 0, 0, cubeDist)){
					StageController.updateBest( ss.move_count + goal );
					System.out.print ("Stage 1+2+3");
					print_move_list( ss.move_count, ss.move_list);
					System.out.print ("Stage 4");
					print_move_list( goal, move_list);
					System.out.println( "" );
					break;
				}
			}

			if( goal + ss.move_count > StageController.goalStage1234 ) continue;

			/* Go to stage 4-5 */

			StageController.nextStage();
			cubeDist = goal;

			for (goal = cubeDist; goal < cubeDist + 5; ++goal) {
				treeSearch (cube, goal, 0, 0, cubeDist);
				if ( StageController.currentStage != 45 ) break;
			}
		}

		pushStopSignal();
		closePipes();
	}

	public int getDistance (){
		CubeStage4 cube1 = new CubeStage4();
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j, dist1, dist2;
		int nDist = 0;

		cube1.m_sym_edge = cube.m_sym_edge;
		cube1.m_corner = cube.m_corner;
		cube1.m_centerUD = cube.m_centerUD; // TODO: use a copy method
		dist1 = cube1.get_dist();

		while( ! cube1.is_solved ()) {

			boolean noMoves = true;
			for (mov_idx = 0; mov_idx < N_STAGE4_SLICE_MOVES; ++mov_idx) {
				cube2.m_sym_edge = cube1.m_sym_edge;
				cube2.m_corner = cube1.m_corner;
				cube2.m_centerUD = cube1.m_centerUD; // TODO: use a copy method
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist();
				if (((dist2+1) % 3) != dist1) continue;
				cube1.m_sym_edge = cube2.m_sym_edge;
				cube1.m_corner = cube2.m_corner;
				cube1.m_centerUD = cube2.m_centerUD; // TODO: use a copy method
				nDist++;
				dist1 = dist2;
				noMoves = false;
				break;
			}
			if( noMoves){
				System.out.println("Could not find a move that lowers the distance !!");
				break;
			}
		}
		return nDist;
	}

	public boolean treeSearch (CubeStage4 cube1, int depth, int moves_done, int move_state, int dist){
		//Statistics.addNode(4, depth);
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			Statistics.addLeaf(4, goal);
			if( StageController.currentStage == 45 ) {
				pushState();
				return false; // true: take the first solution, false: take all solutions.
			}
			return true;
		}
		for (mov_idx = 0; mov_idx < N_STAGE4_SLICE_MOVES; ++mov_idx) {
			if ((stage4_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.m_sym_edge = cube1.m_sym_edge;
				cube2.m_corner = cube1.m_corner;
				cube2.m_centerUD = cube1.m_centerUD; // TODO: use a copy method
				cube2.do_move (mov_idx);
				next_ms = stage4_stm_next_ms[mov_idx];
				int newDist = cube2.new_dist(dist);
				if (newDist > depth-1) continue;
				move_list[moves_done] = (byte)mov_idx;
				if (treeSearch (cube2, depth - 1, moves_done + 1, next_ms, newDist)) return true;
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
