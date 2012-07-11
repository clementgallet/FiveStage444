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
		return Tables.get_dist_packed(prune_table_edgcen.ptable_packed, idx);
	}

	public final int new_dist_edgcen (int dist){
		int idx = edge * Constants.N_STAGE5_CENTERS + Tables.conjCenter5[center][sym];
		return Tables.new_dist(prune_table_edgcen.ptable_packed, idx, dist);
	}

	public int getDistanceEdgCen (){
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;
		
		int edge1 = edge;
		int sym1 = sym;
		int center1 = center;
		dist1 = Tables.get_dist_packed(prune_table_edgcen.ptable_packed, edge1*Constants.N_STAGE5_CENTERS+Tables.conjCenter5[center1][sym1]);

		while ( true ) {
			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE5_MOVES; ++mov_idx) {
				int center2 = Tables.moveCenter5[center1][mov_idx];
				int edge2 = Tables.moveEdge5[edge1][Symmetry.moveConjugate5[mov_idx][sym1]];
				int sym2 = Symmetry.symIdxCo4Multiply[sym1][edge2&0xFF];
				edge2 >>= 8;
				dist2 = Tables.get_dist_packed(prune_table_edgcen.ptable_packed, edge2*Constants.N_STAGE5_CENTERS+Tables.conjCenter5[center2][sym2]);
				if (((dist2+1) % 3) != dist1) continue;
				center1 = center2;
				edge1 = edge2;
				sym1 = sym2;
				dist1 = dist2;
				nDist++;
				noMoves=false;
				break;
			}
			if( noMoves)
				break;
		}
		/* Can be removed for speedup */
		if( ( Tables.conjCenter5[center1][sym1] != 0 ) || ( edge1 != 0 ))
			System.out.println("Wrong pruning distance for stage 5 edges+centers");
		return nDist;
	}
}
