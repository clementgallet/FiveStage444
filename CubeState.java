package cg.fivestage444;

import static cg.fivestage444.Constants.*;
import java.util.Arrays;

//CubeState structure: a cubie-level representation of the cube.
public final class CubeState{

	public byte[] m_edge = new byte[24]; //what's at each edge position
	public byte[] m_cor = new byte[8]; //what's at each corner position (3*cubie + orientation)
	public byte[] m_cen = new byte[24]; //what's at each center position

	private static int rotateCOR_ft[] = {
		 0,  3,  2,  1,  0,  3,	//U face
		 4,  5,  6,  7,  4,  5,	//D face
	     3,  0,  4,  7,  3,  0,	//L face
	     1,  2,  6,  5,  1,  2,	//R face
	     0,  1,  5,  4,  0,  1,	//F face
	     2,  3,  7,  6,  2,  3	//B face
	};
	private static int rotateCOR_ori[] = { 1, 2, 1, 2 };
	private static int rotateCOR_fidx[] = {  0,  2,  0,  6,  8,  6, 12, 14, 12, 18, 20, 18, 24, 26, 24, 30, 32, 30 };
	private static int rotateCOR_tidx[] = {  1,  1,  2,  7,  7,  8, 13, 13, 14, 19, 19, 20, 25, 25, 26, 31, 31, 32 };

	private static int rotateEDGE_fidx[] = {
		 0,  1,  0,  6,  7,  6, 12, 13, 12, 18, 19, 18, 24, 25, 24, 30, 31, 30,
		36, 37, 36, 42, 43, 42, 48, 49, 48, 54, 55, 54, 60, 61, 60, 66, 67, 66,
		72, 73, 72, 78, 79, 78, 84, 85, 84, 90, 91, 90, 96, 97, 96,102,103,102
	};
	private static int rotateEDGE_tidx[] = {
		 1,  0,  2,  7,  6,  8, 13, 12, 14, 19, 18, 20, 25, 24, 26, 31, 30, 32,
   		37, 36, 38, 43, 42, 44, 49, 48, 50, 55, 54, 56, 61, 60, 62, 67, 66, 68,
		73, 72, 74, 79, 78, 80, 85, 84, 86, 91, 90, 92, 97, 96, 98,103,102,104
	};
	private static int rotateEDGE_ft[] = {
		 0, 12,  1, 14,  0, 12, //up face, set 1
	     4,  8,  5, 10,  4,  8, //up face, set 2
		16, 22, 19, 21, 16, 22, //up slice

		 2, 15,  3, 13,  2, 15, //down face, set 1
	     6, 11,  7,  9,  6, 11, //down face, set 2
	    17, 23, 18, 20, 17, 23, //down slice

		 8, 20,  9, 22,  8, 20, //left face, set 1
		12, 16, 13, 18, 12, 16, //left face, set 2
		 0,  6,  3,  5,  0,  6, //left slice

		10, 23, 11, 21, 10, 23, //right face, set 1
		14, 19, 15, 17, 14, 19, //right face, set 2
		 1,  7,  2,  4,  1,  7, //right slice

		 0, 21,  2, 20,  0, 21, //front face, set 1
		 4, 17,  6, 16,  4, 17, //front face, set 2
		 8, 14, 11, 13,  8, 14, //front slice

		 1, 22,  3, 23,  1, 22, //back face, set 1
		 5, 18,  7, 19,  5, 18, //back face, set 2
		 9, 15, 10, 12,  9, 15  //back slice
	};

	private static int rotateCEN_ft[] = {
		 0,  3,  1,  2,  0,  3, //up face
	    16, 10, 21, 14, 16, 10, //up slice, set1
		19,  8, 22, 12, 19,  8, //up slice, set2

		 4,  7,  5,  6,  4,  7, //down face
	    18, 13, 23,  9, 18, 13, //down slice, set 1
	    17, 15, 20, 11, 17, 15, //down slice, set 2

		 8, 11,  9, 10,  8, 11, //left face
		16,  6, 20,  3, 16,  6, //left slice, set 1
		18,  5, 22,  0, 18,  5, //left slice, set 2

		12, 15, 13, 14, 12, 15, //right face
		19,  1, 23,  4, 19,  1, //right slice, set 1
		17,  2, 21,  7, 17,  2, //right slice, set 2

		16, 19, 17, 18, 16, 19, //front face
		 0, 14,  4, 11,  0, 14, //front slice, set 1
		 2, 13,  6,  8,  2, 13, //front slice, set 2

		20, 23, 21, 22, 20, 23, //back face
		 1, 10,  5, 15,  1, 10, //back slice, set 1
		 3,  9,  7, 12,  3,  9  //back slice, set 2
	};


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

	public void rotate_sliceCORNER (int move_code){
		CubeState cube = new CubeState();
		System.arraycopy(m_cor, 0, cube.m_cor, 0, 8);
		cube.rotate_sliceCORNER( move_code, this );
	}

	public void rotate_sliceCORNER (int move_code, CubeState cube){
		int i;
		System.arraycopy(m_cor, 0, cube.m_cor, 0, 8);
		if (( move_code / 3 ) % 3 == 1 )
			return;		//inner slice turn, no corners affected

		int mc = 3*(move_code/9) + ( move_code % 3 );
		int fidx = rotateCOR_fidx[mc];
		int tidx = rotateCOR_tidx[mc];
		if (( mc % 3 != 2 ) && ( mc >= 6 )) { // single L, R, F or B face turn
			for (i = 0; i < 4; ++i) {
				byte tmpface = m_cor[rotateCOR_ft[fidx + i]];
				byte new_ori = (byte)((tmpface >> 3) + rotateCOR_ori[i]);
				new_ori %= 3;
				tmpface = (byte)((tmpface & 0x7) + (new_ori << 3));
				cube.m_cor[rotateCOR_ft[tidx + i]] = tmpface;
			}
		} else {
			for (i = 0; i < 4; ++i) {
				cube.m_cor[rotateCOR_ft[tidx + i]] = m_cor[rotateCOR_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceEDGE (int move_code){
		CubeState cube = new CubeState();
		System.arraycopy(m_edge, 0, cube.m_edge, 0, 24);
		cube.rotate_sliceEDGE( move_code, this );
	}

	public void rotate_sliceEDGE (int move_code, CubeState cube){
		int i, j;
		System.arraycopy(m_edge, 0, cube.m_edge, 0, 24);
		int movdir = move_code % 3;
		int mcx = 3*(move_code/9);
		int layer = (move_code/3)%3; // 0: face, 1: slice, 2: face+slice
		for (j = 0; j < 3; ++j) { // j = 0, 1: face turn. j = 2: slice turn
			if(((layer==1) && (j<2)) || ((layer==0) && (j==2)))
				continue;
			int fidx = rotateEDGE_fidx[3*(mcx+j) + movdir];
			int tidx = rotateEDGE_tidx[3*(mcx+j) + movdir];
			for (i = 0; i < 4; ++i) {
				cube.m_edge[rotateEDGE_ft[tidx + i]] = m_edge[rotateEDGE_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceCENTER (int move_code){
		CubeState cube = new CubeState();
		System.arraycopy(m_cen, 0, cube.m_cen, 0, 24);
		cube.rotate_sliceCENTER( move_code, this );
	}

	public void rotate_sliceCENTER (int move_code, CubeState cube){
		int i, j;
		System.arraycopy(m_cen, 0, cube.m_cen, 0, 24);
		int movdir = move_code % 3;
		int mcx = 3*(move_code/9);
		int layer = (move_code/3)%3; // 0: face, 1: slice, 2: face+slice
		for (j = 0; j < 3; ++j) { // j = 0: face turn. j = 1, 2: slice turn
			if(((layer==1) && (j==0)) || ((layer==0) && (j>0)))
				continue;
			int fidx = rotateEDGE_fidx[3*(mcx+j) + movdir]; // rotateCEN_fidx = rotateEDGE_fidx
			int tidx = rotateEDGE_tidx[3*(mcx+j) + movdir]; // rotateCEN_tidx = rotateEDGE_tidx
			for (i = 0; i < 4; ++i) {
				cube.m_cen[rotateCEN_ft[tidx + i]] = m_cen[rotateCEN_ft[fidx + i]];
			}
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

