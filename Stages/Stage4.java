package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge4;
import cg.fivestage444.Coordinates.Corner4;
import cg.fivestage444.Coordinates.Center4;
import cg.fivestage444.CubeState;
import cg.fivestage444.Util;

public final class Stage4 {

	public final static int N_MOVES = 16;
	public final static int N_SIZE = Edge4.N_COORD * Corner4.N_COORD * Center4.N_COORD;
	public static byte[] prunTable;

	public Edge4 edge;
	public Corner4 corner;
	public Center4 center;

	public Stage4(){
		edge = new Edge4();
		corner = new Corner4();
		center = new Center4();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved() && center.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage4 s ){
		edge.moveTo( m, s.edge );
		corner.moveTo( m, s.corner );
		center.moveTo( m, s.center );
	}

	/* Init */
	public static void init(){
		Edge4.init();
		Corner4.init();
		Center4.init();
		initPruningTable();
	}

	/** Pruning functions **/

	/* Set from an index */
	public void set( int idx ){
		center.coord = idx % Center4.N_COORD;
		idx /= Center4.N_COORD;
		corner.coord = idx % Corner4.N_COORD;
		edge.coord = idx / Corner4.N_COORD;
		edge.sym = 0;
	}

	/* Get an index from this */
	int get(){
		return ( edge.coord * Corner4.N_COORD + corner.conjugate(edge.sym) ) * Center4.N_COORD + center.conjugate(edge.sym);
	}

	/* Get an index from this */
	void normalise(){
		corner.coord = corner.conjugate(edge.sym);
		center.coord = center.conjugate(edge.sym);
		edge.sym = 0;
	}

	/* Get pruning */
	public int pruning(){
		return Util.getPrun2( prunTable, this.get());
	}

	/* Init pruning table */
	public static void initPruningTable(){
		if( prunTable != null ) return;
		final int INV_DEPTH = 13;
		Stage4 s1 = new Stage4();
		Stage4 s2 = new Stage4();

		prunTable = new byte[(N_SIZE+1)/2];
		for (int i=0; i<(N_SIZE+1)/2; i++)
			prunTable[i] = -1;

		/* Set the solved states */
		for( int a=0; a<Center4.SOLVED.length; a++)
			Util.setPrun2( prunTable, Center4.SOLVED[a], 0 );
		int done = Center4.SOLVED.length;

		int depth = 0;
		while (( done < N_SIZE ) && ( depth < 15 )) {
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
						int symS = Edge4.hasSym[s2.edge.coord];
						s2.normalise();
						for (int k=0; symS != 0; symS>>=1, k++) {
							if ((symS & 1) == 0) continue;
							s2.edge.sym = k;
							int idxx = s2.get();
							if( idxx == idx )
								nsym++;
							if (Util.getPrun2(prunTable, idxx) == 0x0f) {
								Util.setPrun2(prunTable, idxx, depth);
								done++;
							}
						}
						pos += 16/nsym;
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
