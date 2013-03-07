package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Cubies.Cubies;

public class RawCoordState {
	public RawCoord rc;

	public RawCoordState(RawCoord rc){
		this.rc = rc;
	}

	public int coord;

	/* Check if solved */
	public boolean isSolved(){
		for (int solved : rc.solvedStates)
			if(coord == solved)
				return true;
		return false;
	}

	/* Check if solved */
	public boolean isSolved(int sym){
		int symed_coord = rc.conj[coord][sym];
		for (int solved : rc.solvedStates)
			if(symed_coord == solved)
				return true;
		return false;
	}

	/* Move */
	public void moveTo( int m, RawCoordState e ){
		e.coord =  rc.move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return rc.conj[coord][sym];
	}

	public void pack(Cubies cubie){
		coord = rc.pack(cubie);
	}

	public void pack(CubeState cube){
		for (Cubies cubie : cube.cubies)
			if(cubie.getClass() == rc.cubieType.getClass())
				coord = rc.pack(cubie);
	}
}
