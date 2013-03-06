package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Util;

public final class Center3 extends SymCoord {

	public Center3(){
		N_COORD = 56980;
		N_RAW_COORD = 450450;
		N_SYM = Stage3.N_SYM;
		SYM_SHIFT = 3;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage3.N_MOVES;

		SolvedStates = new int[]{ 56966, 56974, 56975, 56977, 56978, 56979 };
		cubieType = new CenterCubies();
	}

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		int cenbm = raw_coord/35;
		int cenbm4of8 = raw_coord % 35;
		int ud = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; --i) {
			if (cenbm < Util.Cnk[i][r] ) {
				cube.cubies[i] = (byte)(ud++/4);
			} else {
				cenbm -= Util.Cnk[i][r--];
				if (last_c == -1){
					cube.cubies[i] = 3;
					last_c = i;
				}
				if (cenbm4of8 < Util.Cnk[j][s]) {
					cube.cubies[i] = 3;
				} else {
					cenbm4of8 -= Util.Cnk[j][s--];
					cube.cubies[i] = 2;
				}
			j--;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.cubies[i] = (byte)(i/4);
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
		for (int i = 15; i >= 0; i--) {
			if (cube.cubies[i] >= 2) {
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
