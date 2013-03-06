package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Symmetry;

public class SymCoordState {

	public SymCoord sc;

	public SymCoordState(SymCoord sc){
		this.sc = sc;
	}

	/* Coordinates */
	public int raw_coord;
	public int coord;
	public int sym;

	/* Check if solved */
	public boolean isSolved(){
		for (int s : sc.SolvedStates)
			if( coord == s)
				return true;
		return false;
	}

	/* Move */
	public void moveTo( int m, SymCoordState c ){
		c.coord = sc.move[coord][Symmetry.moveConjugateStage[m][sym]];
		c.sym = Symmetry.symIdxMultiply[c.coord & sc.SYM_MASK][sym];
		c.coord >>>= sc.SYM_SHIFT;
	}

	public long[] getSyms(){
		return new long[]{sc.hasSym[coord]};
	}

	/* Compute the sym coordinate from the raw coordinate */
	public void computeSym (){
		int symcoord = sc.raw2sym[raw_coord];
		this.coord = symcoord >> sc.SYM_SHIFT;
		this.sym = symcoord & sc.SYM_MASK;
	}

	public void pack(Cubies cubie){
		coord = sc.pack(cubie);
	}

}
