package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage2;

public final class Center2 extends SymCoord {

	private final static int N_COORD = 716;
	private final static int N_RAW_COORD = 10626;
	private final static int N_SYM = Stage2.N_SYM;
	private final static int SYM_SHIFT = 4;
	private final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	private final static int N_MOVES = Stage2.N_MOVES;

	private static final int[] SolvedStates = { 122, 242, 243, 245, 246, 247 };

	/* Coordinates */
	public int raw_coord;

	/* Tables */
	private static final short[] sym2raw = new short[N_COORD];
	private static final short[] raw2sym = new short[N_RAW_COORD];
	private static int[] hasSym;
	private static final int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		for (int s : SolvedStates)
			if( coord == s)
				return true;
		return false;
	}

	public int[] getSolvedStates(){
		return SolvedStates;
	}

	public int getSize(){
		return N_COORD;
	}

	/* Move */
	public void moveTo( int m, SymCoord c ){
		c.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		c.sym = Symmetry.symIdxMultiply[c.coord & SYM_MASK][sym];
		c.coord >>>= SYM_SHIFT;
	}

	public long[] getSyms(){
		return new long[]{hasSym[coord]};
	}

	/* Unpack a raw coord to a cube */
	private void unpack(CenterCubies cube)
	{
		int center = raw_coord;
		int r = 4;
		byte udlrf = 0;
		for (int i=23; i>=0; i--) {
			if (center >= Util.Cnk[i][r]) {
				center -= Util.Cnk[i][r--];
				cube.cubies[i] = 5;
			} else {
				cube.cubies[i] = (byte)(udlrf++/4);
			}
		}
	}

	/* Pack a cube into the raw coord */
	private void pack(CenterCubies cube){
		this.raw_coord = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (cube.cubies[i] == 5) {
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
		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		Center2 c = new Center2();

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = (short)( repIdx << SYM_SHIFT );
			c.raw_coord = u;
			c.unpack(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				c.pack(cube2);
				Util.set1bit( isRepTable, c.raw_coord );
				raw2sym[c.raw_coord] = (short)(( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s]);
				if( c.raw_coord == u )
					hasSym[repIdx] |= (1 << s);
			}
			sym2raw[repIdx++] = (short)u;
		}
	}

	private static void initMove (){

		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		Center2 c = new Center2();
		for (int u = 0; u < N_COORD; ++u) {
			c.raw_coord = sym2raw[u];
			c.unpack(cube1);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				c.pack(cube2);
				move[u][m] = raw2sym[c.raw_coord];
			}
		}
	}
}
