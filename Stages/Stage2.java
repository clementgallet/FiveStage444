package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Center2;
import cg.fivestage444.Coordinates.Edge2;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

public final class Stage2 extends Stage {

	public final static int N_MOVES = 28;
	public final static int N_SYM = 16;
	private static PruningTable pTable;

	public final Edge2 edge;
	public final Center2 centerF;
	public final Center2 centerB;

	public Stage2(){
		edge = new Edge2();
		centerF = new Center2();
		centerB = new Center2();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		centerF.pack(cube.centers, 4);
		centerF.computeSym();
		centerB.pack(cube.centers, 5);
		centerB.computeSym();
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved()
			   && ( centerF.coord == centerB.coord )
			   && (( centerF.sym & 0x8 ) == ( centerB.sym & 0x8 ))
			   && (( edge.coord == 0 ) ^ (( centerF.sym & 0x8 ) == 0 ))
			   && centerF.isSolved();
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage2 s = (Stage2)t;
		edge.moveTo( m, s.edge );
		centerF.moveTo( m, s.centerF );
		centerB.moveTo( m, s.centerB );
	}

	/* Init */
	public static void init(){
		Edge2.init();
		Center2.init();
		pTable = new PruningTable(new Center2(), new Edge2(), N_MOVES, 7);
		pTable.initTable();
	}

	/** Pruning function **/

	@Override
	public int pruning(){
		return Math.max( pTable.readTable(centerF.coord * Edge2.N_COORD + edge.conjugate(centerF.sym)),
		                 pTable.readTable(centerB.coord * Edge2.N_COORD + edge.conjugate(centerB.sym)));
	}

	@Override
	public int getMovesNumber() {
		return N_MOVES;
	}
}
