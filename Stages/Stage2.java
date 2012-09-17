package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge2;
import cg.fivestage444.Coordinates.Center2;
import cg.fivestage444.CubeState;
import cg.fivestage444.Util;

public final class Stage2 {

	public final static int N_MOVES = 28;
	public final static int N_SYM = 16;
	private static byte[] prunTable;

	public Edge2 edge;
	public Center2 centerF;
	public Center2 centerB;

	public Stage2(){
		edge = new Edge2();
		centerF = new Center2();
		centerB = new Center2();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved()
		       && ( centerF.coord == centerB.coord )
		       && (( centerF.sym & 0x8 ) == ( centerB.sym & 0x8 ))
		       && (( edge.coord == 0 ) ^ (( centerF.sym & 0x8 ) == 0 )) 
		       && centerF.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage2 s ){
		edge.moveTo( m, s.edge );
		centerF.moveTo( m, s.centerF );
		centerB.moveTo( m, s.centerB );
	}

	/* Init */
	public static void init(){
		Edge2.init();
		Center2.init();
		initPruningTable();
	}

	/** Pruning functions **/

	/* Set from an index */
	private final void set( int idx ){
		edge.coord = idx % Edge2.N_COORD;
		centerF.coord = idx / Edge2.N_COORD;
		centerF.sym = 0;
	}

	/* Get an index from this */
	private final int get(boolean center){
		if( center )
			return centerF.coord * Edge2.N_COORD + edge.conjugate(centerF.sym);
		else
			return centerB.coord * Edge2.N_COORD + edge.conjugate(centerB.sym);
	}

	/* Rotate so that sym is 0 */
	private final void normalise(boolean center){
		if( center ){
			edge.coord = edge.conjugate(centerF.sym);
			centerF.sym = 0;
		}
		else {
			edge.coord = edge.conjugate(centerB.sym);
			centerB.sym = 0;
		}
	}

	/* Get pruning */
	public int pruning(){
		return Math.max( Util.getPrun2( prunTable, this.get(true)), Util.getPrun2( prunTable, this.get(false)));
	}

	/* Init pruning table */
	public static void initPruningTable(){
		final int N_SIZE = Edge2.N_COORD * Center2.N_COORD;
		final int INV_DEPTH = 7;
		Stage2 s1 = new Stage2();
		Stage2 s2 = new Stage2();

		prunTable = new byte[(N_SIZE+1)/2];
		for (int i=0; i<(N_SIZE+1)/2; i++)
			prunTable[i] = -1;

		/* Set the solved states */
		for( int a=0; a<Center2.SOLVED.length; a++){
			s1.centerF.coord = Center2.SOLVED[a];
			s1.edge.coord = 0;
			Util.setPrun2( prunTable, s1.get(true), 0 );
			s1.edge.coord = 414;
			Util.setPrun2( prunTable, s1.get(true), 0 );
		}

		int done = 12;

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
					int idx = s2.get(true);
					if (Util.getPrun2(prunTable, idx) != check) continue;
					done++;
					if (inv) {
						Util.setPrun2(prunTable, i, depth);
						break;
					} else {
						Util.setPrun2(prunTable, idx, depth);
						int nsym = 1;
						unique++;
						int symS = Center2.hasSym[s2.centerF.coord];
						s2.normalise(true);
						for (int k=0; symS != 0; symS>>=1, k++) {
							if ((symS & 1) == 0) continue;
							s2.centerF.sym = k;
							int idxx = s2.get(true);
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
