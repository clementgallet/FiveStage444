package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;
import java.lang.Math;

public final class Stage5Solver extends StageSolver{

	private static int sqs_slice_moves_to_try [] = {
	0xFFE, 0xFF0, 0xFF0, 0xFF0,
	0xFEF, 0xF0F, 0xF0F, 0xF0F,
	0xEFF, 0x0FF, 0x0FF, 0x0FF,
	0xFFF
};

	private static int sqs_stm_next_ms[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	private CubeSqsCoord cube = new CubeSqsCoord();
	private int bestCount;

	Stage5Solver( PipedInputStream pipeIn, PipedOutputStream pipeOut ) throws java.io.IOException{
		super( pipeIn, pipeOut );
		stage_slice_list = new byte[] { Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2 };
	}

	void importState(){
		ss.cube.convert_to_squares (cube);
	}

	int id;
	int best;

	public void run (){
		id = 0;

		while(pullState()) {
			if( id != ss.id ){
				id = ss.id;
				best = 100;
			}

			int distEdgCen = getDistanceEdgCen();
			int distEdgCor = getDistanceEdgCor();
			if( Math.max(distEdgCen, distEdgCor) > ( best - ss.move_count ))
				continue;

			for (goal = Math.max(distEdgCen, distEdgCor); goal < best - ss.move_count; ++goal) {
				if (treeSearch (cube, goal, 0, 12, distEdgCen, distEdgCor)) {
					best = ss.move_count + goal;
					break;
				}
			}
		}

		pushStopSignal();
		closePipes();
	}

	public int getDistanceEdgCen (){
		CubeSqsCoord cube1 = new CubeSqsCoord();
		CubeSqsCoord cube2 = new CubeSqsCoord();
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;
		
		cube1.m_cen12x12x12 = cube.m_cen12x12x12;
		cube1.m_sym_ep96x96x96 = cube.m_sym_ep96x96x96;

		dist1 = cube1.get_dist_edgcen();

		while (! cube1.edges_centers_solved()) {

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < N_SQMOVES; ++mov_idx) {
				cube2.m_cen12x12x12 = cube1.m_cen12x12x12;
				cube2.m_sym_ep96x96x96 = cube1.m_sym_ep96x96x96;
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_edgcen();

				if ((dist2 % 3) != (dist1 - 1)) continue;
				cube1.m_cen12x12x12 = cube2.m_cen12x12x12;
				cube1.m_sym_ep96x96x96 = cube2.m_sym_ep96x96x96;
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

	public int getDistanceEdgCor (){
		CubeSqsCoord cube1 = new CubeSqsCoord();
		CubeSqsCoord cube2 = new CubeSqsCoord();
		int mov_idx, mc, j, dist1, dist2, idx; // TODO: idx is for debugging.
		int nDist = 0;
		
		cube1.m_cp96 = cube.m_cp96;
		cube1.m_sym_ep96x96x96 = cube.m_sym_ep96x96x96;

		dist1 = cube1.get_dist_edgcor();

		while (! cube1.edges_corners_solved()) {

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < N_SQMOVES; ++mov_idx) {
				cube2.m_cp96 = cube1.m_cp96;
				cube2.m_sym_ep96x96x96 = cube1.m_sym_ep96x96x96;
				cube2.do_move (mov_idx);

				dist2 = cube2.get_dist_edgcor();

				if ((dist2 % 3) != (dist1 - 1)) continue;
				cube1.m_cp96 = cube2.m_cp96;
				cube1.m_sym_ep96x96x96 = cube2.m_sym_ep96x96x96;
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

	public boolean treeSearch (CubeSqsCoord cube1, int depth, int moves_done, int move_state, int distEdgCen, int distEdgCor){
		//Statistics.addNode(5, depth);
		CubeSqsCoord cube2 = new CubeSqsCoord();
		int mov_idx, mc, j;
		int next_ms = 0;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			pushState();
			Statistics.addLeaf(5, goal);
			return true; // true: take the first solution, false: take all solutions.
		}
		for (mov_idx = 0; mov_idx < N_SQMOVES; ++mov_idx) {
			cube2.m_cen12x12x12 = cube1.m_cen12x12x12;
			cube2.m_cp96 = cube1.m_cp96;
			cube2.m_sym_ep96x96x96 = cube1.m_sym_ep96x96x96;
			if ((sqs_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
				cube2.do_move (mov_idx);
				next_ms = sqs_stm_next_ms[mov_idx];
				int newDistEdgCen = ((cube2.get_dist_edgcen() - (distEdgCen%3) + 4) % 3 ) + distEdgCen - 1; // TODO: Could make a better formula...
				int newDistEdgCor = ((cube2.get_dist_edgcor() - (distEdgCor%3) + 4) % 3 ) + distEdgCor - 1; // TODO: Could make a better formula...
				if (newDistEdgCen > depth-1) continue;
				if (newDistEdgCor > depth-1) continue;
				mc = mov_idx;
				move_list[moves_done] = (byte)mc;
				if (treeSearch (cube2, depth - 1, moves_done + 1, next_ms, newDistEdgCen, newDistEdgCor)) return true;
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

