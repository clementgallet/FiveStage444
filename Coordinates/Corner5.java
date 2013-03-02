package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage5;

public final class Corner5 extends RawCoord {

	public final static int N_COORD = 96;
	private final static int N_SYM = Stage5.N_SYM;
	private final static int N_MOVES = 6;

	/* Tables */
	private static final short[][] move = new short[N_COORD][N_MOVES];
	private static final short[][] conj = new short[N_COORD][N_SYM*4];

	/* Check if solved */
	public boolean isSolved( int sym ){
		return conj[coord][sym] == 0;
	}

	public int[] getSolvedStates(){
		return new int[]{0};
	}

	public int getSize(){
		return N_COORD;
	}

	/* Move */
	public void moveTo( int m, RawCoord c ){
		int face_move = Moves.stage2face[m];
		c.coord = ( face_move >= 0 ) ? move[coord][face_move] : coord;
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CornerCubies cube)
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
		Util.set4Perm (cube.cubies, this.coord/4);
		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[this.coord/4]][this.coord % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+4] = (byte)(t[i]+4);
		}
	}

	/* Pack a cube into the coord */
	public void pack(CornerCubies cube){
		this.coord = 4*Util.get4Perm (cube.cubies, 0) + (cube.cubies[4] - 4);
	}

	/* Initialisations */
	public static void init(){
		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		Corner5 c = new Corner5();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.face2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					cube1.rightMult(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMult(s);
					c.pack( cube2 );
					conj[u][(s<<2)|cs] = (short)(c.coord);
				}
			}
		}
	}
}
