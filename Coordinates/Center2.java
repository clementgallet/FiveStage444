package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Util;

public final class Center2 extends SymCoord {

	public Center2(){
		N_COORD = 716;
		N_RAW_COORD = 10626;
		N_SYM = Stage2.N_SYM;
		SYM_SHIFT = 4;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage2.N_MOVES;

		SolvedStates = new int[]{ 122, 242, 243, 245, 246, 247 };
		cubieType = new CenterCubies();
	}

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		int center = raw_coord;
		int r = 4;
		byte udlrf = 0;
		for (int i=23; i>=0; i--) {
			if (center >= Util.Cnk[i][r]) {
				center -= Util.Cnk[i][r--];
				cube.cubies[i] = 5;
			} else {
				cube.cubies[i] = (byte)(udlrf++/4);
			}
		}
	}

	/* Pack a cube into the raw coord */
	/* TODO: Deal with this center thing. */
	public int pack(Cubies cube, int c){
		int raw_coord = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (cube.cubies[i] == c) {
				raw_coord += Util.Cnk[i][r--];
			}
		}
		return raw_coord;
	}

	/* Pack a cube into the raw coord */
	public int pack(Cubies cube){
		return pack(cube, 5);
	}
}
