package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Center2;
import cg.fivestage444.Coordinates.Edge2;
import cg.fivestage444.Coordinates.RawCoordState;
import cg.fivestage444.Coordinates.SymCoordState;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

public final class Stage2 extends Stage {

	public final static int N_MOVES = 28;
	public final static int N_SYM = 16;
	private static PruningTable pTable;

	public final RawCoordState edge;
	public final SymCoordState centerF;
	public final SymCoordState centerB;

	public Stage2(){
		edge = new RawCoordState(new Edge2());
		centerF = new SymCoordState(new Center2());
		centerB = new SymCoordState(new Center2());
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		/* TODO: Deal with the two centers thing */
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
/*	public static void init(){
		edge.init();
		Center2.init();
		pTable = new PruningTable(new Center2(), new Edge2(), N_MOVES, 7);
		pTable.initTable();
	}
*/

	/** Pruning function **/

	@Override
	public int pruning(){
		return Math.max( pTable.readTable(centerF.coord * edge.rc.N_COORD + edge.conjugate(centerF.sym)),
		                 pTable.readTable(centerB.coord * edge.rc.N_COORD + edge.conjugate(centerB.sym)));
	}

	@Override
	public int getMovesNumber() {
		return N_MOVES;
	}

	@Override
	public int howManySolutions() {
		return 10;
	}

	@Override
	public int howManyAttempts() {
		return 5;
	}
}
