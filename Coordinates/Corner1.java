package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage1;

public final class Corner1 {

	public final static int N_COORD = 2187;
	private final static int N_SYM = Stage1.N_SYM;
	private final static int N_MOVES = Stage1.N_MOVES;
	private final static int N_FACE_MOVES = 18;
	private static byte stage2face[] = new byte[N_MOVES];

	/* Coordinates */
	public int coord;

	/* Tables */
	private static short[][] move = new short[N_COORD][N_FACE_MOVES];
	private static short[][] conj = new short[N_COORD][N_SYM]; // (2187) 2187*48

	/* Check if solved */
	public boolean isSolved(int sym){
		return conj[coord][sym] == 2173;
	}

	/* Move */
	public void moveTo( int m, Corner1 c ){
		int face_move = stage2face[m];
		c.coord = ( face_move >= 0 ) ? move[coord][face_move] : coord;
	}

	/* Rotate the coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	public void unpack (CornerCubies cube)
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
		/* Initialize stage2face table */
		for( int s = 0; s < N_MOVES; s++ ){
			int m = Moves.stage2moves[s];
			if((( m / 3 ) % 3 ) == 1 )
				stage2face[s] = -1;
			else
				stage2face[s] = (byte)(( m / 9 ) * 3 + ( m % 3 ));
		}

		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		Corner1 c = new Corner1();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				if(stage2face[m] == -1 )
					continue;
				cube1.move (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][stage2face[m]] = (short)(c.coord);
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
