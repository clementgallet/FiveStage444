package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage1;

public final class Corner1 extends RawCoord {

	public Corner1(){
		N_COORD = 2187;
		N_SYM = Stage1.N_SYM;
		N_MOVES = Stage1.N_MOVES; // Change to face moves.
		solvedStates = new int[]{2173};
		cubieType = new CornerCubies();
		rightMultOrConjugate = RIGHTMULT;
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
}
