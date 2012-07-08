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
		center = Tables.move_table_cenSTAGE5[center][sqs_move_code];
		corner = Tables.move_table_cornerSTAGE5[corner][sqs_move_code];

		int newEdge = Tables.move_table_symEdgeSTAGE5[edge][Symmetry.moveConjugate5[sqs_move_code][sym]];

		sym = Symmetry.symIdxCo4Multiply[sym][newEdge&0xFF];
		edge = newEdge >> 8;
	}

	public boolean is_solved (){

		if ( edge == 0 && Tables.move_table_cen_conjSTAGE5[center][sym] == 0 && Tables.move_table_corner_conjSTAGE5[corner][sym] == 0 ) {
			return true;
		}
		return false;
	}

	public boolean edges_centers_solved (){

		if (Tables.move_table_cen_conjSTAGE5[center][sym] == 0 && edge == 0) {
			return true;
		}
		return false;
	}

	/* Pruning functions */

	public final int get_dist_edgcen (){
		int idx = edge * Constants.N_STAGE5_CENTER_PERM + Tables.move_table_cen_conjSTAGE5[center][sym];
		return prune_table_edgcen.get_dist_packed(idx);
	}

	public final int new_dist_edgcen (int dist){
		int idx = edge * Constants.N_STAGE5_CENTER_PERM + Tables.move_table_cen_conjSTAGE5[center][sym];
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
			//System.out.println(cube1.center+","+cube1.corner+","+cube1.edge+","+cube1.sym);
			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE5_MOVES; ++mov_idx) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_edgcen();
				//System.out.println("dists:"+dist1+","+dist2);
				if (((dist2+1) % 3) != dist1) continue;
				cube2.copyTo (cube1);
				nDist++;
				dist1 = dist2;
				noMoves=false;
				break;
			}
			if( noMoves){
				break;
			}
		}
		return nDist;
	}
}
