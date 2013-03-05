package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Center4;
import cg.fivestage444.Coordinates.Corner4;
import cg.fivestage444.Coordinates.Edge4;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage4 extends Stage {

	public final static int N_MOVES = 16;
	public final static int N_SYM = 16;
	private static PruningTable pTable;

	public final Edge4 edge;
	public final Corner4 corner;
	public final Center4 center;

	public Stage4(){
		edge = new Edge4();
		corner = new Corner4();
		center = new Center4();
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		edge.computeSym();
		corner.pack(cube.corners);
		center.pack(cube.centers);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved() && center.isSolved();
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage4 s = (Stage4)t;
		edge.moveTo( m, s.edge );
		corner.moveTo( m, s.corner );
		center.moveTo( m, s.center );
	}

	/* Init */
	public static void init(){
		Edge4.init();
		Corner4.init();
		Center4.init();
		pTable = new PruningTable(new Edge4(), new Corner4(), new Center4(), N_MOVES, 11);
		pTable.initTable(new File("ptable_stage4.rbk"));
	}

	/** Pruning functions **/

	@Override
	public int pruning(){
		return pTable.readTable((edge.coord * Corner4.N_COORD + corner.conjugate(edge.sym) ) * Center4.N_COORD + center.conjugate(edge.sym));
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
