package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Symmetry;

public abstract class RawCoord {

	public static final int RIGHTMULT = 0;
	public static final int CONJUGATE = 1;

	public int N_COORD;
	public int N_SYM;
	public int N_MOVES;
	public int[] solvedStates;
	public int rightMultOrConjugate;

	/* Tables */
	public short[][] move;
	public short[][] conj;

	public Cubies cubieType;

	abstract void unpack(Cubies cube, int coord);
	abstract int pack(Cubies cube);

	public void init(){
		move = new short[N_COORD][N_MOVES];
		conj = new short[N_COORD][N_SYM];

		Cubies cube1 = null;
		Cubies cube2 = null;
		try {
			cube1 = cubieType.getClass().newInstance();
			cube2 = cubieType.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		for (int u = 0; u < N_COORD; ++u) {
			unpack(cube1, u);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = (short) pack(cube2);
			}
			for (int s = 0; s < N_SYM; ++s) {
				if(rightMultOrConjugate == RIGHTMULT)
					cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				if(rightMultOrConjugate == CONJUGATE)
					cube1.conjugate (s, cube2);
				conj[u][s] = (short) pack(cube2);
			}
		}
	}

}
