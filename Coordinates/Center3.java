package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Center3 {

	final static int N_COORD = 56980;
	final static int N_RAW_COORD = 450450;
	final static int N_SYM = 8;
	final static int SYM_SHIFT = 3;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 20;

	static final int SOLVED[] = { 56966, 56974, 56975, 56977, 56978, 56979 };
	
	/* Coordinates */
	int coord;
	int sym;
	int raw_coord;

	/* Tables */
	public static int[] sym2raw = new int[N_COORD];
	public static int[] hasSym;
	public static byte[] symHelper;
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
			if (cenbm < Cnk[i][r] ) {
				cube.m_cen[i] = (byte)(ud++/4);
			} else {
				cenbm -= Cnk[i][r--];
				if (last_c == -1){
					cube.m_cen[i] = 3;
					last_c = i;
				}
				if (cenbm4of8 < Cnk[j][s]) {
					cube.m_cen[i] = 3;
				} else {
					cenbm4of8 -= Cnk[j][s--];
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
				cenbm += Cnk[i][r--];
				if (cube.m_cen[i] != cube.m_cen[last_c]) {
					cenbm4of8 += Cnk[j][s--];
				}
				--j;
			}
		}
		this.raw_coord = 35*cenbm + cenbm4of8;
	}

	/* Compute the sym coordinate from a cube */
	public void pack (CubeState cube){
		CubeState cube2 = new CubeState();
		int i;
		if( this.symHelper == null )
			this.sym = 0;
		else{
			this.packRaw(cube);
			this.sym = this.symHelper[raw_coord];
		}
		for (; this.sym < N_SYM; this.sym++ ){
			cube.rightMultCenters(Symmetry.invSymIdx[sym], cube2);
			this.packRaw( cube2 );
			int rep = Arrays.binarySearch(this.sym2raw, raw_coord);
			if( rep >= 0 ){
				this.coord = rep;
				return;
			}
		}
		return;
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

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		symHelper = new byte[N_RAW_COORD];
		hasSym = new int[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			this.raw_coord = u;
			symHelper[u] = 0;
			this.unpackRaw(cube1);
			for (int sym = 1; sym < N_SYM; ++sym) {
				cube1.rightMultCenters (Symmetry.invSymIdx[sym], cube2);
				this.packRaw( cube2 );
				isRepTable[raw_coord>>>3] |= 1<<(raw_coord&0x7);
				symHelper[raw_coord] = (byte)(Symmetry.invSymIdx[sym]);
				if( raw_coord == u )
					hasSym[repIdx] |= (1 << sym);
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = sym2raw[u];
			this.unpackRaw( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (stage2moves[m], cube2);
				this.pack( cube2 );
				move[u][mc] = ( coord << SYM_SHIFT ) | sym;
			}
		}
	}
}
