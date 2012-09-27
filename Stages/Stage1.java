package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge1;
import cg.fivestage444.Coordinates.Corner1;
import cg.fivestage444.CubeState;
import cg.fivestage444.Util;

public final class Stage1 {

	public final static int N_MOVES = 36;
	public final static int N_SYM = 48;
	public final static int N_SIZE = Edge1.N_COORD * Corner1.N_COORD;
	public static byte[] prunTable;

	Edge1 edge;
	Corner1 corner;

	public Stage1(){
		edge = new Edge1();
		corner = new Corner1();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.packRaw(cube.edges);
        edge.computeSym();
		corner.pack(cube.corners);
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved( edge.sym );
	}

	/* Move */
	public void moveTo( int m, Stage1 s ){
		edge.moveTo( m, s.edge );
		corner.moveTo( m, s.corner );
	}

	/* Init */
	public static void init(){
		Edge1.init();
		Corner1.init();
		initPruningTable();
	}

	/** Pruning functions **/

	/* Set from an index */
	private final void set( int idx ){
		corner.coord = idx % Corner1.N_COORD;
		edge.coord = idx / Corner1.N_COORD;
		edge.sym = 0;
	}

	/* Get an index from this */
	private final int get(){
		return edge.coord * Corner1.N_COORD + corner.conjugate(edge.sym);
	}

	/* Rotate so that the sym is 0 */
	private final void normalise(){
		corner.coord = corner.conjugate(edge.sym);
		edge.sym = 0;
	}

	/* Get pruning */
	public int pruning(){
		return Util.getPrun2( prunTable, this.get());
	}

	/* Init pruning table */
	public static void initPruningTable(){
		if( prunTable != null ) return;
		final int INV_DEPTH = 7;
		Stage1 s1 = new Stage1();
		Stage1 s2 = new Stage1();

		prunTable = new byte[(N_SIZE+1)/2];
		for (int i=0; i<(N_SIZE+1)/2; i++)
			prunTable[i] = -1;

		/* Set the solved states */
		Util.setPrun2( prunTable, 2173, 0 );
		int done = 1;

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
						long symS = Edge1.hasSym[s2.edge.coord];
						s2.normalise();
						for (int k=0; symS != 0; symS>>=1, k++) {
							if ((symS & 0x1L) == 0) continue;
							s2.edge.sym = k;
							int idxx = s2.get();
							if( idxx == idx )
								nsym++;
							if (Util.getPrun2(prunTable, idxx) == 0x0f) {
								Util.setPrun2(prunTable, idxx, depth);
								done++;
							}
						}
						pos += 48/nsym;
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
