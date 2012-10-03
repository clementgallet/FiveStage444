package cg.fivestage444.Coordinates;

public abstract class RawCoord {

	public int coord;

	abstract public int getSize();
	abstract public int[] getSolvedStates();
	abstract public void moveTo( int m, RawCoord rc );
	abstract public int conjugate( int sym );

}
