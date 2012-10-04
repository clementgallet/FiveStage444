package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.Edge2;
import cg.fivestage444.Coordinates.Center2;
import cg.fivestage444.PruningTable;
import cg.fivestage444.Util;

public final class Stage2 {

	public final static int N_MOVES = 28;
	public final static int N_SYM = 16;
	private static PruningTable pTable;

	public final Edge2 edge;
	public final Center2 centerF;
	public final Center2 centerB;

	public Stage2(){
		edge = new Edge2();
		centerF = new Center2();
		centerB = new Center2();
	}

	/* Check if solved */
	public boolean isSolved(){
		return edge.isSolved()
			   && ( centerF.coord == centerB.coord )
			   && (( centerF.sym & 0x8 ) == ( centerB.sym & 0x8 ))
			   && (( edge.coord == 0 ) ^ (( centerF.sym & 0x8 ) == 0 ))
			   && centerF.isSolved();
	}

	/* Move */
	public void moveTo( int m, Stage2 s ){
		edge.moveTo( m, s.edge );
		centerF.moveTo( m, s.centerF );
		centerB.moveTo( m, s.centerB );
	}

	/* Init */
	public static void init(){
		Edge2.init();
		Center2.init();
		pTable = new PruningTable(new Center2(), new Edge2(), N_MOVES);
		pTable.fillTable();
	}

	/** Pruning function **/

	public int pruning(){
		return Math.max( pTable.readTable(centerF.coord * Edge2.N_COORD + edge.conjugate(centerF.sym)),
		                 pTable.readTable(centerB.coord * Edge2.N_COORD + edge.conjugate(centerB.sym)));
	}
}
