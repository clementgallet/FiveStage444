package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage1;

public final class Corner1 extends RawCoord {

	public final static int N_COORD = 2187;
	private final static int N_SYM = Stage1.N_SYM;
	private final static int N_MOVES = Moves.N_FACE_MOVES;

	/* Tables */
	private static final short[][] move = new short[N_COORD][N_MOVES];
	private static final short[][] conj = new short[N_COORD][N_SYM]; // (2187) 2187*48

	/* Check if solved */
	public boolean isSolved(int sym){
		return conj[coord][sym] == 2173;
	}

	public int[] getSolvedStates(){
		return new int[]{2173};
	}

	public int getSize(){
		return N_COORD;
	}

	/* Move */
	public void moveTo( int m, RawCoord c ){
		int face_move = Moves.stage2face[m];
		c.coord = ( face_move >= 0 ) ? move[coord][face_move] : coord;
	}

	/* Rotate the coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	void unpack(CornerCubies cube)
	{
		int i;
		int orientc = this.coord;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {
			int fo = orientc % 3;
			cube.cubies[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		cube.cubies[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	/* Pack a cube into the coord */
	public void pack (CornerCubies cube){
		this.coord = 0;
		for (int i = 0; i < 7; ++i) {
			this.coord = 3 * this.coord + (cube.cubies[i] >> 3);
		}
	}

	/* Initialisations */
	public static void init(){

		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		Corner1 c = new Corner1();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.face2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				cube2.deMirror ();
				c.pack( cube2 );
				conj[u][s] = (short)(c.coord);
			}
		}
	}
}
