package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge5;
import cg.fivestage444.Coordinates.Center5;
import cg.fivestage444.Coordinates.Corner5;

public final class Stage5 {

	final static int N_MOVES = 12;
	static int[] prunTableEdgeCenter;
	static int[] prunTableEdgeCorner;

	Edge5 edge;
	Center5 center;
	Corner5 corner;

	Stage5(){
		edge = new Edge5();
		center = new Center5();
		corner = new Corner5();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube);
		center.pack(cube);
		corner.pack(cube);
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved(edge.sym) && center.isSolved(edge.sym);
	}

	/* Move */
	public void moveTo( int m, Stage5 s ){
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
		corner.moveTo( m, s.corner );
	}

	/* Init */
	public void init(){
		Edge5.init();
		Center5.init();
		Corner5.init();
		initPruningTable(true);
		initPruningTable(false);
	}

	/** Pruning functions **/

	/* Set from an index */
	void set( int idx, boolean useCenter ){
		if( useCenter ){
			center.coord = idx % Center5.N_COORD;
			edge.coord = idx / Center5.N_COORD;
		}
		else{
			corner.coord = idx % Corner5.N_COORD;
			edge.coord = idx / Corner5.N_COORD;
		}
		edge.sym = 0;
	}

	/* Get an index from this */
	int get( boolean useCenter ){
		if( useCenter )
			return edge.coord * Center5.N_COORD + center.conjugate(edge.sym);
		else
			return edge.coord * Corner5.N_COORD + corner.conjugate(edge.sym);
	}

	/* Get pruning */
	public int pruning(){
		return Math.max( getPrun2( prunTableEdgeCenter, this.get(true)), getPrun2( prunTableEdgeCorner, this.get(false)));
	}

	/* Init pruning table */
	public static void initPruningTable(boolean useCenter){
		final static int N_SIZE = Edge5.N_COORD * (useCenter ? Center5.N_COORD : Corner5.N_COORD);
		final static int INV_DEPTH = 7;
		Stage5 s = new Stage5();

		int[] prunTable = useCenter ? prunTableEdgeCenter : prunTableEdgeCorner;
		prunTable = new int[(N_SIZE+7)/8];

		/* Set the solved states */
		setPrun2( prunTable, 0, 0 );
		int done = 1;

		int depth = 0;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = prunTable[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, N_SIZE); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					set(i, useCenter);
					for (int m=0; m<N_MOVES; m++) {
						moveTo(s);
						int idx = s.get(useCenter);
						if (getPrun2(prunTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTable, i, depth);
							break;
						} else {
							setPrun2(prunTable, idx, depth);
							int nsym = 1;
							unique++;
							for (int j=0; j<N_COSYM; j++) {
								long symS = Edge5.hasSym[symx][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									s.edge.sym = (k<<2) + j;
									int idxx = s.get(useCenter);
									if( idxx == idx )
										nsym++;
									if (getPrun2(prunTable, idxx) == 0x0f) {
										setPrun2(prunTable, idxx, depth);
										done++;
									}
								}
							}
							pos += 192/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
