package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage4;

public final class Edge4 {

	public final static int N_COORD = 5968;
	private final static int N_RAW_COORD = 88200*2;
	private final static int N_SYM = Stage4.N_SYM;
	private final static int SYM_SHIFT = 4;
	private final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	private final static int N_MOVES = Stage4.N_MOVES;

	/* Coordinates */
	public int coord;
	public int sym;
	public int raw_coord;

	/* Tables */
	private static final int[] sym2raw = new int[N_COORD];
	private static final int[] raw2sym = new int[N_RAW_COORD];
	public static int[] hasSym;
	private static final int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	/* Move */
	public void moveTo( int m, Edge4 e ){
		e.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		e.sym = Symmetry.symIdxMultiply[e.coord & SYM_MASK][sym];
		e.coord >>>= SYM_SHIFT;
	}

	/* Unpack a raw coord to a cube */
	private void unpack(EdgeCubies cube)
	{
		int ledge4of8 = raw_coord % 70;
		int edge = raw_coord / 70;
		int redge4of8 = edge % 70;
		edge /= 70;
		int perm6_fb = edge % 6;
		int perm6_rl = edge / 6;
		byte[] t = new byte[4];

		int i1 = 0;
		int i2 = 0;
		int r = 4;
		Util.set4Perm( t, perm6_rl );
		for( int i=7; i >= 0; i-- ){
			if( ledge4of8 >= Util.Cnk[i][r] ){
				ledge4of8 -= Util.Cnk[i][r--];
				cube.cubies[i+4] = (byte)( t[i1++] + 4 );
			}
			else
				cube.cubies[i+4] = (byte)( (i2++) + 8);
		}

		i1 = 0;
		i2 = 0;
		r = 4;
		Util.set4Perm( t, perm6_fb );
		for( int i=7; i >= 0; i-- ){
			if( redge4of8 >= Util.Cnk[i][r] ){
				redge4of8 -= Util.Cnk[i][r--];
				cube.cubies[( i < 4 ) ? i : i + 8] = (byte)(i1++);
			}
			else
				cube.cubies[( i < 4 ) ? i : i + 8] = (byte)(t[i2++] + 12);
		}

		for( int i=16; i < 24; i++ )
			cube.cubies[i] = (byte)i;
	}

	/* Pack a cube into the raw coord */
	private void pack(EdgeCubies cube){
		int redge4of8 = 0;
		int ledge4of8 = 0;
		byte[] edges_rl = new byte[8];
		byte[] edges_fb = new byte[8];

		int i_rl = 4;
		int i_fb = 0;
		int r = 4;
		for( int i=7; i>=0;i--){
			if( cube.cubies[i+4] < 8 ){
				ledge4of8 += Util.Cnk[i][r--];
				edges_rl[i_rl++] = cube.cubies[i+4];
			}
			else
				edges_fb[i_fb++] = (byte)(cube.cubies[i+4] - 8);
		}

		i_rl = 0;
		i_fb = 4;
		r = 4;
		for( int i=7; i>=0;i--){
			int u = (i < 4) ? i : i + 8;
			if( cube.cubies[u] < 4 ){
				redge4of8 += Util.Cnk[i][r--];
				edges_rl[i_rl++] = cube.cubies[u];
			}
			else
				edges_fb[i_fb++] = (byte)(cube.cubies[u] - 8);
		}

		int perm6_rl = Util.perm_to_420[Util.get8Perm (edges_rl, 0)]%6;
		int perm6_fb = Util.perm_to_420[Util.get8Perm (edges_fb, 0)]%6;

		this.raw_coord = ((( perm6_rl * 6 + perm6_fb ) * 70 + redge4of8 ) * 70 + ledge4of8 );
	}

	/* Compute the sym coordinate from a cube */
	public void computeSym (){
		int symcoord = raw2sym[raw_coord];
		this.coord = symcoord >> SYM_SHIFT;
		this.sym = symcoord & SYM_MASK;
	}

	/* Initialisations */
	public static void init(){
		initSym2Raw();
		initMove();
	}

	private static void initSym2Raw (){
		int repIdx = 0;
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		Edge4 e = new Edge4();
		byte[] t = new byte[8];
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			e.raw_coord = u;
			e.unpack(cube1);

			/* Only retain configs without parity */
			int ul = Util.get8Perm( cube1.cubies, 4 );
			for (int i=0; i<4; i++)
				t[i] = ( cube1.cubies[i] > 4 ) ? (byte)(cube1.cubies[i]-8) : cube1.cubies[i];
			for (int i=4; i<8; i++)
				t[i] = ( cube1.cubies[i+8] > 4 ) ? (byte)(cube1.cubies[i+8]-8) : cube1.cubies[i+8];
			int uh = Util.get8Perm( t, 0 );
			if( Util.parity_perm8_table[ul] != Util.parity_perm8_table[uh] ) continue; // getting rid of the parity.

			for (int s = 1; s < N_SYM; ++s) {
				cube1.conjugate (s, cube2);
				e.pack(cube2);
				Util.set1bit( isRepTable, e.raw_coord );
				raw2sym[e.raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( e.raw_coord == u )
					hasSym[repIdx] |= (1 << s);
			}
			sym2raw[repIdx++] = u;
		}
	}

	private static void initMove (){
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		Edge4 e = new Edge4();
		for (int u = 0; u < N_COORD; ++u) {
			e.raw_coord = sym2raw[u];
			e.unpack(cube1);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				e.pack(cube2);
				move[u][m] = raw2sym[e.raw_coord];
			}
		}
	}
}
