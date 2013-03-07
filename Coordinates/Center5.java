package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage5;
import cg.fivestage444.Symmetry;

public final class Center5 extends RawCoord {

	private static final short squares_cen_map[] = { 15, 60, 85, 90, 102, 105, 150, 153, 165, 170, 195, 240 };
	private static final byte[] squares_cen_revmap = new byte[256];

	public Center5(){
		N_COORD = 12*12*12;
		N_SYM = Stage5.N_SYM * 4;
		N_MOVES = Stage5.N_MOVES;
		solvedStates = new int[]{0};
		rightMultOrConjugate = CONJUGATE;
	}

	/* Unpack a coord to a cube */
	@Override
	public void unpack (Cubies cube, int coord)
	{
		int cen1 = coord % 12;
		int cen2 = (coord/12) % 12;
		int cen3 = coord/(12*12);
		int x = (squares_cen_map[cen1] << 16) | (squares_cen_map[cen2] << 8) | squares_cen_map[cen3];
		int b = 0x800000;
		for (int i = 0; i < 24; ++i) {
			cube.cubies[i] = (byte) (2*(i/8) + ((x & b) == 0 ? 0 : 1));
			b >>= 1;
		}
	}

	/* Pack a cube into the coord */
	@Override
	public int pack(Cubies cube){
		int x = 0;
		int b = 0x800000;
		for (int i = 0; i < 24; ++i) {
			if ((cube.cubies[i] & 0x1) != 0) {
				x |= b;
			}
			b >>= 1;
		}
		int cen1 = squares_cen_revmap[(x >> 16) & 0xFF];
		int cen2 = squares_cen_revmap[(x >> 8) & 0xFF];
		int cen3 = squares_cen_revmap[x & 0xFF];
		return cen1 + 12*cen2 + 12*12*cen3;
	}

	/* Initialisations */
	@Override
	public void init(){
		for (int i = 0; i < 12; ++i) {
			squares_cen_revmap[squares_cen_map[i]] = (byte)i;
		}
		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		for (int u = 0; u < N_COORD; ++u) {
			unpack( cube1, u );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = (short)(pack( cube2 ));
			}
			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					cube1.rightMult(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMult(s);
					conj[u][(s<<2)|cs] = (short)(pack( cube2 ));
				}
			}
		}
	}
}
