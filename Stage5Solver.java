package fivestage444;

import java.io.PipedOutputStream;
import java.io.PipedInputStream;

import static fivestage444.Constants.*;

public final class Stage5Solver extends StageSolver{

	private static int sq_twist_map1[] = {
	Uf2, Ufs2, Dfs2, Df2,
	Lf2, Lfs2, Rfs2, Rf2,
	Ff2, Ffs2, Bfs2, Bf2
	};

	private static int sq_block_map[] = {
	Uf2, Us2, Ufs2, Dfs2, Ds2, Df2,
	Lf2, Ls2, Lfs2, Rfs2, Rs2, Rf2,
	Ff2, Fs2, Ffs2, Bfs2, Bs2, Bf2,
	Us2Ds2, Ls2Rs2, Fs2Bs2
	};

	private static int n_moves_metric_stg5[] = { N_SQMOVES, N_SQ_TWIST_MOVES, N_SQ_BLOCK_MOVES};

	private static int sqs_slice_moves_to_try [] = {
	0xFFE, 0xFF0, 0xFF0, 0xFF0,
	0xFEF, 0xF0F, 0xF0F, 0xF0F,
	0xEFF, 0x0FF, 0x0FF, 0x0FF,
	0xFFF
};

	private static int sqs_stm_next_ms[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	private static int SQS_TW_MS_U = 0;
	private static int SQS_TW_MS_D = 1;
	private static int SQS_TW_MS_Uu = 2;
	private static int SQS_TW_MS_u = 3;
	private static int SQS_TW_MS_d = 4;
	private static int SQS_TW_MS_UD = 5;
	private static int SQS_TW_MS_Ud = 6;

	private static int SQS_TW_MS_L = 8;
	private static int SQS_TW_MS_R = 9;
	private static int SQS_TW_MS_Ll = 10;
	private static int SQS_TW_MS_l = 11;
	private static int SQS_TW_MS_r = 12;
	private static int SQS_TW_MS_LR = 13;
	private static int SQS_TW_MS_Lr = 14;

	private static int SQS_TW_MS_F = 16;
	private static int SQS_TW_MS_B = 17;
	private static int SQS_TW_MS_Ff = 18;
	private static int SQS_TW_MS_f = 19;
	private static int SQS_TW_MS_b = 20;
	private static int SQS_TW_MS_FB = 21;
	private static int SQS_TW_MS_Fb = 22;

	private static int SQS_TW_MS_X = 23;

	private static int sqs_twist_moves_to_try[] = {
	0xBBA, 0xBB4, 0xBB0, 0xBB0, 0xBB0, 0xBB4, 0xBB0, 0xBB0,
	0xBAB, 0xB4B, 0xB0B, 0xB0B, 0xB0B, 0xB4B, 0xB0B, 0xB0B,
	0xABB, 0x4BB, 0x0BB, 0x0BB, 0x0BB, 0x4BB, 0x0BB, 0xBBB
	};

	private static int sqs_twist_next_ms[][] = {
	{ SQS_TW_MS_X,SQS_TW_MS_u,SQS_TW_MS_X,SQS_TW_MS_UD, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_d,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Ud,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_l,SQS_TW_MS_X,SQS_TW_MS_LR, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_r,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Lr,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },
	{ SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_f,SQS_TW_MS_X,SQS_TW_MS_FB },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_b,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_X,SQS_TW_MS_X,SQS_TW_MS_Fb,SQS_TW_MS_X },
	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B },

	{ SQS_TW_MS_U,SQS_TW_MS_Uu,SQS_TW_MS_X,SQS_TW_MS_D, SQS_TW_MS_L,SQS_TW_MS_Ll,SQS_TW_MS_X,SQS_TW_MS_R, SQS_TW_MS_F,SQS_TW_MS_Ff,SQS_TW_MS_X,SQS_TW_MS_B }
};

	private static int SQS_BL_MS_U = 0;
	private static int SQS_BL_MS_XU = 1;
	private static int SQS_BL_MS_L = 2;
	private static int SQS_BL_MS_XL = 3;
	private static int SQS_BL_MS_F = 4;
	private static int SQS_BL_MS_XF = 5;
	private static int SQS_BL_MS_X = 6;

	private static int sqs_block_moves_to_try[] = {
	0x1B7DD0, 0x1B7DC0, 0x177437, 0x177037, 0x0D0DF7, 0x0C0DF7, 0x1F7DF7
};

	private static int sqs_block_next_ms[][] = {
	{ SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_X, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_X, SQS_BL_MS_XL, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_X, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_X, SQS_BL_MS_XF },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_X, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X,SQS_BL_MS_X, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_X },
	{ SQS_BL_MS_U,SQS_BL_MS_XU,SQS_BL_MS_XU,SQS_BL_MS_X,SQS_BL_MS_XU,SQS_BL_MS_XU, SQS_BL_MS_L,SQS_BL_MS_XL,SQS_BL_MS_XL,SQS_BL_MS_X,SQS_BL_MS_XL,SQS_BL_MS_XL, SQS_BL_MS_F,SQS_BL_MS_XF,SQS_BL_MS_XF,SQS_BL_MS_X,SQS_BL_MS_XF,SQS_BL_MS_XF, SQS_BL_MS_XU, SQS_BL_MS_XL, SQS_BL_MS_XF }
};

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
		int init_move_state[] = { 12, 23, 6 };
		id = 0;

		while(pullState()) {
			if( id != ss.id ){
				id = ss.id;
				best = 100;
			}

			int cubeDist = getDistance();
			if( cubeDist > ( best - ss.move_count ))
				continue;

			for (goal = 0; goal < best - ss.move_count; ++goal) {
				if (treeSearch (cube, goal, 0, init_move_state[metric], cubeDist)) {
					best = ss.move_count + goal;
					break;
				}
			}
		}

		pushStopSignal();
		closePipes();
	}

	public int getDistance (){
		CubeSqsCoord cube1 = new CubeSqsCoord();
		CubeSqsCoord cube2 = new CubeSqsCoord();
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;
		
		cube1.m_cen12x12x12 = cube.m_cen12x12x12;
		cube1.m_sym_ep96x96x96 = cube.m_sym_ep96x96x96;
		//cube1.m_ep96x96x96 = cube.m_ep96x96x96;

		dist1 = cube1.get_dist();

		while (! cube1.edges_centers_solved()) {

			//System.out.println("dist "+nDist+": symedg="+cube1.m_sym_ep96x96x96+" - cen="+cube1.m_cen12x12x12+" - edg="+cube1.m_ep96x96x96);
			//System.out.println("current dist:"+dist1);
			//if( cube1.m_sym_ep96x96x96 == 572929) System.out.println("init_idx = "+(( cube1.m_sym_ep96x96x96 / 48 ) * Constants.N_SQS_CENTER_PERM + Tables.move_table_cen_conjSTAGE5[cube1.m_cen12x12x12][cube1.m_sym_ep96x96x96 % 48])+" - init_dist = "+dist1);

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < n_moves_metric_stg5[metric]; ++mov_idx) {
				cube2.m_cen12x12x12 = cube1.m_cen12x12x12;
				//cube2.m_cp96 = cube1.m_cp96;
				//cube2.m_ep96x96x96 = cube1.m_ep96x96x96;
				cube2.m_sym_ep96x96x96 = cube1.m_sym_ep96x96x96;
				switch (metric) {
				case 0:
					cube2.do_move (mov_idx);
					break;
				case 1:
					for (j = 0; j < 2 && sq_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					break;
				case 2:
					for (j = 0; sq_block_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_block_moves[mov_idx][j];
						cube2.do_move (mc);
					}
					break;
				}
				dist2 = cube2.get_dist();

				//if( cube1.m_sym_ep96x96x96 == 572929) System.out.println("idx = "+(( cube2.m_sym_ep96x96x96 / 48 ) * Constants.N_SQS_CENTER_PERM + Tables.move_table_cen_conjSTAGE5[cube2.m_cen12x12x12][cube2.m_sym_ep96x96x96 % 48])+" dist = "+dist2);

				//System.out.println("moved "+mov_idx+", dist:"+dist2);
				if ((dist2 % 3) != (dist1 - 1)) continue;
				//if (cube2.prune_funcEDGCOR_STAGE5() > depth-1) continue;
				//if (cube2.prune_funcCENCOR_STAGE5() > depth-1) continue;
				cube1.m_cen12x12x12 = cube2.m_cen12x12x12;
				cube1.m_sym_ep96x96x96 = cube2.m_sym_ep96x96x96;
				//cube1.m_ep96x96x96 = cube2.m_ep96x96x96;
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
		//System.out.println("Successfully found distance "+nDist);
		//System.out.println("symedg="+(cube1.m_sym_ep96x96x96%48)+" - cen="+cube1.m_cen12x12x12+" - edg="+cube1.m_ep96x96x96+" - conjCen="+Tables.move_table_cen_conjSTAGE5[cube1.m_cen12x12x12][cube1.m_sym_ep96x96x96 % 48]);
		return nDist;
	}

	public boolean treeSearch (CubeSqsCoord cube1, int depth, int moves_done, int move_state, int distance){
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
		for (mov_idx = 0; mov_idx < n_moves_metric_stg5[metric]; ++mov_idx) {
			boolean did_move = false;
			cube2.m_cen12x12x12 = cube1.m_cen12x12x12;
			cube2.m_cp96 = cube1.m_cp96;
			//cube2.m_ep96x96x96 = cube1.m_ep96x96x96;
			cube2.m_sym_ep96x96x96 = cube1.m_sym_ep96x96x96;
			switch (metric) {
			case 0:
				if ((sqs_slice_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					cube2.do_move (mov_idx);
					next_ms = sqs_stm_next_ms[mov_idx];
					did_move = true;
				}
				break;
			case 1:
				if ((sqs_twist_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; j < 2 && sq_twist_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_twist_moves[mov_idx][j];		//!!! metric dependency
						cube2.do_move (mc);		//!!! metric dependency
					}
					next_ms = sqs_twist_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			case 2:
				if ((sqs_block_moves_to_try[move_state] & (1 << mov_idx)) != 0) {
					for (j = 0; sq_block_moves[mov_idx][j] >= 0; ++j) {
						mc = sq_block_moves[mov_idx][j];
						cube2.do_move (mc);
					}
					next_ms = sqs_block_next_ms[move_state][mov_idx];
					did_move = true;
				}
				break;
			}
			if (did_move) {
				int newDist = ((cube2.get_dist() - (distance%3) + 4) % 3 ) + distance - 1; // TODO: Could make a better formula...
				if (newDist > depth-1) continue;
				//if (cube2.prune_funcEDGCOR_STAGE5() > depth-1) continue;
				//if (cube2.prune_funcCENCOR_STAGE5() > depth-1) continue;
				mc = mov_idx;
				switch (metric) {
				case 1:
					mc = sq_twist_map1[mov_idx];
					break;
				case 2:
					mc = sq_block_map[mov_idx];
					break;
				}
				move_list[moves_done] = (byte)mc;
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

