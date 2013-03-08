package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage1;
import cg.fivestage444.Util;

public final class Edge1 extends SymCoord {

	public Edge1(){
		N_COORD = 15582;
		N_RAW_COORD = 735471;
		N_SYM = Stage1.N_SYM;
		SYM_SHIFT = 6;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage1.N_MOVES;

		SolvedStates = new int[]{0};
		cubieType = new EdgeCubies();
		rightMultOrConjugate = RIGHTMULT;
		HASHCODE_RAW2SYM = -960431297;
		HASHCODE_SYM2RAW = -1678978061;
		HASHCODE_MOVE = -20502585;
	}

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		int c = raw_coord;
		int r = 8;
		byte lrfb = 0;
		byte ud = 16;
		for (int i=23; i>=0; i--) {
			if (c >= Util.Cnk[i][r]) {
				c -= Util.Cnk[i][r--];
				cube.cubies[i] = ud++;
			} else {
				cube.cubies[i] = lrfb++;
			}
		}
	}

	/* Pack a cube into the raw coord */
	public int pack(Cubies cube){
		int raw_coord = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (cube.cubies[i] >= 16) {
				raw_coord += Util.Cnk[i][r--];
			}
		}
		return raw_coord;
	}
}
