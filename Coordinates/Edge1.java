package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import java.util.Arrays;

public final class Edge1 {

	public final static int N_COORD = 15582;
	final static int N_RAW_COORD = 735471;
	final static int N_SYM = 48;
	final static int SYM_SHIFT = 6;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 36;

	/* Coordinates */
	public int coord;
	public int sym;
	int raw_coord;

	/* Tables */
	public static int[] sym2raw = new int[N_COORD];
	public static long[] hasSym;
	public static byte[] symHelper;
	public static int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	/* Move */
	public void moveTo( int m, Edge1 e ){
		e.coord = move[coord][Symmetry.moveConjugateStage[m][sym]];
		e.sym = Symmetry.symIdxMultiply[e.coord & SYM_MASK][sym];
		e.coord >>>= SYM_SHIFT;
	}

	/* Unpack a raw coord to a cube */
	public void unpackRaw (CubeState cube)
	{
		int c = this.raw_coord;
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
	public void packRaw (CubeState cube){
		this.raw_coord = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (cube.m_edge[i] >= 16) {
				this.raw_coord += Cnk[i][r--];
			}
		}
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
			cube.rightMultEdges(Symmetry.invSymIdx[sym], cube2);
			this.packRaw( cube2 );
			int rep = Arrays.binarySearch(this.sym2raw, raw_coord);
			if( rep >= 0 ){
				this.coord = rep;
				return;
			}
		}
		System.out.println("Couldn't find a rep !");
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
		Edge1 e = new Edge1();
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		symHelper = new byte[N_RAW_COORD];
		hasSym = new long[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			e.raw_coord = u;
			symHelper[u] = 0;
			e.unpackRaw(cube1);
			for (int s = 1; s < N_SYM; ++s) {
				cube1.rightMultEdges (Symmetry.invSymIdx[s], cube2);
				e.packRaw( cube2 );
				isRepTable[e.raw_coord>>>3] |= 1<<(e.raw_coord&0x7);
				symHelper[e.raw_coord] = (byte)(Symmetry.invSymIdx[s]);
				if( e.raw_coord == u )
					hasSym[repIdx] |= (0x1L << s);
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Edge1 e = new Edge1();
		for (int u = 0; u < N_COORD; ++u) {
			e.raw_coord = sym2raw[u];
			e.unpackRaw( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (stage2moves[m], cube2);
				e.pack( cube2 );
				move[u][m] = ( e.coord << SYM_SHIFT ) | e.sym;
			}
		}
	}
}
