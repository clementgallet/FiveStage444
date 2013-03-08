package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Util;

public final class Edge2 extends RawCoord {

	public Edge2(){
		N_COORD = 420;
		N_SYM = Stage2.N_SYM;
		N_MOVES = Stage2.N_MOVES;
		solvedStates = new int[]{0, 414};
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
}
