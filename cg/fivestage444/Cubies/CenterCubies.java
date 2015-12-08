package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

import java.util.Arrays;

public final class CenterCubies extends Cubies {

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
	public void move (int move_code){
		/* move_code%3 determine the rotation angle to apply.
		 * However, in our code, 0 is +quarter turn, 1 is -quarter turn and 2 is half turn.
		 * We have to map this to the number of quarter turns to apply.
		 * In practice, we have to map 0->1, 1->3 and 2->2.
		 * That is the purpose of the following obscure operation.
		 */
		int rot = (((-(move_code % 3))+3)%3)+1;

		int layer = move_code / 3;
		switch (layer){
			case 0: // U
				Util.cycle(cubies, 0, 3, 1, 2, rot);
				break;
			case 1: // u
				Util.cycle(cubies, 16, 10, 21, 14, rot);
				Util.cycle(cubies, 19, 8, 22, 12, rot);
				break;
			case 2: // Uw
				Util.cycle(cubies, 0, 3, 1, 2, rot);
				Util.cycle(cubies, 16, 10, 21, 14, rot);
				Util.cycle(cubies, 19, 8, 22, 12, rot);
				break;
			case 3: // D
				Util.cycle(cubies, 4, 7, 5, 6, rot);
				break;
			case 4: // d
				Util.cycle(cubies, 18, 13, 23, 9, rot);
				Util.cycle(cubies, 17, 15, 20, 11, rot);
				break;
			case 5: // Dw
				Util.cycle(cubies, 4, 7, 5, 6, rot);
				Util.cycle(cubies, 18, 13, 23, 9, rot);
				Util.cycle(cubies, 17, 15, 20, 11, rot);
				break;
			case 6: // L
				Util.cycle(cubies, 8, 11, 9, 10, rot);
				break;
			case 7: // l
				Util.cycle(cubies, 16, 6, 20, 3, rot);
				Util.cycle(cubies, 18, 5, 22, 0, rot);
				break;
			case 8: // Lw
				Util.cycle(cubies, 8, 11, 9, 10, rot);
				Util.cycle(cubies, 16, 6, 20, 3, rot);
				Util.cycle(cubies, 18, 5, 22, 0, rot);
				break;
			case 9: // R
				Util.cycle(cubies, 12, 15, 13, 14, rot);
				break;
			case 10: // r
				Util.cycle(cubies, 19, 1, 23, 4, rot);
				Util.cycle(cubies, 17, 2, 21, 7, rot);
				break;
			case 11: // Rw
				Util.cycle(cubies, 12, 15, 13, 14, rot);
				Util.cycle(cubies, 19, 1, 23, 4, rot);
				Util.cycle(cubies, 17, 2, 21, 7, rot);
				break;
			case 12: // F
				Util.cycle(cubies, 16, 19, 17, 18, rot);
				break;
			case 13: // f
				Util.cycle(cubies, 0, 14, 4, 11, rot);
				Util.cycle(cubies, 2, 13, 6, 8, rot);
				break;
			case 14: // Fw
				Util.cycle(cubies, 16, 19, 17, 18, rot);
				Util.cycle(cubies, 0, 14, 4, 11, rot);
				Util.cycle(cubies, 2, 13, 6, 8, rot);
				break;
			case 15: // B
				Util.cycle(cubies, 20, 23, 21, 22, rot);
				break;
			case 16: // b
				Util.cycle(cubies, 1, 10, 5, 15, rot);
				Util.cycle(cubies, 3, 9, 7, 12, rot);
				break;
			case 17: // Bw
				Util.cycle(cubies, 20, 23, 21, 22, rot);
				Util.cycle(cubies, 1, 10, 5, 15, rot);
				Util.cycle(cubies, 3, 9, 7, 12, rot);
				break;
		}
	}

	@Override
	public String toString (){
		return "Centers: " + Arrays.toString(cubies);
	}
}

