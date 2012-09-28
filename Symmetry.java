package cg.fivestage444;

import cg.fivestage444.Moves;
import cg.fivestage444.Cubies.EdgeCubies;

public final class Symmetry {

	public static final int N_SYM = 48;
	public static final byte[][] symEdges = new byte[N_SYM][24];
	public static final byte[][] symCorners = new byte[N_SYM][8];
	public static final byte[][] symCenters = new byte[N_SYM][24];

	static void init (){
		initSymTables();
		initInvSymIdx();
		initSymIdxMultiply();
		initSymIdxCo4Multiply();
		initMoveConjugate();
		initMoveConjugateStage();
	}

	private static void initSymTables(){

		byte[] symRLEdges = {4, 5, 6, 7, 0, 1, 2, 3, 14, 15, 12, 13, 10, 11, 8, 9, 21, 20, 23, 22, 17, 16, 19, 18};
		byte[] symRLCorners = {4, 5, 6, 7, 0, 1, 2, 3};
		byte[] symRLCenters = {2, 3, 0, 1, 6, 7, 4, 5, 14, 15, 12, 13, 10, 11, 8, 9, 19, 18, 17, 16, 23, 22, 21, 20};

		int i, b, c, d, e, idx=0;
		CubeState cube = new CubeState();
		cube.init();
		for (i = 0; i < 24; ++i)
			cube.centers.cubies[i] = (byte)i;

		for (b=0;b<3;b++){ //SymUR3
			for (c=0;c<2;c++){ //SymU
				for (d=0;d<2;d++){ //SymF2
					for (e=0;e<2;e++){ //SymU2
						//SymLR2
						for (i=0; i<24; i++){
							symEdges[idx][i] = cube.edges.cubies[i];
							symEdges[idx+4][i] = cube.edges.cubies[symRLEdges[i]];
							symCenters[idx][i] = cube.centers.cubies[i];
							symCenters[idx+4][i] = cube.centers.cubies[symRLCenters[i]];
						}
						for (i=0; i<8; i++){
							symCorners[idx][i] = cube.corners.cubies[i];
							symCorners[idx+4][i] = (byte)(cube.corners.cubies[symRLCorners[i]] + 24); // 24 means the orientation is mirrored
						}
						idx += 1;
						cube.move (Moves.Uw2);
						cube.move (Moves.Dw2);
					}
					cube.move (Moves.Fw2);
					cube.move (Moves.Bw2);
				}
				idx += 4;
				cube.move (Moves.Uw);
				cube.move (Moves.Dw3);
			}
			cube.move (Moves.Uw3);
			cube.move (Moves.Dw);
			cube.move (Moves.Rw3);
			cube.move (Moves.Lw);
		}
	}

	public static final int[] invSymIdx = new int[N_SYM];

	private static void initInvSymIdx(){

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				if( symEdges[i][symEdges[j][0]] == 0 &&
					symEdges[i][symEdges[j][7]] == 7 &&
					symEdges[i][symEdges[j][18]] == 18 ){
					invSymIdx[i] = j;
					break;
				}
	}

	public static final int[][] symIdxMultiply = new int[N_SYM][N_SYM];

	private static void initSymIdxMultiply(){

		for (int i=0; i<N_SYM; i++)
			for (int j=0; j<N_SYM; j++)
				for (int k=0; k<N_SYM; k++)
					if( symEdges[k][0] == symEdges[i][symEdges[j][0]] &&
						symEdges[k][1] == symEdges[i][symEdges[j][1]] ){
						symIdxMultiply[i][j] = k;
						break;
					}
	}

	public static final int[][] symIdxCo4Multiply = new int[N_SYM*4][N_SYM*4];

	private static void initSymIdxCo4Multiply(){

		for (int i=0; i<N_SYM*4; i++)
			for (int j=0; j<N_SYM*4; j++)
				symIdxCo4Multiply[i][j] = symIdxMultiply[symIdxMultiply[invSymIdx[j>>2]][i&3]][symIdxMultiply[j>>2][j&3]] + ( symIdxMultiply[i>>2][j>>2] << 2 );
	}

	static final byte[][] moveConjugate = new byte[Moves.N_MOVES][N_SYM];

	private static void initMoveConjugate(){

		EdgeCubies cube = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		EdgeCubies cube3 = new EdgeCubies();

		for (int i=0; i<Moves.N_MOVES; i++){
			cube.init();
			cube.move(i);
			for (int j=0; j<N_SYM; j++){
				cube.conjugate(j, cube2);
				for (int k=0; k<Moves.N_MOVES; k++){
					cube3.init();
					cube3.move(k);
					boolean isMove = true;
					for (int l=0; l<24; l++){
						if( cube3.cubies[l] != cube2.cubies[l] ){
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

	public static final int[][] moveConjugateStage = new int[Moves.N_STAGE_MOVES][N_SYM];
	public static final int[][] moveConjugateCo4Stage = new int[Moves.N_STAGE_MOVES][N_SYM*4];

	private static void initMoveConjugateStage(){

		int i, j;

		for (i=0; i<Moves.N_STAGE_MOVES; i++)
			for (j=0; j<N_SYM; j++)
				moveConjugateStage[i][j] = Moves.moves2stage[moveConjugate[Moves.stage2moves[i]][j]];

		for (i=0; i<Moves.N_STAGE_MOVES; i++)
			for (j=0; j<N_SYM*4; j++)
				moveConjugateCo4Stage[i][j] = Moves.moves2stage[moveConjugate[Moves.stage2moves[i]][symIdxMultiply[j>>2][j&3]]];
	}
}
