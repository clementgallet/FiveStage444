package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge5;
import cg.fivestage444.Coordinates.Center5;
import cg.fivestage444.Coordinates.Corner5;
import cg.fivestage444.CubeState;

public final class Stage5 {

	public final static int N_MOVES = 12;
	static int[] prunTableEdgeCenter;
	static int[] prunTableEdgeCorner;

	Edge5 edge;
	Center5 center;
	Corner5 corner;

	public Stage5(){
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
	public static void init(){
		Edge5.init();
		Center5.init();
		Corner5.init();
		initPruningTableEdgeCenter();
		initPruningTableEdgeCorner();
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

	/* Rotate so that sym is 0 */
	void normalise( boolean useCenter ){
		if( useCenter )
			center.coord = center.conjugate(edge.sym);
		else
			corner.coord = corner.conjugate(edge.sym);
		edge.sym = 0;
	}

	/* Get pruning */
	public int pruning(){
		return Math.max( getPrun2( prunTableEdgeCenter, this.get(true)), getPrun2( prunTableEdgeCorner, this.get(false)));
	}

	/* Init pruning table */
	public static void initPruningTableEdgeCenter(){
		final int N_SIZE = Edge5.N_COORD * Center5.N_COORD;
		final int INV_DEPTH = 10;
		Stage5 s1 = new Stage5();
		Stage5 s2 = new Stage5();

		prunTableEdgeCenter = new int[(N_SIZE+7)/8];
		for (int i=0; i<(N_SIZE+7)/8; i++)
			prunTableEdgeCenter[i] = -1;

		/* Set the solved states */
		setPrun2( prunTableEdgeCenter, 0, 0 );
		int done = 1;

		int depth = 0;
		while (( done < N_SIZE ) && ( depth < 15 )) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = prunTableEdgeCenter[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, N_SIZE); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					s1.set(i, true);
					for (int m=0; m<N_MOVES; m++) {
						s1.moveTo(m, s2);
						int idx = s2.get(true);
						if (getPrun2(prunTableEdgeCenter, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTableEdgeCenter, i, depth);
							break;
						} else {
							setPrun2(prunTableEdgeCenter, idx, depth);
							int nsym = 1;
							unique++;
							s2.normalise(true);
							for (int j=0; j<4; j++) {
								long symS = Edge5.hasSym[s2.edge.coord][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									s2.edge.sym = (k<<2) + j;
									int idxx = s2.get(true);
									if( idxx == idx )
										nsym++;
									if (getPrun2(prunTableEdgeCenter, idxx) == 0x0f) {
										setPrun2(prunTableEdgeCenter, idxx, depth);
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

	public static void initPruningTableEdgeCorner(){
		final int N_SIZE = Edge5.N_COORD * Corner5.N_COORD;
		final int INV_DEPTH = 10;
		Stage5 s1 = new Stage5();
		Stage5 s2 = new Stage5();

		prunTableEdgeCorner = new int[(N_SIZE+7)/8];
		for (int i=0; i<(N_SIZE+7)/8; i++)
			prunTableEdgeCorner[i] = -1;

		/* Set the solved states */
		setPrun2( prunTableEdgeCorner, 0, 0 );
		int done = 1;

		int depth = 0;
		while (( done < N_SIZE ) && ( depth < 15 )) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = prunTableEdgeCorner[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, N_SIZE); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					s1.set(i, false);
					for (int m=0; m<N_MOVES; m++) {
						s1.moveTo(m, s2);
						int idx = s2.get(false);
						if (getPrun2(prunTableEdgeCorner, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTableEdgeCorner, i, depth);
							break;
						} else {
							setPrun2(prunTableEdgeCorner, idx, depth);
							int nsym = 1;
							unique++;
							s2.normalise(false);
							for (int j=0; j<4; j++) {
								long symS = Edge5.hasSym[s2.edge.coord][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									s2.edge.sym = (k<<2) + j;
									int idxx = s2.get(false);
									if( idxx == idx )
										nsym++;
									if (getPrun2(prunTableEdgeCorner, idxx) == 0x0f) {
										setPrun2(prunTableEdgeCorner, idxx, depth);
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
