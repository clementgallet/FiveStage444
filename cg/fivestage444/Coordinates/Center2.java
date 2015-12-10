package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Util;
import cg.fivestage444.Moves;
import cg.fivestage444.Symmetry;

public final class Center2 extends SymCoord {

	public Center2(){
		N_COORD = 1612515;
		N_RAW_COORD = 25741485;
		N_SYM = Stage2.N_SYM;
		SYM_SHIFT = 4;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage2.N_MOVES;

		SolvedStates = new int[]{ 56962, 56970, 56971, 56973, 56974, 56975 };
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

	@Override
	public void initSym2Raw (){
		sym2raw = new int[N_COORD];
		raw2sym = new int[N_RAW_COORD];
		int repIdx = 0;
		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit(isRepTable, u)) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			unpack(cube1, u);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.symIdxMultiply[s & 0x04][Symmetry.invSymIdx[s & 0xfb]], cube2);
				cube2.leftMult (s & 0x04); // conjugate the mirror symmetry only
				int raw_coord = pack(cube2);
				Util.set1bit( isRepTable, raw_coord );
				raw2sym[raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( raw_coord == u )
					hasSym[repIdx] |= (0x1L << s);
			}
			sym2raw[repIdx++] = u;
		}
		if(repIdx != N_COORD) {
			System.out.println("The number of sym coordinates is not correct: " + repIdx);
		}
	}
}
