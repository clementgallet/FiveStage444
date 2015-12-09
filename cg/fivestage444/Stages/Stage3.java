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
	private static int moveParity;
	private static PruningTable pTable;

	public final RawCoordState edge;
	public final SymCoordState center;
	public byte parity;

	public Stage3(){
		edge = new RawCoordState(CoordsHandler.edge3);
		center = new SymCoordState(CoordsHandler.center3);
		symState = center;
		STAGE_SIZE = 2 * edge.rc.N_COORD * center.sc.N_COORD;
	}

	/* Pack from CubeState */
	public void pack(CubeState cube){
		edge.pack(cube.edges);
		center.pack(cube.centers);
		parity = (byte)cube.edges.parityUD();
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return ( parity == 0 ) && edge.isSolved() && center.isSolved();
	}

	/* Move */
	@Override
	public void moveTo( int m, Stage t ){
		Stage3 s = (Stage3)t;
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
		s.parity = (byte)( parity ^ (( moveParity >>> m ) & 1 ));
	}

	/* Init */
	public static void init(){
		/* Initialize move parity */
		for( int i = 0; i < N_MOVES; i++){
			int m = Moves.stage2moves[i];
			if (((( m / 3 ) % 3 ) == 1 ) && (( m % 3 ) < 2 ))
				moveParity |= 1 << i;
		}
		pTable = new PruningTable(new SymCoordState(CoordsHandler.center3), new RawCoordState(CoordsHandler.edge3), N_MOVES, 11);
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
		return ((center.coord * edge.rc.N_COORD + edge.conjugate(center.sym)) << 1) + parity;
	}

	public long getId(int sym){
		return ((center.coord * edge.rc.N_COORD + edge.conjugate(sym)) << 1) + parity;
	}

	public void setId(long id){
		parity = (byte)(id & 1);
		id >>= 1;
		edge.coord = (int)(id % edge.rc.N_COORD);
		center.coord = (int)(id / edge.rc.N_COORD);
		center.sym = 0;
	}

	public void normalize(){
		edge.coord = edge.conjugate(center.sym);
		center.sym = 0;
	}

}
