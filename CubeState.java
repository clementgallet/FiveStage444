package cg.fivestage444;

// import static cg.fivestage444.Constants.*;
import java.util.Arrays;

//CubeState structure: a cubie-level representation of the cube.
public final class CubeState{

	public byte[] m_edge = new byte[24]; //what's at each edge position
	public byte[] m_cor = new byte[8]; //what's at each corner position (3*cubie + orientation)
	public byte[] m_cen = new byte[24]; //what's at each center position

	public void init (){
		byte i;
		for (i = 0; i < 24; ++i) {
			m_edge[i] = i;
		}
		for (i = 0; i < 8; ++i) {
			m_cor[i] = i;
		}
		for (i = 0; i < 24; ++i) {
			m_cen[i] = (byte)(i/4);
		}
	}

	public int is_solved (){
		int sym;
		byte i;

		CubeState cube = new CubeState();
		for (sym=0; sym<48; sym++){
			copyTo( cube );
			cube.leftMultEdges  ( sym );
			cube.leftMultCenters( sym );
			cube.leftMultCorners( sym );

			boolean solved = true;
			for (i = 0; i < 24; ++i)
				if( cube.m_edge[i] != i ){
					solved = false;
					break;
				}
			if( ! solved ) continue;

			for (i = 0; i < 8; ++i)
				if( cube.m_cor[i] != i ){
					solved = false;
					break;
				}
			if( ! solved ) continue;

			for (i = 0; i < 24; ++i)
				if( cube.m_cen[i] != (byte)(i/4) ){
					solved = false;
					break;
			}
			if( solved ) return sym;
		}

		return -1;
	}

	public final void do_move (int move_code){
		rotate_sliceEDGE (move_code);
		rotate_sliceCORNER (move_code);
		rotate_sliceCENTER (move_code);
	}

	public void scramble (int move_count, byte[] move_arr){
		int i;
		for (i = 0; i < move_count; ++i) {
			do_move (move_arr[i]);
		}
	}

	public void scramble (int move_count, byte[] move_arr, byte[] move_trans){
		int i;
		for (i = 0; i < move_count; ++i) {
			do_move (move_trans[move_arr[i]]);
		}
	}

	public void copyTo (CubeState cube){
		System.arraycopy(m_edge, 0, cube.m_edge, 0, 24);
		System.arraycopy(m_cor, 0, cube.m_cor, 0, 8);
		System.arraycopy(m_cen, 0, cube.m_cen, 0, 24);
	}

	public void leftMultEdges (int symIdx){
		int i;
		byte[] sym = Symmetry.symEdges[symIdx];
		for (i = 0; i < 24; ++i){
			m_edge[i] = sym[m_edge[i]];
		}
	}

	public void rightMultEdges (int symIdx, CubeState c){
		int i;
		byte[] sym = Symmetry.symEdges[symIdx];

		for (i = 0; i < 24; ++i){
			c.m_edge[i] = m_edge[sym[i]];
		}
	}

	public void conjugateEdges (int symIdx, CubeState c){
		rightMultEdges( Symmetry.invSymIdx[symIdx], c );
		c.leftMultEdges( symIdx );
	}

	public void leftMultCenters (int symIdx){
		int i;

		// Transform centers into unique facelets.
		int[] cenN = new int[6];
		for (i = 0; i < 6; ++i) cenN[i] = 0;

		for (i = 0; i < 24; ++i){
			m_cen[i] = (byte)(m_cen[i] * 4 + cenN[m_cen[i]]++);
		}

		// Multiply.
		byte[] sym = Symmetry.symCenters[symIdx];
		for (i = 0; i < 24; ++i){
			m_cen[i] = sym[m_cen[i]];
		}

		// Transform centers back.
		for (i = 0; i < 24; ++i){
			m_cen[i] /= 4;
		}
	}

	public void rightMultCenters (int symIdx, CubeState c){
		int i;
		byte[] sym = Symmetry.symCenters[symIdx];

		// Conjugate edges and centers.
		for (i = 0; i < 24; ++i){
			c.m_cen[i] = m_cen[sym[i]];
		}
	}

	public void conjugateCenters (int symIdx, CubeState c){
		rightMultCenters( Symmetry.invSymIdx[symIdx], c );
		c.leftMultCenters( symIdx );
	}

	private byte multD3( byte oriA, byte oriB){
		byte ori;
		if (oriA<3 && oriB<3){ //if both cubes are regular cubes... 
			ori = (byte)(oriA + oriB); //just do an addition modulo 3 here  
			if (ori>=3) ori-=3; //the composition is a regular cube
			return ori;
		}
		if (oriA<3 && oriB>=3){ //if cube b is in a mirrored state...
			ori = (byte)(oriA + oriB);
			if (ori>=6) ori-=3; //the composition is a mirrored cube
			return ori;
		}
		if (oriA>=3 && oriB<3){ //if cube a is an a mirrored state...
			ori = (byte)(oriA - oriB);
			if (ori<3) ori+=3; //the composition is a mirrored cube	
			return ori;
		}
		if (oriA>=3 && oriB>=3){ //if both cubes are in mirrored states...
			ori = (byte)(oriA - oriB);
			if (ori<0) ori+=3; //the composition is a regular cube
			return ori;
		}
		return -1;
	}

	public void leftMultCorners (int symIdx){
		int i;
		byte orientA, orientB;
		byte symO[] = Symmetry.symCornersOrient[symIdx];
		byte symP[] = Symmetry.symCornersPerm[symIdx];

		for (i = 0; i < 8; ++i){
			orientA = symO[(m_cor[i] & 0x7)];
			orientB = (byte)(m_cor[i] >> 3);
			m_cor[i] = (byte) ((multD3(orientA, orientB) << 3 ) + symP[(m_cor[i] & 0x7)]);
		}
	}

	public void rightMultCorners (int symIdx, CubeState c){
		int i;
		byte orientA, orientB;
		byte symO[] = Symmetry.symCornersOrient[symIdx];
		byte symP[] = Symmetry.symCornersPerm[symIdx];

		for (i = 0; i < 8; ++i){
			orientA = (byte) (m_cor[symP[i]] >> 3);
			orientB = symO[i];
			c.m_cor[i] = (byte) (( multD3(orientA, orientB) << 3 ) + (m_cor[symP[i]] & 0x7));
		}
	}

	public void deMirrorCorners (){
		int i, co;
		for (i = 0; i < 8; ++i){
			co = (m_cor[i] >> 3);
			if( co >= 3 ){
				co = ( 6 - co ) % 3;
				m_cor[i] = (byte)(( co << 3 ) + (m_cor[i] & 0x7));
			}
		}
	}

	public void conjugateCorners (int symIdx, CubeState c){
		rightMultCorners( Symmetry.invSymIdx[symIdx], c );
		c.leftMultCorners( symIdx );
	}

	public void leftMult (int symIdx){
		leftMultEdges (symIdx);
		leftMultCenters (symIdx);
		leftMultCorners (symIdx);
	}

	public void rightMult (int symIdx){
		CubeState c = new CubeState();
		copyTo(c);
		c.rightMultEdges (symIdx, this);
		c.rightMultCenters (symIdx, this);
		c.rightMultCorners (symIdx, this);
	}

	public int edgeUD_parity (){
		int i, j;
		int parity = 0;
		byte[] t = new byte[16];

		for (i = 0; i < 16; ++i) {
			t[i] = m_edge[i];
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

	public void rotate_sliceCORNER (int move_code, CubeState cube){
		System.arraycopy(m_cor, 0, cube.m_cor, 0, 8);
		cube.rotate_sliceCORNER( move_code );
	}

	public void rotate_sliceCORNER (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		if(( layer % 3 ) == 1 ) return;
		int face = layer / 3;
		switch (face){
			case 0: // U
				Util.swap(m_cor, 0, 5, 1, 4, rot);
				break;
			case 1: // D
				Util.swap(m_cor, 6, 2, 7, 3, rot);
				break;
			case 2: // L
				Util.swapCorners(m_cor, 5, 0, 6, 3, rot);
				break;
			case 3: // R
				Util.swapCorners(m_cor, 4, 1, 7, 2, rot);
				break;
			case 4: // F
				Util.swapCorners(m_cor, 0, 4, 2, 6, rot);
				break;
			case 5: // B
				Util.swapCorners(m_cor, 1, 5, 3, 7, rot);
				break;
		}
	}

	public void rotate_sliceEDGE (int move_code, CubeState cube){
		System.arraycopy(m_edge, 0, cube.m_edge, 0, 24);
		cube.rotate_sliceEDGE( move_code );
	}

	public void rotate_sliceEDGE (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		switch (layer){
			case 0: // U
				Util.swap(m_edge, 0, 12, 1, 14, rot);
				Util.swap(m_edge, 4, 8, 5, 10, rot);
				break;
			case 1: // u
				Util.swap(m_edge, 16, 22, 19, 21, rot);
				break;
			case 2: // Uw
				Util.swap(m_edge, 0, 12, 1, 14, rot);
				Util.swap(m_edge, 4, 8, 5, 10, rot);
				Util.swap(m_edge, 16, 22, 19, 21, rot);
				break;
			case 3: // D
				Util.swap(m_edge, 2, 15, 3, 13, rot);
				Util.swap(m_edge, 6, 11, 7, 9, rot);
				break;
			case 4: // d
				Util.swap(m_edge, 17, 23, 18, 20, rot);
				break;
			case 5: // Dw
				Util.swap(m_edge, 2, 15, 3, 13, rot);
				Util.swap(m_edge, 6, 11, 7, 9, rot);
				Util.swap(m_edge, 17, 23, 18, 20, rot);
				break;
			case 6: // L
				Util.swap(m_edge, 8, 20, 9, 22, rot);
				Util.swap(m_edge, 12, 16, 13, 18, rot);
				break;
			case 7: // l
				Util.swap(m_edge, 0, 6, 3, 5, rot);
				break;
			case 8: // Lw
				Util.swap(m_edge, 8, 20, 9, 22, rot);
				Util.swap(m_edge, 12, 16, 13, 18, rot);
				Util.swap(m_edge, 0, 6, 3, 5, rot);
				break;
			case 9: // R
				Util.swap(m_edge, 10, 23, 11, 21, rot);
				Util.swap(m_edge, 14, 19, 15, 17, rot);
				break;
			case 10: // r
				Util.swap(m_edge, 1, 7, 2, 4, rot);
				break;
			case 11: // Rw
				Util.swap(m_edge, 10, 23, 11, 21, rot);
				Util.swap(m_edge, 14, 19, 15, 17, rot);
				Util.swap(m_edge, 1, 7, 2, 4, rot);
				break;
			case 12: // F
				Util.swap(m_edge, 0, 21, 2, 20, rot);
				Util.swap(m_edge, 4, 17, 6, 16, rot);
				break;
			case 13: // f
				Util.swap(m_edge, 8, 14, 11, 13, rot);
				break;
			case 14: // Fw
				Util.swap(m_edge, 0, 21, 2, 20, rot);
				Util.swap(m_edge, 4, 17, 6, 16, rot);
				Util.swap(m_edge, 8, 14, 11, 13, rot);
				break;
			case 15: // B
				Util.swap(m_edge, 1, 22, 3, 23, rot);
				Util.swap(m_edge, 5, 18, 7, 19, rot);
				break;
			case 16: // b
				Util.swap(m_edge, 9, 15, 10, 12, rot);
				break;
			case 17: // Bw
				Util.swap(m_edge, 1, 22, 3, 23, rot);
				Util.swap(m_edge, 5, 18, 7, 19, rot);
				Util.swap(m_edge, 9, 15, 10, 12, rot);
				break;
		}
	}

	public void rotate_sliceCENTER (int move_code, CubeState cube){
		System.arraycopy(m_cen, 0, cube.m_cen, 0, 24);
		cube.rotate_sliceCENTER( move_code );
	}

	public void rotate_sliceCENTER (int move_code){
		int rot = move_code % 3;
		int layer = move_code / 3;
		switch (layer){
			case 0: // U
				Util.swap(m_cen, 0, 3, 1, 2, rot);
				break;
			case 1: // u
				Util.swap(m_cen, 16, 10, 21, 14, rot);
				Util.swap(m_cen, 19, 8, 22, 12, rot);
				break;
			case 2: // Uw
				Util.swap(m_cen, 0, 3, 1, 2, rot);
				Util.swap(m_cen, 16, 10, 21, 14, rot);
				Util.swap(m_cen, 19, 8, 22, 12, rot);
				break;
			case 3: // D
				Util.swap(m_cen, 4, 7, 5, 6, rot);
				break;
			case 4: // d
				Util.swap(m_cen, 18, 13, 23, 9, rot);
				Util.swap(m_cen, 17, 15, 20, 11, rot);
				break;
			case 5: // Dw
				Util.swap(m_cen, 4, 7, 5, 6, rot);
				Util.swap(m_cen, 18, 13, 23, 9, rot);
				Util.swap(m_cen, 17, 15, 20, 11, rot);
				break;
			case 6: // L
				Util.swap(m_cen, 8, 11, 9, 10, rot);
				break;
			case 7: // l
				Util.swap(m_cen, 16, 6, 20, 3, rot);
				Util.swap(m_cen, 18, 5, 22, 0, rot);
				break;
			case 8: // Lw
				Util.swap(m_cen, 8, 11, 9, 10, rot);
				Util.swap(m_cen, 16, 6, 20, 3, rot);
				Util.swap(m_cen, 18, 5, 22, 0, rot);
				break;
			case 9: // R
				Util.swap(m_cen, 12, 15, 13, 14, rot);
				break;
			case 10: // r
				Util.swap(m_cen, 19, 1, 23, 4, rot);
				Util.swap(m_cen, 17, 2, 21, 7, rot);
				break;
			case 11: // Rw
				Util.swap(m_cen, 12, 15, 13, 14, rot);
				Util.swap(m_cen, 19, 1, 23, 4, rot);
				Util.swap(m_cen, 17, 2, 21, 7, rot);
				break;
			case 12: // F
				Util.swap(m_cen, 16, 19, 17, 18, rot);
				break;
			case 13: // f
				Util.swap(m_cen, 0, 14, 4, 11, rot);
				Util.swap(m_cen, 2, 13, 6, 8, rot);
				break;
			case 14: // Fw
				Util.swap(m_cen, 16, 19, 17, 18, rot);
				Util.swap(m_cen, 0, 14, 4, 11, rot);
				Util.swap(m_cen, 2, 13, 6, 8, rot);
				break;
			case 15: // B
				Util.swap(m_cen, 20, 23, 21, 22, rot);
				break;
			case 16: // b
				Util.swap(m_cen, 1, 10, 5, 15, rot);
				Util.swap(m_cen, 3, 9, 7, 12, rot);
				break;
			case 17: // Bw
				Util.swap(m_cen, 20, 23, 21, 22, rot);
				Util.swap(m_cen, 1, 10, 5, 15, rot);
				Util.swap(m_cen, 3, 9, 7, 12, rot);
				break;
		}
	}

	public void print (){
		System.out.print("Edges: ");
		for (int i=0; i<24; i++)
			System.out.print(m_edge[i]+"-");
		System.out.println("");
		System.out.print("Corners: ");
		for (int i=0; i<8; i++)
			System.out.print(m_cor[i]+"-");
		System.out.println("");
		System.out.print("Centers: ");
		for (int i=0; i<24; i++)
			System.out.print(m_cen[i]+"-");
		System.out.println("");
	}
}

