package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.*;
import cg.fivestage444.CubeState;
import cg.fivestage444.Moves;
import cg.fivestage444.PruningTable;

import java.io.File;

public final class Stage3 extends Stage {

	public final static int N_MOVES = 20;
	public final static int N_SYM = 8;
	private static int moveParity;
	private static PruningTable pTable;

	public final Edge3State edge;
	public final SymCoordState center;
	public byte parity;

	public Stage3(){
		edge = new Edge3State(new Edge3());
		center = new SymCoordState(new Center3());
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
		//Edge3.init();
		//Center3.init();
		pTable = new PruningTable(new SymCoordState(new Center3()), new Edge3State(new Edge3()), N_MOVES, 11);
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

	@Override
	public int howManySolutions() {
		return 10;
	}

	@Override
	public int howManyAttempts() {
		return 5;
	}
}
