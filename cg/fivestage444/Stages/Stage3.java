package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.*;
import cg.fivestage444.CoordsHandler;
import cg.fivestage444.CubeState;
import cg.fivestage444.Moves;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage3 extends Stage {

	public final static int N_MOVES = 20;
	public final static int N_SYM = 8;
	private static PruningTable pTable;

	public final Edge3State edge;
	public final SymCoordState center;

	public Stage3(){
		edge = new Edge3State(CoordsHandler.edge3);
		center = new SymCoordState(CoordsHandler.center3);
		symState = center;
		STAGE_SIZE = edge.rc.N_COORD * center.sc.N_COORD;
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		center.pack(cube.centers);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved() && center.isSolved();
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage3 s = (Stage3)t;
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
	}

	/* Init */
	public static void init(){
		pTable = new PruningTable(new SymCoordState(CoordsHandler.center3), new Edge3State(CoordsHandler.edge3), N_MOVES, 11);
		pTable.initTable(new File("ptable_stage3.rbk"));

	}

	/** Pruning function **/

	@Override
	public int pruning(){
		return pTable.readTable(center.coord * edge.rc.N_COORD + edge.conjugate(center.sym));
	}

	@Override
	public int getMovesNumber() {
		return N_MOVES;
	}

	public long getId(){
		return center.coord * edge.rc.N_COORD + edge.conjugate(center.sym);
	}

	public long getId(int sym){
		return center.coord * edge.rc.N_COORD + edge.conjugate(sym);
	}

	public void setId(long id){
		edge.coord = (int)(id % edge.rc.N_COORD);
		center.coord = (int)(id / edge.rc.N_COORD);
		center.sym = 0;
	}

	public void normalize(){
		edge.coord = edge.conjugate(center.sym);
		center.sym = 0;
	}

}
