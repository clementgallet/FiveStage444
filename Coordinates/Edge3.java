package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;

public final class Edge3 {

	public final static int N_COORD = 12870;
	final static int N_SYM = 8;
	final static int N_MOVES = 20;

	/* Coordinates */
	public int coord;

	/* Tables */
	public static short[][] move = new short[N_COORD][N_MOVES];
	public static short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 12375;
	}

	/* Move */
	public void moveTo( int m, Edge3 e ){
		e.coord = move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int edge = coord;
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Util.Cnk[i][r]) {
				edge -= Util.Cnk[i][r--];
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
				this.coord += Util.Cnk[i][r--];
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
		Edge3 e = new Edge3();
		for (int u = 0; u < N_COORD; ++u) {
			e.coord = u;
			e.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (Moves.stage2moves[m], cube2);
				e.pack( cube2 );
				move[u][m] = (short)e.coord;
			}
		}
	}

	public static void initConj (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Edge3 e = new Edge3();
		for (int u = 0; u < N_COORD; ++u) {
			e.coord = u;
			e.unpack( cube1 );
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMultEdges (Symmetry.invSymIdx[s], cube2);
				e.pack( cube2 );
				conj[u][s] = (short)e.coord;
			}
		}
	}
}
