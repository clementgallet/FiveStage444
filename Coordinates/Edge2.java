package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage2;

public final class Edge2 {

	public final static int N_COORD = 420;
	private final static int N_SYM = Stage2.N_SYM;
	private final static int N_MOVES = Stage2.N_MOVES;

	/* Coordinates */
	public int coord;

	/* Tables */
	private static short[][] move = new short[N_COORD][N_MOVES];
	private static short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return ( coord == 0 ) || ( coord == 414 );
	}

	/* Move */
	public void moveTo( int m, Edge2 e ){
		e.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CubeState cube)
	{
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = this.coord / 6;
		Util.set4Perm (t6, this.coord % 6);
		for (i = 0; i < 16; ++i)
			cube.m_edge[i] = (byte)i;

		byte f = 16;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if ( edgeFbm >= Util.Cnk[i][r] ) {
				edgeFbm -= Util.Cnk[i][r--];
				cube.m_edge[16 + i] = f++;
			} else {
				cube.m_edge[16 + i] = (byte)(20 + t6[b++]);
			}
		}
	}

	/* Pack a cube into the coord */
	private void pack (CubeState cube){
		int u = Util.get8Perm (cube.m_edge, 16);
		this.coord = Util.perm_to_420[u];
	}

	/* Initialisations */
	public static void init(){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Edge2 e = new Edge2();
		for (int u = 0; u < N_COORD; ++u) {
			e.coord = u;
			e.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (Moves.stage2moves[m], cube2);
				e.pack( cube2 );
				move[u][m] = (short)(e.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMultEdges (Symmetry.invSymIdx[s], cube2);
				e.pack( cube2 );
				conj[u][s] = (short)(e.coord);
			}
		}
	}
}
