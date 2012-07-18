package cg.fivestage444;

import static cg.fivestage444.Constants.*;

public final class Symmetry {

	static byte[][] symEdges = new byte[N_SYM][24];
	static byte[][] symCornersPerm = new byte[N_SYM][8];
	static byte[][] symCornersOrient = new byte[N_SYM][8];
	static byte[][] symCenters = new byte[N_SYM][24];

	static void init (){
		initSymTables();
		initInvSymIdx();
		initSymIdxMultiply();
		initSymIdxCo4Multiply();
		initMoveConjugate();
		initMoveConjugateStage();
	}

	static void initSymTables (){

		byte[] symRLEdges = {4, 5, 6, 7, 0, 1, 2, 3, 14, 15, 12, 13, 10, 11, 8, 9, 21, 20, 23, 22, 17, 16, 19, 18};
		byte[] symRLCorners = {1, 0, 3, 2, 5, 4, 7, 6};
		byte[] symRLCenters = {2, 3, 0, 1, 6, 7, 4, 5, 14, 15, 12, 13, 10, 11, 8, 9, 19, 18, 17, 16, 23, 22, 21, 20};

		int i, a, b, c, d, e, idx=0;
		CubeState cube = new CubeState();
		for (i = 0; i < 24; ++i) {
			cube.m_edge[i] = (byte)i;
			cube.m_cen[i] = (byte)i;
		}
		for (i = 0; i < 8; ++i) {
			cube.m_cor[i] = (byte)i;
		}

		for (b=0;b<3;b++){ //SymUR3
			for (c=0;c<2;c++){ //SymU
				for (d=0;d<2;d++){ //SymF2
					for (e=0;e<2;e++){ //SymU2
						//SymLR2
						for (i=0; i<24; i++){
							symEdges[idx][i] = cube.m_edge[i];
							symEdges[idx+4][i] = cube.m_edge[symRLEdges[i]];
							symCenters[idx][i] = cube.m_cen[i];
							symCenters[idx+4][i] = cube.m_cen[symRLCenters[i]];
						}
						for (i=0; i<8; i++){
							symCornersPerm[idx][i] = (byte)(cube.m_cor[i] % 8);
							symCornersPerm[idx+4][i] = (byte)(cube.m_cor[symRLCorners[i]] % 8);
							symCornersOrient[idx][i] = (byte)(cube.m_cor[i] / 8);
							symCornersOrient[idx+4][i] = (byte)(3 + (cube.m_cor[symRLCorners[i]] / 8));
						}
						idx += 1;
						cube.do_move (Uw2);
						cube.do_move (Dw2);
					}
					cube.do_move (Fw2);
					cube.do_move (Bw2);
				}
				idx += 4;
				cube.do_move (Uw);
				cube.do_move (Dw3);
			}
			cube.do_move (Uw3);
			cube.do_move (Dw);
			cube.do_move (Rw3);
			cube.do_move (Lw);
		}
	}

	static int[] invSymIdx = new int[N_SYM];

	static void initInvSymIdx(){

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				if( symCornersPerm[i][symCornersPerm[j][0]] == 0 &&
				    symCornersPerm[i][symCornersPerm[j][1]] == 1 &&
				    symCornersPerm[i][symCornersPerm[j][2]] == 2 ){
					invSymIdx[i] = j;
					break;
				}
	}

	static int[][] symIdxMultiply = new int[N_SYM][N_SYM];

	static void initSymIdxMultiply(){

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				for (int k=0; k<N_SYM; k++)
					if( symEdges[k][0] == symEdges[i][symEdges[j][0]] &&
					    symEdges[k][1] == symEdges[i][symEdges[j][1]] ){
						symIdxMultiply[i][j] = k;
						break;
					}
	}

	static int[][] symIdxCo4Multiply = new int[N_SYM*4][N_SYM*4];

	static void initSymIdxCo4Multiply(){

		for (int i=0; i<N_SYM*4; i++)
			for (int j=0; j<N_SYM*4; j++)
				symIdxCo4Multiply[i][j] = symIdxMultiply[symIdxMultiply[invSymIdx[i>>2]][j&3]][symIdxMultiply[i>>2][i&3]] + ( symIdxMultiply[j>>2][i>>2] << 2 );
	}

	static byte[][] moveConjugate = new byte[N_MOVES][N_SYM];

	static void initMoveConjugate(){

		CubeState cube = new CubeState();
		CubeState cube2 = new CubeState();
		CubeState cube3 = new CubeState();

		for (int i=0; i<N_MOVES; i++){
			cube.init();
			cube.do_move(i);
			for (int j=0; j<N_SYM; j++){
				cube.conjugateEdges(j, cube2);
				for (int k=0; k<N_MOVES; k++){
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
						moveConjugate[i][j] = (byte)k;
						break;
					}
				}
			}
		}
	}

	static int[][] moveConjugateStage = new int[N_MOVES][N_SYM];
	static int[][] moveConjugateCo4Stage = new int[N_MOVES][N_SYM*4];

	static void initMoveConjugateStage(){

		int i, j;

		for (i=0; i<N_MOVES; i++)
			for (j=0; j<N_SYM; j++)
				moveConjugateStage[i][j] = moves2stage[moveConjugate[stage2moves[i]][j]];

		for (i=0; i<N_MOVES; i++)
			for (j=0; j<N_SYM*4; j++)
				moveConjugateCo4Stage[i][j] = moves2stage[moveConjugate[stage2moves[i]][symIdxMultiply[j>>2][j&3]]];
	}
}
