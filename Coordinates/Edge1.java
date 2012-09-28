package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage1;

public final class Edge1 extends SymCoord {

	public final static int N_COORD = 15582;
	private final static int N_RAW_COORD = 735471;
	private final static int N_SYM = Stage1.N_SYM;
	private final static int SYM_SHIFT = 6;
	private final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	private final static int N_MOVES = Stage1.N_MOVES;

	/* Coordinates */
	private int raw_coord;

	/* Tables */
	private static final int[] sym2raw = new int[N_COORD];
	private static final int[] raw2sym = new int[N_RAW_COORD];
	public static long[] hasSym;
	private static final int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	public int[] getSolvedStates(){
		return new int[]{0};
	}

	public int getSize(){
		return N_COORD;
	}

	/* Move */
	public void moveTo( int m, SymCoord e ){
		e.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		e.sym = Symmetry.symIdxMultiply[e.coord & SYM_MASK][sym];
		e.coord >>>= SYM_SHIFT;
	}

	public long getSyms(){
		return hasSym[coord];
	}

	/* Unpack a raw coord to a cube */
	private void unpack(EdgeCubies cube)
	{
		int c = this.raw_coord;
		int r = 8;
		byte lrfb = 0;
		byte ud = 16;
		for (int i=23; i>=0; i--) {
			if (c >= Util.Cnk[i][r]) {
				c -= Util.Cnk[i][r--];
				cube.cubies[i] = ud++;
			} else {
				cube.cubies[i] = lrfb++;
			}
		}
	}

	/* Pack a cube into the raw coord */
	public void pack(EdgeCubies cube){
		this.raw_coord = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (cube.cubies[i] >= 16) {
				this.raw_coord += Util.Cnk[i][r--];
			}
		}
	}

	/* Compute the sym coordinate from the raw coordinate */
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
		Edge1 e = new Edge1();
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			e.raw_coord = u;
			e.unpack(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				e.pack(cube2);
				Util.set1bit( isRepTable, e.raw_coord );
				raw2sym[e.raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( e.raw_coord == u )
					hasSym[repIdx] |= (0x1L << s);
			}
			sym2raw[repIdx++] = u;
		}
	}

	private static void initMove (){
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		Edge1 e = new Edge1();
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
