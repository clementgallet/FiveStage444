package cg.fivestage444.Coordinates;

public abstract class SymCoord {

	public int coord;
	public int sym;

	abstract public int getSize();
	abstract public int[] getSolvedStates();
	abstract public void moveTo( int m, SymCoord rc );
	abstract public long getSyms();

}
