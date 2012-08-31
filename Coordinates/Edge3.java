package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Edge2 {

	final static int N_COORD = 12870*2;
	final static int N_SYM = 8;
	final static int N_MOVES = 20;
	private static int moveParity;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return (( coord & 1 ) == 0 ) && (( coord >> 1 ) == 12375 );
	}

	/* Move */
	public void moveTo( int m, Edge3 e ){
		e.coord = ( move[coord>>>1][m] << 1 ) | (( coord & 1 ) ^ (( moveParity >>> m ) & 1 ));
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int edge = coord >>> 1;
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Cnk[i][r]) {
				edge -= Cnk[i][r--];
				cube.m_edge[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				cube.m_edge[i] = e1++;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.m_edge[i] = (byte)i;
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		this.coord = 0;
		int r = 8;
		for (int i=15; i>=0; i--) {
			if (cube.m_edge[i] < 4 || cube.m_edge[i] >= 12) {
				this.coord += Cnk[i][r--];
			}
		}
		this.coord = ( this.coord << 1 ) | cube.edgeUD_parity();
	}

	/* Initialisations */
	public static void init(){
		/* Initialize move parity */
		for( int i = 0; i < N_MOVES; i++){
			int m = stage2moves[i];
			if (((( m / 3 ) % 3 ) == 1 ) && (( m % 3 ) < 2 ))
				moveParity |= 1 << i;
		}
		initMove();
		initConj();
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD>>>1; ++u) {
			this.coord = u << 1;
			this.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (stage2moves[m], cube2);
				this.pack( cube2 );
				move[u][m] = coord >>> 1;
			}
		}
	}

	public static void initConj (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD >>> 1; ++u) {
			this.coord = u << 1;
			this.unpack( cube1 );
			for (int sym = 0; sym < N_SYM; ++sym) {
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				this.pack( cube2 );
				conj[u][sym] = coord >>> 1;
			}
		}
	}
}
