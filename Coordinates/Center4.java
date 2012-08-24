package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Center4 {

	final static int N_COORD = 35;
	final static int N_SYM = 16;
	final static int N_MOVES = 16;

	static final int SOLVED[] = { 0, 14, 20, 23, 27, 28 };

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[N_COORD][N_MOVES];
	public static short[][] conj = new short[N_COORD][N_SYM];

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

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int center = coord;
		int i;
		int r = 4;
		cube.m_cen[7] = 1;
		for (i = 6; i >= 0; i--) {
			if ( center >= Cnk[i][r] ) {
				center -= Cnk[i][r--];
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
	public void pack (CubeState cube){
		int i;
		this.coord = 0;
		int r = 4;
		for (i = 6; i >= 0; i--) {
			if (m_cen[i] != m_cen[7]) {
				this.coord += Cnk[i][r--];
			}
		}
	}

	/* Initialisations */
	public static void init(){
		initMove();
		initConj();
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = u;
			this.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (stage2moves[m], cube2);
				this.pack( cube2 );
				move[u][m] = coord;
			}
		}
	}

	public static void initConj (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = u;
			this.unpack( cube1 );
			for (int sym = 0; sym < N_SYM; ++sym) {
				cube1.conjugateCenters (sym, cube2);
				this.pack( cube2 );
				conj[u][sym] = coord;
			}
		}
	}
}
