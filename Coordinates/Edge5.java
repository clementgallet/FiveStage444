package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;
import cg.fivestage444.Stages.Stage5;

public final class Edge5 extends SymCoord {

	public final static int N_COORD = 7444;
	private final static int N_RAW_COORD = 96*96*96;
	private final static int N_SYM = Stage5.N_SYM;
	private final static int SYM_SHIFT = 8;
	private final static int SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
	private final static int N_MOVES = Stage5.N_MOVES;

	/* Coordinates */
	public int raw_coord;

	/* Tables */
	private static final int[] sym2raw = new int[N_COORD];
	private static final int[] raw2sym = new int[N_RAW_COORD];
	public static long[][] hasSym;
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
		e.coord = move[coord][Symmetry.moveConjugateCo4Stage[m][sym]];
		e.sym = Symmetry.symIdxCo4Multiply[e.coord & SYM_MASK][sym];
		e.coord >>>= SYM_SHIFT;
	}

	public long[] getSyms(){
		return hasSym[coord];
	}

	/* Unpack a raw coord to a cube */
	private void unpack(EdgeCubies cube)
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
		Util.set4Perm (cube.cubies, ep1/4);

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep1/4]][ep1 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+4] = (byte)(t[i]+4);
		}

		Util.set4Perm (t, ep2/4);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+8] = (byte)(t[i]+8);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep2/4]][ep2 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+12] = (byte)(t[i]+12);
		}

		Util.set4Perm (t, ep3/4);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+16] = (byte)(t[i]+16);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep3/4]][ep3 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+20] = (byte)(t[i]+20);
		}
	}

	/* Pack a cube into the raw coord */
	private void pack(EdgeCubies cube){
		int ep1 = Util.get4Perm (cube.cubies, 0);
		int ep2 = Util.get4Perm (cube.cubies, 8);
		int ep3 = Util.get4Perm (cube.cubies, 16);
		this.raw_coord = 96*96*(4*ep3 + (cube.cubies[20] - 20)) + 96*(4*ep2 + (cube.cubies[12] - 12)) + 4*ep1 + (cube.cubies[4] - 4);
	}

	/* Compute the sym coordinate from a cube */
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
		Edge5 e = new Edge5();
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD][4];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			e.raw_coord = u;
			e.unpack(cube1);

			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					if(s==0 && cs==0) continue;
					cube1.rightMult(Symmetry.symIdxMultiply[Symmetry.invSymIdx[s]][Symmetry.invSymIdx[cs]], cube2);
					cube2.leftMult(s);
					e.pack(cube2);
					Util.set1bit( isRepTable, e.raw_coord );
					raw2sym[e.raw_coord] = ( repIdx << SYM_SHIFT ) + ( Symmetry.invSymIdx[s] << 2 ) + Symmetry.invSymIdx[cs];
					if( e.raw_coord == u )
						hasSym[repIdx][cs] |= (0x1L << s);
				}
			}
			sym2raw[repIdx++] = u;
		}
	}

	private static void initMove (){
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		Edge5 e = new Edge5();
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
