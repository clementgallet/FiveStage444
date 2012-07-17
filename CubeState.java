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

	public boolean edgeUD_parity_odd (){
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
		return parity != 0;
	}

	public void rotate_sliceCORNER (int move_code){
		int i;
		if (( move_code / 3 ) % 3 == 1 )
			return;		//inner slice turn, no corners affected

		int mc = 3*(move_code/9) + ( move_code % 3 );
		int fidx = rotateCOR_fidx[mc];
		int tidx = rotateCOR_tidx[mc];
		byte[] old_m_cor = new byte[8];
		System.arraycopy(m_cor, 0, old_m_cor, 0, 8);
		if (( mc % 3 != 2 ) && ( mc >= 6 )) { // single L, R, F or B face turn
			for (i = 0; i < 4; ++i) {
				byte tmpface = old_m_cor[rotateCOR_ft[fidx + i]];
				byte new_ori = (byte)((tmpface >> 3) + rotateCOR_ori[i]);
				new_ori %= 3;
				tmpface = (byte)((tmpface & 0x7) + (new_ori << 3));
				m_cor[rotateCOR_ft[tidx + i]] = tmpface;
			}
		} else {
			for (i = 0; i < 4; ++i) {
				m_cor[rotateCOR_ft[tidx + i]] = old_m_cor[rotateCOR_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceEDGE (int move_code){
		byte[] old_m_edge = new byte[24];
		int i, j;
		System.arraycopy(m_edge, 0, old_m_edge, 0, 24);
		int movdir = move_code % 3;
		int mcx = 3*(move_code/9);
		int layer = (move_code/3)%3; // 0: face, 1: slice, 2: face+slice
		for (j = 0; j < 3; ++j) { // j = 0, 1: face turn. j = 2: slice turn
			if(((layer==1) && (j<2)) || ((layer==0) && (j==2)))
				continue;
			int fidx = rotateEDGE_fidx[3*(mcx+j) + movdir];
			int tidx = rotateEDGE_tidx[3*(mcx+j) + movdir];
			for (i = 0; i < 4; ++i) {
				m_edge[rotateEDGE_ft[tidx + i]] = old_m_edge[rotateEDGE_ft[fidx + i]];
			}
		}
	}

	public void rotate_sliceCENTER (int move_code){
		byte[] old_m_cen = new byte[24];
		int i, j;
		System.arraycopy(m_cen, 0, old_m_cen, 0, 24);
		int movdir = move_code % 3;
		int mcx = 3*(move_code/9);
		int layer = (move_code/3)%3; // 0: face, 1: slice, 2: face+slice
		for (j = 0; j < 3; ++j) { // j = 0: face turn. j = 1, 2: slice turn
			if(((layer==1) && (j==0)) || ((layer==0) && (j>0)))
				continue;
			int fidx = rotateEDGE_fidx[3*(mcx+j) + movdir]; // rotateCEN_fidx = rotateEDGE_fidx
			int tidx = rotateEDGE_tidx[3*(mcx+j) + movdir]; // rotateCEN_tidx = rotateEDGE_tidx
			for (i = 0; i < 4; ++i) {
				m_cen[rotateCEN_ft[tidx + i]] = old_m_cen[rotateCEN_ft[fidx + i]];
			}
		}
	}

	public int convert_edges_to_stage1 (){
		int idx = 0;
		int r = 8;
		for (int i=23; i>=0; i--) {
			if (m_edge[i] >= 16) {
				idx += Cnk[i][r--];
			}
		}
		return idx;
	}

	public void convert_edges1_to_std_cube (int edge)
	{
		int r = 8;
		byte lrfb = 0;
		byte ud = 16;
		for (int i=23; i>=0; i--) {
			if (edge >= Cnk[i][r]) {
				edge -= Cnk[i][r--];
				m_edge[i] = ud++;
			} else {
				m_edge[i] = lrfb++;
			}
		}
	}

	public int convert_symedges_to_stage1 (){
		CubeState cube = new CubeState();
		int i;
		for (int sym=(Tables.symHelper1 == null)?0:Tables.symHelper1[convert_edges_to_stage1()]; sym < Constants.N_SYM_STAGE1; sym++ ){
			rightMultEdges(Symmetry.invSymIdx[sym], cube);
			int edge = cube.convert_edges_to_stage1();
			int rep = Arrays.binarySearch(Tables.sym2rawEdge1, edge);
			if( rep >= 0 )
				return ( rep << 6 ) | sym;
		}
		return -1;
	}

	public short convert_corners_to_stage1 (){
		int i, orientc = 0;
		for (i = 0; i < 7; ++i) {	//don't want 8th edge orientation
			orientc = 3*orientc + (m_cor[i] >> 3);
		}
		return (short)orientc;
	}

	public void convert_corners1_to_std_cube (int corner)
	{
		int i;

		int orientc = corner;
		int orientcmod3 = 0;
		for (i = 6; i >= 0; --i) {	//don't want 8th edge orientation
			byte fo = (byte)(orientc % 3);
			m_cor[i] = (byte)(i + (fo << 3));
			orientcmod3 += fo;
			orientc /= 3;
		}
		m_cor[7] = (byte)(7 + (((24 - orientcmod3) % 3) << 3));
	}

	public short convert_edges_to_stage2 (){
		int u = Constants.get8Perm (m_edge, 16);
		return Tables.perm_to_420[u];
	}

	public void convert_edges2_to_std_cube (int edge){
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = edge / 6;
		Constants.set4Perm (t6, edge % 6);
		for (i = 0; i < 16; ++i)
			m_edge[i] = (byte)i;

		byte f = 16;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if ( edgeFbm >= Cnk[i][r] ) {
				edgeFbm -= Cnk[i][r--];
				m_edge[16 + i] = f++;
			} else {
				m_edge[16 + i] = (byte)(20 + t6[b++]);
			}
		}
	}

	public short convert_centers_to_stage2 (int c){
		int idx = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (m_cen[i] == c) {
				idx += Cnk[i][r--];
			}
		}
		return (short)idx;
	}

	public void convert_centers2_to_std_cube (int center){
		int r = 4;
		byte udlrf = 0;
		for (int i=23; i>=0; i--) {
			if (center >= Cnk[i][r]) {
				center -= Cnk[i][r--];
				m_cen[i] = 5;
			} else {
				m_cen[i] = (byte)(udlrf++/4);
			}
		}
	}

	public short convert_symcenters_to_stage2 (int c){
		CubeState cube = new CubeState();
		int rep;
		for (int sym=0; sym < Constants.N_SYM_STAGE2; sym++ ){
			rightMultCenters(Symmetry.invSymIdx[sym], cube);
			rep = Arrays.binarySearch(Tables.sym2rawCenter2, cube.convert_centers_to_stage2(c));
			if( rep >= 0 )
				return (short)(( rep << 4 ) + sym);
		}
		return -1;
	}

	public int convert_centers_to_stage3 (){
		int cenbm = 0;
		int cenbm4of8 = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; i--) {
			if (m_cen[i] >= 2) {
				if (last_c == -1)
					last_c = i;
				cenbm += Cnk[i][r--];
				if (m_cen[i] != m_cen[last_c]) {
					cenbm4of8 += Cnk[j][s--];
				}
				--j;
			}
		}
		return 35*cenbm + cenbm4of8;
	}

	public void convert_centers3_to_std_cube (int center){
		int cenbm = center/35;
		int cenbm4of8 = center % 35;
		int ud = 0;
		int j = 7;
		int r = 8;
		int s = 4;
		int last_c = -1;
		for (int i = 15; i >= 0; --i) {
			if (cenbm < Cnk[i][r] ) {
				m_cen[i] = (byte)(ud++/4);
			} else {
				cenbm -= Cnk[i][r--];
				if (last_c == -1){
					m_cen[i] = 3;
					last_c = i;
				}
				if (cenbm4of8 < Cnk[j][s]) {
					m_cen[i] = 3;
				} else {
					cenbm4of8 -= Cnk[j][s--];
					m_cen[i] = 2;
				}
			j--;
			}
		}
		for (int i = 16; i < 24; ++i) {
			m_cen[i] = (byte)(i/4);
		}
	}

	public int convert_symcenters_to_stage3 (){
		CubeState cube = new CubeState();
		int rep;
		for (int sym=(Tables.symHelper3 == null)?0:Tables.symHelper3[convert_centers_to_stage3()]; sym < Constants.N_SYM_STAGE3; sym++ ){
			rightMultCenters(Symmetry.invSymIdx[sym], cube);
			rep = Arrays.binarySearch(Tables.sym2rawCenter3, cube.convert_centers_to_stage3());
			if( rep >= 0 )
				return ( rep << 3 ) | sym;
		}
		return -1;
	}

	public short convert_edges_to_stage3 (){
		int idx = 0;
		int r = 8;
		for (int i=15; i>=0; i--) {
			if (m_edge[i] < 4 || m_edge[i] >= 12) {
				idx += Cnk[i][r--];
			}
		}
		return (short)idx;
	}

	public void convert_edges3_to_std_cube (int edge){
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Cnk[i][r]) {
				edge -= Cnk[i][r--];
				m_edge[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				m_edge[i] = e1++;
			}
		}
		for (int i = 16; i < 24; ++i) {
			m_edge[i] = (byte)i;
		}
	}

	private static final byte std_to_sqs[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	private static final byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };

	public int convert_edges_to_stage4 (){
		int redge4of8 = 0;
		int ledge4of8 = 0;
		byte[] edges_rl = new byte[8];
		byte[] edges_fb = new byte[8];

		int i_rl = 4;
		int i_fb = 0;
		int r = 4;
		for( int i=7; i>=0;i--){
			if( m_edge[i+4] < 8 ){
				ledge4of8 += Cnk[i][r--];
				edges_rl[i_rl++] = m_edge[i+4];
			}
			else
				edges_fb[i_fb++] = (byte)(m_edge[i+4] - 8);
		}

		i_rl = 0;
		i_fb = 4;
		r = 4;
		for( int i=7; i>=0;i--){
			int u = (i < 4) ? i : i + 8;
			if( m_edge[u] < 4 ){
				redge4of8 += Cnk[i][r--];
				edges_rl[i_rl++] = m_edge[u];
			}
			else
				edges_fb[i_fb++] = (byte)(m_edge[u] - 8);
		}

		int perm6_rl = Tables.perm_to_420[get8Perm (edges_rl, 0)]%6;
		int perm6_fb = Tables.perm_to_420[get8Perm (edges_fb, 0)]%6;

		return ((( perm6_rl * 6 + perm6_fb ) * 70 + redge4of8 ) * 70 + ledge4of8 );
	}

	public void convert_edges4_to_std_cube (int edge){

		int ledge4of8 = edge % 70;
		edge /= 70;
		int redge4of8 = edge % 70;
		edge /= 70;
		int perm6_fb = edge % 6;
		int perm6_rl = edge / 6;
		byte[] t = new byte[4];

		int i1 = 0;
		int i2 = 0;
		int r = 4;
		Constants.set4Perm( t, perm6_rl );
		for( int i=7; i >= 0; i-- ){
			if( ledge4of8 >= Cnk[i][r] ){
				ledge4of8 -= Cnk[i][r--];
				m_edge[i+4] = (byte)( t[i1++] + 4 );
			}
			else
				m_edge[i+4] = (byte)( (i2++) + 8);
		}

		i1 = 0;
		i2 = 0;
		r = 4;
		Constants.set4Perm( t, perm6_fb );
		for( int i=7; i >= 0; i-- ){
			if( redge4of8 >= Cnk[i][r] ){
				redge4of8 -= Cnk[i][r--];
				m_edge[( i < 4 ) ? i : i + 8] = (byte)(i1++);
			}
			else
				m_edge[( i < 4 ) ? i : i + 8] = (byte)(t[i2++] + 12);
		}

		for( int i=16; i < 24; i++ )
			m_edge[i] = (byte)i;
	}

	public int convert_symedges_to_stage4 (){
		CubeState cube = new CubeState();
		int rep;
		for (int sym=0; sym < Constants.N_SYM_STAGE4; sym++ ){
			conjugateEdges(sym, cube);
			rep = Arrays.binarySearch(Tables.sym2rawEdge4, cube.convert_edges_to_stage4() );
			if( rep >= 0 )
				return ( rep << 4 ) | sym;
		}
		return -1;
	}

	public short convert_corners_to_stage4 (){
		int i;
		byte[] t6 = new byte[8];

		//Note: for corners, use of perm_to_420 array requires "squares" style mapping.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		for (i = 0; i < 8; ++i) {
			t6[std_to_sqs[i]] = std_to_sqs[m_cor[i]];
		}
		int u = Constants.get8Perm (t6, 0);
		return Tables.perm_to_420[u];
	}

	public void convert_corners4_to_std_cube (int corner){
		int i;
		byte[] t6 = new byte[4];
		byte[] t8 = new byte[8];
		//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		int cor_bm = corner / 6;
		Constants.set4Perm (t6, corner % 6);
		int a = 0;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if (cor_bm >= Cnk[i][r] ) {
				cor_bm -= Cnk[i][r--];
				t8[i] = (byte)a++;
			} else {
				t8[i] = (byte)(4 + t6[b++]);
			}
		}
		for (i = 0; i < 8; ++i) {
			m_cor[sqs_to_std[i]] = sqs_to_std[t8[i]];
		}
	}

	public byte convert_centers_to_stage4 (){
		int i;
		int cenbm4of8 = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if (m_cen[i] == 0) {
				cenbm4of8 += Cnk[i][r--];
			}
		}
		return (byte)cenbm4of8;
	}

	public void convert_centers4_to_std_cube (int center){
		int i;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if ( center >= Cnk[i][r] ) {
				center -= Cnk[i][r--];
				m_cen[i] = 0;
			} else {
				m_cen[i] = 1;
			}
		}
		for (i = 8; i < 24; ++i) {
			m_cen[i] = (byte)(i/4);
		}
	}

	private static final byte std_to_sqs_cor[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
	private static final byte sqs_to_std_cor[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
	private static final byte std_to_sqs_cen[] = {
		0,  3,  1,  2,  5,  6,  4,  7,
		8, 11,  9, 10, 13, 14, 12, 15,
		16, 19, 17, 18, 21, 22, 20, 23
	};
	private static final byte sqs_to_std_cen[] = {
		0,  2,  3,  1,  6,  4,  5,  7,
		8, 10, 11,  9, 14, 12, 13, 15,
		16, 18, 19, 17, 22, 20, 21, 23
	};
	private static final int sqs_rep_to_perm[][] = {
		{  0,  7, 16, 23 },
		{  1,  6, 17, 22 },
		{  2, 10, 13, 21 },
		{  3, 11, 12, 20 },
		{  4,  8, 15, 19 },
		{  5,  9, 14, 18 }
	};

	private static final int sqs_perm_to_rep[] = {
		0, 1, 2, 3, 4, 5,
		1, 0, 4, 5, 2, 3,
		3, 2, 5, 4, 0, 1,
		5, 4, 3, 2, 1, 0
	};


	public int convert_edges_to_stage5 (){
		int ep1 = Constants.get4Perm (m_edge, 0);
		int ep2 = Constants.get4Perm (m_edge, 8);
		int ep3 = Constants.get4Perm (m_edge, 16);
		return 96*96*(4*ep3 + (m_edge[20] - 20)) + 96*(4*ep2 + (m_edge[12] - 12)) + 4*ep1 + (m_edge[4] - 4);
	}

	public void convert_edges5_to_std_cube (int edge){
		int i;

		int ep1 = edge % 96;
		int ep2 = (edge/96) % 96;
		int ep3 = edge/(96*96);
		byte[] t = new byte[4];
		Constants.set4Perm (m_edge, ep1/4);

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep1/4]][ep1 % 4]);
		for (i = 0; i < 4; ++i) {
			m_edge[i+4] = (byte)(t[i]+4);
		}

		Constants.set4Perm (t, ep2/4);
		for (i = 0; i < 4; ++i) {
			m_edge[i+8] = (byte)(t[i]+8);
		}

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep2/4]][ep2 % 4]);
		for (i = 0; i < 4; ++i) {
			m_edge[i+12] = (byte)(t[i]+12);
		}

		Constants.set4Perm (t, ep3/4);
		for (i = 0; i < 4; ++i) {
			m_edge[i+16] = (byte)(t[i]+16);
		}

		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep3/4]][ep3 % 4]);
		for (i = 0; i < 4; ++i) {
			m_edge[i+20] = (byte)(t[i]+20);
		}
	}

	public int convert_symedges_to_stage5 (){
		CubeState cube = new CubeState();
		CubeState cube2 = new CubeState();
		int rep;
		for (int sym=Tables.symHelper5[convert_edges_to_stage5()]; sym < Constants.N_SYM_STAGE5; sym++ ){
			System.arraycopy(m_edge, 0, cube.m_edge, 0, 24);
			cube.leftMultEdges(sym);
			for (int cosym=0; cosym < 4; cosym++ ){
				cube.rightMultEdges (Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
				rep = Arrays.binarySearch(Tables.sym2rawEdge5, cube2.convert_edges_to_stage5 ());
				if( rep >= 0 ){
					return ( rep << 8 ) | ( sym << 2 ) | cosym;
				}
			}
		}
		System.out.println("Nope...");
		return -1;
	}

	public short convert_centers_to_stage5 (){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.

		byte[] new_m_cen = new byte[24];
		for (i = 0; i < 24; ++i) {
			new_m_cen[std_to_sqs_cen[i]] = (byte)(std_to_sqs_cen[4*m_cen[i]]/4);
		}

		int x = 0;
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			if ((new_m_cen[i] & 0x1) != 0) {
				x |= b;
			}
			b >>= 1;
		}
		short cen1 = (short)Tables.squares_cen_revmap[(x >> 16) & 0xFF];
		short cen2 = (short)Tables.squares_cen_revmap[(x >> 8) & 0xFF];
		short cen3 = (short)Tables.squares_cen_revmap[x & 0xFF];
		short cen = (short)(cen1 + 12*cen2 + 12*12*cen3);
		return cen;
	}

	public void convert_centers5_to_std_cube (int center){
		int i;
		byte[] old_m_cen = new byte[24];

		int cen1 = center % 12;
		int cen2 = (center/12) % 12;
		int cen3 = center/(12*12);
		int x = (Tables.squares_cen_map[cen1] << 16) | (Tables.squares_cen_map[cen2] << 8) | Tables.squares_cen_map[cen3];
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			old_m_cen[i] = (byte) (2*(i/8) + ((x & b) == 0 ? 0 : 1));
			b >>= 1;
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		for (i = 0; i < 24; ++i) {
			m_cen[sqs_to_std_cen[i]] = (byte)(sqs_to_std_cen[4*old_m_cen[i]]/4);
		}
	}

	public byte convert_corners_to_stage5 (){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.

		byte[] new_m_cor = new byte[8];
		for (i = 0; i < 8; ++i) {
			new_m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[m_cor[i]];
		}

		return (byte)(4*Constants.get4Perm (new_m_cor, 0) + (new_m_cor[4] - 4));
	}

	public void convert_corners5_to_std_cube (int corner){
		int i;
		byte[] old_m_cor = new byte[8];
		byte[] t = new byte[4];
		Constants.set4Perm (old_m_cor, corner/4);
		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[corner/4]][corner % 4]);
		for (i = 0; i < 4; ++i) {
			old_m_cor[i+4] = (byte)(t[i]+4);
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		for (i = 0; i < 8; ++i) {
			m_cor[sqs_to_std_cor[i]] = sqs_to_std_cor[old_m_cor[i]];
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

