package cg.fivestage444;

import static cg.fivestage444.Constants.Cnk;

public final class CubeStage1 {

	public int corner;
	public int edge;
	public int sym;

	public static PruningStage1 prune_table;

	public final void copyTo (CubeStage1 cube1){
		cube1.corner = corner;
		cube1.edge = edge;
		cube1.sym = sym;
	}

	public final void do_move (int move_code){
		if ((Constants.stage1_slice_moves[move_code]/3)%3 != 1 )
			corner = Tables.move_table_co[corner][Constants.basic_to_face[move_code]];

		int newEdge = Tables.move_table_symEdgeSTAGE1[edge][Symmetry.moveConjugate1[move_code][sym]];

		sym = Symmetry.symIdxMultiply[newEdge & 0x3F][sym];
		edge = newEdge >> 6 ;
	}

	public boolean is_solved (){
		if (( edge == 0 ) && Tables.move_table_co_conj[corner][sym] == 1906)
			return true;
		return false;
	}

	/* Pruning functions */

	private final int get_idx (){
		return Constants.N_CORNER_ORIENT * edge + Tables.move_table_co_conj[corner][sym];
	}

	public final int get_dist (){
		return prune_table.get_dist_packed(get_idx());
	}

	public final int new_dist (int dist){
		return prune_table.new_dist(get_idx(), dist);
	}

	public int getDistance (){
		CubeStage1 cube1 = new CubeStage1();
		CubeStage1 cube2 = new CubeStage1();
		int mov_idx, j, dist1, dist2;
		int nDist = 0;

		copyTo (cube1);
		dist1 = cube1.get_dist();

		while( true ) {

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE1_MOVES; ++mov_idx) {
				cube1.copyTo (cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist();
				if (((dist2+1) % 3) != dist1) continue; // If distance is not lowered by 1, continue.
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
