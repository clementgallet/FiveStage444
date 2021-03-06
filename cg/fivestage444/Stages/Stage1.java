package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.RawCoordState;
import cg.fivestage444.Coordinates.SymCoordState;
import cg.fivestage444.CoordsHandler;
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
		edge = new SymCoordState(CoordsHandler.edge1);
		symState = edge;
		corner = new RawCoordState(CoordsHandler.corner1);
		STAGE_SIZE = edge.sc.N_COORD * corner.rc.N_COORD;
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
		pTable = new PruningTable(new SymCoordState(CoordsHandler.edge1), new RawCoordState(CoordsHandler.corner1), N_MOVES, 7);
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

	public long getId(){
		return edge.coord * corner.rc.N_COORD + corner.conjugate(edge.sym);
	}

	public long getId(int sym){
		return edge.coord * corner.rc.N_COORD + corner.conjugate(sym);
	}

	public void setId(long id){
		corner.coord = (int)(id % corner.rc.N_COORD);
		edge.coord = (int)(id / corner.rc.N_COORD);
		edge.sym = 0;
	}

	public void normalize(){
		corner.coord = corner.conjugate(edge.sym);
		edge.sym = 0;
	}

}
