package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Util;

public final class Center2 extends SymCoord {

	public Center2(){
		N_COORD = 1612515;
		N_RAW_COORD = 25741485;
		N_SYM = Stage2.N_SYM;
		SYM_SHIFT = 4;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage2.N_MOVES;

		SolvedStates = new int[]{ 122, 242, 243, 245, 246, 247 }; // FIXME!
		cubieType = new CenterCubies();
		rightMultOrConjugate = RIGHTMULT;
		HASHCODE_RAW2SYM = -1;
		HASHCODE_SYM2RAW = -1;
		HASHCODE_MOVE = -1;
	}

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		int cenbm = raw_coord/35;
		int cenbm4of8 = raw_coord % 35;
		int udlr = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		for (int i = 23; i >= 0; --i) {
			if (cenbm < Util.Cnk[i][r] ) {
				cube.cubies[i] = (byte)(udlr++/4);
			} else {
				cenbm -= Util.Cnk[i][r--];
				if (cenbm4of8 < Util.Cnk[j][s]) {
					cube.cubies[i] = 5;
				} else {
					cenbm4of8 -= Util.Cnk[j][s--];
					cube.cubies[i] = 4;
				}
			j--;
			}
		}
	}

	/* Pack a cube into the raw coord */
	public int pack(Cubies cube){
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 23; i >= 0; i--) {
			if (cube.cubies[i] >= 4) {
				if (last_c == -1)
					last_c = i;
				cenbm += Util.Cnk[i][r--];
				if (cube.cubies[i] != cube.cubies[last_c]) {
					cenbm4of8 += Util.Cnk[j][s--];
				}
				--j;
			}
		}
		return 35*cenbm + cenbm4of8;
	}
}
