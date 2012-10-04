package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge3;
import cg.fivestage444.Coordinates.Center3;
import cg.fivestage444.Moves;
import cg.fivestage444.PruningTable;
import cg.fivestage444.Util;

import java.io.File;

public final class Stage3 {

	public final static int N_MOVES = 20;
	public final static int N_SYM = 8;
	private static int moveParity;
	public static PruningTable pTable;

	public final Edge3 edge;
	public final Center3 center;
	public byte parity;

	public Stage3(){
		edge = new Edge3();
		center = new Center3();
	}

	/* Check if solved */
	public boolean isSolved(){
		return ( parity == 0 ) && edge.isSolved() && center.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage3 s ){
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
		Edge3.init();
		Center3.init();
		pTable = new PruningTable(new Center3(), new Edge3(), N_MOVES, 11);
		pTable.initTable(new File("ptable_stage3.rbk"));

	}

	/** Pruning function **/

	public int pruning(){
		return pTable.readTable(center.coord * Edge3.N_COORD + edge.conjugate(center.sym));
	}
}
