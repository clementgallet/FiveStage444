package cg.fivestage444.Cubies;

import cg.fivestage444.Util;
import cg.fivestage444.Symmetry;
import java.util.Arrays;

public final class EdgeCubies{

	public byte[] cubies = new byte[24]; //what's at each edge position

	public void init (){
		for (byte i = 0; i < 24; ++i)
			cubies[i] = i;
	}

	public boolean is_solved(){
		for (byte i = 0; i < 24; ++i)
			if( cubies[i] != i )
				return false;
		return true;
	}

	public void copyTo( EdgeCubies e ){
		System.arraycopy(cubies, 0, e.cubies, 0, 24);
	}	

	public void leftMult (int symIdx){
		for (int i = 0; i < 24; ++i)
			cubies[i] = Symmetry.symEdges[symIdx][cubies[i]];
	}

	public void rightMult (int symIdx, EdgeCubies e){
		for (int i = 0; i < 24; ++i)
			e.cubies[i] = cubies[Symmetry.symEdges[symIdx][i]];
	}

	public void conjugate (int symIdx, EdgeCubies e){
		rightMult( Symmetry.invSymIdx[symIdx], e );
		e.leftMult( symIdx );
	}

	public int edgeUD_parity (){
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

	public void move (int move_code, EdgeCubies e){
		System.arraycopy(cubies, 0, e.cubies, 0, 24);
		e.move( move_code );
	}

	public void move (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		switch (layer){
			case 0: // U
				Util.swap(cubies, 0, 12, 1, 14, rot);
				Util.swap(cubies, 4, 8, 5, 10, rot);
				break;
			case 1: // u
				Util.swap(cubies, 16, 22, 19, 21, rot);
				break;
			case 2: // Uw
				Util.swap(cubies, 0, 12, 1, 14, rot);
				Util.swap(cubies, 4, 8, 5, 10, rot);
				Util.swap(cubies, 16, 22, 19, 21, rot);
				break;
			case 3: // D
				Util.swap(cubies, 2, 15, 3, 13, rot);
				Util.swap(cubies, 6, 11, 7, 9, rot);
				break;
			case 4: // d
				Util.swap(cubies, 17, 23, 18, 20, rot);
				break;
			case 5: // Dw
				Util.swap(cubies, 2, 15, 3, 13, rot);
				Util.swap(cubies, 6, 11, 7, 9, rot);
				Util.swap(cubies, 17, 23, 18, 20, rot);
				break;
			case 6: // L
				Util.swap(cubies, 8, 20, 9, 22, rot);
				Util.swap(cubies, 12, 16, 13, 18, rot);
				break;
			case 7: // l
				Util.swap(cubies, 0, 6, 3, 5, rot);
				break;
			case 8: // Lw
				Util.swap(cubies, 8, 20, 9, 22, rot);
				Util.swap(cubies, 12, 16, 13, 18, rot);
				Util.swap(cubies, 0, 6, 3, 5, rot);
				break;
			case 9: // R
				Util.swap(cubies, 10, 23, 11, 21, rot);
				Util.swap(cubies, 14, 19, 15, 17, rot);
				break;
			case 10: // r
				Util.swap(cubies, 1, 7, 2, 4, rot);
				break;
			case 11: // Rw
				Util.swap(cubies, 10, 23, 11, 21, rot);
				Util.swap(cubies, 14, 19, 15, 17, rot);
				Util.swap(cubies, 1, 7, 2, 4, rot);
				break;
			case 12: // F
				Util.swap(cubies, 0, 21, 2, 20, rot);
				Util.swap(cubies, 4, 17, 6, 16, rot);
				break;
			case 13: // f
				Util.swap(cubies, 8, 14, 11, 13, rot);
				break;
			case 14: // Fw
				Util.swap(cubies, 0, 21, 2, 20, rot);
				Util.swap(cubies, 4, 17, 6, 16, rot);
				Util.swap(cubies, 8, 14, 11, 13, rot);
				break;
			case 15: // B
				Util.swap(cubies, 1, 22, 3, 23, rot);
				Util.swap(cubies, 5, 18, 7, 19, rot);
				break;
			case 16: // b
				Util.swap(cubies, 9, 15, 10, 12, rot);
				break;
			case 17: // Bw
				Util.swap(cubies, 1, 22, 3, 23, rot);
				Util.swap(cubies, 5, 18, 7, 19, rot);
				Util.swap(cubies, 9, 15, 10, 12, rot);
				break;
		}
	}

	public void print (){
		System.out.print("Edges: ");
		for (int i=0; i<24; i++)
			System.out.print(cubies[i]+"-");
		System.out.println("");
	}
}

