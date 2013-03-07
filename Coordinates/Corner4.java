package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Util;

public final class Corner4 extends RawCoord {

	public Corner4(){
		N_COORD = 420;
		N_SYM = Stage4.N_SYM;
		N_MOVES = Stage4.N_MOVES; // Was 10, TODO: change back to face moves.
		solvedStates = new int[]{0};
		cubieType = new CornerCubies();
		rightMultOrConjugate = CONJUGATE;
	}

	/* Unpack a coord to a cube */
	@Override
	public void unpack (Cubies cube, int coord)
	{
		int i;
		byte[] t6 = new byte[4];
		int cor_bm = coord / 6;
		Util.set4Perm (t6, coord % 6);
		int a = 0;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if (cor_bm >= Util.Cnk[i][r] ) {
				cor_bm -= Util.Cnk[i][r--];
				cube.cubies[i] = (byte)a++;
			} else {
				cube.cubies[i] = (byte)(4 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	@Override
	public int pack(Cubies cube){
		int u = Util.get8Perm (cube.cubies, 0);
		return Util.perm_to_420[u];
	}
}
