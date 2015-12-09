package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.*;
import cg.fivestage444.CoordsHandler;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage5 extends Stage {

	public final static int N_MOVES = 12;
	public final static int N_SYM = 192;
	private static PruningTable pTable;

	public final Edge5State edge;
	public final RawCoordState center;
	public final RawCoordState corner;

	public Stage5(){
		edge = new Edge5State(CoordsHandler.edge5);
		symState = edge;
		center = new RawCoordState(CoordsHandler.center5);
		corner = new RawCoordState(CoordsHandler.corner5);
		STAGE_SIZE = edge.sc.N_COORD * corner.rc.N_COORD * center.rc.N_COORD;
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
		pTable = new PruningTable(new Edge5State(CoordsHandler.edge5), new RawCoordState(CoordsHandler.center5), new RawCoordState(CoordsHandler.corner5), N_MOVES, 16);
		pTable.initTable(new File("ptable_stage5.rbk"));
	}

	/** Pruning functions **/

	@Override
	public int pruning(){
		return pTable.readTable((edge.coord * corner.rc.N_COORD + corner.conjugate(edge.sym) ) * center.rc.N_COORD + center.conjugate(edge.sym));
	}

	@Override
	public int getMovesNumber() {
		return N_MOVES;
	}

	public long getId(){
		return (edge.coord * corner.rc.N_COORD + corner.conjugate(edge.sym) ) * center.rc.N_COORD + center.conjugate(edge.sym);
	}

	public long getId(int sym){
		return (edge.coord * corner.rc.N_COORD + corner.conjugate(sym) ) * center.rc.N_COORD + center.conjugate(sym);
	}

	public void setId(long id){
		center.coord = (int)(id % center.rc.N_COORD);
		id /= center.rc.N_COORD;
		corner.coord = (int)(id % corner.rc.N_COORD);
		edge.coord = (int)(id / corner.rc.N_COORD);
		edge.sym = 0;
	}

	public void normalize(){
		corner.coord = corner.conjugate(edge.sym);
		center.coord = center.conjugate(edge.sym);
		edge.sym = 0;
	}

}
