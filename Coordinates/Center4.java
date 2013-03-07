package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Util;

public final class Center4 extends RawCoord {

	public Center4(){
		N_COORD = 35;
		N_SYM = Stage4.N_SYM;
		N_MOVES = Stage4.N_MOVES;
		solvedStates = new int[]{ 0, 14, 20, 23, 27, 28 };
		cubieType = new CenterCubies();
		rightMultOrConjugate = CONJUGATE;
	}

	/* Unpack a coord to a cube */
	@Override
	public void unpack (Cubies cube, int coord)
	{
		int center = coord;
		int i;
		int r = 4;
		cube.cubies[7] = 1;
		for (i = 6; i >= 0; i--) {
			if ( center >= Util.Cnk[i][r] ) {
				center -= Util.Cnk[i][r--];
				cube.cubies[i] = 0;
			} else {
				cube.cubies[i] = 1;
			}
		}
		for (i = 8; i < 24; ++i) {
			cube.cubies[i] = (byte)(i/4);
		}
	}

	/* Pack a cube into the coord */
	@Override
	public int pack(Cubies cube){
		int i;
		int coord = 0;
		int r = 4;
		for (i = 6; i >= 0; i--) {
			if (cube.cubies[i] != cube.cubies[7]) {
				coord += Util.Cnk[i][r--];
			}
		}
		return coord;
	}
}
