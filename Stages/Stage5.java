package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge5;
import cg.fivestage444.Coordinates.Center5;
import cg.fivestage444.Coordinates.Corner5;
import cg.fivestage444.PruningTable;
import cg.fivestage444.Util;

import java.io.File;

public final class Stage5 {

	public final static int N_MOVES = 12;
	public final static int N_SYM = 48;
	private static PruningTable pTableEdgeCenter;
	private static PruningTable pTableEdgeCorner;

	public final Edge5 edge;
	public final Center5 center;
	public final Corner5 corner;

	public Stage5(){
		edge = new Edge5();
		center = new Center5();
		corner = new Corner5();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved() && corner.isSolved(edge.sym) && center.isSolved(edge.sym);
	}

	/* Move */
	public void moveTo( int m, Stage5 s ){
		edge.moveTo( m, s.edge );
		center.moveTo( m, s.center );
		corner.moveTo( m, s.corner );
	}

	/* Init */
	public static void init(){
		Edge5.init();
		Center5.init();
		Corner5.init();
		pTableEdgeCenter = new PruningTable(new Edge5(), new Center5(), N_MOVES, 11);
		pTableEdgeCenter.initTable(new File("ptable_stage5_edgcen.rbk"));
		pTableEdgeCorner = new PruningTable(new Edge5(), new Corner5(), N_MOVES, 11);
		pTableEdgeCorner.initTable(new File("ptable_stage5_edgcor.rbk"));
	}

	/** Pruning functions **/

	public int pruning(){
		return Math.max( pTableEdgeCenter.readTable(edge.coord * Center5.N_COORD + center.conjugate(edge.sym)),
		                 pTableEdgeCorner.readTable(edge.coord * Corner5.N_COORD + corner.conjugate(edge.sym)));
	}
}
