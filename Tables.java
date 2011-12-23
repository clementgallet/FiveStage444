package fivestage444;

import java.lang.Thread;

public final class Tables {

	public final void init_all (){
		threadInit4Of8.start();
		threadInitParityTable.start();
		threadInitEbmEloc.start();
		threadInitMap96.start();
		threadInitBm12.start();
		threadInitCloc.start();
		threadInitPerm420.start();
		threadInitSymEdgeToEdgeStage1.start();
		threadInitSymEdgeStage1.start();
		threadInitCornerStage1.start();
		threadInitCornerConjStage1.start();
		threadInitCenterStage2.start();
		threadInitEdgeStage2.start();
		threadInitE16Bm.start();
		threadInitCenterStage3.start();
		threadInitSymCenterToCenterStage3.start();
		threadInitSymCenterStage3.start();
		threadInitEdgeStage3.start();
		threadInitEdgeConjStage3.start();
		threadInitEdgeBStage4.start();
		threadInitEdgeAStage4.start();
		threadInitEdgeRepStage4.start();
		threadInitSymEdgeStage4.start();
		threadInitCornerStage4.start();
		threadInitCornerConjStage4.start();
		threadInitCenterStage4.start();
		threadInitCenterConjStage4.start();
		threadInitSquares2nd.start();
		threadInitSquaresMovemap.start();
		threadInitSquaresCenterMap.start();
		threadInitSymEdgeToEdgeStage5.start();
		threadInitSymEdgeStage5.start();
		threadInitCornerConjStage5.start();
		threadInitCenterConjStage5.start();

		waitAllThreads();
	}

	public final void waitAllThreads (){
		try{
		threadInit4Of8.join();
		threadInitParityTable.join();
		threadInitEbmEloc.join();
		threadInitMap96.join();
		threadInitBm12.join();
		threadInitCloc.join();
		threadInitPerm420.join();
		threadInitSymEdgeToEdgeStage1.join();
		threadInitSymEdgeStage1.join();
		threadInitCornerStage1.join();
		threadInitCornerConjStage1.join();
		threadInitCenterStage2.join();
		threadInitEdgeStage2.join();
		threadInitE16Bm.join();
		threadInitCenterStage3.join();
		threadInitSymCenterToCenterStage3.join();
		threadInitSymCenterStage3.join();
		threadInitEdgeStage3.join();
		threadInitEdgeConjStage3.join();
		threadInitEdgeBStage4.join();
		threadInitEdgeAStage4.join();
		threadInitEdgeRepStage4.join();
		threadInitSymEdgeStage4.join();
		threadInitCornerStage4.join();
		threadInitCornerConjStage4.join();
		threadInitCenterStage4.join();
		threadInitCenterConjStage4.join();
		threadInitSquares2nd.join();
		threadInitSquaresMovemap.join();
		threadInitSquaresCenterMap.join();
		threadInitSymEdgeToEdgeStage5.join();
		threadInitSymEdgeStage5.join();
		threadInitCornerConjStage5.join();
		threadInitCenterConjStage5.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}

	/*** init_4of8 ***/
	public static final byte[] bm4of8_to_70 = new byte[256];
	public static final short[] bm4of8 = new short[70]; // (256). Was 'byte', now 'short' :(

	private class Init4Of8 extends Thread {

	public void run (){
		System.out.println( "Starting init4of8..." );
		int a1, a2, a3, a4;
		int i;
		int count = 0;
		for (i = 0; i < 256; ++i) {
			bm4of8_to_70[i] = 99;
		}
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
		System.out.println( "Finishing init4of8..." );
	}

	}

	private Init4Of8 threadInit4Of8 = new Init4Of8();

	/*** init_parity_table ***/
	private static final boolean[] parity_perm8_table = new boolean[40320];

	private static final int get_parity8 (int x){
		int i, j;
		int parity = 0;
		byte[] t = new byte[8];
		Constants.perm_n_unpack (8, x, t, 0);
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

	private class InitParityTable extends Thread {

	public void run (){
		System.out.println( "Starting parity table..." );
		int x;

		for (x = 0; x < 40320; ++x) {
			parity_perm8_table[x] = (get_parity8 (x) != 0);
		}
		System.out.println( "Finishing parity table..." );
	}
	}

	private InitParityTable threadInitParityTable = new InitParityTable();

	/*** init_eloc ***/
	public static final int[] ebm2eloc = new int[4096*4096];
	public static final int[] eloc2ebm = new int[Constants.N_EDGE_COMBO8];

	private static int POW2_24 = 4096*4096;

	private class InitEbmEloc extends Thread {

	public void run (){
		System.out.println( "Starting ebm eloc..." );
		int a1, a2, a3, a4, a5, a6, a7, a8;
		int count = 0;
		for (a1 = 0; a1 < POW2_24; ++a1) {
			ebm2eloc[a1] = 999999;
		}
		for (a1 = 0; a1 < 24-7; ++a1) {
		 for (a2 = a1 + 1; a2 < 24-6; ++a2) {
		  for (a3 = a2 + 1; a3 < 24-5; ++a3) {
		   for (a4 = a3 + 1; a4 < 24-4; ++a4) {
		    for (a5 = a4 + 1; a5 < 24-3; ++a5) {
		     for (a6 = a5 + 1; a6 < 24-2; ++a6) {
		      for (a7 = a6 + 1; a7 < 24-1; ++a7) {
		       for (a8 = a7 + 1; a8 < 24; ++a8) {
		        eloc2ebm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4) |
		                          (1 << a5) | (1 << a6) | (1 << a7) | (1 << a8);
		        ebm2eloc[eloc2ebm[count]] = count++;
		       }
		      }
		     }
		    }
		   }
		  }
		 }
		}
		System.out.println( "Finishing ebm eloc..." );
	}
	}

	private InitEbmEloc threadInitEbmEloc = new InitEbmEloc();

	/*** init map96 ***/
	private static final byte[][] map96 = new byte[96][8];

	private class InitMap96 extends Thread {

	public void run (){
		System.out.println( "Starting map 96..." );
		int a1, i;
		byte[] t = new byte[8];
		byte f;
		for (a1 = 0; a1 < 24; ++a1) {
			Constants.perm_n_unpack (4, a1, t, 0); // TODO: Use nextPerm.
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
		System.out.println( "Finishing map 96..." );
	}
	}

	private InitMap96 threadInitMap96 = new InitMap96();

	/*** init bm12 ***/
	private static final int[][] bm12_4of8_to_high_idx = new int[4096][70];
	private static final int[][] bm12_4of8_to_low_idx = new int[4096][70];

	private class InitBm12 extends Thread {

	public void run (){

		try{
			threadInit4Of8.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting bm 12..." );

		int a1, a2;
		int i;
		short u;
		byte[] t = new byte[8];
		// (old) optimization in progress...
		for (u = 0; u < 4096; ++u) {
			int u1;
			for (u1 = 0; u1 < 70; ++u1) {
				int u12 = u;
				int bbbb = bm4of8[u1];
				int j1 = 0;
				int j2;
				for (i = 0; u12 != 0; ++i) {
					if ((u12 & 0x1) != 0) {
						if ((bbbb & 0x1) == 0x1) {
							t[j1++] = (byte)i;
						}
						bbbb >>= 1;
					}
					u12 >>= 1;
				}
				a1 = 0;
				a2 = 24*24*24;
				for (j2 = 0; j2 < j1; ++j2) {
					a1 += a2*t[j2];
					a2 /= 24;
				}
				bm12_4of8_to_low_idx[u][u1] = a1;
				u12 = u;
				bbbb = bm4of8[u1];
				j1 = 0;
				for (i = 24 - 1; u12 != 0; --i) {
					if ((u12 & 0x800) != 0) {
						if ((bbbb & 0x80) != 0) {
							t[j1++] = (byte)i;
						}
						bbbb <<= 1;
					}
					u12 <<= 1;
					u12 &= 0xFFF;		//need this to become 0 after no more than 12 iterations
				}
				a1 = 0;
				a2 = 1;
				for (j2 = 0; j2 < j1; ++j2) {
					a1 += a2*t[j2];
					a2 *= 24;
				}
				bm12_4of8_to_high_idx[u][u1] = a1;
			}
		}
		System.out.println( "Finishing bm 12..." );
	}
	}

	private InitBm12 threadInitBm12 = new InitBm12();

	public static final int swapbits (int x, int b){
		int x2 = x & b;
		if (x2 == 0 || x2 == b) {
			return x;
		}
		return x ^ b;
	}


	/*** init_cloc ***/
	public static final int[] c4_to_cloc = new int[24*24*24*24];
	public static final int[] cloc_to_bm = new int[Constants.N_CENTER_COMBO4];

	private class InitCloc extends Thread {

	public void run (){
		System.out.println( "Starting cloc..." );
		int a1, a2, a3, a4; //, a5, a6, a7, a8;
		int count = 0;

		count = 0;
		for (a1 = 0; a1 < 24-3; ++a1) {
		 for (a2 = a1 + 1; a2 < 24-2; ++a2) {
		  for (a3 = a2 + 1; a3 < 24-1; ++a3) {
		   for (a4 = a3 + 1; a4 < 24; ++a4) {
			cloc_to_bm[count] = (1 << a1) | (1 << a2) | (1 << a3) | (1 << a4);
			c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a3 + a4] = count;
			c4_to_cloc[24*24*24*a1 + 24*24*a2 + 24*a4 + a3] = count;
			c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a2 + a4] = count;
			c4_to_cloc[24*24*24*a1 + 24*24*a3 + 24*a4 + a2] = count;
			c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a2 + a3] = count;
			c4_to_cloc[24*24*24*a1 + 24*24*a4 + 24*a3 + a2] = count;

			c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a3 + a4] = count;
			c4_to_cloc[24*24*24*a2 + 24*24*a1 + 24*a4 + a3] = count;
			c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a1 + a4] = count;
			c4_to_cloc[24*24*24*a2 + 24*24*a3 + 24*a4 + a1] = count;
			c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a1 + a3] = count;
			c4_to_cloc[24*24*24*a2 + 24*24*a4 + 24*a3 + a1] = count;

			c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a2 + a4] = count;
			c4_to_cloc[24*24*24*a3 + 24*24*a1 + 24*a4 + a2] = count;
			c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a1 + a4] = count;
			c4_to_cloc[24*24*24*a3 + 24*24*a2 + 24*a4 + a1] = count;
			c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a1 + a2] = count;
			c4_to_cloc[24*24*24*a3 + 24*24*a4 + 24*a2 + a1] = count;

			c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a2 + a3] = count;
			c4_to_cloc[24*24*24*a4 + 24*24*a1 + 24*a3 + a2] = count;
			c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a1 + a3] = count;
			c4_to_cloc[24*24*24*a4 + 24*24*a2 + 24*a3 + a1] = count;
			c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a1 + a2] = count;
			c4_to_cloc[24*24*24*a4 + 24*24*a3 + 24*a2 + a1] = count;
			++count;
		   }
		  }
		 }
		}
		System.out.println( "Finishing cloc..." );
	}
	}

	private InitCloc threadInitCloc = new InitCloc();

	/*** init_perm_to_420 ***/
	public static final short[] perm_to_420 = new short[40320]; // (420)

	private class InitPerm420 extends Thread {

	public void run (){

		try{
		threadInit4Of8.join();
		threadInitMap96.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting perm 420..." );
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
				Constants.perm_n_unpack (8, v, t, 0);
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
					u2 = Constants.perm_n_pack (8, t3, 0);
					perm_to_420[u2] = (short)(6*u + v);
				}
			}
		}
		System.out.println( "Finshing perm 420..." );
	}
	}

	private InitPerm420 threadInitPerm420 = new InitPerm420();

	/*** init stage 1 symEdgeToEdge ***/
	public static final int[] symEdgeToEdgeSTAGE1 = new int[Constants.N_SYMEDGE_COMBO8];
	public static final int[] hasSymEdgeSTAGE1 = new int[Constants.N_SYMEDGE_COMBO8];

	private class InitSymEdgeToEdgeStage1 extends Thread {

	public void run (){

		try{
		threadInitEbmEloc.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting symEdgeToEdge stage 1..." );
		int i, sym;
		byte lrfb, ud;
		int u, repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();

		byte[] isRepTable = new byte[(Constants.N_EDGE_COMBO8>>3) + 1];
		for (u = 0; u < Constants.N_EDGE_COMBO8; ++u) {
			if( Constants.get_value_1bit(u, isRepTable) != 0 ) continue;
			int ebm = eloc2ebm[u];
			lrfb = 0;
			ud = 16;
			for (i = 0; i < 24; ++i) {
				if ((ebm & (1 << i)) == 0) {
					cube1.m_edge[i] = lrfb++;
				} else {
					cube1.m_edge[i] = ud++;
				}
			}

			for (sym = 1; sym < Constants.N_SYM_STAGE1; ++sym) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.conjugateEdges (sym);
				ebm = 0;
				for (i = 0; i < 24; ++i) {
					if (cube2.m_edge[i] >= 16) {
						ebm |= (1 << i);
					}
				}
				int edge = ebm2eloc[ebm];
				Constants.set_1_1bit( edge, isRepTable); // not a rep.
				if( edge == u )
					hasSymEdgeSTAGE1[repIdx] |= (1 << sym);
			}
			symEdgeToEdgeSTAGE1[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 1... generated "+repIdx+" reps." );
	}
	}

	private InitSymEdgeToEdgeStage1 threadInitSymEdgeToEdgeStage1 = new InitSymEdgeToEdgeStage1();

	/*** init stage 1 symEdges ***/
	public static final int[][] move_table_symEdgeSTAGE1 = new int[Constants.N_SYMEDGE_COMBO8][Constants.N_BASIC_MOVES]; // (46371) 46371*36 = 6677424

	private class InitSymEdgeStage1 extends Thread {

	public void run (){

		try{
		threadInitEbmEloc.join();
		threadInitSymEdgeToEdgeStage1.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting symEdge stage 1..." );
		int i, mc;
		byte lrfb, ud;
		int u, edge;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();
		cube1.init ();
		cube2.init ();
		cube3.init ();
		for (u = 0; u < Constants.N_SYMEDGE_COMBO8; ++u) {
			int ebm = eloc2ebm[symEdgeToEdgeSTAGE1[u]];
			lrfb = 0;
			ud = 16;
			for (i = 0; i < 24; ++i) {
				if ((ebm & (1 << i)) == 0) {
					cube1.m_edge[i] = lrfb++;
				} else {
					cube1.m_edge[i] = ud++;
				}
			}

			for (mc = 0; mc < Constants.N_BASIC_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (mc);
				int minEdge = 99999999;
				int minSym = 0;
				for (int sym=0; sym < Constants.N_SYM_STAGE1; sym++ ){
					System.arraycopy(cube2.m_edge, 0, cube3.m_edge, 0, 24);
					cube3.conjugateEdges(sym);
					ebm = 0;
					for (i = 0; i < 24; ++i) {
						if (cube3.m_edge[i] >= 16) {
							ebm |= (1 << i);
						}
					}
					if( ebm2eloc[ebm] < minEdge){
						minEdge = ebm2eloc[ebm];
						minSym = sym;
					}
				}

				move_table_symEdgeSTAGE1[u][mc] = Symmetry.getRep(symEdgeToEdgeSTAGE1, minEdge)*Constants.N_SYM_STAGE1 + minSym;
			}
		}
		System.out.println( "Finishing symEdge stage 1..." );
	}
	}

	private InitSymEdgeStage1 threadInitSymEdgeStage1 = new InitSymEdgeStage1();

	/*** init stage 1 corners ***/
	public static final short[][] move_table_co = new short[Constants.N_CORNER_ORIENT][Constants.N_FACE_MOVES]; // (2187) 2187*18

	private class InitCornerStage1 extends Thread {

	public void run (){

		try{
		threadInitEbmEloc.join();
		threadInit4Of8.join();
		threadInitPerm420.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting corner stage 1..." );
		int i, mc;
		int u;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeStage1 s1 = new CubeStage1();
		CubeStage1 s2 = new CubeStage1();
		s1.init ();
		s2.init ();
		for (u = 0; u < Constants.N_CORNER_ORIENT; ++u) {
			s1.m_co = (short)u;
			s1.convert_to_std_cube (cube1);
			for (mc = 0; mc < Constants.N_BASIC_MOVES; ++mc) {
				int fmc = Constants.basic_to_face[mc];
				if (fmc >= 0) {
					System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
					cube2.rotate_sliceCORNER (mc);
					cube2.convert_to_stage1 (s2);
					move_table_co[u][fmc] = s2.m_co;
				}
			}
		}
		System.out.println( "Finishing corner stage 1..." );
	}
	}

	private InitCornerStage1 threadInitCornerStage1 = new InitCornerStage1();

	/*** init stage 1 corner conjugate ***/
	public static final short[][] move_table_co_conj = new short[Constants.N_CORNER_ORIENT][Constants.N_SYM_STAGE1]; // (2187) 2187*48

	private class InitCornerConjStage1 extends Thread {

	public void run (){

		try{
		threadInitEbmEloc.join();
		threadInit4Of8.join();
		threadInitPerm420.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting corner conjugate stage 1..." );
		int i, sym;
		int u;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeStage1 s1 = new CubeStage1();
		CubeStage1 s2 = new CubeStage1();
		s1.init ();
		s2.init ();
		for (u = 0; u < Constants.N_CORNER_ORIENT; ++u) {
			s1.m_co = (short)u;
			s1.convert_to_std_cube (cube1);
			for (sym = 0; sym < Constants.N_SYM_STAGE1; ++sym) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.conjugate (sym);
				cube2.convert_to_stage1 (s2);
				move_table_co_conj[u][sym] = s2.m_co;
			}
		}
		System.out.println( "Finishing corner conjugate stage 1..." );
	}
	}

	private InitCornerConjStage1 threadInitCornerConjStage1 = new InitCornerConjStage1();

	/*** init stage 2 centers ***/
	public static final short[][] move_table_cenSTAGE2 = new short[Constants.N_CENTER_COMBO4][Constants.N_STAGE2_SLICE_MOVES]; // 10626*28

	private class InitCenterStage2 extends Thread {

	public void run (){

		try{
		threadInitCloc.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting center stage 2..." );
		int i, j;
		int u;
		byte[] t = new byte[8];
		int mc, udlrf;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		for (u = 0; u < Constants.N_CENTER_COMBO4; ++u) {
			int cbm = cloc_to_bm[u];
			udlrf = 0;
			for (i = 0; i < 24; ++i) {
				if ((cbm & (1 << i)) == 0) {
					cube1.m_cen[i] = (byte)(udlrf++/4);
				} else {
					cube1.m_cen[i] = 5;
				}
			}
			for (mc = 0; mc < Constants.N_STAGE2_SLICE_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceCENTER (Constants.stage2_slice_moves[mc]);
				j = 0;
				for (i = 0; i < 24; ++i) {
					if (cube2.m_cen[i] == 5) {
						t[j++] = (byte)i;
					}
				}
				int idx = 24*24*24*t[0] + 24*24*t[1] + 24*t[2] + t[3];
				move_table_cenSTAGE2[u][mc] = (short)c4_to_cloc[idx];
			}
		}
		System.out.println( "Finishing center stage 2..." );
	}
	}

	private InitCenterStage2 threadInitCenterStage2 = new InitCenterStage2();

	/*** init stage 2 edges ***/
	public static final short[][] move_table_edgeSTAGE2 = new short[Constants.N_STAGE2_EDGE_CONFIGS][Constants.N_STAGE2_SLICE_MOVES]; // 420*28

	private class InitEdgeStage2 extends Thread {

	public void run (){

		try{
		threadInitEbmEloc.join();
		threadInit4Of8.join();
		threadInitPerm420.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting edge stage 2..." );
		short u;
		int mc;
		CubeStage2 s1 = new CubeStage2();
		for (u = 0; u < Constants.N_STAGE2_EDGE_CONFIGS; ++u) {
			for (mc = 0; mc < Constants.N_STAGE2_SLICE_MOVES; ++mc) {
				s1.m_edge = u;
				s1.do_move_slow (mc);
				move_table_edgeSTAGE2[u][mc] = s1.m_edge;
			}
		}
		System.out.println( "Finishing edge stage 2..." );
	}
	}

	private InitEdgeStage2 threadInitEdgeStage2 = new InitEdgeStage2();

	public static final int stage2_cen_to_cloc4sf (int cen){
		int cenbm = eloc2ebm[cen / 70];
		int idx1 = bm12_4of8_to_high_idx[cenbm >> 12][cen % 70];
		idx1 += bm12_4of8_to_low_idx[cenbm & 0xFFF][cen % 70];
		return c4_to_cloc[idx1];
	}

	public static final int stage2_cen_to_cloc4sb (int cen){
		int cenbm = eloc2ebm[cen / 70];
		int cenbm4of8 = bm4of8[cen % 70];
		int comp_70 = bm4of8_to_70[(~cenbm4of8) & 0xFF];	// (old) could be a direct lookup
		int idx2 = bm12_4of8_to_high_idx[cenbm >> 12][comp_70];
		idx2 += bm12_4of8_to_low_idx[cenbm & 0xFFF][comp_70];
		return c4_to_cloc[idx2];
	}

	/*** init_stage3 ***/
	public static final int[] e16bm2eloc = new int[256*256];
	public static final int[] eloc2e16bm = new int[Constants.N_COMBO_16_8];

	private static int POW2_16 = 256*256;

	private class InitE16Bm extends Thread {

	public void run (){
		System.out.println( "Starting e16bm..." );
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
		System.out.println( "Finishing e16bm..." );
	}
	}

	private InitE16Bm threadInitE16Bm = new InitE16Bm();

	public static final int[][] move_table_cenSTAGE3 = new int[Constants.N_STAGE3_CENTER_CONFIGS][Constants.N_STAGE3_SLICE_MOVES]; // 900900*20 = 72MB

	private class InitCenterStage3 extends Thread {

	public void run (){

		try{
		threadInitE16Bm.join();
		threadInit4Of8.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting center stage 3..." );
		int mc;
		int u;
		CubeStage3 s3 = new CubeStage3();
		CubeState cube1 = new CubeState();
		s3.init ();
		cube1.init ();
		for (u = 0; u < Constants.N_STAGE3_CENTER_CONFIGS; ++u) {
			for (mc = 0; mc < Constants.N_STAGE3_SLICE_MOVES; ++mc) {
				s3.m_centerLR = u;
				s3.convert_centers_to_std_cube(cube1);
				cube1.rotate_sliceCENTER(Constants.stage3_slice_moves[mc]);
				cube1.convert_centers_to_stage3 (s3);
				move_table_cenSTAGE3[u][mc] = s3.m_centerLR;
			}
		}
		System.out.println( "Finishing center stage 3..." );
	}
	}

	private InitCenterStage3 threadInitCenterStage3 = new InitCenterStage3();

	/*** init stage 3 symCenterToCenter ***/
	public static final int[] symCenterToCenterSTAGE3 = new int[Constants.N_STAGE3_SYMCENTER_CONFIGS];
	public static final int[] hasSymCenterSTAGE3 = new int[Constants.N_STAGE3_SYMCENTER_CONFIGS]; // Could use less than int. Problem with (byte)1 << 7 and sign...

	private class InitSymCenterToCenterStage3 extends Thread {

	public void run (){

		try{
		threadInitE16Bm.join();
		threadInit4Of8.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting symCenterToCenter stage 3..." );
		int i, sym;
		int u, repIdx = 0;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeStage3 s3 = new CubeStage3();

		byte[] isRepTable = new byte[(Constants.N_STAGE3_CENTER_CONFIGS>>3) + 1];
		for (u = 0; u < Constants.N_STAGE3_CENTER_CONFIGS; ++u) {
			if( Constants.get_value_1bit(u, isRepTable) != 0 ) continue;

			s3.m_centerLR = u;
			s3.convert_centers_to_std_cube(cube1);

			for (sym = 1; sym < Constants.N_SYM_STAGE3; ++sym) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.conjugateCenters (sym);
				cube2.convert_centers_to_stage3 (s3);
				Constants.set_1_1bit( s3.m_centerLR, isRepTable); // not a rep.
				if( s3.m_centerLR == u )
					hasSymCenterSTAGE3[repIdx] |= (1 << sym);
			}
			symCenterToCenterSTAGE3[repIdx++] = u;
		}
		System.out.println( "Finishing symCenterToCenter stage 3... generated "+repIdx+" reps." );
	}
	}

	private InitSymCenterToCenterStage3 threadInitSymCenterToCenterStage3 = new InitSymCenterToCenterStage3();

	/*** init stage 3 symCenters ***/
	public static final int[][] move_table_symCenterSTAGE3 = new int[Constants.N_STAGE3_SYMCENTER_CONFIGS][Constants.N_STAGE3_SLICE_MOVES]; // TODO...

	private class InitSymCenterStage3 extends Thread {

	public void run (){

		try{
		threadInitSymCenterToCenterStage3.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting symCenters stage 3..." );
		int i, mc;
		byte lrfb, ud;
		int u, edge;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();
		cube1.init ();
		cube2.init ();
		cube3.init ();
		CubeStage3 s3 = new CubeStage3();
		for (u = 0; u < Constants.N_STAGE3_SYMCENTER_CONFIGS; ++u) {
			s3.m_centerLR = symCenterToCenterSTAGE3[u];
			s3.convert_centers_to_std_cube(cube1);

			for (mc = 0; mc < Constants.N_STAGE3_SLICE_MOVES; ++mc) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.rotate_sliceEDGE (Constants.stage3_slice_moves[mc]);
				int minCen = 99999999;
				int minSym = 0;
				for (int sym=0; sym < Constants.N_SYM_STAGE3; sym++ ){
					System.arraycopy(cube2.m_edge, 0, cube3.m_edge, 0, 24);
					cube3.conjugateCenters(sym);
					cube3.convert_centers_to_stage3 (s3);
					if( s3.m_centerLR < minCen){
						minCen = s3.m_centerLR;
						minSym = sym;
					}
				}

				move_table_symCenterSTAGE3[u][mc] = Symmetry.getRep(symCenterToCenterSTAGE3, minCen)*Constants.N_SYM_STAGE3 + minSym;
			}
		}
		System.out.println( "Finishing symCenter stage 3..." );
	}
	}

	private InitSymCenterStage3 threadInitSymCenterStage3 = new InitSymCenterStage3();

	/*** init stage 3 edges ***/
	public static final short[][] move_table_edgeSTAGE3 = new short[Constants.N_STAGE3_EDGE_CONFIGS][Constants.N_STAGE3_SLICE_MOVES]; // (12870) 12870*20

	private class InitEdgeStage3 extends Thread {

	public void run (){

		try{
		threadInitE16Bm.join();
		threadInit4Of8.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting edge stage 3..." );
		int mc;
		int u;
		CubeStage3 s3 = new CubeStage3();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (u = 0; u < Constants.N_STAGE3_EDGE_CONFIGS; ++u) {
			s3.m_edge = (short)u;
			s3.convert_to_std_cube(cube1);
			for (mc = 0; mc < Constants.N_STAGE3_SLICE_MOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.do_move (Constants.stage3_slice_moves[mc]);
				cube2.convert_edges_to_stage3 (s3);
				move_table_edgeSTAGE3[u][mc] = s3.m_edge;
			}
		}
		System.out.println( "Finishing edge stage 3..." );
	}
	}

	private InitEdgeStage3 threadInitEdgeStage3 = new InitEdgeStage3();

	/*** init stage 3 edge conjugate ***/
	public static final short[][] move_table_edge_conjSTAGE3 = new short[Constants.N_STAGE3_EDGE_CONFIGS][Constants.N_SYM_STAGE3]; // (2187) 2187*48

	private class InitEdgeConjStage3 extends Thread {

	public void run (){

		try{
		threadInitE16Bm.join();
		threadInit4Of8.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting edge conjugate stage 3..." );
		int i, sym;
		int u;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeStage3 s1 = new CubeStage3();
		CubeStage3 s2 = new CubeStage3();
		s1.init ();
		s2.init ();
		for (u = 0; u < Constants.N_STAGE3_EDGE_CONFIGS; ++u) {
			s1.m_edge = (short)u;
			s1.convert_edges_to_std_cube (cube1);
			for (sym = 0; sym < Constants.N_SYM_STAGE3; ++sym) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.conjugate (sym);
				cube2.convert_edges_to_stage3 (s2);
				move_table_edge_conjSTAGE3[u][sym] = s2.m_edge;
			}
		}
		System.out.println( "Finishing edge conjugate stage 3..." );
	}
	}

	private InitEdgeConjStage3 threadInitEdgeConjStage3 = new InitEdgeConjStage3();


	/*** init_stage4 ***/
	public static final int sqs_rep_to_perm[][] = {
		{  0,  7, 16, 23 },
		{  1,  6, 17, 22 },
		{  2, 10, 13, 21 },
		{  3, 11, 12, 20 },
		{  4,  8, 15, 19 },
		{  5,  9, 14, 18 }
	};

	private static final void array8_to_set_a (byte[] t, CubeState result_cube){
		int i;
		int j = 0;
		for (i = 0; i < 8; ++i) {
			if (i >= 4) {
				j = i + 8;
			} else {
				j = i;
			}
			byte t1 = t[i];
			if (t1 >= 4) {
				t1 += 8;
			}
			result_cube.m_edge[j] = t1;
		}
	}

	private static final void array8_to_set_b (byte[] t, CubeState result_cube){
		int i;
		for (i = 0; i < 8; ++i) {
			result_cube.m_edge[4 + i] = (byte)(t[i] + 4);
		}
	}

	public static final void lrfb_to_cube_state (int u, CubeState result_cube){
		byte[] t = new byte[8];
		result_cube.init ();
		Constants.perm_n_unpack (8, u % 40320, t, 0);
		array8_to_set_a (t, result_cube);
		Constants.perm_n_unpack (8, u / 40320, t, 0);
		array8_to_set_b (t, result_cube);
	}

	
	private static final int[] stage4_edge_hB = new int[40320]; // (40320 ?). Change from short to int then :(
	private static final int[] stage4_edge_hgB = new int[40320]; // (40320 ?). Change from short to int then :(

	private class InitEdgeBStage4 extends Thread {

	public void run (){
		System.out.println( "Starting edge B stage 4..." );
		byte i;
		int u;
		CubeState cs1 = new CubeState();
		CubeState cs2 = new CubeState();
		CubeState cs3 = new CubeState();
		cs1.init ();
		cs2.init ();
		for (i = 0; i < 16; ++i ){
			cs2.m_edge[i] = i;
		}
		for (u = 0; u < 40320; ++u) {
			// lrfb_to_cube_state (40320*u, cs2); // Replaced by a nextPerm
			int rep = 999;
			int reph = 65000;
			int Blr, Bfb;

			for (i = 4; i < 8; ++i) {
				cs1.m_edge[i] = i;
			}

			for (Blr = 0; Blr < 24; ++Blr) {

				for (i = 8; i < 12; ++i) {
					cs1.m_edge[i] = i;
				}

				for (Bfb = 0; Bfb < 24; ++Bfb) {
					cs3.compose_edge (cs1, cs2);
					int u3h = cs3.cube_state_to_lrfb_h ();
					if (u3h < reph) {
						reph = u3h;
						rep = 24 * Blr + Bfb;
					}
					Constants.nextPerm( cs1.m_edge, 4, 8 );
				}
				Constants.nextPerm( cs1.m_edge, 4, 4 );
			}
			stage4_edge_hgB[u] = reph;
			int repBlr = sqs_perm_to_rep[rep/24];
			int repBfb = sqs_perm_to_rep[rep%24];
			stage4_edge_hB[u] = 6*repBlr + repBfb;
			Constants.nextPerm( cs2.m_edge, 8, 4 );
		}
		System.out.println( "Finishing edge B stage 4..." );
	}
	}

	private InitEdgeBStage4 threadInitEdgeBStage4 = new InitEdgeBStage4();

	private static final int[][] stage4_edge_hgA = new int[40320][36]; // (40320 ?). Change from short to int then :(

	private class InitEdgeAStage4 extends Thread {

	public void run (){
		System.out.println( "Starting edge A stage 4..." );
		int i;
		int u, h1, h2;
		CubeState cs1 = new CubeState();
		CubeState cs2 = new CubeState();
		CubeState cs3 = new CubeState();
		cs1.init ();
		cs2.init ();
		for (u = 0; u < 40320; ++u) {
			lrfb_to_cube_state (u, cs2);
			for (h1 = 0; h1 < 36; ++h1) {
				int repl = 65000;
				int replr = h1 / 6;
				int repfb = h1 % 6;
				for (h2 = 0; h2 < 16; ++h2) {
					Constants.perm_n_unpack (4, sqs_rep_to_perm[replr][h2%4], cs1.m_edge, 0);
					if(( h2 % 4 ) == 0){ // Only need to update once every 4 iterations
						Constants.perm_n_unpack (4, sqs_rep_to_perm[repfb][h2/4], cs1.m_edge, 12);
						for (i = 12; i < 16; ++i) {
							cs1.m_edge[i] += 12;
						}
					}
					cs3.compose_edge (cs1, cs2);
					int u3l = cs3.cube_state_to_lrfb_l();
					if (u3l < repl) {
						repl = u3l;
					}
				}
				stage4_edge_hgA[u][h1] = repl;
			}
		}
		System.out.println( "Finishing edge A stage 4..." );
	}
	}

	private InitEdgeAStage4 threadInitEdgeAStage4 = new InitEdgeAStage4();

	public static final int lrfb_get_edge_rep (int u){
		int reph = stage4_edge_hgB[u/40320];
		int repl = stage4_edge_hgA[u % 40320][stage4_edge_hB[u/40320]];
		return 40320*reph + repl;
	}

	public static final int[] symEdgeToEdgeSTAGE4 = new int[Constants.N_STAGE4_SYMEDGE_CONFIGS]; // 5968
	public static final int[] hasSymEdgeSTAGE4 = new int[Constants.N_STAGE4_SYMEDGE_CONFIGS]; // 5968

	private class InitEdgeRepStage4 extends Thread {
	public void run (){

		try{
		threadInitEdgeBStage4.join();
		threadInitEdgeAStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
		

		System.out.println( "Starting edge rep stage 4..." );
		int u1, sym;
		CubeState cs1 = new CubeState();
		CubeState cs2 = new CubeState();
		cs1.init ();
		cs2.init ();

		int repcount = 0;
		//int n = 40320*40320; // 952222297
		int n = 952222297+1;
		for (u1 = 0; u1 < n; ++u1) {
			if ((u1 << 22 ) == 0) { // Throughly u1 % 1000 == 0
				if (repcount == 5958 && u1 < 104700000 ) { // Obtained though execution
					u1 = 952220735; // big gap !!
				}
				if (repcount == Constants.N_STAGE4_SYMEDGE_CONFIGS) {
					break;
				}
			}
			int uH = u1 / 40320;
			int uL = u1 % 40320;
			if (parity_perm8_table[uH] != parity_perm8_table[uL]) {
				continue;
			}
			int myrep = lrfb_get_edge_rep (u1);
			if (myrep == u1) {

				lrfb_to_cube_state(u1, cs1);
				boolean isRep = true;
				for (sym = 1; sym < Constants.N_SYM_STAGE4; ++sym) {
					System.arraycopy(cs1.m_edge, 0, cs2.m_edge, 0, 24);
					cs2.conjugateEdges (sym);
					int newLrfb = lrfb_get_edge_rep(cs2.cube_state_to_lrfb ());
					if( newLrfb == u1 )
						hasSymEdgeSTAGE4[repcount] |= ( 1 << sym );
					if( newLrfb < u1 ){
						isRep = false;
						hasSymEdgeSTAGE4[repcount] = 0;
						break;
					}
				}
				if (isRep){
					symEdgeToEdgeSTAGE4[repcount++] = u1;
				}
			}
		}
		System.out.println( "Finishing edge rep stage 4, found "+repcount+" reps, last reped is "+symEdgeToEdgeSTAGE4[repcount-1]+"..." );
	}
	}

	private InitEdgeRepStage4 threadInitEdgeRepStage4 = new InitEdgeRepStage4();

	public static final int[][] move_table_symEdgeSTAGE4 = new int[Constants.N_STAGE4_SYMEDGE_CONFIGS][Constants.N_STAGE4_SLICE_MOVES]; // (5968*16) 5968*16.

	private class InitSymEdgeStage4 extends Thread {
	public void run (){

		try{
		threadInit4Of8.join();
		threadInitEdgeRepStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting sym edge stage 4..." );
		int mc;
		int u;
		CubeState cs1 = new CubeState();
		CubeState cs2 = new CubeState();
		CubeState cs3 = new CubeState();
		cs1.init ();
		for (u = 0; u < Constants.N_STAGE4_SYMEDGE_CONFIGS; ++u) {
			lrfb_to_cube_state (symEdgeToEdgeSTAGE4[u], cs1);
			for (mc = 0; mc < Constants.N_STAGE4_SLICE_MOVES; ++mc) {
				System.arraycopy(cs1.m_edge, 0, cs2.m_edge, 0, 24);
				cs2.rotate_sliceEDGE (Constants.stage4_slice_moves[mc]);
				int minEdge = 1999999999;
				int minSym = 0;
				for (int sym=0; sym < Constants.N_SYM_STAGE4; sym++ ){ /* TODO: Duplicate of convert_to_stage4 !! Same for stage1 */
					System.arraycopy(cs2.m_edge, 0, cs3.m_edge, 0, 24);
					cs3.conjugateEdges(sym);

					int u2 = lrfb_get_edge_rep(cs3.cube_state_to_lrfb ());

					if( u2 < minEdge){
						minEdge = u2;
						minSym = sym;
					}
				}
				move_table_symEdgeSTAGE4[u][mc] = Symmetry.getRep(symEdgeToEdgeSTAGE4, minEdge)*Constants.N_SYM_STAGE4 + minSym;
			}
		}
		System.out.println( "Finishing sym edge stage 4..." );
	}
	}

	private InitSymEdgeStage4 threadInitSymEdgeStage4 = new InitSymEdgeStage4();

	public static final short[][] move_table_cornerSTAGE4 = new short[Constants.N_STAGE4_CORNER_CONFIGS][Constants.N_STAGE4_SLICE_MOVES]; // (420) 420*16.

	private class InitCornerStage4 extends Thread {
	public void run (){

		try{
		threadInit4Of8.join();
		threadInitPerm420.join();
		threadInitEdgeRepStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting corner stage 4..." );
		int mc;
		int u;
		CubeStage4 s4 = new CubeStage4();
		CubeStage4 s4a = new CubeStage4();
		s4.init ();
		s4a.init ();
		CubeState cs1 = new CubeState();
		cs1.init ();
		for (u = 0; u < Constants.N_STAGE4_CORNER_CONFIGS; ++u) {
			for (mc = 0; mc < Constants.N_STAGE4_SLICE_MOVES; ++mc) {
				s4.m_corner = (short)u;
				s4.convert_to_std_cube (cs1);
				cs1.rotate_sliceCORNER (Constants.stage4_slice_moves[mc]);
				cs1.convert_to_stage4 (s4a);
				move_table_cornerSTAGE4[u][mc] = s4a.m_corner;
			}
		}
		System.out.println( "Finishing corner stage 4..." );
	}
	}

	private InitCornerStage4 threadInitCornerStage4 = new InitCornerStage4();

	public static final short[][] move_table_corner_conjSTAGE4 = new short[Constants.N_STAGE4_CORNER_CONFIGS][Constants.N_SYM_STAGE4]; // (420) 420*16.

	private class InitCornerConjStage4 extends Thread {
	public void run (){

		try{
		threadInit4Of8.join();
		threadInitPerm420.join();
		threadInitEdgeRepStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting corner conj stage 4..." );
		int sym;
		int u;
		CubeStage4 s4 = new CubeStage4();
		CubeStage4 s4a = new CubeStage4();
		s4.init ();
		s4a.init ();
		CubeState cs1 = new CubeState();
		cs1.init ();
		for (u = 0; u < Constants.N_STAGE4_CORNER_CONFIGS; ++u) {
			for (sym = 0; sym < Constants.N_SYM_STAGE4; ++sym) {
				s4.m_corner = (short)u;
				s4.convert_to_std_cube (cs1);
				cs1.conjugate (sym);
				cs1.convert_to_stage4 (s4a);
				move_table_corner_conjSTAGE4[u][sym] = s4a.m_corner;
			}
		}
		System.out.println( "Finishing corner conj stage 4..." );
	}
	}

	private InitCornerConjStage4 threadInitCornerConjStage4 = new InitCornerConjStage4();

	public static final byte[][] move_table_cenSTAGE4 = new byte[Constants.N_STAGE4_CENTER_CONFIGS][Constants.N_STAGE4_SLICE_MOVES]; // (70) 70*16.

	private class InitCenterStage4 extends Thread {
	public void run (){

		try{
		threadInit4Of8.join();
		threadInitPerm420.join();
		threadInitEdgeRepStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting center stage 4..." );
		int mc;
		int u;
		CubeStage4 s4 = new CubeStage4();
		CubeStage4 s4a = new CubeStage4();
		s4.init ();
		s4a.init ();
		CubeState cs1 = new CubeState();
		cs1.init ();
		for (u = 0; u < Constants.N_STAGE4_CENTER_CONFIGS; ++u) {
			for (mc = 0; mc < Constants.N_STAGE4_SLICE_MOVES; ++mc) {
				s4.m_centerUD = (byte)u;
				s4.convert_to_std_cube (cs1);
				cs1.rotate_sliceCENTER (Constants.stage4_slice_moves[mc]);
				cs1.convert_to_stage4 (s4a);
				move_table_cenSTAGE4[u][mc] = s4a.m_centerUD;
			}
		}
		System.out.println( "Finishing center stage 4..." );
	}
	}

	private InitCenterStage4 threadInitCenterStage4 = new InitCenterStage4();

	public static final byte[][] move_table_cen_conjSTAGE4 = new byte[Constants.N_STAGE4_CENTER_CONFIGS][Constants.N_SYM_STAGE4]; // (70) 70*16.

	private class InitCenterConjStage4 extends Thread {
	public void run (){

		try{
		threadInit4Of8.join();
		threadInitPerm420.join();
		threadInitEdgeRepStage4.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting center stage 4..." );
		int sym;
		int u;
		CubeStage4 s4 = new CubeStage4();
		CubeStage4 s4a = new CubeStage4();
		s4.init ();
		s4a.init ();
		CubeState cs1 = new CubeState();
		cs1.init ();
		for (u = 0; u < Constants.N_STAGE4_CENTER_CONFIGS; ++u) {
			for (sym = 0; sym < Constants.N_SYM_STAGE4; ++sym) {
				s4.m_centerUD = (byte)u;
				s4.convert_to_std_cube (cs1);
				cs1.conjugate (sym);
				cs1.convert_to_stage4 (s4a);
				move_table_cen_conjSTAGE4[u][sym] = s4a.m_centerUD;
			}
		}
		System.out.println( "Finishing center conj stage 4..." );
	}
	}

	private InitCenterConjStage4 threadInitCenterConjStage4 = new InitCenterConjStage4();

	/*** init_stage5 ***/
	//map a "squares" move code to one of six "canonical" move codes,
	//or -1 for moves that don't affect the corresponding pieces.
	public static final int squares_map[][] = {
		{  0, -1,  1, -1, -1,  2, -1,  3, -1,  4, -1,  5 },		//UD centers
		{ -1,  4, -1,  5,  0, -1,  1, -1, -1,  2, -1,  3 },		//LR centers
		{ -1,  2, -1,  3, -1,  4, -1,  5,  0, -1,  1, -1 }		//FB centers
	};

	public static final short squares_cen_map[] = { 0x0F, 0x33, 0x3C, 0x55, 0x5A, 0x66, 0x99, 0xA5, 0xAA, 0xC3, 0xCC, 0xF0 };

	public static final int sqs_perm_to_rep[] = {
		0, 1, 2, 3, 4, 5,
		1, 0, 4, 5, 2, 3,
		3, 2, 5, 4, 0, 1,
		5, 4, 3, 2, 1, 0
	};

	private static int mov_lst[] = { Constants.Uf2, Constants.Df2, Constants.Lf2, Constants.Rf2, Constants.Ff2, Constants.Bf2 };
	private static short cen_swapbits_map[] = {
		0x90, 0x60, //Uf2
		0x09, 0x06, //Df2
		0x82, 0x28, //Ls2
		0x41, 0x14, //Rs2
		0x84, 0x48, //Fs2
		0x21, 0x12  //Bs2
	};

	private static final int[][] squares_2nd_perm = new int[24][4];

	private class InitSquares2nd extends Thread {
	public void run (){
		System.out.println( "Starting squares 2nd perm..." );
		int i;
		for (i = 0; i < 24; ++i) {
			switch (sqs_perm_to_rep[i]) {
			case 0:
				squares_2nd_perm[i][0] = 0;
				squares_2nd_perm[i][1] = 7;
				squares_2nd_perm[i][2] = 16;
				squares_2nd_perm[i][3] = 23;
				break;
			case 1:
				squares_2nd_perm[i][0] = 1;
				squares_2nd_perm[i][1] = 6;
				squares_2nd_perm[i][2] = 17;
				squares_2nd_perm[i][3] = 22;
				break;
			case 2:
				squares_2nd_perm[i][0] = 2;
				squares_2nd_perm[i][1] = 10;
				squares_2nd_perm[i][2] = 13;
				squares_2nd_perm[i][3] = 21;
				break;
			case 3:
				squares_2nd_perm[i][0] = 3;
				squares_2nd_perm[i][1] = 11;
				squares_2nd_perm[i][2] = 12;
				squares_2nd_perm[i][3] = 20;
				break;
			case 4:
				squares_2nd_perm[i][0] = 4;
				squares_2nd_perm[i][1] = 8;
				squares_2nd_perm[i][2] = 15;
				squares_2nd_perm[i][3] = 19;
				break;
			case 5:
				squares_2nd_perm[i][0] = 5;
				squares_2nd_perm[i][1] = 9;
				squares_2nd_perm[i][2] = 14;
				squares_2nd_perm[i][3] = 18;
				break;
			}
		}
		System.out.println( "Finishing squares 2nd perm..." );
	}
	}

	private InitSquares2nd threadInitSquares2nd = new InitSquares2nd();

	public static final byte[][] move_table_cornerSTAGE5 = new byte[Constants.N_SQS_CORNER_PERM][Constants.N_SQMOVES]; // TODO: (96) 96*12

	private class InitSquaresMovemap extends Thread { // TODO: Update the name ?
	public void run (){

		try{
		threadInitSquares2nd.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting squares movemap..." );
		int i, j, k, first_perm = 0, second_perm;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube2.init ();
		for (i = 0; i < 96; ++i) {
			if(( i % 4 ) == 0 ){ // Only need to update once every 4 iterations.
				first_perm = i / 4;
				Constants.perm_n_unpack (4, first_perm, cube1.m_edge, 0);
			}
			second_perm = squares_2nd_perm[first_perm][i % 4];
			Constants.perm_n_unpack (4, second_perm, cube1.m_cor, 4);
			for (j = 4; j < 8; ++j) {
				cube1.m_cor[j] += 4;
			}
			for (j = 0; j < 6; ++j) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.rotate_sliceCORNER (mov_lst[j]);
				int x1 = Constants.perm_n_pack (4, cube2.m_cor, 0);
				int x2 = cube2.m_cor[4] - 4;
				move_table_cornerSTAGE5[i][2*j]= (byte)(4*x1 + x2);
				move_table_cornerSTAGE5[i][2*j+1]= (byte)(i);
			}
		}
		System.out.println( "Finishing squares movemap..." );
	}
	}

	private InitSquaresMovemap threadInitSquaresMovemap = new InitSquaresMovemap();

	public static final byte[] squares_cen_revmap = new byte[256]; // (12)
	public static final byte[][] squares_cen_movemap = new byte[12][6]; // (12)
	public static final short[][] move_table_cenSTAGE5 = new short[Constants.N_SQS_CENTER_PERM][Constants.N_SQMOVES]; // TODO: (1728) 1728*12 = 20736

	private class InitSquaresCenterMap extends Thread {
	public void run (){

		System.out.println( "Starting squares center map..." );
		int i, j;
		for (i = 0; i < 256; ++i) {
			squares_cen_revmap[i] = 0;
		}
		for (i = 0; i < 12; ++i) {
			squares_cen_revmap[squares_cen_map[i]] = (byte)i;
		}
		for (i = 0; i < 12; ++i) {
			int x = squares_cen_map[i];
			for (j = 0; j < 6; ++j) {
				int x2 = swapbits (x, cen_swapbits_map[2*j]);
				x2 = swapbits (x2, cen_swapbits_map[2*j + 1]);
				squares_cen_movemap[i][j] = squares_cen_revmap[x2];
			}
		}
		for (i = 0; i < 12*12*12; ++i) {
			for (int m = 0; m < 12; ++m) {
				int pos = 1;
				int res = 0;
				int cen = i;
				for (int k=0; k <= 2; k++) {
					int cenk = cen % 12;
					int move_code6 = Tables.squares_map[k][m];
					if (move_code6 < 0)
						res += pos*cenk;
					else
						res += pos*squares_cen_movemap[cenk][move_code6];
					pos *= 12;
					cen /= 12;
				}

				move_table_cenSTAGE5[i][m]= (short) res;
			}
		}
		System.out.println( "Finishing squares center map..." );
	}
	}

	private InitSquaresCenterMap threadInitSquaresCenterMap = new InitSquaresCenterMap();

	/*** init stage 5 symEdgeToEdge ***/
	public static final int[] symEdgeToEdgeSTAGE5 = new int[Constants.N_SQS_SYMEDGE_PERM];
	public static final long[] hasSymEdgeSTAGE5 = new long[Constants.N_SQS_SYMEDGE_PERM];

	private class InitSymEdgeToEdgeStage5 extends Thread {

	public void run (){

		System.out.println( "Starting symEdgeToEdge stage 5..." );
		int i, sym;
		int u, repIdx = 0;
		CubeSqsCoord cs = new CubeSqsCoord();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();

		byte[] isRepTable = new byte[Constants.N_SQS_EDGE_PERM >> 3];
		for (u = 0; u < ( Constants.N_SQS_EDGE_PERM >> 3 ); ++u){
			isRepTable[u] = 0;
		}
		for (u = 0; u < Constants.N_SQS_SYMEDGE_PERM; ++u){
			hasSymEdgeSTAGE5[u] = 0;
		}

		for (u = 0; u < Constants.N_SQS_EDGE_PERM; ++u) {
			if( Constants.get_value_1bit( u, isRepTable ) != 0 ) continue;
			cs.m_ep96x96x96 = u;
			cs.convert_edges_to_std_cube (cube1);

			for (sym = 1; sym < Constants.N_SYM_STAGE5; ++sym) { // starts with 1 because sym 0 is id.
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.conjugateEdges (sym);
				int edge = cube2.convert_edges_to_squares ();
				Constants.set_1_1bit( edge, isRepTable ); // not a rep.
				if( edge == u )
					hasSymEdgeSTAGE5[repIdx] |= ( 0x1L << sym );
			}
			symEdgeToEdgeSTAGE5[repIdx++] = u;
		}
		System.out.println( "Finishing symEdgeToEdge stage 5... generated "+repIdx+" reps." );
	}
	}

	private InitSymEdgeToEdgeStage5 threadInitSymEdgeToEdgeStage5 = new InitSymEdgeToEdgeStage5();

	/*** init stage 5 symEdges ***/
	public static final int[][] move_table_symEdgeSTAGE5 = new int[Constants.N_SQS_SYMEDGE_PERM][Constants.N_SQMOVES]; // (21908*48) 21908*12 = 6677424

	private class InitSymEdgeStage5 extends Thread {

	public void run (){

		try{
		threadInitSymEdgeToEdgeStage5.join();
		threadInitSquaresMovemap.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting symEdge stage 5..." );
		int i, mc;
		int u, edge;
		CubeSqsCoord cs = new CubeSqsCoord();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();
		cube1.init ();
		cube2.init ();
		cube3.init ();
		for (u = 0; u < Constants.N_SQS_SYMEDGE_PERM; ++u) {
			cs.m_ep96x96x96 = symEdgeToEdgeSTAGE5[u];
			cs.convert_edges_to_std_cube (cube1);

			for (mc = 0; mc < Constants.N_SQMOVES; ++mc) {
				System.arraycopy(cube1.m_edge, 0, cube2.m_edge, 0, 24);
				cube2.rotate_sliceEDGE (Constants.stage5_slice_moves[mc]);
				int minEdge = 99999999;
				int minSym = 0;
				for (int sym=0; sym < Constants.N_SYM_STAGE5; sym++ ){
					System.arraycopy(cube2.m_edge, 0, cube3.m_edge, 0, 24); // TODO: duplicate in CubeState !!
					cube3.conjugateEdges(sym);
					edge = cube3.convert_edges_to_squares ();
					if( edge < minEdge){
						minEdge = edge;
						minSym = sym;
					}
				}

				move_table_symEdgeSTAGE5[u][mc] = Symmetry.getRep(symEdgeToEdgeSTAGE5, minEdge)*Constants.N_SYM_STAGE5 + minSym;
			}
		}
		System.out.println( "Finishing symEdge stage 5..." );
	}
	}

	private InitSymEdgeStage5 threadInitSymEdgeStage5 = new InitSymEdgeStage5();

	/*** init stage 5 corner conjugate ***/
	public static final byte[][] move_table_corner_conjSTAGE5 = new byte[Constants.N_SQS_CORNER_PERM][Constants.N_SYM_STAGE5]; // (96) 96*48

	private class InitCornerConjStage5 extends Thread {

	public void run (){

		try{
		threadInitSquaresMovemap.join();
		threadInitSquaresCenterMap.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting corner conjugate stage 5..." );
		int i, sym;
		int u;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeSqsCoord s1 = new CubeSqsCoord();
		s1.init ();
		for (u = 0; u < Constants.N_SQS_CORNER_PERM; ++u) {
			s1.m_cp96 = (byte)u;
			s1.convert_to_std_cube (cube1);
			for (sym = 0; sym < Constants.N_SYM_STAGE5; ++sym) {
				System.arraycopy(cube1.m_cor, 0, cube2.m_cor, 0, 8);
				cube2.conjugate (sym);
				move_table_corner_conjSTAGE5[u][sym] = cube2.convert_corners_to_squares ();
			}
		}
		System.out.println( "Finishing corner conjugate stage 5..." );
	}
	}

	private InitCornerConjStage5 threadInitCornerConjStage5 = new InitCornerConjStage5();

	/*** init stage 5 center conjugate ***/
	public static final short[][] move_table_cen_conjSTAGE5 = new short[Constants.N_SQS_CENTER_PERM][Constants.N_SYM_STAGE5]; // (1728) 1728*48

	private class InitCenterConjStage5 extends Thread {

	public void run (){

		try{
		threadInitSquaresMovemap.join();
		threadInitSquaresCenterMap.join();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}

		System.out.println( "Starting center conjugate stage 5..." );
		int i, sym;
		int u;
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		cube1.init ();
		cube2.init ();
		CubeSqsCoord s1 = new CubeSqsCoord();
		s1.init ();
		for (u = 0; u < Constants.N_SQS_CENTER_PERM; ++u) {
			s1.m_cen12x12x12 = (short)u;
			s1.convert_to_std_cube (cube1);
			for (sym = 0; sym < Constants.N_SYM_STAGE5; ++sym) {
				System.arraycopy(cube1.m_cen, 0, cube2.m_cen, 0, 24);
				cube2.conjugate (sym);
				short cen = cube2.convert_centers_to_squares();
				move_table_cen_conjSTAGE5[u][sym] = cen;
			}
		}
		System.out.println( "Finishing center conjugate stage 5..." );
	}
	}

	private InitCenterConjStage5 threadInitCenterConjStage5 = new InitCenterConjStage5();
	/*
	public static final int squares_move (int pos96, int move_code6){
		if (move_code6 < 0) {
			return pos96;
		}
		return squares_movemap[pos96][move_code6];
	}

	
	public static final int squares_move_corners (int pos96, int sqs_move_code){
		return squares_move (pos96, squares_map[3][sqs_move_code]);
	}
	
	public static final int squares_move_edges (int pos96, int sqs_move_code, int edge_group){
		return squares_move (pos96, squares_map[edge_group][sqs_move_code]);
	}

	
	public static final int squares_move_centers (int pos12, int sqs_move_code, int cen_group){
		int move_code6 = squares_map[4+cen_group][sqs_move_code];
		if (move_code6 < 0) {
			return pos12;
		}
		return squares_cen_movemap[pos12][move_code6];
	}
	*/

}
