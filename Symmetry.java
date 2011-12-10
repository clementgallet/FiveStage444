package fivestage444;

import static fivestage444.Constants.*;

public final class Symmetry {

	static byte[][] symEdges = new byte[N_SYM][24];
	static byte[][] symCornersPerm = new byte[N_SYM][8];
	static byte[][] symCornersOrient = new byte[N_SYM][8];
	static byte[][] symCenters = new byte[N_SYM][24];

	static void initSymTables (){

		System.out.println( "Starting initSymTables..." );

		byte[] symRLEdges = {4, 5, 6, 7, 0, 1, 2, 3, 14, 15, 12, 13, 10, 11, 8, 9, 21, 20, 23, 22, 17, 16, 19, 18};
		byte[] symRLCorners = {1, 0, 3, 2, 5, 4, 7, 6};
		byte[] symRLCenters = {2, 3, 0, 1, 6, 7, 4, 5, 14, 15, 12, 13, 10, 11, 8, 9, 19, 18, 17, 16, 23, 22, 21, 20};

		int i, a, b, c, d, idx=0;
		CubeState cube = new CubeState();
		for (i = 0; i < 24; ++i) {
			cube.m_edge[i] = (byte)i;
			cube.m_cen[i] = (byte)i;
		}
		for (i = 0; i < 8; ++i) {
			cube.m_cor[i] = (byte)i;
		}

		for (a=0;a<3;a++){ //SymUR3
			for (b=0;b<2;b++){ //SymF2
				for (c=0;c<4;c++){ //SymU4
					//SymLR2
					for (i=0; i<24; i++){
						symEdges[idx][i] = cube.m_edge[i];
						symEdges[idx+1][i] = cube.m_edge[symRLEdges[i]];
						symCenters[idx][i] = cube.m_cen[i];
						symCenters[idx+1][i] = cube.m_cen[symRLCenters[i]];
					}
					for (i=0; i<8; i++){
						symCornersPerm[idx][i] = (byte)(cube.m_cor[i] % 8);
						symCornersPerm[idx+1][i] = (byte)(cube.m_cor[symRLCorners[i]] % 8);
						symCornersOrient[idx][i] = (byte)(cube.m_cor[i] / 8);
						symCornersOrient[idx+1][i] = (byte)(3 + (cube.m_cor[symRLCorners[i]] / 8));
					}
					idx += 2;

					cube.do_move (Uf);
					cube.do_move (Us);
					cube.do_move (Ds3);
					cube.do_move (Df3);
				}
				cube.do_move (Ff2);
				cube.do_move (Fs2);
				cube.do_move (Bs2);
				cube.do_move (Bf2);
			}
			cube.do_move (Uf3);
			cube.do_move (Us3);
			cube.do_move (Ds);
			cube.do_move (Df);
			cube.do_move (Rf3);
			cube.do_move (Rs3);
			cube.do_move (Ls);
			cube.do_move (Lf);
		}

		System.out.println( "Finishing initSymTables..." );
	}

	static int[] invSymIdx = new int[N_SYM];

	static void initInvSymIdx(){

		System.out.println( "Starting initInvSymIdx..." );

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				if( symCornersPerm[i][symCornersPerm[j][0]] == 0 &&
				    symCornersPerm[i][symCornersPerm[j][1]] == 1 &&
				    symCornersPerm[i][symCornersPerm[j][2]] == 2    ){
					invSymIdx[i] = j;
					break;
				}

		System.out.println( "Finishing initInvSymIdx..." );
	}

	static int[][] symIdxMultiply = new int[N_SYM][N_SYM];

	static void initSymIdxMultiply(){

		System.out.println( "Starting initSymIdxMultiply..." );

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				for (int k=0; k<N_SYM; k++)
					if( symEdges[k][0] == symEdges[i][symEdges[j][0]] &&
					    symEdges[k][1] == symEdges[i][symEdges[j][1]]    ){
						symIdxMultiply[i][j] = k;
						break;
					}

		System.out.println( "Finishing initSymIdxMultiply..." );
	}

	static int[][] moveConjugate = new int[N_BASIC_MOVES][N_SYM];

	static void initMoveConjugate(){

		System.out.println( "Starting initMoveConjugate..." );

		CubeState cube = new CubeState();
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();

		for (int i=0; i<N_BASIC_MOVES; i++){
			cube.init();
			cube.do_move(i);
			for (int j=0; j<N_SYM; j++){
				cube.copyTo(cube2);
				cube2.conjugate(j);
				for (int k=0; k<N_BASIC_MOVES; k++){
					cube3.init();
					cube3.do_move(k);
					boolean isMove = true;
					for (int l=0; l<24; l++){
						if( cube3.m_edge[l] != cube2.m_edge[l] ){
							isMove = false;
							break;
						}
					}
					if( isMove ){
						moveConjugate[i][j] = k;
						break;
					}
				}
			}
		}

		System.out.println( "Finishing initMoveConjugate..." );
	}

	static int getRep(int[] table, int number){
		int a = 0;
		int b = table.length;
		int c = (a + b)/2;
		while( table[c] != number ){
			if( table[c] > number )
				b = c;
			else
				a = c;
			c = (a + b)/2;
		}

		return c;
	}
}
