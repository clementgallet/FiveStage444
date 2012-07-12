package cg.fivestage444;

public final class CubeStage5 {

	public int edge;
	public int sym;
	public int center;
	public int corner;

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

}
