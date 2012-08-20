package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Corner1 {

	static int MAX_COORD = 2187;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_FACE_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM_STAGE1]; // (2187) 2187*48

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
	public void toCube (CubeState cube)
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
	public void pack (CubeState c){
		this.coord = 0;
		for (int i = 0; i < 7; ++i) {
			this.coord = 3 * this.coord + (c.m_cor[i] >> 3);
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
		for (int u = 0; u < MAX_COORD; ++u) {
			this.coord = u;
			this.toCube( cube1 );
			for (int mc = 0; mc < N_STAGE1_MOVES; ++mc) {
				if(stage2face[mc] == -1 )
					continue;
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage2moves[mc]);
				this.pack( cube2 );
				move[u][stage2face[mc]] = coord;
			}
		}
	}

	public static void initConj (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < MAX_COORD; ++u) {
			this.coord = u;
			this.toCube( cube1 );
			for (int sym = 0; sym < N_SYM_STAGE1; ++sym) {
				cube1.rightMultCorners (Symmetry.invSymIdx[sym], cube2);
				cube2.deMirrorCorners ();
				this.pack( cube2 );
				conj[u][sym] = coord;
			}
		}
	}
}
