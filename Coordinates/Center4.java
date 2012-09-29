package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage4;

public final class Center4 extends RawCoord {

	public final static int N_COORD = 35;
	private final static int N_SYM = Stage4.N_SYM;
	private final static int N_MOVES = Stage4.N_MOVES;

	public static final int SolvedStates[] = { 0, 14, 20, 23, 27, 28 };

	/* Tables */
	private static final short[][] move = new short[N_COORD][N_MOVES];
	private static final short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		for (int s : SolvedStates)
			if (coord == s)
				return true;
		return false;
	}

	public int[] getSolvedStates(){
		return SolvedStates;
	}

	public int getSize(){
		return N_COORD;
	}

	/* Move */
	public void moveTo( int m, RawCoord c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CenterCubies cube)
	{
		int center = coord;
		int i;
		int r = 4;
		cube.cubies[7] = 1;
		for (i = 6; i >= 0; i--) {
			if ( center >= Util.Cnk[i][r] ) {
				center -= Util.Cnk[i][r--];
				cube.cubies[i] = 0;
			} else {
				cube.cubies[i] = 1;
			}
		}
		for (i = 8; i < 24; ++i) {
			cube.cubies[i] = (byte)(i/4);
		}
	}

	/* Pack a cube into the coord */
	private void pack (CenterCubies cube){
		int i;
		this.coord = 0;
		int r = 4;
		for (i = 6; i >= 0; i--) {
			if (cube.cubies[i] != cube.cubies[7]) {
				this.coord += Util.Cnk[i][r--];
			}
		}
	}

	/* Initialisations */
	public static void init(){
		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		Center4 c = new Center4();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)c.coord;
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.conjugate (s, cube2);
				c.pack( cube2 );
				conj[u][s] = (short)c.coord;
			}
		}
	}
}
