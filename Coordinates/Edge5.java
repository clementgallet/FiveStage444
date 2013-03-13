package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage5;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

public final class Edge5 extends SymCoord {

	public Edge5(){
		N_COORD = 7444;
		N_RAW_COORD = 96*96*96;
		N_SYM = Stage5.N_SYM;
		SYM_SHIFT = 8;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage5.N_MOVES;

		SolvedStates = new int[]{0};
		cubieType = new EdgeCubies();
		rightMultOrConjugate = CONJUGATE;
		HASHCODE_RAW2SYM = -990244243;
		HASHCODE_SYM2RAW = -1356505959;
		HASHCODE_MOVE = -2123193957;
	}

	public static long[][] hasSym;

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		final int sqs_rep_to_perm[][] = {
			{  0,  7, 16, 23 },
			{  1,  6, 17, 22 },
			{  2, 10, 13, 21 },
			{  3, 11, 12, 20 },
			{  4,  8, 15, 19 },
			{  5,  9, 14, 18 }
		};

		final int sqs_perm_to_rep[] = {
			0, 1, 2, 3, 4, 5,
			1, 0, 4, 5, 2, 3,
			3, 2, 5, 4, 0, 1,
			5, 4, 3, 2, 1, 0
		};

		int i;
		int ep1 = raw_coord % 96;
		int ep2 = (raw_coord/96) % 96;
		int ep3 = raw_coord/(96*96);
		byte[] t = new byte[4];
		Util.set4Perm (cube.cubies, ep1/4);

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep1/4]][ep1 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+4] = (byte)(t[i]+4);
		}

		Util.set4Perm (t, ep2/4);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+8] = (byte)(t[i]+8);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep2/4]][ep2 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+12] = (byte)(t[i]+12);
		}

		Util.set4Perm (t, ep3/4);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+16] = (byte)(t[i]+16);
		}

		Util.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[ep3/4]][ep3 % 4]);
		for (i = 0; i < 4; ++i) {
			cube.cubies[i+20] = (byte)(t[i]+20);
		}
	}

	/* Pack a cube into the raw coord */
	public int pack(Cubies cube){
		int ep1 = Util.get4Perm (cube.cubies, 0);
		int ep2 = Util.get4Perm (cube.cubies, 8);
		int ep3 = Util.get4Perm (cube.cubies, 16);
		return 96*96*(4*ep3 + (cube.cubies[20] - 20)) + 96*(4*ep2 + (cube.cubies[12] - 12)) + 4*ep1 + (cube.cubies[4] - 4);
	}

	@Override
	public void initSym2Raw (){
		sym2raw = new int[N_COORD];
		raw2sym = new int[N_RAW_COORD];

		int repIdx = 0;
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD][4];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			unpack(cube1, u);

			for (int s = 0; s < N_SYM/4; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					if(s==0 && cs==0) continue;
					cube1.rightMult(Symmetry.symIdxMultiply[Symmetry.invSymIdx[s]][Symmetry.invSymIdx[cs]], cube2);
					cube2.leftMult(s);
					int raw_coord = pack(cube2);
					Util.set1bit( isRepTable, raw_coord );
					raw2sym[raw_coord] = ( repIdx << SYM_SHIFT ) + ( Symmetry.invSymIdx[s] << 2 ) + Symmetry.invSymIdx[cs];
					if( raw_coord == u )
						hasSym[repIdx][cs] |= (0x1L << s);
				}
			}
			sym2raw[repIdx++] = u;
		}
	}

	@Override
	public void initMove (){
		move = new int[N_COORD][N_MOVES];

		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		for (int u = 0; u < N_COORD; ++u) {
			unpack(cube1, sym2raw[u]);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = raw2sym[pack(cube2)];
			}
		}
	}
}
