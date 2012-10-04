package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge4;
import cg.fivestage444.Coordinates.Corner4;
import cg.fivestage444.Coordinates.Center4;
import cg.fivestage444.PruningTable;
import cg.fivestage444.Util;

import java.io.File;

public final class Stage4 {

	public final static int N_MOVES = 16;
	public final static int N_SYM = 16;
	public static PruningTable pTable;

	public final Edge4 edge;
	public final Corner4 corner;
	public final Center4 center;

	public Stage4(){
		edge = new Edge4();
		corner = new Corner4();
		center = new Center4();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved() && center.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage4 s ){
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

	public int pruning(){
		return pTable.readTable((edge.coord * Corner4.N_COORD + corner.conjugate(edge.sym) ) * Center4.N_COORD + center.conjugate(edge.sym));
	}
}
