package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge3;
import cg.fivestage444.Coordinates.Center3;
import cg.fivestage444.CubeState;
import cg.fivestage444.Moves;
import cg.fivestage444.Util;

public final class Stage3 {

	public final static int N_MOVES = 20;
	public final static int N_SYM = 8;
	public final static int N_SIZE = Edge3.N_COORD * Center3.N_COORD;
	private static byte[] prunTableEdge;
	private static byte[] prunTableCenter;
	public static byte[] prunTable;
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
		if( Util.FULL_PRUNING3 )
			initPruningTable();
		else{
			initPruningTableEdge();
			initPruningTableCenter();
		}
	}

	/** Pruning functions **/

	/* Set from an index */
	private final void set( int idx ){
		edge.coord = idx % Edge3.N_COORD;
		center.coord = idx / Edge3.N_COORD;
		center.sym = 0;
	}

	/* Get an index from this */
	private final int get(){
		return center.coord * Edge3.N_COORD + edge.conjugate(center.sym);
	}

	/* Rotate so that the sym is 0 */
	private final void normalise(){
		edge.coord = edge.conjugate(center.sym);
		center.sym = 0;
	}

	/* Get pruning */
	public int pruning(){
		if( Util.FULL_PRUNING3 )
			return Util.getPrun2( prunTable, this.get());
		else
			return Math.max( Util.getPrun2( prunTableEdge, edge.coord ), Util.getPrun2( prunTableCenter, center.coord ));
	}

	/* Init pruning table */
	public static void initPruningTableEdge(){
		final int N_SIZE = Edge3.N_COORD;
		final int INV_DEPTH = 7;
		Edge3 e1 = new Edge3();
		Edge3 e2 = new Edge3();

		prunTableEdge = new byte[(Edge3.N_COORD+1)/2];
		for (int i=0; i<(Edge3.N_COORD+1)/2; i++)
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
			for (int i=0; i<Edge3.N_COORD; i++) {
				if (Util.getPrun2(prunTableEdge, i) != select) continue;
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
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}

	public static void initPruningTableCenter(){
		final int N_SIZE = Center3.N_COORD;
		final int INV_DEPTH = 7;
		Center3 c1 = new Center3();
		Center3 c2 = new Center3();

		prunTableCenter = new byte[(Center3.N_COORD+1)/2];
		for (int i=0; i<(Center3.N_COORD+1)/2; i++)
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
			for (int i=0; i<Center3.N_COORD; i++) {
				if (Util.getPrun2(prunTableCenter, i) != select) continue;
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
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}

	public static void initPruningTable(){
		if( prunTable != null ) return;
		final int INV_DEPTH = 11;
		Stage3 s1 = new Stage3();
		Stage3 s2 = new Stage3();

		prunTable = new byte[(N_SIZE+1)/2];
		for (int i=0; i<(N_SIZE+1)/2; i++)
			prunTable[i] = -1;

		/* Set the solved states */
		for( int a=0; a<Center3.SOLVED.length; a++)
			Util.setPrun2( prunTable, Center3.SOLVED[a] * Edge3.N_COORD + 12375, 0 );
		int done = Center3.SOLVED.length;

		int depth = 0;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE; i++) {
				if (Util.getPrun2(prunTable, i) != select) continue;
				s1.set(i);
				for (int m=0; m<N_MOVES; m++) {
					s1.moveTo(m, s2);
					int idx = s2.get();
					if (Util.getPrun2(prunTable, idx) != check) continue;
					done++;
					if (inv) {
						Util.setPrun2(prunTable, i, depth);
						break;
					} else {
						Util.setPrun2(prunTable, idx, depth);
						int nsym = 1;
						unique++;
						int symS = Center3.hasSym[s2.center.coord];
						s2.normalise();
						for (int k=0; symS != 0; symS>>=1, k++) {
							if ((symS & 0x1) == 0) continue;
							s2.center.sym = k;
							int idxx = s2.get();
							if( idxx == idx )
								nsym++;
							if (Util.getPrun2(prunTable, idxx) == 0x0f) {
								Util.setPrun2(prunTable, idxx, depth);
								done++;
							}
						}
						pos += 8/nsym;
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
