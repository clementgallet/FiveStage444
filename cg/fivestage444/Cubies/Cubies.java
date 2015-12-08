package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;

abstract public class Cubies implements Cloneable {

	public byte[] cubies;

	@Override
	public Object clone() throws CloneNotSupportedException{
		Cubies c = (Cubies) super.clone();
		c.cubies = new byte[cubies.length];
		System.arraycopy(cubies, 0, c.cubies, 0, cubies.length);
		return c;
	}

	abstract public void init ();

	public boolean is_solved(){
		Cubies solved = null;
		try {
			solved = this.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		solved.init();
		for (int i=0; i<cubies.length; i++)
			if (cubies[i] != solved.cubies[i])
				return false;
		return true;
	}

	public void copyTo( Cubies c ){
		System.arraycopy(cubies, 0, c.cubies, 0, cubies.length);
	}

	abstract public void leftMult (int symIdx);

	abstract public void rightMult (int symIdx, Cubies e);

	public void conjugate (int symIdx, Cubies c){
		rightMult( Symmetry.invSymIdx[symIdx], c );
		c.leftMult( symIdx );
	}

	public void move (int move_code, Cubies c){
		System.arraycopy(cubies, 0, c.cubies, 0, cubies.length);
		c.move( move_code );
	}

	abstract public void move (int move_code);

}
