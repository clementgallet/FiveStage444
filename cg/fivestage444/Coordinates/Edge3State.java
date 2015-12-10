package cg.fivestage444.Coordinates;

import cg.fivestage444.Symmetry;
import cg.fivestage444.CubeState;
import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;

public class Edge3State extends RawCoordState {

	public Edge3State(){}

	public Edge3State(RawCoord rc){
		super(rc);
	}

	/* Move */
	@Override
	public void moveTo( int m, RawCoordState e ){
		e.coord = (rc.move[coord >> 1][m] << 1) | ((coord & 1) ^ (( ((Edge3)rc).moveParity >>> m ) & 1 ));
	}

	/* Get the conjugated coordinate */
	@Override
	public int conjugate( int sym ){
		return (rc.conj[coord >> 1][sym] << 1) | (coord & 1);
	}

	@Override
	public void unpack(Cubies cubie){
		rc.unpack(cubie, coord >> 1);
	}

	@Override
	public void pack(Cubies cubie){
		coord = (rc.pack(cubie) << 1) | ((EdgeCubies)cubie).parityUD();
	}

	@Override
	public void pack(CubeState cube){
		coord = (rc.pack(cube.edges) << 1) | cube.edges.parityUD();
	}

}
