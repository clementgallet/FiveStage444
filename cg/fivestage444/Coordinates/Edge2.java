package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Util;
import cg.fivestage444.Moves;
import cg.fivestage444.Symmetry;

public final class Edge2 extends RawCoord {

	public Edge2(){
		N_COORD = 420;
		N_SYM = Stage2.N_SYM;
		N_MOVES = Stage2.N_MOVES;
		solvedStates = new int[]{414};
		cubieType = new EdgeCubies();
		rightMultOrConjugate = RIGHTMULT;
		HASHCODE_MOVE = -619515515;
		HASHCODE_CONJ = -1576838975;
	}

	/* Unpack a coord to a cube */
	public void unpack (Cubies cube, int coord)
	{
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = coord / 6;
		Util.set4Perm (t6, coord % 6);
		for (i = 0; i < 16; ++i)
			cube.cubies[i] = (byte)i;

		byte f = 16;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if ( edgeFbm >= Util.Cnk[i][r] ) {
				edgeFbm -= Util.Cnk[i][r--];
				cube.cubies[16 + i] = f++;
			} else {
				cube.cubies[16 + i] = (byte)(20 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	public int pack(Cubies cube){
		int u = Util.get8Perm (cube.cubies, 16);
		return Util.perm_to_420[u];
	}

	/* Initialisations */
	@Override
	public void init(){
		move = new short[N_COORD][N_MOVES];
		conj = new short[N_COORD][N_SYM];

		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		for (int u = 0; u < N_COORD; ++u) {
			unpack( cube1, u );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = (short)(pack( cube2 ));
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.symIdxMultiply[s & 0x04][Symmetry.invSymIdx[s & 0xfb]], cube2);
				cube2.leftMult (s & 0x04); // conjugate the mirror symmetry only
				conj[u][s] = (short) pack(cube2);
			}
		}
	}

}
