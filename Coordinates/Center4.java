package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage4;

public final class Center4 {

	public final static int N_COORD = 35;
	private final static int N_SYM = Stage4.N_SYM;
	private final static int N_MOVES = Stage4.N_MOVES;

	public static final int SOLVED[] = { 0, 14, 20, 23, 27, 28 };

	/* Coordinates */
	public int coord;

	/* Tables */
	private static short[][] move = new short[N_COORD][N_MOVES];
	private static short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		for (int i=0; i < SOLVED.length; i++)
			if( coord == SOLVED[i])
				return true;
		return false;
	}

	/* Move */
	public void moveTo( int m, Center4 c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CubeState cube)
	{
		int center = coord;
		int i;
		int r = 4;
		cube.m_cen[7] = 1;
		for (i = 6; i >= 0; i--) {
			if ( center >= Util.Cnk[i][r] ) {
				center -= Util.Cnk[i][r--];
				cube.m_cen[i] = 0;
			} else {
				cube.m_cen[i] = 1;
			}
		}
		for (i = 8; i < 24; ++i) {
			cube.m_cen[i] = (byte)(i/4);
		}
	}

	/* Pack a cube into the coord */
	private void pack (CubeState cube){
		int i;
		this.coord = 0;
		int r = 4;
		for (i = 6; i >= 0; i--) {
			if (cube.m_cen[i] != cube.m_cen[7]) {
				this.coord += Util.Cnk[i][r--];
			}
		}
	}

	/* Initialisations */
	public static void init(){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center4 c = new Center4();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)c.coord;
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.conjugateCenters (s, cube2);
				c.pack( cube2 );
				conj[u][s] = (short)c.coord;
			}
		}
	}
}
