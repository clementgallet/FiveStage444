package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Corner1 {

	final static int N_COORD = 2187;
	final static int N_SYM = 48;
	final static int N_MOVES = 36;
	final static int N_FACE_MOVES = 18;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_FACE_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM]; // (2187) 2187*48

	/* Check if solved */
	public boolean isSolved(int sym){
		return conj[coord][sym] == 1906;
	}

	/* Move */
	public void moveTo( int m, Corner1 c ){
		int face_move = stage2face[m];
		c.coord = ( face_move >= 0 ) ? move[coord][face_move] : coord;
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int i;
		int orientc = this.coord;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {
			int fo = orientc % 3;
			cube.m_cor[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		cube.m_cor[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		this.coord = 0;
		for (int i = 0; i < 7; ++i) {
			this.coord = 3 * this.coord + (cube.m_cor[i] >> 3);
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
				if(stage2face[m] == -1 )
					continue;
				cube1.rotate_sliceCORNER (stage2moves[mc], cube2);
				this.pack( cube2 );
				move[u][stage2face[mc]] = coord;
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
				cube1.rightMultCorners (Symmetry.invSymIdx[sym], cube2);
				cube2.deMirrorCorners ();
				this.pack( cube2 );
				conj[u][sym] = coord;
			}
		}
	}
}
