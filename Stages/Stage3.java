package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge3;
import cg.fivestage444.Coordinates.Center3;

public final class Stage3 {

	final static int N_MOVES = 20;
	static int[] prunTableEdge;
	static int[] prunTableCenter;

	Edge3 edge;
	Center3 center;

	Stage3(){
		edge = new Edge3();
		center = new Center3();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && center.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage3 s ){
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
	}

	/* Init */
	public void init(){
		Edge3.init();
		Center3.init();
		initPruningTableEdge();
		initPruningTableCenter();
	}

	/** Pruning functions **/

	/* Get pruning */
	public int pruning(){
		return Math.max( getPrun2( prunTableEdge, edge.coord ), getPrun2( prunTableCenter, center.coord ));
	}

	/* Init pruning table */
	public static void initPruningTableEdge(){
		final static int N_SIZE = Edge3.N_COORD;
		final static int INV_DEPTH = 7;
		Edge3 e = new Edge3();

		prunTableEdge = new int[(Edge3.N_COORD+7)/8];

		/* Set the solved states */
		setPrun2( prunTableEdge, 12375<<1, 0 );
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
					this.edge.coord = i;
					for (int m=0; m<N_MOVES; m++) {
						this.edge.moveTo(e);
						if (getPrun2(prunTableEdge, e.coord) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTableEdge, i, depth);
							break;
						} else {
							setPrun2(prunTableEdge, e.coord, depth);
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}

	public static void initPruningTableCenter(){
		final static int N_SIZE = Center3.N_COORD;
		final static int INV_DEPTH = 7;
		Center3 e = new Center3();

		prunTableCenter = new int[(Center3.N_COORD+7)/8];

		/* Set the solved states */
		for( int a=0; a<Center3.SOLVED.length; a++)
			setPrun2( prunTableCenter, Center3.SOLVED[a], 0 );
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
					this.center.coord = i;
					for (int m=0; m<N_MOVES; m++) {
						this.center.moveTo(c);
						if (getPrun2(prunTableCenter, c.coord) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTableCenter, i, depth);
							break;
						} else {
							setPrun2(prunTableCenter, c.coord, depth);
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d", depth, done));
		}
	}
}
