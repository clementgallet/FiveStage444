package cg.fivestage444;

import static cg.fivestage444.Constants.Cnk;

public final class CubeStage1 {

	public int corner;
	public int edge;
	public int sym;

	public final void copyTo (CubeStage1 cube1){
		cube1.corner = corner;
		cube1.edge = edge;
		cube1.sym = sym;
	}

	public final void do_move (int move_code){
		if ((Constants.stage1_slice_moves[move_code]/3)%3 != 1 )
			corner = Tables.moveCorner1[corner][Constants.basic_to_face[move_code]];

		int newEdge = Tables.moveEdge1[edge][Symmetry.moveConjugate1[move_code][sym]];

		sym = Symmetry.symIdxMultiply[newEdge & 0x3F][sym];
		edge = newEdge >> 6 ;
	}

	public boolean is_solved (){
		if (( edge == 0 ) && Tables.conjCorner1[corner][sym] == 1906)
			return true;
		return false;
	}

}
