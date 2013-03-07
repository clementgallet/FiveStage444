package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Corner1;
import cg.fivestage444.Coordinates.Edge1;
import cg.fivestage444.Coordinates.RawCoordState;
import cg.fivestage444.Coordinates.SymCoordState;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage1 extends Stage {

	public final static int N_MOVES = 36;
	public final static int N_SYM = 48;
	private static PruningTable pTable;

	private final SymCoordState edge;
	private final RawCoordState corner;

	public Stage1(){
		edge = new SymCoordState(new Edge1());
		corner = new RawCoordState(new Corner1());
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		corner.pack(cube.corners);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved( edge.sym );
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage s ){
		Stage1 t = (Stage1)s;
		edge.moveTo( m, t.edge );
		corner.moveTo( m, t.corner );
	}

	/* Init */
	public static void init(){
		//Edge1.init();
		//Corner1.init();
		pTable = new PruningTable(new Edge1(), new Corner1(), N_MOVES, 7);
		pTable.initTable(new File("ptable_stage1.rbk"));
	}

	/** Pruning function **/

	@Override
	public int pruning(){
		return pTable.readTable(edge.coord * corner.rc.N_COORD + corner.conjugate(edge.sym));
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
