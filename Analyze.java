package cg.fivestage444;

import static cg.fivestage444.Constants.*;

public final class Analyze {

	static int length4;
	static CubeState cube = new CubeState();
	static long unique, pos, done;
	static byte[] move_list_stage4 = new byte[30];

	static byte[] allPos5 = new byte[N_STAGE5_SYMEDGES*N_STAGE5_CORNERS*N_STAGE5_CENTERS>>>3];

	public static void main(String[] args){
		Tools.init();
		Tables.initSymEdgeToEdgeStage5();
		distTable5in4(0);
	}

	public static void distTable5in4 (int coset){

		int edge = coset / (N_STAGE4_CORNERS*N_STAGE4_CENTERS);
		int corner = (coset / N_STAGE4_CENTERS) % N_STAGE4_CORNERS;
		int center = coset % (N_STAGE4_CORNERS*N_STAGE4_CENTERS);
	
		cube.convert_edges4_to_std_cube(Tables.sym2rawEdge4[edge]);
		cube.convert_corners4_to_std_cube(corner);
		cube.convert_centers4_to_std_cube(center);

		int cubeDistEdgCor = Tables.prunDistEdgCor4(edge, 0, corner);

		done = 0;
		for (length4 = 0; length4 < 19; ++length4) {
			unique = 0;
			pos = 0;
			search_stage4 (center, corner, edge, 0, length4, 0, N_STAGE4_MOVES, cubeDistEdgCor );
			System.out.println(String.format("%2d%12d%10d", length4, pos, unique));
		}
	}

	public static void search_stage4 (int center, int corner, int edge, int sym, int depth, int moves_done, int last_move, int dist){
		int mov_idx, j;
		if (depth == 0)
			if( corner == 0 && edge == 0 )
				for (int i = 0; i < Constants.stage4_solved_centers_bm.length; ++i)
					if (center == Constants.stage4_solved_centers_bm[i]){
						save_stage5 ();
						return;
					}

		for (mov_idx = 0; mov_idx < N_STAGE4_SEARCH; ++mov_idx) {
			if (stage4_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move */
			int centerx = Tables.moveCenter4[center][mov_idx];
			int cornerx = Tables.moveCorner4[corner][mov_idx];
			int edgex = Tables.moveEdge4[edge][Symmetry.moveConjugate4[mov_idx][sym]];
			int symx = Symmetry.symIdxMultiply[edgex & 0xF][sym];
			edgex >>= 4;

			/* Compute new distance */
			int newDistEdgCen = Tables.getPrun2(Tables.prunTableEdgCen4, edgex*N_STAGE4_CENTERS+Tables.conjCenter4[centerx][symx]);
			if (newDistEdgCen > depth-1) continue;
			int newDist = Tables.new_dist(Tables.prunTableEdgCor4, edgex*Constants.N_STAGE4_CORNERS+Tables.conjCorner4[cornerx][symx], dist);
			if (newDist > depth-1) continue;
			move_list_stage4[moves_done] = (byte)mov_idx;
			search_stage4 (centerx, cornerx, edgex, symx, depth - 1, moves_done + 1, mov_idx, newDist);
		}
	}

	public static void save_stage5 (){
		int i;

		CubeState c = new CubeState();
		cube.copyTo(c);
		c.scramble( length4, move_list_stage4, stage4_slice_moves );

		int edge = c.convert_symedges_to_stage5();
		int sym = edge & 0xFF;
		edge >>>= 8;
		int corner = Tables.conjCorner5[c.convert_corners_to_stage5()][sym];
		int center = Tables.conjCenter5[c.convert_centers_to_stage5()][sym];

		long idx = ((long)edge * N_STAGE5_CORNERS + corner) * N_STAGE5_CENTERS + center;
		if(( allPos5[(int)(idx>>>3)] >>> (int)(idx & 0x7) & 1 ) == 0 ){
			unique++;
			int nsym = 1;
			allPos5[(int)(idx>>>3)] |= 1 << (idx & 0x7);
			for (int j=0; j<4; j++) {
				long symS = Tables.hasSymEdgeSTAGE5[edge][j];
				for (int k=0; symS != 0; symS>>=1, k++) {
					if ((symS & 0x1L) == 0) continue;
					long idxx = ((long)edge * N_STAGE5_CORNERS + Tables.conjCorner5[corner][(k<<2)+j]) * N_STAGE5_CENTERS + Tables.conjCenter5[center][(k<<2)+j];
					if(( allPos5[(int)(idxx>>>3)] >>> (int)(idxx & 0x7) & 1 ) == 0 ){
						allPos5[(int)(idxx>>>3)] |= 1 << (idxx & 0x7);
						done++;
					}
					nsym++;
				}
			}
			pos += 192/nsym;
		}
	}
}
