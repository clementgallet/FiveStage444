package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

public final class CenterCubies implements Cloneable{

	public byte[] cubies = new byte[24]; //what's at each center position

	public void init (){
		for (int i = 0; i < 24; ++i)
			cubies[i] = (byte)(i/4);
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		CenterCubies center = (CenterCubies) super.clone();
		center.cubies = new byte[cubies.length];
		System.arraycopy(cubies, 0, center.cubies, 0, cubies.length);
	}

	public boolean is_solved(){
		for (byte i = 0; i < 24; ++i)
			if( cubies[i] != (byte)(i/4) )
				return false;
		return true;
	}

	public void copyTo( CenterCubies c ){
		System.arraycopy(cubies, 0, c.cubies, 0, 24);
	}	

	public void leftMult (int symIdx){
		int[] cenN = new int[6]; // Transform centers into unique facelets.
		for (int i = 0; i < 24; ++i)
			cubies[i] = (byte)(Symmetry.symCenters[symIdx][cubies[i]*4+cenN[cubies[i]]++] / 4);
	}

	public void rightMult (int symIdx, CenterCubies c){
		for (int i = 0; i < 24; ++i)
			c.cubies[i] = cubies[Symmetry.symCenters[symIdx][i]];
	}

	public void conjugate (int symIdx, CenterCubies c){
		rightMult( Symmetry.invSymIdx[symIdx], c );
		c.leftMult( symIdx );
	}

	public void move (int move_code, CenterCubies c){
		System.arraycopy(cubies, 0, c.cubies, 0, 24);
		c.move( move_code );
	}

	public void move (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		switch (layer){
			case 0: // U
				Util.swap(cubies, 0, 3, 1, 2, rot);
				break;
			case 1: // u
				Util.swap(cubies, 16, 10, 21, 14, rot);
				Util.swap(cubies, 19, 8, 22, 12, rot);
				break;
			case 2: // Uw
				Util.swap(cubies, 0, 3, 1, 2, rot);
				Util.swap(cubies, 16, 10, 21, 14, rot);
				Util.swap(cubies, 19, 8, 22, 12, rot);
				break;
			case 3: // D
				Util.swap(cubies, 4, 7, 5, 6, rot);
				break;
			case 4: // d
				Util.swap(cubies, 18, 13, 23, 9, rot);
				Util.swap(cubies, 17, 15, 20, 11, rot);
				break;
			case 5: // Dw
				Util.swap(cubies, 4, 7, 5, 6, rot);
				Util.swap(cubies, 18, 13, 23, 9, rot);
				Util.swap(cubies, 17, 15, 20, 11, rot);
				break;
			case 6: // L
				Util.swap(cubies, 8, 11, 9, 10, rot);
				break;
			case 7: // l
				Util.swap(cubies, 16, 6, 20, 3, rot);
				Util.swap(cubies, 18, 5, 22, 0, rot);
				break;
			case 8: // Lw
				Util.swap(cubies, 8, 11, 9, 10, rot);
				Util.swap(cubies, 16, 6, 20, 3, rot);
				Util.swap(cubies, 18, 5, 22, 0, rot);
				break;
			case 9: // R
				Util.swap(cubies, 12, 15, 13, 14, rot);
				break;
			case 10: // r
				Util.swap(cubies, 19, 1, 23, 4, rot);
				Util.swap(cubies, 17, 2, 21, 7, rot);
				break;
			case 11: // Rw
				Util.swap(cubies, 12, 15, 13, 14, rot);
				Util.swap(cubies, 19, 1, 23, 4, rot);
				Util.swap(cubies, 17, 2, 21, 7, rot);
				break;
			case 12: // F
				Util.swap(cubies, 16, 19, 17, 18, rot);
				break;
			case 13: // f
				Util.swap(cubies, 0, 14, 4, 11, rot);
				Util.swap(cubies, 2, 13, 6, 8, rot);
				break;
			case 14: // Fw
				Util.swap(cubies, 16, 19, 17, 18, rot);
				Util.swap(cubies, 0, 14, 4, 11, rot);
				Util.swap(cubies, 2, 13, 6, 8, rot);
				break;
			case 15: // B
				Util.swap(cubies, 20, 23, 21, 22, rot);
				break;
			case 16: // b
				Util.swap(cubies, 1, 10, 5, 15, rot);
				Util.swap(cubies, 3, 9, 7, 12, rot);
				break;
			case 17: // Bw
				Util.swap(cubies, 20, 23, 21, 22, rot);
				Util.swap(cubies, 1, 10, 5, 15, rot);
				Util.swap(cubies, 3, 9, 7, 12, rot);
				break;
		}
	}

// --Commented out by Inspection START (28/09/12 19:19):
//	public void print (){
//		System.out.print("Centers: ");
//		for (int i=0; i<24; i++)
//			System.out.print(cubies[i]+"-");
//		System.out.println("");
//	}
// --Commented out by Inspection STOP (28/09/12 19:19)
}

