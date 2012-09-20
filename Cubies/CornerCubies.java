package cg.fivestage444.Cubies;

import cg.fivestage444.Util;
import cg.fivestage444.Symmetry;
import java.util.Arrays;

public final class CornerCubies{

	public byte[] cubies = new byte[8]; //what's at each corner position (3*cubie + orientation)

	public void init (){
		for (byte i = 0; i < 8; ++i)
			cubies[i] = i;
	}

	public boolean is_solved(){
		for (byte i = 0; i < 8; ++i)
			if( cubies[i] != i )
				return false;
		return true;
	}

	public void copyTo( CornerCubies c ){
		System.arraycopy(cubies, 0, c.cubies, 0, 8);
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

	public void leftMult (int symIdx){
		for (int i = 0; i < 8; ++i){
			int corner = Symmetry.symCorners[symIdx][(cubies[i] & 0x7)];
			cubies[i] = (byte) ((multD3(corner >>> 3, cubies[i] >>> 3) << 3 ) + ( corner & 0x7 ));
		}
	}

	public void rightMult (int symIdx, CornerCubies c){
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

	public void conjugate (int symIdx, CornerCubies c){
		rightMult( Symmetry.invSymIdx[symIdx], c );
		c.leftMult( symIdx );
	}

	public void move (int move_code, CornerCubies c){
		System.arraycopy(cubies, 0, c.cubies, 0, 8);
		c.move( move_code );
	}

	public void move (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		if(( layer % 3 ) == 1 ) return;
		int face = layer / 3;
		switch (face){
			case 0: // U
				Util.swap(cubies, 0, 5, 1, 4, rot);
				break;
			case 1: // D
				Util.swap(cubies, 6, 2, 7, 3, rot);
				break;
			case 2: // L
				Util.swapCorners(cubies, 5, 0, 6, 3, rot);
				break;
			case 3: // R
				Util.swapCorners(cubies, 4, 1, 7, 2, rot);
				break;
			case 4: // F
				Util.swapCorners(cubies, 0, 4, 2, 6, rot);
				break;
			case 5: // B
				Util.swapCorners(cubies, 1, 5, 3, 7, rot);
				break;
		}
	}

	public void print (){
		System.out.print("Corners: ");
		for (int i=0; i<8; i++)
			System.out.print(cubies[i]+"-");
		System.out.println("");
	}
}

