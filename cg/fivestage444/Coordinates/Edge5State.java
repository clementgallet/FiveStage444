package cg.fivestage444.Coordinates;

import cg.fivestage444.Symmetry;

public class Edge5State extends SymCoordState {

	public Edge5State(){}

	public Edge5State(SymCoord sc){
		super(sc);
	}

	/* Move */
	@Override
	public void moveTo( int m, SymCoordState e ){
		e.coord = sc.move[coord][Symmetry.moveConjugateCo4Stage[m][sym]];
		e.sym = Symmetry.symIdxCo4Multiply[e.coord & sc.SYM_MASK][sym];
		e.coord >>>= sc.SYM_SHIFT;
	}

	@Override
	public long[] getSyms(){
		return ((Edge5)sc).hasSym[coord];
	}

}
