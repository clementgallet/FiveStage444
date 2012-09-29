package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage3;

public final class Edge3 extends RawCoord {

	public final static int N_COORD = 12870;
	private final static int N_SYM = Stage3.N_SYM;
	private final static int N_MOVES = Stage3.N_MOVES;

	/* Tables */
	private static final short[][] move = new short[N_COORD][N_MOVES];
	private static final short[][] conj = new short[N_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 12375;
	}

	/* Move */
	public void moveTo( int m, RawCoord e ){
		e.coord = move[coord][m];
	}

	public int[] getSolvedStates(){
		return new int[]{12375};
	}

	public int getSize(){
		return N_COORD;
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (EdgeCubies cube)
	{
		int edge = coord;
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Util.Cnk[i][r]) {
				edge -= Util.Cnk[i][r--];
				cube.cubies[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				cube.cubies[i] = e1++;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.cubies[i] = (byte)i;
		}
	}

	/* Pack a cube into the coord */
	private void pack (EdgeCubies cube){
		this.coord = 0;
		int r = 8;
		for (int i=15; i>=0; i--) {
			if (cube.cubies[i] < 4 || cube.cubies[i] >= 12) {
				this.coord += Util.Cnk[i][r--];
			}
		}
	}

	/* Initialisations */
	public static void init(){
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		Edge3 e = new Edge3();
		for (int u = 0; u < N_COORD; ++u) {
			e.coord = u;
			e.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				e.pack( cube2 );
				move[u][m] = (short)e.coord;
			}
			for (int s = 0; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				e.pack( cube2 );
				conj[u][s] = (short)e.coord;
			}
		}
	}
}
