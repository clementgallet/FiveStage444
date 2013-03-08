package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.*;
import cg.fivestage444.CoordsHandler;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage5 extends Stage {

	public final static int N_MOVES = 12;
	public final static int N_SYM = 48;
	private static PruningTable pTableEdgeCenter;
	private static PruningTable pTableEdgeCorner;

	public final Edge5State edge;
	public final RawCoordState center;
	public final RawCoordState corner;

	public Stage5(){
		edge = new Edge5State(CoordsHandler.edge5);
		center = new RawCoordState(CoordsHandler.center5);
		corner = new RawCoordState(CoordsHandler.corner5);
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		corner.pack(cube.corners);
		center.pack(cube.centers);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved(edge.sym) && center.isSolved(edge.sym);
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage5 s = (Stage5)t;
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
		corner.moveTo( m, s.corner );
	}

	/* Init */
	public static void init(){
		//Edge5.init();
		//Center5.init();
		//Corner5.init();
		pTableEdgeCenter = new PruningTable(new Edge5State(CoordsHandler.edge5), new RawCoordState(CoordsHandler.center5), N_MOVES, 11);
		pTableEdgeCenter.initTable(new File("ptable_stage5_edgcen.rbk"));
		pTableEdgeCorner = new PruningTable(new Edge5State(CoordsHandler.edge5), new RawCoordState(CoordsHandler.corner5), N_MOVES, 11);
		pTableEdgeCorner.initTable(new File("ptable_stage5_edgcor.rbk"));
	}

	/** Pruning functions **/

	@Override
	public int pruning(){
		return Math.max( pTableEdgeCenter.readTable(edge.coord * center.rc.N_COORD + center.conjugate(edge.sym)),
		                 pTableEdgeCorner.readTable(edge.coord * corner.rc.N_COORD + corner.conjugate(edge.sym)));
	}

	@Override
	public int getMovesNumber() {
		return N_MOVES;
	}

	@Override
	public int howManySolutions() {
		return 1;
	}

	@Override
	public int howManyAttempts() {
		return 1;
	}
}
