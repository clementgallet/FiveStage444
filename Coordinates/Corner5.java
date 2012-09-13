package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;

public final class Corner5 {

	public final static int N_COORD = 96;
	final static int N_SYM = 48;
	final static int N_MOVES = 12;

	/* Coordinates */
	public int coord;

	/* Tables */
	public static short[][] move = new short[N_COORD][N_MOVES];
	public static short[][] conj = new short[N_COORD][N_SYM*4];

	/* Check if solved */
	public boolean isSolved( int sym ){
		return conj[coord][sym] == 0;
	}

	/* Move */
	public void moveTo( int m, Corner5 c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
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
		byte[] old_m_cor = new byte[8];
		byte[] t = new byte[4];
		Util.set4Perm (old_m_cor, this.coord/4);
		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[this.coord/4]][this.coord % 4]);
		for (i = 0; i < 4; ++i) {
			old_m_cor[i+4] = (byte)(t[i]+4);
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		final byte sqs_to_std_cor[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		for (i = 0; i < 8; ++i) {
			cube.m_cor[sqs_to_std_cor[i]] = sqs_to_std_cor[old_m_cor[i]];
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.

		final byte std_to_sqs_cor[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		byte[] new_m_cor = new byte[8];
		for (i = 0; i < 8; ++i) {
			new_m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[cube.m_cor[i]];
		}

		this.coord = 4*Util.get4Perm (new_m_cor, 0) + (new_m_cor[4] - 4);
	}

	/* Initialisations */
	public static void init(){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Corner5 c = new Corner5();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCORNER (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					cube1.rightMultCorners(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMultCorners(s);
					c.pack( cube2 );
					conj[u][(s<<2)|cs] = (short)(c.coord);
				}
			}
		}
	}
}
