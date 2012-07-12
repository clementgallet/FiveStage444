package cg.fivestage444;

public final class CubeStage4 {

	public int center; //center coordinate (70)
	public int corner; //corner coordinate	(420)
	public int edge; //sym edge coordinate (5968*16)
	public int sym;

	public static PruningStage4EdgCen prune_table_edgcen;

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

}
