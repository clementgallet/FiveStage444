package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage5;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

public final class Corner5 extends RawCoord {

	public Corner5(){
		N_COORD = 96;
		N_SYM = Stage5.N_SYM * 4;
		N_MOVES = Stage5.N_MOVES; // Was 6. TODO: Change back to face moves.
		solvedStates = new int[]{0};
		cubieType = new CornerCubies();
		rightMultOrConjugate = CONJUGATE;
		HASHCODE_MOVE = 1753223873;
		HASHCODE_CONJ = 566953985;
	}

	/* Unpack a coord to a cube */
	@Override
	public void unpack (Cubies cube, int coord)
	{
		final int sqs_rep_to_perm[][] = {
			{  0,  7, 16, 23 },
			{  1,  6, 17, 22 },
			{  2, 10, 13, 21 },
			{  3, 11, 12, 20 },
			{  4,  8, 15, 19 },
			{  5,  9, 14, 18 }
		};

		final int sqs_perm_to_rep[] = {
			0, 1, 2, 3, 4, 5,
			1, 0, 4, 5, 2, 3,
			3, 2, 5, 4, 0, 1,
			5, 4, 3, 2, 1, 0
		};
		int i;
		byte[] t = new byte[4];
		Util.set4Perm (cube.cubies, coord/4);
		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[coord/4]][coord % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+4] = (byte)(t[i]+4);
		}
	}

	/* Pack a cube into the coord */
	@Override
	public int pack(Cubies cube){
		return 4*Util.get4Perm (cube.cubies, 0) + (cube.cubies[4] - 4);
	}

	/* Initialisations */
	@Override
	public void init(){
		move = new short[N_COORD][N_MOVES];
		conj = new short[N_COORD][N_SYM];

		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		for (int u = 0; u < N_COORD; ++u) {
			unpack( cube1, u );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.face2moves[m], cube2);
				move[u][m] = (short)(pack( cube2 ));
			}
			for (int s = 0; s < N_SYM/4; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					cube1.rightMult(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMult(s);
					conj[u][(s<<2)|cs] = (short)(pack( cube2 ));
				}
			}
		}
	}
}
