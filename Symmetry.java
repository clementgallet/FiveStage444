package fivestage444;

public final class Symmetry {

	static byte[][] symEdges = new byte[48][24];
	static byte[][] symCornersPerm = new byte[48][8];
	static byte[][] symCornersOrient = new byte[48][8];
	static byte[][] symCenters = new byte[48][24];

	void initSymTables (){

		byte[] symRLEdges = {4, 5, 6, 7, 0, 1, 2, 3, 14, 15, 12, 13, 10, 11, 8, 9, 21, 20, 23, 22, 17, 16, 19, 18};
		byte[] symRLCorners = {1, 0, 3, 2, 5, 4, 7, 6};
		byte[] symRLCenters = {2, 3, 0, 1, 6, 7, 4, 5, 14, 15, 12, 13, 10, 11, 8, 9, 19, 18, 17, 16, 23, 22, 21, 20};

		int i, a, b, c, d, idx=0;
		CubeState cube = new CubeState();
		for (i = 0; i < 24; ++i) {
			m_edge[i] = i;
			m_cen[i] = i;
		}
		for (i = 0; i < 8; ++i) {
			m_cor[i] = i;
		}

		for (a=0;a<3;a++){ //SymUR3
			for (b=0;b<2;b++){ //SymF2
				for (c=0;c<4;c++){ //SymU4
					for (d=0;d<2;d++){ //SymLR2
						for (i=0; i<24; i++)
							symEdges[idx][i] = cube.m_edge[i];
							symEdges[idx+1][i] = cube.m_edge[symRLEdges[i]];
							symCenters[idx][i] = cube.m_cor[i];
							symCenters[idx+1][i] = cube.m_cor[symRLCenters[i]];
						}
						for (i=0; i<8; i++)
							symCornersPerm[idx][i] = cube.m_cor[i] % 8;
							symCornersPerm[idx+1][i] = cube.m_cor[symRLCorners[i]] % 8;
							symCornersOrient[idx][i] = cube.m_cor[i] / 8;
							symCornersOrient[idx+1][i] = 3 + (cube.m_cor[symRLCorners[i]] / 8);
							//symCornersOrient[idx+1][i] = (3 - (cube.m_cor[symRLCorners[i]] / 8)) % 3;
						}
						idx += 2;
					}
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
	}

	static int[] invSymIdx = new int[48];

	void initInvSymIdx(){

		for (int i=0; i<48; i++)
			for (int j=0; j<48; j++)
				if( symCornersPerm[i][symCornersPerm[j][0]] == 0 &&
				    symCornersPerm[i][symCornersPerm[j][1]] == 1 &&
				    symCornersPerm[i][symCornersPerm[j][2]] == 2    ){
					invSymIdx[i] = j;
					break;
				}
	}

	static int[][] symIdxMultiply = new int[48][48];

	void initSymIdxMultiply(){

		for (int i=0; i<48; i++)
			for (int j=0; j<48; j++)
				for (int k=0; k<48; k++)
					if( symEdges[k][0] == symEdges[i][symEdges[j][0]] &&
					    symEdges[k][1] == symEdges[i][symEdges[j][1]]    ){
						symIdxMultiply[i][j] = k;
						break;
					}
	}

}
