package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.*;
import cg.fivestage444.CoordsHandler;
import cg.fivestage444.CubeState;
import cg.fivestage444.PruningTable;
import cg.fivestage444.Symmetry;

import java.io.File;

public final class Stage2 extends Stage {

	public final static int N_MOVES = 28;
	public final static int N_SYM = 16;
	private static PruningTable pTable;

	public final RawCoordState edge;
	public final SymCoordState center;

	public Stage2(){
		edge = new RawCoordState(CoordsHandler.edge2);
		center = new SymCoordState(CoordsHandler.center2);
		symState = center;
		STAGE_SIZE = (long) edge.rc.N_COORD * center.sc.N_COORD;
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		center.pack(cube.centers);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return edge.isSolved() && center.isSolved(); // FIXME! Seems to be e==414 & sym==7 or e==0 & sym==14. Why???
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage2 s = (Stage2)t;
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
	}

	/* Init */
	public static void init(){
		pTable = new PruningTable(new SymCoordState(CoordsHandler.center2), new RawCoordState(CoordsHandler.edge2), N_MOVES, 11);
		pTable.initTable(new File("ptable_stage2.rbk"));
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
		return (long)center.coord * center.sc.N_COORD + edge.conjugate(center.sym);
 	}

	public long getId(int sym){
		return (long)center.coord * center.sc.N_COORD + edge.conjugate(sym);
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
