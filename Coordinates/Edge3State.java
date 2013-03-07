package cg.fivestage444.Coordinates;

public class Edge3State extends RawCoordState{
	public Edge3State(RawCoord rc){
		super(rc);
	}

	/* Check if solved */
	@Override
	public boolean isSolved(){
		return coord == 12375;
	}

}
