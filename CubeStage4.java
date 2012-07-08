package cg.fivestage444;

public final class CubeStage4 {

	public int center; //center coordinate (70)
	public int corner; //corner coordinate	(420)
	public int edge; //sym edge coordinate (5968*16)
	public int sym;

	public static PruningStage4 prune_table;
	public static PruningStage4EdgCen prune_table_edgcen;
	public static PruningStage4EdgCor prune_table_edgcor;

	public final void copyTo (CubeStage4 cube1){
		cube1.edge = edge;
		cube1.sym = sym;
		cube1.corner = corner;
		cube1.center = center;
	}

	public final void do_move (int move_code){
		center = Tables.moveCenter4[center][move_code];
		corner = Tables.moveCorner4[corner][move_code];

		int newEdge = Tables.moveEdge4[edge][Symmetry.moveConjugate4[move_code][sym]];

		sym = Symmetry.symIdxMultiply[newEdge & 0xF][sym];
		edge = newEdge >> 4;
	}

	public boolean is_solved (){
		int i;

		if (corner != 0) {
			return false;	//not solved if wrong corner value
		}
		if (edge != 0) {
			return false;	//not solved if wrong edge value
		}
		for (i = 0; i < Constants.stage4_solved_centers_bm.length; ++i)
			if (center == Constants.stage4_solved_centers_bm[i])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	public boolean edges_corners_solved (){
		return (corner == 0 && edge == 0);
	}

	/* Convert functions */

	/* Pruning functions */

	public final int get_idx (){
		return (( edge * Constants.N_STAGE4_CORNERS + Tables.conjCorner4[corner][sym] ) * Constants.N_STAGE4_CENTERS ) + Tables.conjCenter4[center][sym];
	}

	public final int get_dist (){
		return prune_table.get_dist_packed(get_idx());
	}

	public final int new_dist (int dist){
		return prune_table.new_dist(get_idx(), dist);
	}

	public final int get_dist_edgcor (){
		int idx = edge * Constants.N_STAGE4_CORNERS + Tables.conjCorner4[corner][sym];
		return prune_table_edgcor.get_dist_packed(idx);
	}

	public final int new_dist_edgcor (int dist){
		int idx = edge * Constants.N_STAGE4_CORNERS + Tables.conjCorner4[corner][sym];
		return prune_table_edgcor.new_dist(idx, dist);
	}

	public int getDistance (){
		CubeStage4 cube1 = new CubeStage4();
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j, dist1, dist2;
		int nDist = 0;

		copyTo(cube1);
		dist1 = cube1.get_dist();

		while( ! cube1.is_solved ()) {

			boolean noMoves = true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE4_MOVES; ++mov_idx) {
				cube1.copyTo(cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist();
				if (((dist2+1) % 3) != dist1) continue;
				cube2.copyTo(cube1);
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
	public int getDistanceEdgCor (){
		CubeStage4 cube1 = new CubeStage4();
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j, dist1, dist2;
		int nDist = 0;

		copyTo(cube1);
		dist1 = cube1.get_dist_edgcor();

		while( true ) {

			boolean noMoves = true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE4_MOVES; ++mov_idx) {
				cube1.copyTo(cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_edgcor();
				if (((dist2+1) % 3) != dist1) continue;
				cube2.copyTo(cube1);
				nDist++;
				dist1 = dist2;
				noMoves = false;
				break;
			}
			if( noMoves){
				break;
			}
		}
		return nDist;
	}
}
