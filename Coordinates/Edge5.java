package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Edge5 {

	final static int N_COORD = 7444;
	final static int N_RAW_COORD = 96*96*96;
	final static int N_SYM = 48;
	final static int SYM_SHIFT = 8;
	final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	final static int N_MOVES = 12;

	/* Coordinates */
	int coord;
	int sym;
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
		// TODO: Could merge both arrays into one.
		static final int sqs_rep_to_perm[][] = {
			{  0,  7, 16, 23 },
			{  1,  6, 17, 22 },
			{  2, 10, 13, 21 },
			{  3, 11, 12, 20 },
			{  4,  8, 15, 19 },
			{  5,  9, 14, 18 }
		};

		static final int sqs_perm_to_rep[] = {
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
		Constants.set4Perm (cube.m_edge, ep1/4);

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep1/4]][ep1 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+4] = (byte)(t[i]+4);
		}

		Constants.set4Perm (t, ep2/4);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+8] = (byte)(t[i]+8);
		}

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep2/4]][ep2 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+12] = (byte)(t[i]+12);
		}

		Constants.set4Perm (t, ep3/4);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+16] = (byte)(t[i]+16);
		}

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep3/4]][ep3 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.m_edge[i+20] = (byte)(t[i]+20);
		}
	}

	/* Pack a cube into the raw coord */
	public void packRaw (CubeState cube){
		int ep1 = Constants.get4Perm (cube.m_edge, 0);
		int ep2 = Constants.get4Perm (cube.m_edge, 8);
		int ep3 = Constants.get4Perm (cube.m_edge, 16);
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

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		symHelper = new byte[N_RAW_COORD];
		hasSym = new long[N_COORD][4];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			this.raw_coord = u;
			symHelper[u] = 0;
			this.unpack(cube1);

			for (int sym = 0; sym < N_SYM; ++sym) {
				for (int cosym = 0; cosym < 4; ++cosym) {
					if(sym==0 && cosym==0) continue;
					cube1.rightMultEdges(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultEdges(sym);
					this.packRaw( cube2 );
					isRepTable[raw_coord>>>3] |= 1<<(raw_coord&0x7);
					symHelper[raw_coord] = (byte)(Symmetry.invSymIdx[sym]);
					if( raw_coord == u )
						hasSym[repIdx][cosym] |= (0x1L << sym);
				}
			}
			sym2raw[repIdx++] = u;
		}
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = sym2raw[u];
			this.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceEDGE (stage2moves[m], cube2);
				this.pack( cube2 );
				move[u][mc] = ( coord << SYM_SHIFT ) | sym;
			}
		}
	}
}
