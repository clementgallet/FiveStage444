package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Edge2 {

	final static int N_COORD = 420;
	final static int N_SYM = 16;
	final static int N_MOVES = 28;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return ( coord == 0 ) || ( coord == 414 );
	}

	/* Move */
	public void moveTo( int m, Edge2 e ){
		e.coord =  move[coord][m];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = this.coord / 6;
		Constants.set4Perm (t6, this.coord % 6);
		for (i = 0; i < 16; ++i)
			cube.m_edge[i] = (byte)i;

		byte f = 16;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if ( edgeFbm >= Cnk[i][r] ) {
				edgeFbm -= Cnk[i][r--];
				cube.m_edge[16 + i] = f++;
			} else {
				cube.m_edge[16 + i] = (byte)(20 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		int u = Constants.get8Perm (cube.m_edge, 16);
		this.coord = Tables.perm_to_420[u];
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
				cube1.rotate_sliceEDGE (stage2moves[m], cube2);
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
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				this.pack( cube2 );
				conj[u][sym] = coord;
			}
		}
	}
}
