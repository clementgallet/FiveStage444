package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import java.util.Arrays;

public final class Edge5 {

	public final static int N_COORD = 7444;
	final static int N_RAW_COORD = 96*96*96;
	final static int N_SYM = 48;
	final static int SYM_SHIFT = 8;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 12;

	/* Coordinates */
	public int coord;
	public int sym;
	int raw_coord;

	/* Tables */
	public static int[] sym2raw = new int[N_COORD];
	public static long[][] hasSym;
	public static byte[] symHelper;
	public static int[][] move = new int[N_COORD][N_MOVES];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	/* Move */
	public void moveTo( int m, Edge5 e ){
		e.coord = move[coord][Symmetry.moveConjugateCo4Stage[m][sym]];
		e.sym = Symmetry.symIdxCo4Multiply[e.coord & SYM_MASK][sym];
		e.coord >>>= SYM_SHIFT;
	}

	/* Unpack a raw coord to a cube */
	public void unpackRaw (CubeState cube)
	{
		final int sqs_rep_to_perm[][] = {
			{  0,  7, 16, 23 },
			{  1,  6, 17, 22 },
			{  2, 10, 13, 21 },
			{  3, 11, 12, 20 },
			{  4,  8, 15, 19 },
			{  5,  9, 14, 18 }
		};

		final int sqs_perm_to_rep[] = {
			0, 1, 2, 3, 4, 5,
			1, 0, 4, 5, 2, 3,
			3, 2, 5, 4, 0, 1,
			5, 4, 3, 2, 1, 0
		};

		int i;
		int ep1 = raw_coord % 96;
		int ep2 = (raw_coord/96) % 96;
		int ep3 = raw_coord/(96*96);
		byte[] t = new byte[4];
		Util.set4Perm (cube.m_edge, ep1/4);

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep1/4]][ep1 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+4] = (byte)(t[i]+4);
		}

		Util.set4Perm (t, ep2/4);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+8] = (byte)(t[i]+8);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep2/4]][ep2 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+12] = (byte)(t[i]+12);
		}

		Util.set4Perm (t, ep3/4);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+16] = (byte)(t[i]+16);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep3/4]][ep3 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+20] = (byte)(t[i]+20);
		}
	}

	/* Pack a cube into the raw coord */
	public void packRaw (CubeState cube){
		int ep1 = Util.get4Perm (cube.m_edge, 0);
		int ep2 = Util.get4Perm (cube.m_edge, 8);
		int ep3 = Util.get4Perm (cube.m_edge, 16);
		this.raw_coord = 96*96*(4*ep3 + (cube.m_edge[20] - 20)) + 96*(4*ep2 + (cube.m_edge[12] - 12)) + 4*ep1 + (cube.m_edge[4] - 4);
	}

	/* Compute the sym coordinate from a cube */
	public void pack (CubeState cube){
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();
		int i;
		if( this.symHelper == null )
			this.sym = 0;
		else{
			this.packRaw(cube);
			this.sym = this.symHelper[raw_coord];
		}
		for (; this.sym < N_SYM; this.sym++ ){
			System.arraycopy(cube.m_edge, 0, cube2.m_edge, 0, 24);
			cube2.leftMultEdges(sym);
			for (int cosym=0; cosym < 4; cosym++ ){
				cube2.rightMultEdges (Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube3);
				this.packRaw( cube3 );
				int rep = Arrays.binarySearch(this.sym2raw, raw_coord);
				if( rep >= 0 ){
					this.coord = rep;
					this.sym = ( this.sym << 2 ) | cosym;
					return;
				}
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
		Edge5 e = new Edge5();
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		symHelper = new byte[N_RAW_COORD];
		hasSym = new long[N_COORD][4];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			e.raw_coord = u;
			symHelper[u] = 0;
			e.unpackRaw(cube1);

			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					if(s==0 && cs==0) continue;
					cube1.rightMultEdges(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMultEdges(s);
					e.packRaw( cube2 );
					isRepTable[e.raw_coord>>>3] |= 1<<(e.raw_coord&0x7);
					symHelper[e.raw_coord] = (byte)(Symmetry.invSymIdx[s]);
					if( e.raw_coord == u )
						hasSym[repIdx][cs] |= (0x1L << s);
				}
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Edge5 e = new Edge5();
		for (int u = 0; u < N_COORD; ++u) {
			e.raw_coord = sym2raw[u];
			e.unpackRaw( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (Moves.stage2moves[m], cube2);
				e.pack( cube2 );
				move[u][m] = ( e.coord << SYM_SHIFT ) | e.sym;
			}
		}
	}
}
