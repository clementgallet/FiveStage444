package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import java.util.Arrays;

public final class Center2 {

	public final static int N_COORD = 716;
	final static int N_RAW_COORD = 10626;
	final static int N_SYM = 16;
	final static int SYM_SHIFT = 4;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 28;

	public static final int SOLVED[] = { 122, 242, 243, 245, 246, 247 };

	/* Coordinates */
	public int coord;
	public int sym;
	public int raw_coord;

	/* Tables */
	public static short[] sym2raw = new short[N_COORD];
	public static short[] raw2sym = new short[N_RAW_COORD];
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
	public void moveTo( int m, Center2 c ){
		c.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		c.sym = Symmetry.symIdxMultiply[c.coord & SYM_MASK][sym];
		c.coord >>>= SYM_SHIFT;
	}

	/* Unpack a raw coord to a cube */
	public void unpackRaw (CubeState cube)
	{
		int center = raw_coord;
		int r = 4;
		byte udlrf = 0;
		for (int i=23; i>=0; i--) {
			if (center >= Util.Cnk[i][r]) {
				center -= Util.Cnk[i][r--];
				cube.m_cen[i] = 5;
			} else {
				cube.m_cen[i] = (byte)(udlrf++/4);
			}
		}
	}

	/* Pack a cube into the raw coord */
	public void packRaw (CubeState cube, int c){
		this.raw_coord = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (cube.m_cen[i] == c) {
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

	public static void initSym2Raw (){
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center2 c = new Center2();

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			raw2sym[u] = (short)( repIdx << SYM_SHIFT );
			c.raw_coord = u;
			c.unpackRaw(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMultCenters (Symmetry.invSymIdx[s], cube2);
				c.packRaw( cube2, 5 );
				isRepTable[c.raw_coord>>>3] |= 1<<(c.raw_coord&0x7);
				raw2sym[c.raw_coord] = (short)(( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s]);
				//raw2sym[c.raw_coord] = (short)(( repIdx << SYM_SHIFT ) + s);
				if( c.raw_coord == u )
					hasSym[repIdx] |= (1 << s);
			}
			sym2raw[repIdx++] = (short)u;
		}
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center2 c = new Center2();
		for (int u = 0; u < N_COORD; ++u) {
			c.raw_coord = sym2raw[u];
			c.unpackRaw( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (Moves.stage2moves[m], cube2);
				c.packRaw( cube2, 5 );
				move[u][m] = raw2sym[c.raw_coord];
			}
		}
	}
}
