package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge1;
import cg.fivestage444.Coordinates.Corner1;

public final class Stage1 {

	final static int N_MOVES = 36;

	Edge1 edge;
	Corner1 corner;

	byte[] prunTable;

	Stage1(){
		edge = new Edge1();
		corner = new Corner1();
	}

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

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved( edge.sym );
	}

	/* Move */
	public void moveTo( int m, Stage1 s ){
		edge.moveTo( m, s.edge );
		corner.moveTo( m, s.corner );
	}

	/* Init pruning table */
	public void initPruningTable(){
		final static int N_SIZE = Edge1.N_COORD * Corner1.N_COORD;
		final static int INV_DEPTH = 7;
		Stage1 s = new Stage1();

		prunTable = new byte[N_SIZE/4+1];

		/* Set the solved states */
		setPrun4( prunTable, 1906, 3 );
		int done = 1;

		int depth = 0;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0 : ((depth+2)%3)+1;
			int check = inv ? ((depth+2)%3)+1 : 0;
			int save = (depth % 3) + 1;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = prunTable[i>>2];
				if (!inv && val == 0) {
					i += 4;
					continue;
				}
				for (int end=Math.min(i+4, N_SIZE); i<end; i++, val>>=2) {
					if ((val & 0x3)/*getPrun4(prunTable, i)*/ != select) continue;
					set(i);
					for (int m=0; m<N_MOVES; m++) {
						moveTo(s);
						int idx = s.get();
						if (getPrun4(prunTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun4(prunTable, i, save);
							break;
						} else {
							setPrun4(prunTable, idx, save);
							int nsym = 1;
							unique++;
							long symS = symState[symx];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 0x1L) == 0) continue;
								s.edge.sym = k;
								int idxx = s.get();
								if( idxx == idx )
									nsym++;
								if (getPrun4(prunTable, idxx) == 0) {
									setPrun4(prunTable, idxx, save);
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
