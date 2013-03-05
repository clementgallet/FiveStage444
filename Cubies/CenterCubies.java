package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

import java.util.Arrays;

public final class CenterCubies extends Cubies implements Cloneable{

	public CenterCubies(){
		cubies = new byte[24];
	}

	@Override
	public void init (){
		for (int i = 0; i < 24; ++i)
			cubies[i] = (byte)(i/4);
	}

	@Override
	public void leftMult (int symIdx){
		int[] cenN = new int[6]; // Transform centers into unique facelets.
		for (int i = 0; i < 24; ++i)
			cubies[i] = (byte)(Symmetry.symCenters[symIdx][cubies[i]*4+cenN[cubies[i]]++] / 4);
	}

	@Override
	public void rightMult (int symIdx, Cubies c){
		for (int i = 0; i < 24; ++i)
			c.cubies[i] = cubies[Symmetry.symCenters[symIdx][i]];
	}

	@Override
	public void conjugate (int symIdx, Cubies c){
		rightMult( Symmetry.invSymIdx[symIdx], c );
		c.leftMult( symIdx );
	}

	@Override
	public void move (int move_code, Cubies c){
		System.arraycopy(cubies, 0, c.cubies, 0, 24);
		c.move( move_code );
	}

	@Override
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

	@Override
	public String toString (){
		return "Centers: " + Arrays.toString(cubies);
	}
}

