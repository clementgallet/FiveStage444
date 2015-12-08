package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Util;

public final class Edge3 extends RawCoord {

	public Edge3(){
		N_COORD = 12870;
		N_SYM = Stage3.N_SYM;
		N_MOVES = Stage3.N_MOVES;
		solvedStates = new int[]{494, 12375}; /* ok for pruning, but for stage solve, it's only 12375 ! */
		cubieType = new EdgeCubies();
		rightMultOrConjugate = RIGHTMULT;
		HASHCODE_MOVE = 629540689;
		HASHCODE_CONJ = -540377615;
	}

	/* Unpack a coord to a cube */
	public void unpack (Cubies cube, int coord)
	{
		int edge = coord;
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Util.Cnk[i][r]) {
				edge -= Util.Cnk[i][r--];
				cube.cubies[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				cube.cubies[i] = e1++;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.cubies[i] = (byte)i;
		}
	}

	/* Pack a cube into the coord */
	public int pack(Cubies cube){
		int coord = 0;
		int r = 8;
		for (int i=15; i>=0; i--) {
			if (cube.cubies[i] < 4 || cube.cubies[i] >= 12) {
				coord += Util.Cnk[i][r--];
			}
		}
		return coord;
	}

}
