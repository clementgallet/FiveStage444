package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

import java.util.Arrays;

public final class EdgeCubies extends Cubies implements Cloneable{

	public EdgeCubies(){
		cubies = new byte[24];
	}

	@Override
	public void init (){
		for (byte i = 0; i < 24; ++i)
			cubies[i] = i;
	}

	@Override
	public void leftMult (int symIdx){
		for (int i = 0; i < 24; ++i)
			cubies[i] = Symmetry.symEdges[symIdx][cubies[i]];
	}

	@Override
	public void rightMult (int symIdx, Cubies e){
		for (int i = 0; i < 24; ++i)
			e.cubies[i] = cubies[Symmetry.symEdges[symIdx][i]];
	}

	@Override
	public void conjugate (int symIdx, Cubies e){
		rightMult( Symmetry.invSymIdx[symIdx], e );
		e.leftMult( symIdx );
	}

	@Override
	public void move (int move_code, Cubies e){
		System.arraycopy(cubies, 0, e.cubies, 0, 24);
		e.move( move_code );
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
				Util.cycle(cubies, 0, 12, 1, 14, rot);
				Util.cycle(cubies, 4, 8, 5, 10, rot);
				break;
			case 1: // u
				Util.cycle(cubies, 16, 22, 19, 21, rot);
				break;
			case 2: // Uw
				Util.cycle(cubies, 0, 12, 1, 14, rot);
				Util.cycle(cubies, 4, 8, 5, 10, rot);
				Util.cycle(cubies, 16, 22, 19, 21, rot);
				break;
			case 3: // D
				Util.cycle(cubies, 2, 15, 3, 13, rot);
				Util.cycle(cubies, 6, 11, 7, 9, rot);
				break;
			case 4: // d
				Util.cycle(cubies, 17, 23, 18, 20, rot);
				break;
			case 5: // Dw
				Util.cycle(cubies, 2, 15, 3, 13, rot);
				Util.cycle(cubies, 6, 11, 7, 9, rot);
				Util.cycle(cubies, 17, 23, 18, 20, rot);
				break;
			case 6: // L
				Util.cycle(cubies, 8, 20, 9, 22, rot);
				Util.cycle(cubies, 12, 16, 13, 18, rot);
				break;
			case 7: // l
				Util.cycle(cubies, 0, 6, 3, 5, rot);
				break;
			case 8: // Lw
				Util.cycle(cubies, 8, 20, 9, 22, rot);
				Util.cycle(cubies, 12, 16, 13, 18, rot);
				Util.cycle(cubies, 0, 6, 3, 5, rot);
				break;
			case 9: // R
				Util.cycle(cubies, 10, 23, 11, 21, rot);
				Util.cycle(cubies, 14, 19, 15, 17, rot);
				break;
			case 10: // r
				Util.cycle(cubies, 1, 7, 2, 4, rot);
				break;
			case 11: // Rw
				Util.cycle(cubies, 10, 23, 11, 21, rot);
				Util.cycle(cubies, 14, 19, 15, 17, rot);
				Util.cycle(cubies, 1, 7, 2, 4, rot);
				break;
			case 12: // F
				Util.cycle(cubies, 0, 21, 2, 20, rot);
				Util.cycle(cubies, 4, 17, 6, 16, rot);
				break;
			case 13: // f
				Util.cycle(cubies, 8, 14, 11, 13, rot);
				break;
			case 14: // Fw
				Util.cycle(cubies, 0, 21, 2, 20, rot);
				Util.cycle(cubies, 4, 17, 6, 16, rot);
				Util.cycle(cubies, 8, 14, 11, 13, rot);
				break;
			case 15: // B
				Util.cycle(cubies, 1, 22, 3, 23, rot);
				Util.cycle(cubies, 5, 18, 7, 19, rot);
				break;
			case 16: // b
				Util.cycle(cubies, 9, 15, 10, 12, rot);
				break;
			case 17: // Bw
				Util.cycle(cubies, 1, 22, 3, 23, rot);
				Util.cycle(cubies, 5, 18, 7, 19, rot);
				Util.cycle(cubies, 9, 15, 10, 12, rot);
				break;
		}
	}

	public int parityUD (){
		int i, j;
		int parity = 0;
		byte[] t = new byte[16];

		for (i = 0; i < 16; ++i) {
			t[i] = cubies[i];
		}
		for (i = 0; i < 15; ++i) {
			if (t[i] == i) {
				continue;
			}
			for (j = i + 1; j < 16; ++j) {
				if (t[j] == i) {
					//"swap" the i & j elements, but don't bother updating the "i"-element
					//as it isn't needed anymore.
					t[j] = t[i];
				}
			}
			parity ^= 1;
		}
		return parity;
	}

	@Override
	public String toString (){
		return "Edges: " + Arrays.toString(cubies);
	}
}

