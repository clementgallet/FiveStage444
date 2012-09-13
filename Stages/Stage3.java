package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge3;
import cg.fivestage444.Coordinates.Center3;
import cg.fivestage444.CubeState;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;

public final class Stage3 {

	public final static int N_MOVES = 20;
	static int[] prunTableEdge;
	static int[] prunTableCenter;
	private static int moveParity;

	public Edge3 edge;
	public Center3 center;
	public byte parity;

	public Stage3(){
		edge = new Edge3();
		center = new Center3();
	}

	/* Check if solved */
	public boolean isSolved(){
		return ( parity == 0 ) && edge.isSolved() && center.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage3 s ){
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
		s.parity = (byte)( parity ^ (( moveParity >>> m ) & 1 ));
	}

	/* Init */
	public static void init(){
		/* Initialize move parity */
		for( int i = 0; i < N_MOVES; i++){
			int m = Moves.stage2moves[i];
			if (((( m / 3 ) % 3 ) == 1 ) && (( m % 3 ) < 2 ))
				moveParity |= 1 << i;
		}
		Edge3.init();
		Center3.init();
		initPruningTableEdge();
		initPruningTableCenter();
	}

	/** Pruning functions **/

	/* Get pruning */
	public int pruning(){
		return Math.max( Util.getPrun2( prunTableEdge, edge.coord ), Util.getPrun2( prunTableCenter, center.coord ));
	}

	/* Init pruning table */
	public static void initPruningTableEdge(){
		final int N_SIZE = Edge3.N_COORD;
		final int INV_DEPTH = 7;
		Edge3 e1 = new Edge3();
		Edge3 e2 = new Edge3();

		prunTableEdge = new int[(Edge3.N_COORD+7)/8];
		for (int i=0; i<(Edge3.N_COORD+7)/8; i++)
			prunTableEdge[i] = -1;

		/* Set the solved states */
		Util.setPrun2( prunTableEdge, 12375, 0 );
		int done = 1;

		int depth = 0;
		while (done < Edge3.N_COORD) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			for (int i=0; i<Edge3.N_COORD;) {
				int val = prunTableEdge[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, Edge3.N_COORD); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					e1.coord = i;
					for (int m=0; m<N_MOVES; m++) {
						e1.moveTo(m, e2);
						if (Util.getPrun2(prunTableEdge, e2.coord) != check) continue;
						done++;
						if (inv) {
							Util.setPrun2(prunTableEdge, i, depth);
							break;
						} else {
							Util.setPrun2(prunTableEdge, e2.coord, depth);
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}

	public static void initPruningTableCenter(){
		final int N_SIZE = Center3.N_COORD;
		final int INV_DEPTH = 7;
		Center3 c1 = new Center3();
		Center3 c2 = new Center3();

		prunTableCenter = new int[(Center3.N_COORD+7)/8];
		for (int i=0; i<(Center3.N_COORD+7)/8; i++)
			prunTableCenter[i] = -1;

		/* Set the solved states */
		for( int a=0; a<Center3.SOLVED.length; a++)
			Util.setPrun2( prunTableCenter, Center3.SOLVED[a], 0 );
		int done = Center3.SOLVED.length;

		int depth = 0;
		while (done < Center3.N_COORD) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			for (int i=0; i<Center3.N_COORD;) {
				int val = prunTableCenter[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, Center3.N_COORD); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					c1.coord = i;
					for (int m=0; m<N_MOVES; m++) {
						c1.moveTo(m, c2);
						if (Util.getPrun2(prunTableCenter, c2.coord) != check) continue;
						done++;
						if (inv) {
							Util.setPrun2(prunTableCenter, i, depth);
							break;
						} else {
							Util.setPrun2(prunTableCenter, c2.coord, depth);
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}
}
