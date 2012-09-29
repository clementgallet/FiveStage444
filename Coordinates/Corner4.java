package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage4;

public final class Corner4 extends RawCoord {

	public final static int N_COORD = 420;
	private final static int N_SYM = Stage4.N_SYM;
	private final static int N_MOVES = 10;

	/* Tables */
	private static final short[][] move = new short[N_COORD][N_MOVES];
	private static final short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
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
				cube.cubies[i] = (byte)a++;
			} else {
				cube.cubies[i] = (byte)(4 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	private void pack (CornerCubies cube){
		int u = Util.get8Perm (cube.cubies, 0);
		this.coord = Util.perm_to_420[u];
	}

	/* Initialisations */
	public static void init(){
		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		Corner4 c = new Corner4();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.face2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.conjugate (s, cube2);
				c.pack( cube2 );
				conj[u][s] = (short)(c.coord);
			}
		}
	}
}
