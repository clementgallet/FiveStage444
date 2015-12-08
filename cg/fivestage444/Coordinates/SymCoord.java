package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Moves;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

import java.util.Arrays;
import java.util.logging.Logger;

public abstract class SymCoord {
	private static final Logger l = Logger.getLogger(RawCoord.class.getName());

	public static final int RIGHTMULT = 0;
	public static final int CONJUGATE = 1;

	public int N_COORD;
	public int N_RAW_COORD;
	public int N_SYM;
	public int SYM_SHIFT;
	public int SYM_MASK;
	public int N_MOVES;
	public int rightMultOrConjugate;
	public int HASHCODE_RAW2SYM = -1;
	public int HASHCODE_SYM2RAW = -1;
	public int HASHCODE_MOVE = -1;


	public int[] SolvedStates;
	public Cubies cubieType;

	/* Tables */
	public int[] sym2raw;
	public int[] raw2sym;
	public long[] hasSym;
	public int[][] move;

	abstract void unpack(Cubies cube, int coord);
	abstract int pack(Cubies cube);

	/* Initialisations */
	public void init(){
		initSym2Raw();
		initMove();
	}

	public void initSym2Raw (){
		sym2raw = new int[N_COORD];
		raw2sym = new int[N_RAW_COORD];
		int repIdx = 0;
		Cubies cube1 = null;
		Cubies cube2 = null;
		try {
			cube1 = cubieType.getClass().newInstance();
			cube2 = cubieType.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit(isRepTable, u)) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			unpack(cube1, u);
			for (int s = 1; s < N_SYM; ++s) {
				if(rightMultOrConjugate == RIGHTMULT)
					cube1.rightMult (Symmetry.invSymIdx[s], cube2);
				if(rightMultOrConjugate == CONJUGATE)
					cube1.conjugate(s, cube2);
				int raw_coord = pack(cube2);
				Util.set1bit( isRepTable, raw_coord );
				raw2sym[raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( raw_coord == u )
					hasSym[repIdx] |= (0x1L << s);
			}
			sym2raw[repIdx++] = u;
		}
		if(repIdx != N_COORD)
			l.severe("The number of sym coordinates is not correct!");
		if((HASHCODE_RAW2SYM != -1) && (Arrays.hashCode(raw2sym) != HASHCODE_RAW2SYM))
			l.severe("Array raw2sym has not been filled correctly!");
		if((HASHCODE_SYM2RAW != -1) && (Arrays.hashCode(sym2raw) != HASHCODE_SYM2RAW))
			l.severe("Array sym2raw has not been filled correctly!");
	}

	public void initMove (){
		move = new int[N_COORD][N_MOVES];
		Cubies cube1 = null;
		Cubies cube2 = null;
		try {
			cube1 = cubieType.getClass().newInstance();
			cube2 = cubieType.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		for (int u = 0; u < N_COORD; ++u) {
			unpack(cube1, sym2raw[u]);
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.move (Moves.stage2moves[m], cube2);
				move[u][m] = raw2sym[pack(cube2)];
			}
		}
		if((HASHCODE_MOVE != -1) && (Arrays.deepHashCode(move) != HASHCODE_MOVE))
			l.severe("Array move has not been filled correctly!");
	}

}
