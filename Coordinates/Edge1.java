package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Edge1 {

	static int MAX_COORD = 15582;
	static int MAX_RAW_COORD = 735471;

	/* Coordinates */
	int coord;
	int sym;
	int raw_coord;

	/* Tables */
	public static int[] sym2raw = new int[MAX_COORD];
	public static long[][] hasSym;
	public static byte[] symHelper;
	public static int[][] move = new int[MAX_COORD][N_STAGE1_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		return edge == 0;
	}

	/* Move */
	public void moveTo( int m, Edge1 e ){
		e.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		e.sym = Symmetry.symIdxMultiply[e.coord & 0x3F][sym];
		e.coord >>>= 6;
	}

	/* Unpack a raw coord to a cube */
	public void toCube (CubeState cube)
	{
		int c = raw_coord;
		int r = 8;
		byte lrfb = 0;
		byte ud = 16;
		for (int i=23; i>=0; i--) {
			if (c >= Cnk[i][r]) {
				c -= Cnk[i][r--];
				cube.m_edge[i] = ud++;
			} else {
				cube.m_edge[i] = lrfb++;
			}
		}
	}

	/* Pack a cube into the raw coord */
	public void packRaw (CubeState c){
		this.raw_coord = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (c.m_edge[i] >= 16) {
				this.raw_coord += Cnk[i][r--];
			}
		}
	}

	/* Compute the sym coordinate from a cube */
	public void pack (CubeState c){
		CubeState cube = new CubeState();
		int i;
		if( this.symHelper == null )
			this.sym = 0;
		else{
			this.packRaw(c);
			this.sym = this.symHelper[raw_coord];
		}
		for (; this.sym < N_SYM_STAGE1; this.sym++ ){
			c.rightMultEdges(Symmetry.invSymIdx[sym], cube);
			this.packRaw( cube );
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

		byte[] isRepTable = new byte[(MAX_RAW_COORD>>3) + 1];
		symHelper = new byte[MAX_RAW_COORD];
		hasSym = new long[MAX_COORD][1];
		for (int u = 0; u < MAX_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			this.raw_coord = u;
			symHelper[u] = 0;
			this.toCube(cube1);
			for (int sym = 1; sym < N_SYM_STAGE1; ++sym) {
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				this.packRaw( cube2 );
				isRepTable[raw_coord>>>3] |= 1<<(raw_coord&0x7);
				symHelper[raw_coord] = (byte)(Symmetry.invSymIdx[sym]);
				if( raw_coord == u )
					hasSym[repIdx][0] |= (0x1L << sym);
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE1_SYMEDGES; ++u) {
			this.coord = sym2raw[u];
			this.toCube( cube1 );
			for (int mc = 0; mc < N_STAGE1_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				this.pack( cube2 );
				move[u][mc] = ( coord << 6 ) | sym;
			}
		}
	}
}
