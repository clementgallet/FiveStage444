package cg.fivestage444;

public final class CubeStage5 {

	public int edge;
	public int sym;
	public int center;
	public int corner;

	public static PruningStage5EdgCen prune_table_edgcen;
	public static PruningStage5EdgCor prune_table_edgcor;

	public final void copyTo (CubeStage5 cube1){
		cube1.center = center;
		cube1.corner = corner;
		cube1.edge = edge;
		cube1.sym = sym;
	}

	public final void do_move (int sqs_move_code){
		center = Tables.moveCenter5[center][sqs_move_code];
		corner = Tables.moveCorner5[corner][sqs_move_code];

		int newEdge = Tables.moveEdge5[edge][Symmetry.moveConjugate5[sqs_move_code][sym]];

		sym = Symmetry.symIdxCo4Multiply[sym][newEdge&0xFF];
		edge = newEdge >> 8;
	}

	/* Pruning functions */

	public final int get_dist_edgcen (){
		int idx = edge * Constants.N_STAGE5_CENTERS + Tables.conjCenter5[center][sym];
		return prune_table_edgcen.get_dist_packed(idx);
	}

	public final int new_dist_edgcen (int dist){
		int idx = edge * Constants.N_STAGE5_CENTERS + Tables.conjCenter5[center][sym];
		return prune_table_edgcen.new_dist(idx, dist);
	}

	public int getDistanceEdgCen (){
		CubeStage5 cube1 = new CubeStage5();
		CubeStage5 cube2 = new CubeStage5();
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;
		
		copyTo (cube1);
		dist1 = cube1.get_dist_edgcen();

		while ( true ) {
			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE5_MOVES; ++mov_idx) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_edgcen();
				if (((dist2+1) % 3) != dist1) continue;
				cube2.copyTo (cube1);
				nDist++;
				dist1 = dist2;
				noMoves=false;
				break;
			}
			if( noMoves)
				break;
		}
		/* Can be removed for speedup */
		if( ( Tables.conjCenter5[cube1.center][cube1.sym] != 0 ) || ( cube1.edge != 0 ))
			System.out.println("Wrong pruning distance for stage 5 edges+centers");
		return nDist;
	}
}
