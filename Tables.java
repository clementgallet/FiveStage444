package cg.fivestage444;

import static cg.fivestage444.Constants.*;
import java.util.Arrays;

public final class Tables {

	public static final void init (){
		init4Of8();
		initMap96();
		initPerm420();
		initE16Bm();
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
	}

	/*** init_4of8 ***/
	public static final byte[] bm4of8_to_70 = new byte[256];
	public static final short[] bm4of8 = new short[70]; // (256). Was 'byte', now 'short' :(

	public static void init4Of8 (){
		int a1, a2, a3, a4;
		int i;
		int count = 0;
		for (a1 = 0; a1 < 8-3; ++a1) {
			for (a2 = a1+1; a2 < 8-2; ++a2) {
				for (a3 = a2+1; a3 < 8-1; ++a3) {
					for (a4 = a3+1; a4 < 8; ++a4) {
						bm4of8[count] = (short)((1 << a1) | (1 << a2) | (1 << a3) | (1 << a4));
						bm4of8_to_70[bm4of8[count]] = (byte)count;
						++count;
					}
				}
			}
		}
	}

	/*** init_parity_table ***/
	private static final boolean[] parity_perm8_table = new boolean[40320];

	private static final int get_parity8 (int x){
		int i, j;
		int parity = 0;
		byte[] t = new byte[8];
		perm_n_unpack (8, x, t, 0);
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
			perm_n_unpack (4, a1, t, 0); // TODO: Use nextPerm.
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

		for (u = 0; u < 40320; ++u) {
			perm_to_420[u] = 999;
		}
		for (u = 0; u < 70; ++u) {
			int bm = bm4of8[u];
			for (v = 0; v < 6; ++v) {
				perm_n_unpack (8, v, t, 0);
					for (w = 0; w < 96; ++w) {
					for (i = 0; i < 8; ++i) {
						t2[i] = map96[w][t[i]];
					}
					int f = 0;
					int b = 4;
					for (i = 0; i < 8; ++i) {
						if ((bm & (1 << i)) == 0) {
							t3[i] = t2[b++];
						} else {
							t3[i] = t2[f++];
						}
					}
					u2 = perm_n_pack (8, t3, 0);
					perm_to_420[u2] = (short)(6*u + v);
				}
			}
		}
	}

	/*** init stage 1 symEdgeToEdge ***/
	public static int[] sym2rawEdge1 = new int[N_STAGE1_SYMEDGES];
	public static long[] hasSymEdgeSTAGE1;

	public static void initSymEdgeToEdgeStage1 (){
		System.out.println( "Starting symEdgeToEdge stage 1..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();

		byte[] isRepTable = new byte[(N_STAGE1_EDGES>>3) + 1];
		hasSymEdgeSTAGE1 = new long[N_STAGE1_SYMEDGES];
		for (int u = 0; u < N_STAGE1_EDGES; ++u) {
			if( get_value_1bit(u, isRepTable) != 0 ) continue;
			cube1.convert_edges1_to_std_cube(u);
			for (int sym = 1; sym < N_SYM_STAGE1; ++sym) {
				cube1.rightMultEdges (Symmetry.invSymIdx[sym], cube2);
				int edge = cube2.convert_edges_to_stage1();
				set_1_1bit( edge, isRepTable); // not a rep.
				if( edge == u )
					hasSymEdgeSTAGE1[repIdx] |= (0x1L << sym);
			}
			sym2rawEdge1[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 1... generated "+repIdx+" reps." );
	}

	/*** init stage 1 symEdges ***/
	public static int[][] moveEdge1 = new int[N_STAGE1_SYMEDGES][N_STAGE1_MOVES]; // (15582*64) 15582*36 = 560952

	public static void initSymEdgeStage1 (){

		System.out.println( "Starting symEdge stage 1..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE1_SYMEDGES; ++u) {
			cube1.convert_edges1_to_std_cube( sym2rawEdge1[u] );
			for (int mc = 0; mc < N_STAGE1_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage1_slice_moves[mc]);
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
				if((stage1_slice_moves[mc]/3)%3 == 1 )
					continue;
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage1_slice_moves[mc]);
				moveCorner1[u][basic_to_face[mc]] = cube2.convert_corners_to_stage1();
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
				cube2.rotate_sliceEDGE (stage2_slice_moves[mc]);
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
	public static int[] hasSymCenterSTAGE2;

	public static void initSymCenterToCenterStage2 (){

		System.out.println( "Starting symCenterToCenter stage 2..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] isRepTable = new byte[(N_STAGE2_CENTER>>3) + 1];
		hasSymCenterSTAGE2 = new int[N_STAGE2_SYMCENTER];
		for (int u = 0; u < N_STAGE2_CENTER; ++u) {
			if( get_value_1bit(u, isRepTable) != 0 ) continue;
			cube1.convert_centers2_to_std_cube( u );

			for (int sym = 1; sym < N_SYM_STAGE2; ++sym) {
				cube1.rightMultCenters (Symmetry.invSymIdx[sym], cube2);
				short cen = cube2.convert_centers_to_stage2(5);
				set_1_1bit( cen, isRepTable); // not a rep.
				if( cen == u ){
					hasSymCenterSTAGE2[repIdx] |= (1 << sym);
				}
			}
			sym2rawCenter2[repIdx++] = (short)u;
		}
		System.out.println( "Finishing symCenterToCenter stage 2... generated "+repIdx+" reps." );
	}

	/*** init stage 2 symCenters ***/
	public static short[][] moveCenter2 = new short[N_STAGE2_SYMCENTER][N_STAGE2_MOVES]; // (716*16) 716*28

	public static void initSymCenterStage2 (){

		System.out.println( "Starting symCenter stage 2..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE2_SYMCENTER; ++u) {
			cube1.convert_centers2_to_std_cube( sym2rawCenter2[u] );
			for (int mc = 0; mc < N_STAGE2_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage2_slice_moves[mc]);
				moveCenter2[u][mc] = cube2.convert_symcenters_to_stage2(5);
			}
		}
		System.out.println( "Finishing symCenter stage 2..." );
	}

	/*** init_stage3 ***/
	public static final int[] e16bm2eloc = new int[256*256];
	public static final int[] eloc2e16bm = new int[N_STAGE3_EDGES];

	private static int POW2_16 = 256*256;

	public static void initE16Bm (){
		int a1, a2, a3, a4, a5, a6, a7, a8;

		int count = 0;
		for (a1 = 0; a1 < POW2_16; ++a1) {
			e16bm2eloc[a1] = 999999;
		}
		for (a1 = 0; a1 < 16-7; ++a1) {
		 for (a2 = a1 + 1; a2 < 16-6; ++a2) {
		  for (a3 = a2 + 1; a3 < 16-5; ++a3) {
		   for (a4 = a3 + 1; a4 < 16-4; ++a4) {
		    for (a5 = a4 + 1; a5 < 16-3; ++a5) {
		     for (a6 = a5 + 1; a6 < 16-2; ++a6) {
		      for (a7 = a6 + 1; a7 < 16-1; ++a7) {
		       for (a8 = a7 + 1; a8 < 16; ++a8) {
		        eloc2e16bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
		                            (1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
		        e16bm2eloc[eloc2e16bm[count]] = count++;
		       }
		      }
		     }
		    }
		   }
		  }
		 }
		}
	}

	/*** init stage 3 symCenterToCenter ***/
	public static int[] sym2rawCenter3 = new int[N_STAGE3_SYMCENTERS];
	public static int[] hasSymCenterSTAGE3;

	public static void initSymCenterToCenterStage3 (){

		System.out.println( "Starting symCenterToCenter stage 3..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] isRepTable = new byte[(N_STAGE3_CENTERS>>3) + 1];
		hasSymCenterSTAGE3 = new int[N_STAGE3_SYMCENTERS];
		for (int u = 0; u < N_STAGE3_CENTERS; ++u) {
			if( get_value_1bit(u, isRepTable) != 0 ) continue;
			cube1.convert_centers3_to_std_cube(u);
			for (int sym = 0; sym < N_SYM_STAGE3; ++sym) {
				for (int cosym = 0; cosym < 2; cosym++) {
					cube1.rightMultCenters(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultCenters(sym);
					int cen = cube2.convert_centers_to_stage3();
					set_1_1bit( cen, isRepTable); // not a rep.
					if( cen == u )
						hasSymCenterSTAGE3[repIdx] |= (1 << ( sym<<1+cosym ));
				}
			}
			sym2rawCenter3[repIdx++] = u;
		}
		System.out.println( "Finishing symCenterToCenter stage 3... generated "+repIdx+" reps." );
	}

	/*** init stage 3 symCenters ***/
	public static int[][] moveCenter3 = new int[N_STAGE3_SYMCENTERS][N_STAGE3_MOVES]; // (906640) 113330*20=9MB

	public static void initSymCenterStage3 (){

		System.out.println( "Starting symCenters stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_SYMCENTERS; ++u) {
			cube1.convert_centers3_to_std_cube(sym2rawCenter3[u]);
			for (int mc = 0; mc < N_STAGE3_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage3_slice_moves[mc]);
				moveCenter3[u][mc] = cube2.convert_symcenters_to_stage3();
			}
		}
		System.out.println( "Finishing symCenter stage 3..." );
	}

	/*** init stage 3 edges ***/
	public static short[][] moveEdge3 = new short[N_STAGE3_EDGES][N_STAGE3_MOVES]; // (12870) 12870*20

	public static void initEdgeStage3 (){

		System.out.println( "Starting edge stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_EDGES; ++u) {
			cube1.convert_edges3_to_std_cube(u);
			for (int mc = 0; mc < N_STAGE3_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (stage3_slice_moves[mc]);
				moveEdge3[u][mc] = cube2.convert_edges_to_stage3();
			}
		}
		System.out.println( "Finishing edge stage 3..." );
	}

	/*** init stage 3 edge conjugate ***/
	public static short[][] conjEdge3 = new short[N_STAGE3_EDGES][N_SYM_STAGE3*2];

	public static void initEdgeConjStage3 (){

		System.out.println( "Starting edge conjugate stage 3..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE3_EDGES; ++u) {
			cube1.convert_edges3_to_std_cube (u);
			for (int sym = 0; sym < N_SYM_STAGE3; ++sym) {
				for (int cosym = 0; cosym < 2; ++cosym) {
					cube1.rightMultEdges(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultEdges(sym);
					conjEdge3[u][(sym<<1)+cosym] = cube2.convert_edges_to_stage3();
				}
			}
		}
		System.out.println( "Finishing edge conjugate stage 3..." );
	}

	/*** init_stage4 ***/

	public static int[] sym2rawEdge4 = new int[N_STAGE4_SYMEDGES]; // 5968
	public static int[] hasSymEdgeSTAGE4;

	public static void initSymEdgeToEdgeStage4 (){
		System.out.println( "Starting symEdgeToEdge stage 4..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		byte[] t = new byte[8];

		byte[] isRepTable = new byte[((N_STAGE4_EDGES*2)>>3) + 1];
		hasSymEdgeSTAGE4 = new int[N_STAGE4_SYMEDGES];
		for (int u = 0; u < N_STAGE4_EDGES*2; ++u) { // *2 because you didn't take care of the parity.
			if( get_value_1bit(u, isRepTable) != 0 ) continue;
			cube1.convert_edges4_to_std_cube( u );

			/* Only retain configs without parity */
			int ul = perm_n_pack( 8, cube1.m_edge, 4 );
			for (int i=0; i<4; i++)
				t[i] = cube1.m_edge[i];
			for (int i=4; i<8; i++)
				t[i] = cube1.m_edge[i+8];
			int uh = perm_n_pack( 8, t, 0 );
			if( parity_perm8_table[ul] != parity_perm8_table[uh] ) continue; // getting rid of the parity.

			for (int sym = 0; sym < N_SYM_STAGE4; ++sym) {
				cube1.conjugateEdges (sym, cube2);
				int edge = cube2.convert_edges_to_stage4();
				set_1_1bit( edge, isRepTable); // not a rep.
				if( edge == u )
					hasSymEdgeSTAGE4[repIdx] |= (1 << sym);
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
				cube2.rotate_sliceEDGE (stage4_slice_moves[mc]);
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
				cube2.rotate_sliceCORNER (stage4_slice_moves[mc]);
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

	public static byte[][] moveCenter4 = new byte[N_STAGE4_CENTERS][N_STAGE4_MOVES]; // (70) 70*16.

	public static void initCenterStage4 (){

		System.out.println( "Starting center stage 4..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE4_CENTERS; ++u) {
			cube1.convert_centers4_to_std_cube (u);
			for (int mc = 0; mc < N_STAGE4_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage4_slice_moves[mc]);
				moveCenter4[u][mc] = cube2.convert_centers_to_stage4();
			}
		}
		System.out.println( "Finishing center stage 4..." );
	}

	public static byte[][] conjCenter4 = new byte[N_STAGE4_CENTERS][N_SYM_STAGE4]; // (70) 70*16.

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

	public static byte[][] moveCorner5 = new byte[N_STAGE5_CORNERS][N_STAGE5_MOVES]; // (96) 96*12

	public static void initSquaresMovemap (){

		System.out.println( "Starting corner stage 5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CORNERS; ++u) {
			cube1.convert_corners5_to_std_cube (u);
			for (int m = 0; m < N_STAGE5_MOVES; ++m) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (stage5_slice_moves[m]);
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

	public static short[][] moveCenter5 = new short[N_STAGE5_CENTERS][N_STAGE5_MOVES]; // TODO: (1728) 1728*12 = 20736

	public static void initCenterStage5 (){

		System.out.println( "Starting center stage5..." );
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_STAGE5_CENTERS; ++u) {
			cube1.convert_centers5_to_std_cube (u);
			for (int m = 0; m < N_STAGE5_MOVES; ++m) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (stage5_slice_moves[m]);
				moveCenter5[u][m]= cube2.convert_centers_to_stage5();
			}
		}
		System.out.println( "Finishing center stage5..." );
	}

	/*** init stage 5 symEdgeToEdge ***/
	public static int[] sym2rawEdge5 = new int[N_STAGE5_SYMEDGES];
	public static long[][] hasSymEdgeSTAGE5;

	public static void initSymEdgeToEdgeStage5 (){

		System.out.println( "Starting symEdgeToEdge stage 5..." );
		int repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();

		byte[] isRepTable = new byte[N_STAGE5_EDGES >> 3];
		hasSymEdgeSTAGE5 = new long[N_STAGE5_SYMEDGES][4];

		for (int u = 0; u < N_STAGE5_EDGES; ++u) {
			if( get_value_1bit( u, isRepTable ) != 0 ) continue;
			cube1.convert_edges5_to_std_cube (u);

			for (int sym = 0; sym < N_SYM_STAGE5; ++sym) {
				for (int cosym = 0; cosym < 4; ++cosym) {
					cube1.rightMultEdges(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultEdges(sym);
					int edge = cube2.convert_edges_to_stage5 ();
					set_1_1bit( edge, isRepTable ); // not a rep.
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
				cube2.rotate_sliceEDGE (stage5_slice_moves[mc]);
				moveEdge5[u][mc] = cube2.convert_symedges_to_stage5();
			}
		}
		System.out.println( "Finishing symEdge stage 5..." );
	}

	/*** init stage 5 corner conjugate ***/
	public static byte[][] conjCorner5 = new byte[N_STAGE5_CORNERS][N_SYM_STAGE5*4]; // (96) 96*48

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
}

