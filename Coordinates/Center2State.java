package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;

public class Center2State extends SymCoordState{

	public Center2State(SymCoord sc){
		super(sc);
	}

	public void pack(Cubies cubies, int center){
		raw_coord = ((Center2)sc).pack(cubies, center);
		computeSym();
	}
}
