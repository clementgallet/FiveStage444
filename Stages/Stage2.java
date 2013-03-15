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
	public final Center2State centerF;
	public final Center2State centerB;

	public Stage2(){
		edge = new RawCoordState(CoordsHandler.edge2);
		centerF = new Center2State(CoordsHandler.center2);
		symState = centerF;
		centerB = new Center2State(CoordsHandler.center2);
		STAGE_SIZE = edge.rc.N_COORD * centerF.sc.N_COORD * centerB.sc.N_COORD * centerB.sc.N_SYM;
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		/* TODO: Deal with the two centers thing */
		centerF.pack(cube.centers, 4);
		centerB.pack(cube.centers, 5);
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
		//edge.init();
		//Center2.init();
		pTable = new PruningTable(new Center2State(CoordsHandler.center2), new RawCoordState(CoordsHandler.edge2), N_MOVES, 7);
		pTable.initTable(new File("ptable_stage2.rbk"));
	}


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

	public int getId(){
		return (centerF.coord * centerB.sc.N_COORD + centerB.coord )
				              * centerB.sc.N_SYM   + Symmetry.symIdxMultiply[centerB.sym][centerF.sym] // TODO: Untested!!
                              * edge.rc.N_COORD    + edge.conjugate(centerF.sym);
 	}

	public int getId(int sym){
		centerF.sym = sym;
		return getId();
	}

	public void setId(int id){
		edge.coord = id % edge.rc.N_COORD;
		id /= edge.rc.N_COORD;
		centerB.sym = id % centerB.sc.N_SYM;
		id /= centerB.sc.N_SYM;
		centerB.coord = id % centerB.sc.N_COORD;
		centerF.coord = id / centerB.sc.N_COORD;
		centerF.sym = 0; // TODO: Untested either
	}

	public void normalize(){
		edge.coord = edge.conjugate(centerF.sym);
		centerB.sym = Symmetry.symIdxMultiply[centerB.sym][centerF.sym];
		centerF.sym = 0;
	}

}
