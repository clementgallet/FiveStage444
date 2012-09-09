package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import java.util.Arrays;

public final class Center3 {

	public final static int N_COORD = 56980;
	final static int N_RAW_COORD = 450450;
	final static int N_SYM = 8;
	final static int SYM_SHIFT = 3;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 20;

	public static final int SOLVED[] = { 56966, 56974, 56975, 56977, 56978, 56979 };
	
	/* Coordinates */
	public int coord;
	public int sym;
	public int raw_coord;

	/* Tables */
	public static int[] sym2raw = new int[N_COORD];
	public static int[] raw2sym = new int[N_RAW_COORD];
	public static int[] hasSym;
	public static int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		for (int i=0; i < SOLVED.length; i++)
			if( coord == SOLVED[i])
				return true;
		return false;
	}

	/* Move */
	public void moveTo( int m, Center3 c ){
		c.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		c.sym = Symmetry.symIdxMultiply[c.coord & SYM_MASK][sym];
		c.coord >>>= SYM_SHIFT;
	}

	/* Unpack a raw coord to a cube */
	public void unpackRaw (CubeState cube)
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
				cube.m_cen[i] = (byte)(ud++/4);
			} else {
				cenbm -= Util.Cnk[i][r--];
				if (last_c == -1){
					cube.m_cen[i] = 3;
					last_c = i;
				}
				if (cenbm4of8 < Util.Cnk[j][s]) {
					cube.m_cen[i] = 3;
				} else {
					cenbm4of8 -= Util.Cnk[j][s--];
					cube.m_cen[i] = 2;
				}
			j--;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.m_cen[i] = (byte)(i/4);
		}
	}

	/* Pack a cube into the raw coord */
	public void packRaw (CubeState cube){
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; i--) {
			if (cube.m_cen[i] >= 2) {
				if (last_c == -1)
					last_c = i;
				cenbm += Util.Cnk[i][r--];
				if (cube.m_cen[i] != cube.m_cen[last_c]) {
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

	public static void initSym2Raw (){
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center3 c = new Center3();

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			c.raw_coord = u;
			c.unpackRaw(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMultCenters (Symmetry.invSymIdx[s], cube2);
				c.packRaw( cube2 );
				isRepTable[c.raw_coord>>>3] |= 1<<(c.raw_coord&0x7);
				raw2sym[c.raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( c.raw_coord == u )
					hasSym[repIdx] |= (1 << s);
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center3 c = new Center3();
		for (int u = 0; u < N_COORD; ++u) {
			c.raw_coord = sym2raw[u];
			c.unpackRaw( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (Moves.stage2moves[m], cube2);
				c.packRaw( cube2 );
				move[u][m] = raw2sym[c.raw_coord];
			}
		}
	}
}
