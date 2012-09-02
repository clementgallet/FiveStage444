package cg.fivestage444.Stages;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Edge2;
import cg.fivestage444.Coordinates.Center2;

public final class Stage2 {

	final static int N_MOVES = 28;
	static int[] prunTable;

	Edge2 edge;
	Center2 centerF;
	Center2 centerB;

	Stage2(){
		edge = new Edge2();
		centerF = new Center2();
		centerB = new Center2();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube);
		centerF.pack(cube, 4);
		centerB.pack(cube, 5);
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved()
		       && ( cornerF.coord == cornerB.coord )
		       && (( cornerF.sym & 0x8 ) == ( cornerB.sym & 0x8 ))
		       && (( edge.coord == 0 ) ^ (( cornerF.sym & 0x8 ) == 0 )) 
		       && cornerF.isSolved();
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
	void set( int idx ){
		edge.coord = idx % Edge2.N_COORD;
		centerF.coord = idx / Edge2.N_COORD;
		centerF.sym = 0;
	}

	/* Get an index from this */
	int get(boolean centerF){
		if( centerF )
			return centerF.coord * Edge2.N_COORD + edge.conjugate(centerF.sym);
		else
			return centerB.coord * Edge2.N_COORD + edge.conjugate(centerB.sym);
	}

	/* Get pruning */
	public int pruning(){
		return Math.max( getPrun2( prunTable, this.get(true)), getPrun2( prunTable, this.get(false)));
	}

	/* Init pruning table */
	public static void initPruningTable(){
		final static int N_SIZE = Edge2.N_COORD * Center2.N_COORD;
		final static int INV_DEPTH = 7;
		Stage2 s = new Stage2();

		prunTable = new int[(N_SIZE+7)/8];

		/* Set the solved states */
		for( int a=0; a<Center2.SOLVED.length; a++){
			centerF.coord = Center2.SOLVED[a];
			edge.coord = 0;
			setPrun2( prunTable, get(), 0 );
			edge.coord = 414;
			setPrun2( prunTable, get(), 0 );
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
			for (int i=0; i<N_SIZE;) {
				int val = prunTable[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, N_SIZE); i<end; i++, val>>=4) {
					if ((val & 0xf)/*getPrun2(prunTable, i)*/ != select) continue;
					set(i);
					for (int m=0; m<N_MOVES; m++) {
						moveTo(s);
						int idx = s.get(true);
						if (getPrun2(prunTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTable, i, depth);
							break;
						} else {
							setPrun2(prunTable, idx, depth);
							int nsym = 1;
							unique++;
							int symS = Center2.hasSym[symx];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 1) == 0) continue;
								s.centerF.sym = k;
								int idxx = s.get(true);
								if( idxx == idx )
									nsym++;
								if (getPrun2(prunTable, idxx) == 0x0f) {
									setPrun2(prunTable, idxx, depth);
									done++;
								}
							}
							pos += 16/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}
}
