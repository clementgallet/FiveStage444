package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage1;
import cg.fivestage444.Symmetry;

public final class Corner1 extends RawCoord {

	public Corner1(){
		N_COORD = 2187;
		N_SYM = Stage1.N_SYM;
		N_MOVES = Stage1.N_MOVES; // Change to face moves.
		solvedStates = new int[]{2173};
		cubieType = new CornerCubies();
		rightMultOrConjugate = RIGHTMULT;
		HASHCODE_MOVE = 1153856044;
		HASHCODE_CONJ = -428730808;
	}

	/* Unpack a coord to a cube */
	void unpack(Cubies cube, int coord)
	{
		int i;
		int orientc = coord;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {
			int fo = orientc % 3;
			cube.cubies[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		cube.cubies[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	/* Pack a cube into the coord */
	public int pack (Cubies cube){
		int coord = 0;
		for (int i = 0; i < 7; ++i) {
			coord = 3 * coord + (cube.cubies[i] >> 3);
		}
		return coord;
	}

	@Override
	public void init(){
		move = new short[N_COORD][N_MOVES];
		conj = new short[N_COORD][N_SYM];

		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();

		for (int u = 0; u < N_COORD; ++u) {
			unpack(cube1, u);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = (short) pack(cube2);
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				cube2.deMirror(); /* I need to override this method just to add this line... :( */
				conj[u][s] = (short) pack(cube2);
			}
		}
	}

}
