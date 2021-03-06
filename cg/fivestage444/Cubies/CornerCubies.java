package cg.fivestage444.Cubies;

import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

import java.util.Arrays;

public final class CornerCubies extends Cubies {

	public CornerCubies(){
		cubies = new byte[8];
	}

	public void init (){
		for (byte i = 0; i < 8; ++i)
			cubies[i] = i;
	}

	private int multD3( int oriA, int oriB){
		int ori;
		if (oriA<3 && oriB<3){ //if both cubes are regular cubes... 
			ori = oriA + oriB; //just do an addition modulo 3 here  
			if (ori>=3) ori-=3; //the composition is a regular cube
			return ori;
		}
		if (oriA<3 && oriB>=3){ //if cube b is in a mirrored state...
			ori = oriA + oriB;
			if (ori>=6) ori-=3; //the composition is a mirrored cube
			return ori;
		}
		if (oriA>=3 && oriB<3){ //if cube a is an a mirrored state...
			ori = oriA - oriB;
			if (ori<3) ori+=3; //the composition is a mirrored cube	
			return ori;
		}
		if (oriA>=3 && oriB>=3){ //if both cubes are in mirrored states...
			ori = oriA - oriB;
			if (ori<0) ori+=3; //the composition is a regular cube
			return ori;
		}
		return -1;
	}

	@Override
	public void leftMult (int symIdx){
		for (int i = 0; i < 8; ++i){
			int corner = Symmetry.symCorners[symIdx][(cubies[i] & 0x7)];
			cubies[i] = (byte) ((multD3(corner >>> 3, cubies[i] >>> 3) << 3 ) + ( corner & 0x7 ));
		}
	}

	@Override
	public void rightMult (int symIdx, Cubies c){
		for (int i = 0; i < 8; ++i){
			int corner = Symmetry.symCorners[symIdx][i];
			c.cubies[i] = (byte) (( multD3(cubies[corner & 0x7] >>> 3, corner >>> 3) << 3 ) + (cubies[corner & 0x7] & 0x7));
		}
	}

	public void deMirror (){
		for (int i = 0; i < 8; ++i){
			int co = (cubies[i] >> 3);
			if( co >= 3 ){
				co = ( 6 - co ) % 3;
				cubies[i] = (byte)(( co << 3 ) + (cubies[i] & 0x7));
			}
		}
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
		if(( layer % 3 ) == 1 ) return; /* We are rotating an inner layer, which is unaffected by the corners. */
		int face = layer / 3;
		switch (face){
			case 0: // U
				Util.cycle(cubies, 0, 5, 1, 4, rot);
				break;
			case 1: // D
				Util.cycle(cubies, 6, 2, 7, 3, rot);
				break;
			case 2: // L
				Util.cycleAndOrient(cubies, 5, 0, 6, 3, rot);
				break;
			case 3: // R
				Util.cycleAndOrient(cubies, 4, 1, 7, 2, rot);
				break;
			case 4: // F
				Util.cycleAndOrient(cubies, 0, 4, 2, 6, rot);
				break;
			case 5: // B
				Util.cycleAndOrient(cubies, 1, 5, 3, 7, rot);
				break;
		}
	}

	@Override
	public String toString (){
		return "Corners: " + Arrays.toString(cubies);
	}
}

