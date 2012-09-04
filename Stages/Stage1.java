package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge1;
import cg.fivestage444.Coordinates.Corner1;
import cg.fivestage444.CubeState;

public final class Stage1 {

	public final static int N_MOVES = 36;
	public final static int N_SIZE = Edge1.N_COORD * Corner1.N_COORD;
	public static int[] prunTable;

	Edge1 edge;
	Corner1 corner;

	public Stage1(){
		edge = new Edge1();
		corner = new Corner1();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube);
		corner.pack(cube);
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
	void set( int idx ){
		corner.coord = idx % Corner1.N_COORD;
		edge.coord = idx / Corner1.N_COORD;
		edge.sym = 0;
	}

	/* Get an index from this */
	int get(){
		return edge.coord * Corner1.N_COORD + corner.conjugate(edge.sym);
	}

	/* Get pruning */
	public int pruning(){
		return getPrun2( prunTable, this.get());
	}

	/* Init pruning table */
	public static void initPruningTable(){
		if( prunTable != null ) return;
		final int INV_DEPTH = 7;
		Stage1 s1 = new Stage1();
		Stage1 s2 = new Stage1();

		prunTable = new int[(N_SIZE+7)/8];
		for (int i=0; i<(N_SIZE+7)/8; i++)
			prunTable[i] = -1;

		/* Set the solved states */
		setPrun2( prunTable, 1906, 0 );
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
					if ((val & 0x0f)/*getPrun2(prunTable, i)*/ != select) continue;
					s1.set(i);
					for (int m=0; m<N_MOVES; m++) {
						s1.moveTo(m, s2);
						int idx = s2.get();
						if (getPrun2(prunTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTable, i, depth);
							break;
						} else {
							setPrun2(prunTable, idx, depth);
							int nsym = 1;
							unique++;
							long symS = Edge1.hasSym[s2.edge.coord];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 0x1L) == 0) continue;
								s2.edge.sym = k;
								int idxx = s2.get();
								if( idxx == idx )
									nsym++;
								if (getPrun2(prunTable, idxx) == 0x0f) {
									setPrun2(prunTable, idxx, depth);
									done++;
								}
							}
							pos += 48/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
