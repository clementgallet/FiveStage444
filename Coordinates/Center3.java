package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage3;

public final class Center3 extends SymCoord {

	private final static int N_COORD = 56980;
	private final static int N_RAW_COORD = 450450;
	private final static int N_SYM = Stage3.N_SYM;
	private final static int SYM_SHIFT = 3;
	private final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	private final static int N_MOVES = Stage3.N_MOVES;

	private static final int[] SolvedStates = { 56966, 56974, 56975, 56977, 56978, 56979 };
	
	/* Coordinates */
	public int raw_coord;

	/* Tables */
	private static final int[] sym2raw = new int[N_COORD];
	private static final int[] raw2sym = new int[N_RAW_COORD];
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

	public long getSyms(){
		return hasSym[coord];
	}

	/* Unpack a raw coord to a cube */
	private void unpack(CenterCubies cube)
	{
		int cenbm = this.raw_coord/35;
		int cenbm4of8 = this.raw_coord % 35;
		int ud = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; --i) {
			if (cenbm < Util.Cnk[i][r] ) {
				cube.cubies[i] = (byte)(ud++/4);
			} else {
				cenbm -= Util.Cnk[i][r--];
				if (last_c == -1){
					cube.cubies[i] = 3;
					last_c = i;
				}
				if (cenbm4of8 < Util.Cnk[j][s]) {
					cube.cubies[i] = 3;
				} else {
					cenbm4of8 -= Util.Cnk[j][s--];
					cube.cubies[i] = 2;
				}
			j--;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.cubies[i] = (byte)(i/4);
		}
	}

	/* Pack a cube into the raw coord */
	private void pack(CenterCubies cube){
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; i--) {
			if (cube.cubies[i] >= 2) {
				if (last_c == -1)
					last_c = i;
				cenbm += Util.Cnk[i][r--];
				if (cube.cubies[i] != cube.cubies[last_c]) {
					cenbm4of8 += Util.Cnk[j][s--];
				}
				--j;
			}
		}
		this.raw_coord = 35*cenbm + cenbm4of8;
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
		Center3 c = new Center3();

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			c.raw_coord = u;
			c.unpack(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				c.pack(cube2);
				Util.set1bit( isRepTable, c.raw_coord );
				raw2sym[c.raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( c.raw_coord == u )
					hasSym[repIdx] |= (1 << s);
			}
			sym2raw[repIdx++] = u;
		}
	}

	private static void initMove (){

		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		Center3 c = new Center3();
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
