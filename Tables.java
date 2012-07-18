package cg.fivestage444;

import static cg.fivestage444.Constants.*;
import java.util.Arrays;

public final class Tables {

	public static final void init (){
		initMap96();
		initPerm420();
		initSquaresCenterMap();
	}

	public static final void init_tables (){
		initSymEdgeToEdgeStage1();
		initSymEdgeStage1();
		initCornerStage1();
		initCornerConjStage1();
		initEdgeStage2();
		initEdgeConjStage2();
		initSymCenterToCenterStage2();
		initSymCenterStage2();
		initSymCenterToCenterStage3();
		initSymCenterStage3();
		initEdgeStage3();
		initEdgeConjStage3();
		initParityTable();
		initSymEdgeToEdgeStage4();
		initSymEdgeStage4();
		initCornerStage4();
		initCornerConjStage4();
		initCenterStage4();
		initCenterConjStage4();
		initSquaresMovemap();
		initCornerConjStage5();
		initCenterStage5();
		initCenterConjStage5();
		initSymEdgeToEdgeStage5();
		initSymEdgeStage5();

		initPrunEdgCen2();
		initPrunEdgCen4();
		initPrunEdgCor5();

		initPrun1();

		if( FULL_PRUNING_STAGE4 )
			initPrun4();
		else
			initPrunEdgCor4();

		if( FULL_PRUNING_STAGE5 )
			initPrun5();
		else
			initPrunEdgCen5();

	}

	/*** init_parity_table ***/
	private static final boolean[] parity_perm8_table = new boolean[40320];

	private static final int get_parity8 (int x){
		int i, j;
		int parity = 0;
		byte[] t = new byte[8];
		set8Perm (t, x);
		for (i = 0; i < 7; ++i) {
			if (t[i] == i) {
				continue;
			}
			for (j = i + 1; j < 8; ++j) {
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

	public static void initParityTable (){
		int x;

		for (x = 0; x < 40320; ++x) {
			parity_perm8_table[x] = (get_parity8 (x) != 0);
		}
	}

	/*** init map96 ***/
	public static final byte[][] map96 = new byte[96][8];

	private static void initMap96 (){
		int a1, i;
		byte[] t = new byte[8];
		byte f;
		for (a1 = 0; a1 < 24; ++a1) {
			set4Perm (t, a1);
			for (i = 0; i < 4; ++i) {
				t[i+4] = (byte)(t[i] + 4);
			}
			for (i = 0; i < 8; ++i) {
				map96[4*a1][i] = t[i];
			}
			f = t[4]; t[4]= t[5]; t[5] = f;
			f = t[6]; t[6]= t[7]; t[7] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 1][i] = t[i];
			}
			f = t[4]; t[4]= t[7]; t[7] = f;
			f = t[5]; t[5]= t[6]; t[6] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 2][i] = t[i];
			}
			f = t[4]; t[4]= t[5]; t[5] = f;
			f = t[6]; t[6]= t[7]; t[7] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 3][i] = t[i];
			}
		}
	}

	/*** init_perm_to_420 ***/
	public static final short[] perm_to_420 = new short[40320]; // (420)

	public static void initPerm420 (){
		int i;
		int u, v, w, u2;
		byte[] t = new byte[8];
		byte[] t2 = new byte[8];
		byte[] t3 = new byte[8];

		for (v = 0; v < 6; ++v) {
			set8Perm (t, v);
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i)
					t2[i] = map96[w][t[i]];
				for (u = 0; u < 70; ++u) {
					int f = 0;
					int b = 4;
					int p = u;
					int r = 4;
					for (i = 7; i >= 0; --i) {
						if (p >= Cnk[i][r]) {
							p -= Cnk[i][r--];
							t3[i] = t2[f++];
						} else {
							t3[i] = t2[b++];
						}
					}
					u2 = get8Perm (t3, 0);
					perm_to_420[u2] = (short)(6*u + v);
				}
			}
		}
	}

	/*** init stage 1 symEdgeToEdge ***/
	public static int[] sym2rawEdge1 = new int[N_STAGE1_SYMEDGES];
	public static long[][] hasSymEdgeSTAGE1;
	public static byte[] symHelper1;

	public static void initSymEdgeToEdgeStage1 (){
		System.out.println( "Starting symEdgeToEdge stage 1..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();

		byte[] isRepTable = new byte[(N_STAGE1_EDGES>>3) + 1];
		symHelper1 = new byte[N_STAGE1_EDGES];
		hasSymEdgeSTAGE1 = new long[N_STAGE1_SYMEDGES][1];
		for (int u = 0; u < N_STAGE1_EDGES; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			symHelper1[u] = 0;
			cube1.convert_edges1_to_std_cube(u);
			for (int sym = 1; sym < N_SYM_STAGE1; ++sym) {
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				int edge = cube2.convert_edges_to_stage1();
				isRepTable[edge>>>3] |= 1<<(edge&0x7);
				symHelper1[edge] = (byte)(Symmetry.invSymIdx[sym]);
				if( edge == u )
					hasSymEdgeSTAGE1[repIdx][0] |= (0x1L << sym);
			}
			sym2rawEdge1[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 1... generated "+repIdx+" reps." );
	}

	/*** init stage 1 symEdges ***/
	public static int[][] moveEdge1 = new int[N_STAGE1_SYMEDGES][N_STAGE1_MOVES];

	public static void initSymEdgeStage1 (){

		System.out.println( "Starting symEdge stage 1..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE1_SYMEDGES; ++u) {
			cube1.convert_edges1_to_std_cube( sym2rawEdge1[u] );
			for (int mc = 0; mc < N_STAGE1_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				moveEdge1[u][mc] = cube2.convert_symedges_to_stage1();
			}
		}
		System.out.println( "Finishing symEdge stage 1..." );
	}

	/*** init stage 1 corners ***/
	public static short[][] moveCorner1 = new short[N_STAGE1_CORNERS][N_FACE_MOVES]; // (2187) 2187*18

	public static void initCornerStage1 (){

		System.out.println( "Starting corner stage 1..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE1_CORNERS; ++u) {
			cube1.convert_corners1_to_std_cube (u);
			for (int mc = 0; mc < N_STAGE1_MOVES; ++mc) {
				if((stage2moves[mc]/3)%3 == 1 )
					continue;
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage2moves[mc]);
				moveCorner1[u][stage2face[mc]] = cube2.convert_corners_to_stage1();
			}
		}
		System.out.println( "Finishing corner stage 1..." );
	}

	/*** init stage 1 corner conjugate ***/
	public static short[][] conjCorner1 = new short[N_STAGE1_CORNERS][N_SYM_STAGE1]; // (2187) 2187*48

	public static void initCornerConjStage1 (){

		System.out.println( "Starting corner conjugate stage 1..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE1_CORNERS; ++u) {
			cube1.convert_corners1_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE1; ++sym) {
				cube1.rightMultCorners (Symmetry.invSymIdx[sym], cube2);
				cube2.deMirrorCorners ();
				conjCorner1[u][sym] = cube2.convert_corners_to_stage1 ();
			}
		}
		System.out.println( "Finishing corner conjugate stage 1..." );
	}

	/*** init stage 2 edges ***/
	public static short[][] moveEdge2 = new short[N_STAGE2_EDGES][N_STAGE2_MOVES]; // 420*28

	public static void initEdgeStage2 (){

		System.out.println( "Starting edge stage 2..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE2_EDGES; ++u) {
			cube1.convert_edges2_to_std_cube(u);
			for (int mc = 0; mc < N_STAGE2_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				moveEdge2[u][mc] = cube2.convert_edges_to_stage2();
			}
		}
		System.out.println( "Finishing edge stage 2..." );
	}

	/*** init stage 2 edge conjugate ***/
	public static short[][] conjEdge2 = new short[N_STAGE2_EDGES][N_SYM_STAGE2]; // (420) 420*16

	public static void initEdgeConjStage2 (){

		System.out.println( "Starting edge conjugate stage 2..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE2_EDGES; ++u) {
			cube1.convert_edges2_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE2; ++sym) {
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				conjEdge2[u][sym] = cube2.convert_edges_to_stage2 ();
			}
		}
		System.out.println( "Finishing edge conjugate stage 2..." );
	}

	/*** init stage 2 symCenterToCenter ***/
	public static short[] sym2rawCenter2 = new short[N_STAGE2_SYMCENTER];
	public static long[][] hasSymCenterSTAGE2;

	public static void initSymCenterToCenterStage2 (){

		System.out.println( "Starting symCenterToCenter stage 2..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] isRepTable = new byte[(N_STAGE2_CENTER>>3) + 1];
		hasSymCenterSTAGE2 = new long[N_STAGE2_SYMCENTER][1];
		for (int u = 0; u < N_STAGE2_CENTER; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			cube1.convert_centers2_to_std_cube( u );

			for (int sym = 1; sym < N_SYM_STAGE2; ++sym) {
				cube1.rightMultCenters (Symmetry.invSymIdx[sym], cube2);
				short cen = cube2.convert_centers_to_stage2(5);
				isRepTable[cen>>>3] |= 1<<(cen&0x7);
				if( cen == u ){
					hasSymCenterSTAGE2[repIdx][0] |= (1 << sym);
				}
			}
			sym2rawCenter2[repIdx++] = (short)u;
		}
		System.out.println( "Finishing symCenterToCenter stage 2... generated "+repIdx+" reps." );
	}

	/*** init stage 2 symCenters ***/
	public static int[][] moveCenter2 = new int[N_STAGE2_SYMCENTER][N_STAGE2_MOVES]; // (716*16) 716*28

	public static void initSymCenterStage2 (){

		System.out.println( "Starting symCenter stage 2..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE2_SYMCENTER; ++u) {
			cube1.convert_centers2_to_std_cube( sym2rawCenter2[u] );
			for (int mc = 0; mc < N_STAGE2_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage2moves[mc]);
				moveCenter2[u][mc] = cube2.convert_symcenters_to_stage2(5);
			}
		}
		System.out.println( "Finishing symCenter stage 2..." );
	}

	/*** init stage 3 symCenterToCenter ***/
	public static int[] sym2rawCenter3 = new int[N_STAGE3_SYMCENTERS];
	public static int[] hasSymCenterSTAGE3;
	public static byte[] symHelper3;

	public static void initSymCenterToCenterStage3 (){

		System.out.println( "Starting symCenterToCenter stage 3..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] isRepTable = new byte[(N_STAGE3_CENTERS>>3) + 1];
		hasSymCenterSTAGE3 = new int[N_STAGE3_SYMCENTERS];
		symHelper3 = new byte[N_STAGE3_CENTERS];
		for (int u = 0; u < N_STAGE3_CENTERS; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			symHelper3[u] = 0;
			cube1.convert_centers3_to_std_cube(u);
			for (int sym = 0; sym < N_SYM_STAGE3; ++sym) {
				cube1.rightMultCenters(Symmetry.invSymIdx[sym], cube2);
				int cen = cube2.convert_centers_to_stage3();
				isRepTable[cen>>>3] |= 1<<(cen&0x7);
				symHelper3[cen] = (byte)(Symmetry.invSymIdx[sym]);
				if( cen == u )
					hasSymCenterSTAGE3[repIdx] |= (1 << sym );
			}
			sym2rawCenter3[repIdx++] = u;
		}
		System.out.println( "Finishing symCenterToCenter stage 3... generated "+repIdx+" reps." );
	}

	/*** init stage 3 symCenters ***/
	public static int[][] moveCenter3 = new int[N_STAGE3_SYMCENTERS][N_STAGE3_MOVES];

	public static void initSymCenterStage3 (){

		System.out.println( "Starting symCenters stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_SYMCENTERS; ++u) {
			cube1.convert_centers3_to_std_cube(sym2rawCenter3[u]);
			for (int mc = 0; mc < N_STAGE3_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage2moves[mc]);
				moveCenter3[u][mc] = cube2.convert_symcenters_to_stage3();
			}
		}
		System.out.println( "Finishing symCenter stage 3..." );
	}

	/*** init stage 3 edges ***/
	public static short[][] moveEdge3 = new short[N_STAGE3_EDGES][N_STAGE3_MOVES];

	public static void initEdgeStage3 (){

		System.out.println( "Starting edge stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_EDGES; ++u) {
			cube1.convert_edges3_to_std_cube(u);
			for (int mc = 0; mc < N_STAGE3_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				moveEdge3[u][mc] = cube2.convert_edges_to_stage3();
			}
		}
		System.out.println( "Finishing edge stage 3..." );
	}

	/*** init stage 3 edge conjugate ***/
	public static short[][] conjEdge3 = new short[N_STAGE3_EDGES][N_SYM_STAGE3];

	public static void initEdgeConjStage3 (){

		System.out.println( "Starting edge conjugate stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_EDGES; ++u) {
			cube1.convert_edges3_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE3; ++sym) {
				cube1.rightMultEdges(Symmetry.invSymIdx[sym], cube2);
				conjEdge3[u][sym] = cube2.convert_edges_to_stage3();
			}
		}
		System.out.println( "Finishing edge conjugate stage 3..." );
	}

	/*** init_stage4 ***/

	public static int[] sym2rawEdge4 = new int[N_STAGE4_SYMEDGES]; // 5968
	public static long[][] hasSymEdgeSTAGE4;

	public static void initSymEdgeToEdgeStage4 (){
		System.out.println( "Starting symEdgeToEdge stage 4..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] t = new byte[8];

		byte[] isRepTable = new byte[((N_STAGE4_EDGES*2)>>3) + 1];
		hasSymEdgeSTAGE4 = new long[N_STAGE4_SYMEDGES][1];
		for (int u = 0; u < N_STAGE4_EDGES*2; ++u) { // *2 because you didn't take care of the parity.
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			cube1.convert_edges4_to_std_cube( u );

			/* Only retain configs without parity */
			int ul = get8Perm( cube1.m_edge, 4 );
			for (int i=0; i<4; i++)
				t[i] = ( cube1.m_edge[i] > 4 ) ? (byte)(cube1.m_edge[i]-8) : cube1.m_edge[i];
			for (int i=4; i<8; i++)
				t[i] = ( cube1.m_edge[i+8] > 4 ) ? (byte)(cube1.m_edge[i+8]-8) : cube1.m_edge[i+8];
			int uh = get8Perm( t, 0 );
			if( parity_perm8_table[ul] != parity_perm8_table[uh] ) continue; // getting rid of the parity.

			for (int sym = 0; sym < N_SYM_STAGE4; ++sym) {
				cube1.conjugateEdges (sym, cube2);
				int edge = cube2.convert_edges_to_stage4();
				isRepTable[edge>>>3] |= 1<<(edge&0x7);
				if( edge == u )
					hasSymEdgeSTAGE4[repIdx][0] |= (1 << sym);
			}
			sym2rawEdge4[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 4... generated "+repIdx+" reps." );
	}

	public static int[][] moveEdge4 = new int[N_STAGE4_SYMEDGES][N_STAGE4_MOVES]; // (5968*16) 5968*16.

	public static void initSymEdgeStage4 (){

		System.out.println( "Starting sym edge stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_SYMEDGES; ++u) {
			cube1.convert_edges4_to_std_cube( sym2rawEdge4[u] );
			for (int mc = 0; mc < N_STAGE4_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				moveEdge4[u][mc] = cube2.convert_symedges_to_stage4();
			}
		}
		System.out.println( "Finishing sym edge stage 4..." );
	}

	public static short[][] moveCorner4 = new short[N_STAGE4_CORNERS][N_STAGE4_MOVES]; // (420) 420*16.

	public static void initCornerStage4 (){

		System.out.println( "Starting corner stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_CORNERS; ++u) {
			cube1.convert_corners4_to_std_cube (u);
			for (int mc = 0; mc < N_STAGE4_MOVES; ++mc) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage2moves[mc]);
				moveCorner4[u][mc] = cube2.convert_corners_to_stage4();
			}
		}
		System.out.println( "Finishing corner stage 4..." );
	}

	public static short[][] conjCorner4 = new short[N_STAGE4_CORNERS][N_SYM_STAGE4]; // (420) 420*16.

	public static void initCornerConjStage4 (){

		System.out.println( "Starting corner conj stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_CORNERS; ++u) {
			cube1.convert_corners4_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE4; ++sym) {
				cube1.conjugateCorners (sym, cube2);
				conjCorner4[u][sym] = cube2.convert_corners_to_stage4();
			}
		}
		System.out.println( "Finishing corner conj stage 4..." );
	}

	public static short[][] moveCenter4 = new short[N_STAGE4_CENTERS][N_STAGE4_MOVES];

	public static void initCenterStage4 (){

		System.out.println( "Starting center stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_CENTERS; ++u) {
			cube1.convert_centers4_to_std_cube (u);
			for (int mc = 0; mc < N_STAGE4_MOVES; ++mc) {
				if ((stage2moves[mc] % 3 ) == 2) continue;
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage2moves[mc]);
				moveCenter4[u][mc] = cube2.convert_centers_to_stage4();
			}
		}
		System.out.println( "Finishing center stage 4..." );
	}

	public static short[][] conjCenter4 = new short[N_STAGE4_CENTERS][N_SYM_STAGE4];

	public static void initCenterConjStage4 (){

		System.out.println( "Starting center stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_CENTERS; ++u) {
			cube1.convert_centers4_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE4; ++sym) {
				cube1.conjugateCenters (sym, cube2);
				conjCenter4[u][sym] = cube2.convert_centers_to_stage4();
			}
		}
		System.out.println( "Finishing center conj stage 4..." );
	}

	/*** init_stage5 ***/

	public static final short squares_cen_map[] = { 0x0F, 0x33, 0x3C, 0x55, 0x5A, 0x66, 0x99, 0xA5, 0xAA, 0xC3, 0xCC, 0xF0 };

	public static short[][] moveCorner5 = new short[N_STAGE5_CORNERS][N_STAGE5_MOVES]; // (96) 96*12

	public static void initSquaresMovemap (){

		System.out.println( "Starting corner stage 5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CORNERS; ++u) {
			cube1.convert_corners5_to_std_cube (u);
			for (int m = 0; m < N_STAGE5_MOVES; ++m) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage2moves[m]);
				moveCorner5[u][m] = cube2.convert_corners_to_stage5();
			}
		}
		System.out.println( "Finishing corner stage 5..." );
	}

	public static final byte[] squares_cen_revmap = new byte[256]; // (12)

	public static void initSquaresCenterMap (){

		int i, j;
		for (i = 0; i < 256; ++i) {
			squares_cen_revmap[i] = 0;
		}
		for (i = 0; i < 12; ++i) {
			squares_cen_revmap[squares_cen_map[i]] = (byte)i;
		}
	}

	public static short[][] moveCenter5 = new short[N_STAGE5_CENTERS][N_STAGE5_MOVES];

	public static void initCenterStage5 (){

		System.out.println( "Starting center stage5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CENTERS; ++u) {
			cube1.convert_centers5_to_std_cube (u);
			for (int m = 0; m < N_STAGE5_MOVES; ++m) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage2moves[m]);
				moveCenter5[u][m]= cube2.convert_centers_to_stage5();
			}
		}
		System.out.println( "Finishing center stage5..." );
	}

	/*** init stage 5 symEdgeToEdge ***/
	public static int[] sym2rawEdge5 = new int[N_STAGE5_SYMEDGES];
	public static long[][] hasSymEdgeSTAGE5;
	public static byte[] symHelper5 = new byte[N_STAGE5_EDGES];

	public static void initSymEdgeToEdgeStage5 (){

		System.out.println( "Starting symEdgeToEdge stage 5..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();

		byte[] isRepTable = new byte[N_STAGE5_EDGES >> 3];
		hasSymEdgeSTAGE5 = new long[N_STAGE5_SYMEDGES][4];

		for (int u = 0; u < N_STAGE5_EDGES; ++u) {
			if(((isRepTable[u>>>3]>>(u&0x7))&1) != 0 ) continue;
			cube1.convert_edges5_to_std_cube (u);
			symHelper5[u] = 0;
			for (int sym = 0; sym < N_SYM_STAGE5; ++sym) {
				for (int cosym = 0; cosym < 4; ++cosym) {
					if(sym==0 && cosym==0) continue;
					cube1.rightMultEdges(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultEdges(sym);
					int edge = cube2.convert_edges_to_stage5 ();
					isRepTable[edge>>>3] |= 1<<(edge&0x7);
					symHelper5[edge] = (byte)(Symmetry.invSymIdx[sym]);
					if( edge == u )
						hasSymEdgeSTAGE5[repIdx][cosym] |= ( 0x1L << sym );
				}
			}
			sym2rawEdge5[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 5... generated "+repIdx+" reps." );
	}

	/*** init stage 5 symEdges ***/
	public static int[][] moveEdge5 = new int[N_STAGE5_SYMEDGES][N_STAGE5_MOVES]; // (21908*48) 21908*12 = 6677424

	public static void initSymEdgeStage5 (){

		System.out.println( "Starting symEdge stage 5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_SYMEDGES; ++u) {
			cube1.convert_edges5_to_std_cube (sym2rawEdge5[u]);
			for (int mc = 0; mc < N_STAGE5_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage2moves[mc]);
				moveEdge5[u][mc] = cube2.convert_symedges_to_stage5();
			}
		}
		System.out.println( "Finishing symEdge stage 5..." );
	}

	/*** init stage 5 corner conjugate ***/
	public static short[][] conjCorner5 = new short[N_STAGE5_CORNERS][N_SYM_STAGE5*4]; // (96) 96*48

	public static void initCornerConjStage5 (){

		System.out.println( "Starting corner conjugate stage 5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CORNERS; ++u) {
			cube1.convert_corners5_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE5; ++sym) {
				for (int cosym = 0; cosym < 4; ++cosym) {
					cube1.rightMultCorners(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultCorners(sym);
					conjCorner5[u][(sym<<2) + cosym] = cube2.convert_corners_to_stage5 ();
				}
			}
		}
		System.out.println( "Finishing corner conjugate stage 5..." );
	}

	/*** init stage 5 center conjugate ***/
	public static short[][] conjCenter5 = new short[N_STAGE5_CENTERS][N_SYM_STAGE5*4]; // (1728) 1728*48

	public static void initCenterConjStage5 (){

		System.out.println( "Starting center conjugate stage 5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CENTERS; ++u) {
			cube1.convert_centers5_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE5; ++sym) {
				for (int cosym = 0; cosym < 4; ++cosym) {
					cube1.rightMultCenters(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultCenters(sym);
					conjCenter5[u][(sym<<2) + cosym] = cube2.convert_centers_to_stage5();
				}
			}
		}
		System.out.println( "Finishing center conjugate stage 5..." );
	}

	/** Pruning functions **/

	public static PruningStage3Cen prune_table_cen3;
	public static PruningStage3Edg prune_table_edg3;

        public static final void setPrun2(int[] table, int index, int value) {
                table[index >> 3] ^= (0x0f ^ value) << ((index & 7) << 2);
        }

        public static final int getPrun2(int[] table, int index) {
                return (table[index >> 3] >> ((index & 7) << 2)) & 0x0f;
        }

	public static void initRawSymPrun(int[] prunTable, final int INV_DEPTH, 
			final short[][] rawMove, final short[][] rawConj,
			final int[][] symMove, final long[][] symState, 
			final int[] solvedStates, final int SYM_SHIFT) {

		final int SYM_MASK = (1 << SYM_SHIFT) - 1;
		final int N_RAW = rawMove.length;
		final int N_SYM = symMove.length;
		final int N_SIZE = N_RAW * N_SYM;
		final int N_MOVES = symMove[0].length;
		final int N_COSYM = symState[0].length;

		for (int i=0; i<(N_RAW*N_SYM+7)/8; i++)
			prunTable[i] = -1;

		for (int i=0; i<solvedStates.length; i++){
			setPrun2( prunTable, solvedStates[i], 0 );
		}
		int depth = 0;
		int done = solvedStates.length;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0x0f : depth;
			int check = inv ? depth : 0x0f;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = prunTable[i>>3];
				if (!inv && val == -1) {
					i += 8;
					continue;
				}
				for (int end=Math.min(i+8, N_SIZE); i<end; i++, val>>=4) {
					if ((val & 0x0f)/*getPrun4(tempTable, i)*/ != select) continue;
					int raw = i % N_RAW;
					int sym = i / N_RAW;
					for (int m=0; m<N_MOVES; m++) {
						int symx = symMove[sym][m];
						int rawx = rawConj[rawMove[raw][m]][symx & SYM_MASK];
						symx >>>= SYM_SHIFT;
						int idx = symx * N_RAW + rawx;
						if (getPrun2(prunTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun2(prunTable, i, depth);
							break;
						} else {
							setPrun2(prunTable, idx, depth);
							int nsym = 1;
							unique++;
							for (int j=0; j<N_COSYM; j++) {
								long symS = symState[symx][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									int idxx = symx * N_RAW + rawConj[rawx][k*N_COSYM+j];
									nsym++;
									if (getPrun2(prunTable, idxx) == 0x0f) {
										setPrun2(prunTable, idxx, depth);
										done++;
									}
								}
							}
							//pos += 48/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}
	}

	public static int[] prunTableEdgCen2 = new int[(N_STAGE2_SYMCENTER*N_STAGE2_EDGES+7)/8];
	public static void initPrunEdgCen2(){

		int[] solved = new int [stage2_solved_symcenters.length*2];
                for (int i=0; i < stage2_solved_symcenters.length; i++){
                        solved[2*i] = stage2_solved_symcenters[i]*N_STAGE2_EDGES + 414;
                        solved[2*i+1] = stage2_solved_symcenters[i]*N_STAGE2_EDGES + 0;
		}
		initRawSymPrun( prunTableEdgCen2, 18, moveEdge2, conjEdge2, moveCenter2, hasSymCenterSTAGE2, solved, 4);
	}

	public static int[] prunTableEdgCen4 = new int[(N_STAGE4_SYMEDGES*N_STAGE4_CENTERS+7)/8];
	public static void initPrunEdgCen4(){

		int[] solved = new int[stage4_solved_centers_bm.length];
		for (int i=0; i < stage4_solved_centers_bm.length; i++)
			solved[i] = stage4_solved_centers_bm[i];
		initRawSymPrun( prunTableEdgCen4, 18, moveCenter4, conjCenter4, moveEdge4, hasSymEdgeSTAGE4, solved, 4);
	}

	public static int[] prunTableEdgCor5 = new int[(N_STAGE5_SYMEDGES*N_STAGE5_CORNERS+7)/8];
	public static void initPrunEdgCor5(){

		int[] solved = {0};
		initRawSymPrun( prunTableEdgCor5, 18, moveCorner5, conjCorner5, moveEdge5, hasSymEdgeSTAGE5, solved, 8);
	}

	private static int[] nd = new int[30 * 4];
	private static byte[] get_packed = new byte[243*8];
	static {
		for (int i=0; i<243; i++) {
			for (int j=0; j<5; j++) {
				int l = i;
				for (int k=1; k<=j; k++)
					l /= 3;
				get_packed[i*8+j] = (byte)(l % 3);
			}
		}
		for (int i=0; i<30; i++) {
			for (int j=0; j<3; j++) {
				nd[i*4+j] = i + (j - i + 30 + 1) % 3 - 1;
			}
		}
	}

	public static final int get_dist_packed(final byte[] table, long idx) {
		if (idx < table.length*4) {
			int data = table[(int)(idx >>> 2)]&0x0FF;
			return get_packed[(data<<3) | (int)(idx & 3)];
		} else {
			int data = table[(int)(idx-table.length*4L)]&0x0FF;
			return get_packed[(data<<3) | 4];
		}
	}

	public static final int new_dist(final byte[] table, long idx, int dist) {
		return nd[(dist << 2) | get_dist_packed(table, idx)];
	}

	public static final int getPrun4 (final byte[] table, int idx){
		return (table[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	public static final void setPrun4 (byte[] table, int idx, int value){
		table[idx>>2] |= (byte)(value << ((idx & 0x3) << 1));
	}


	public static void initRawSymPrunPacked(byte[] prunTable, final int INV_DEPTH, 
			final short[][] rawMove, final short[][] rawConj,
			final int[][] symMove, final long[][] symState, 
			final int[] solvedStates, final byte[] moveMap, final int SYM_SHIFT) {

		final int SYM_MASK = (1 << SYM_SHIFT) - 1;
		final int N_RAW = rawMove.length;
		final int N_SYM = symMove.length;
		final int N_SIZE = N_RAW * N_SYM;
		final int N_SIZE_PACKED = N_SIZE / 5 + 1;
		final int N_MOVES = symMove[0].length;
		final int N_COSYM = symState[0].length;

		long maxSym = 0;
		for (int i=0; i<N_SYM; i++)
			if(symState[i][0]>maxSym)
				maxSym = symState[i][0];
		int N_SSYM = 0;
		while(maxSym!=0){
			maxSym >>>= 1;
			N_SSYM++;
		}
		N_SSYM *= N_COSYM;

		System.out.println("Has "+N_SSYM+" symmetries");

		byte[] tempTable = new byte[(N_SIZE/4)+1];
		for (int i=0; i<solvedStates.length; i++){
			setPrun4( tempTable, solvedStates[i], 3 );
		}
		int depth = 0;
		int done = solvedStates.length;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0 : ((depth+2)%3)+1;
			int check = inv ? ((depth+2)%3)+1 : 0;
			int save = (depth % 3) + 1;
			depth++;
			int pos = 0;
			int unique = 0;
			for (int i=0; i<N_SIZE;) {
				int val = tempTable[i>>2];
				if (!inv && val == 0) {
					i += 4;
					continue;
				}
				for (int end=Math.min(i+4, N_SIZE); i<end; i++, val>>=2) {
					if ((val & 0x3)/*getPrun4(tempTable, i)*/ != select) continue;
					int raw = i % N_RAW;
					int sym = i / N_RAW;
					for (int m=0; m<N_MOVES; m++) {
						int symx = symMove[sym][m];
						int mm = ( moveMap == null ) ? m : moveMap[m];
						int rawx = rawConj[mm>=0 ? rawMove[raw][mm] : raw][symx & SYM_MASK];
						symx >>>= SYM_SHIFT;
						int idx = symx * N_RAW + rawx;
						if (getPrun4(tempTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun4(tempTable, i, save);
							break;
						} else {
							setPrun4(tempTable, idx, save);
							int nsym = 1;
							unique++;
							for (int j=0; j<N_COSYM; j++) {
								long symS = symState[symx][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									int idxx = symx * N_RAW + rawConj[rawx][k*N_COSYM+j];
									if( idxx == idx )
										nsym++;
									if (getPrun4(tempTable, idxx) == 0) {
										setPrun4(tempTable, idxx, save);
										done++;
									}
								}
							}
							pos += N_SSYM/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, pos, unique));
		}

		for (int i=0; i<N_SIZE_PACKED; i++) {
			int n = 1;
			int value = 0;
			for (int j=0; j<4; j++){
				value += n * (getPrun4(tempTable, (i<<2)+j) % 3);
				n *= 3;
			}
			if ( ((N_SIZE_PACKED<<2)+i) < N_SIZE)
				value += n * (getPrun4(tempTable, (N_SIZE_PACKED<<2)+i) % 3);
			prunTable[i] = (byte)value;
		}
		tempTable = null;
		System.gc();
	}

	public static byte[] prunTable1 = new byte[N_STAGE1_SYMEDGES*N_STAGE1_CORNERS/5+1];
	public static void initPrun1(){
		int[] solved = {1906};
		initRawSymPrunPacked( prunTable1, 9, moveCorner1, conjCorner1, moveEdge1, hasSymEdgeSTAGE1, solved, Constants.stage2face, 6);
	}

	public final static int prunDist1(int edge, int sym, int corner){

		int idx = Constants.N_STAGE1_CORNERS*edge+conjCorner1[corner][sym];
		int dist1 = get_dist_packed(prunTable1, idx);
		int d;
		for (d = 0; ( idx != 1906 ) && ( d < 20 ); d++){
			for (int m = 0; m < Constants.N_STAGE1_MOVES; ++m) {
				int mm = Constants.stage2face[m];
				int cornerx = ( mm >= 0 ) ? moveCorner1[corner][mm] : corner;
				int edgex = moveEdge1[edge][Symmetry.moveConjugateStage[m][sym]];
				int symx = Symmetry.symIdxMultiply[edgex & 0x3F][sym];
				edgex >>= 6;
				int idxx = Constants.N_STAGE1_CORNERS*edgex+conjCorner1[cornerx][symx];
				int dist2 = get_dist_packed(prunTable1, idxx);
				if (((dist2+1) % 3) != dist1) continue;
				edge = edgex;
				sym = symx;
				corner = cornerx;
				dist1 = dist2;
				idx = idxx;
				break;
			}
		}
		return d;
	}

	public static byte[] prunTableEdgCor4 = new byte[N_STAGE4_SYMEDGES*N_STAGE4_CORNERS/5+1];
	public static void initPrunEdgCor4(){
		int[] solved = {0};
		initRawSymPrunPacked( prunTableEdgCor4, 11, moveCorner4, conjCorner4, moveEdge4, hasSymEdgeSTAGE4, solved, null, 4);
	}

	public static final int prunDistEdgCor4 (int edge, int sym, int corner){
		
		int idx = edge*Constants.N_STAGE4_CORNERS+conjCorner4[corner][sym];
		int dist1 = get_dist_packed(prunTableEdgCor4, idx);
		int d;
		for( d=0; idx != 0; d++)
			for (int m = 0; m < Constants.N_STAGE4_MOVES; ++m) {
				int cornerx = moveCorner4[corner][m];
				int edgex = moveEdge4[edge][Symmetry.moveConjugateStage[m][sym]];
				int symx = Symmetry.symIdxMultiply[edgex&0xF][sym];
				edgex >>= 4;
				int idxx = edgex*Constants.N_STAGE4_CORNERS+conjCorner4[cornerx][symx];
				int dist2 = get_dist_packed(prunTableEdgCor4, idxx);
				if (((dist2+1) % 3) != dist1) continue;
				corner = cornerx;
				edge = edgex;
				sym = symx;
				dist1 = dist2;
				idx = idxx;
				break;
			}
		return d;
	}

	public static byte[] prunTableEdgCen5 = new byte[N_STAGE5_SYMEDGES*N_STAGE5_CENTERS/5+1];
	public static void initPrunEdgCen5(){
		int[] solved = {0};
		initRawSymPrunPacked( prunTableEdgCen5, 11, moveCenter5, conjCenter5, moveEdge5, hasSymEdgeSTAGE5, solved, null, 8);
	}

	public static final int prunDistEdgCen5 (int edge, int sym, int center){
		
		int idx = edge*Constants.N_STAGE5_CENTERS+conjCenter5[center][sym];
		int dist1 = get_dist_packed(prunTableEdgCen5, idx);
		int d;
		for( d=0; idx != 0; d++){
			for (int m = 0; m < Constants.N_STAGE5_MOVES; ++m) {
				int centerx = moveCenter5[center][m];
				int edgex = moveEdge5[edge][Symmetry.moveConjugateCo4Stage[m][sym]];
				int symx = Symmetry.symIdxCo4Multiply[sym][edgex&0xFF];
				edgex >>= 8;
				int idxx = edgex*Constants.N_STAGE5_CENTERS+conjCenter5[centerx][symx];
				int dist2 = get_dist_packed(prunTableEdgCen5, idxx);
				if (((dist2+1) % 3) != dist1) continue;
				center = centerx;
				edge = edgex;
				sym = symx;
				dist1 = dist2;
				idx = idxx;
				break;
			}
		}
		return d;
	}

	public static final int getPrun4 (final byte[] table, long idx){
		return (table[(int)(idx>>2)] >> ((idx & 0x3) << 1)) & 0x3;
	}

	public static final void setPrun4 (byte[] table, long idx, int value){
		table[(int)(idx>>2)] |= (byte)(value << ((idx & 0x3) << 1));
	}


	public static void initRawRawSymPrunPacked(byte[] prunTable, final int INV_DEPTH, 
			final short[][] raw1Move, final short[][] raw1Conj,
			final short[][] raw2Move, final short[][] raw2Conj,
			final int[][] symMove, final long[][] symState, 
			final int[] solvedStates, final int SYM_SHIFT) {

		final int SYM_MASK = (1 << SYM_SHIFT) - 1;
		final int N_RAW1 = raw1Move.length;
		final int N_RAW2 = raw2Move.length;
		final int N_RAWS = N_RAW1 * N_RAW2;
		final int N_SYM = symMove.length;
		final long N_SIZE = ((long) N_RAW1 ) * N_RAW2 * N_SYM;
		final int N_SIZE_PACKED = (int)(N_SIZE / 5 + 1);
		final int N_MOVES = symMove[0].length;
		final int N_COSYM = symState[0].length;

		long maxSym = 0;
		for (int i=0; i<N_SYM; i++)
			if(symState[i][0]>maxSym)
				maxSym = symState[i][0];
		int N_SSYM = 0;
		while(maxSym!=0){
			maxSym >>>= 1;
			N_SSYM++;
		}
		N_SSYM *= N_COSYM;
		System.out.println("Has "+N_SSYM+" symmetries");

		byte[] tempTable = new byte[(int)(N_SIZE/4)+1];
		for (int i=0; i<solvedStates.length; i++){
			setPrun4( tempTable, solvedStates[i], 3 );
		}
		int depth = 0;
		long done = solvedStates.length;
		while (done < N_SIZE) {
			boolean inv = depth > INV_DEPTH;
			int select = inv ? 0 : ((depth+2)%3)+1;
			int check = inv ? ((depth+2)%3)+1 : 0;
			int save = (depth % 3) + 1;
			depth++;
			double pos = 0;
			long unique = 0;
			for (long i=0; i<N_SIZE;) {
				int val = tempTable[(int)(i>>2)];
				if (!inv && val == 0) {
					i += 4;
					continue;
				}
				for (long end=Math.min(i+4, N_SIZE); i<end; i++, val>>=2) {
					if ((val & 0x3)/*getPrun4(tempTable, i)*/ != select) continue;
					int raw1 = (int)(i % N_RAW1);
					int raw2 = (int)((i / N_RAW1) % N_RAW2);
					int sym = (int)(i / N_RAWS);
					for (int m=0; m<N_MOVES; m++) {
						int symx = symMove[sym][m];
						int raw1x = raw1Conj[raw1Move[raw1][m]][symx & SYM_MASK];
						int raw2x = raw2Conj[raw2Move[raw2][m]][symx & SYM_MASK];
						symx >>>= SYM_SHIFT;
						long idx = ((long)symx * N_RAW2 + raw2x) * N_RAW1 + raw1x;
						if (getPrun4(tempTable, idx) != check) continue;
						done++;
						if (inv) {
							setPrun4(tempTable, i, save);
							break;
						} else {
							setPrun4(tempTable, idx, save);
							int nsym = 1;
							unique++;
							for (int j=0; j<N_COSYM; j++) {
								long symS = symState[symx][j];
								for (int k=0; symS != 0; symS>>=1, k++) {
									if ((symS & 0x1L) == 0) continue;
									long idxx = ((long)symx*N_RAW2+raw2Conj[raw2x][k*N_COSYM+j])*N_RAW1+raw1Conj[raw1x][k*N_COSYM+j];
									if( idxx == idx )
										nsym++;
									if (getPrun4(tempTable, idxx) == 0) {
										setPrun4(tempTable, idxx, save);
										done++;
									}
								}
							}
							//if( (N_SSYM/nsym)*nsym != N_SSYM) System.out.println(nsym);
							pos += ((double)N_SSYM)/nsym;
						}
					}
				}
			}
			System.out.println(String.format("%2d%12d%10d", depth, (int)pos, unique));
		}

		for (long i=0; i<N_SIZE_PACKED; i++) {
			int n = 1;
			int value = 0;
			for (int j=0; j<4; j++){
				value += n * (getPrun4(tempTable, (i<<2)+j) % 3);
				n *= 3;
			}
			if ( ((((long)N_SIZE_PACKED)<<2)+i) < N_SIZE)
				value += n * (getPrun4(tempTable, (((long)N_SIZE_PACKED)<<2)+i) % 3);
			prunTable[(int)i] = (byte)value;
		}
		tempTable = null;
		System.gc();
	}


	public static byte[] prunTable4;
	static {
		if( FULL_PRUNING_STAGE4 )
			prunTable4 = new byte[N_STAGE4_SYMEDGES*N_STAGE4_CENTERS*N_STAGE4_CORNERS/5+1];
	}

	public static void initPrun4(){
		int[] solved = new int[stage4_solved_centers_bm.length];
		for (int i=0; i < stage4_solved_centers_bm.length; i++)
			solved[i] = stage4_solved_centers_bm[i];
		initRawRawSymPrunPacked( prunTable4, 20, moveCenter4, conjCenter4, moveCorner4, conjCorner4, moveEdge4, hasSymEdgeSTAGE4, solved, 4);
	}

	public static final int prunDist4 (int edge, int sym, int corner, int center){
		
		long idx = (long)(edge*Constants.N_STAGE4_CORNERS+conjCorner4[corner][sym])*N_STAGE4_CENTERS+conjCenter4[center][sym];
		int dist1 = get_dist_packed(prunTable4, idx);
		int d = 0;
		boolean didMove = true;
		while( didMove ){
			didMove = false;
			for (int m = 0; m < Constants.N_STAGE4_MOVES; ++m) {
				int cornerx = moveCorner4[corner][m];
				int centerx = moveCenter4[center][m];
				int edgex = moveEdge4[edge][Symmetry.moveConjugateStage[m][sym]];
				int symx = Symmetry.symIdxMultiply[edgex&0xF][sym];
				edgex >>= 4;
				long idxx = (long)(edgex*Constants.N_STAGE4_CORNERS+conjCorner4[cornerx][symx])*N_STAGE4_CENTERS+conjCenter4[centerx][symx];
				int dist2 = get_dist_packed(prunTable4, idxx);
				if (((dist2+1) % 3) != dist1) continue;
				corner = cornerx;
				center = centerx;
				edge = edgex;
				sym = symx;
				dist1 = dist2;
				idx = idxx;
				didMove = true;
				d++;
				break;
			}
		}
		return d;
	}

	public static byte[] prunTable5;
	static {
		if( FULL_PRUNING_STAGE5 )
			prunTable5 = new byte[N_STAGE5_SYMEDGES*N_STAGE5_CENTERS*N_STAGE5_CORNERS/5+1];
	}

	public static void initPrun5(){
		int[] solved = {0};
		initRawRawSymPrunPacked( prunTable5, 30, moveCenter5, conjCenter5, moveCorner5, conjCorner5, moveEdge5, hasSymEdgeSTAGE5, solved, 8);
	}

}



