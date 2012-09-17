package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage4;

public final class Corner4 {

	public final static int N_COORD = 420;
	private final static int N_SYM = Stage4.N_SYM;
	private final static int N_MOVES = Stage4.N_MOVES;

	/* Coordinates */
	public int coord;

	/* Tables */
	private static short[][] move = new short[N_COORD][N_MOVES];
	private static short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	/* Move */
	public void moveTo( int m, Corner4 c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CubeState cube)
	{
		int i;
		byte[] t6 = new byte[4];
		int cor_bm = this.coord / 6;
		Util.set4Perm (t6, this.coord % 6);
		int a = 0;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if (cor_bm >= Util.Cnk[i][r] ) {
				cor_bm -= Util.Cnk[i][r--];
				cube.m_cor[i] = (byte)a++;
			} else {
				cube.m_cor[i] = (byte)(4 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	private void pack (CubeState cube){
		int u = Util.get8Perm (cube.m_cor, 0);
		this.coord = Util.perm_to_420[u];
	}

	/* Initialisations */
	public static void init(){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Corner4 c = new Corner4();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCORNER (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.conjugateCorners (s, cube2);
				c.pack( cube2 );
				conj[u][s] = (short)(c.coord);
			}
		}
	}
}
