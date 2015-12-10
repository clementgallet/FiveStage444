package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Util;
import cg.fivestage444.Moves;

public final class Edge3 extends RawCoord {

	public static int moveParity;

	public Edge3(){
		N_COORD = 12870*2; // Will be double after init
		N_SYM = Stage3.N_SYM;
		N_MOVES = Stage3.N_MOVES;
		solvedStates = new int[]{12375*2};
		cubieType = new EdgeCubies();
		rightMultOrConjugate = CONJUGATE;
		HASHCODE_MOVE = 629540689;
		HASHCODE_CONJ = -540377615;
	}

	@Override
	public void init(){
		N_COORD = 12870; // To initialise properly the move and conj arrays.
		super.init();
		N_COORD = 12870*2; // Back to the real value with edge parity.

		/* Initialize move parity */
		for( int i = 0; i < N_MOVES; i++){
			int m = Moves.stage2moves[i];
			if (((( m / 3 ) % 3 ) == 1 ) && (( m % 3 ) < 2 ))
				moveParity |= 1 << i;
		}
	}


	/* Unpack a coord to a cube */
	public void unpack (Cubies cube, int coord)
	{
		int edge = coord;
		byte e0 = 0;
		byte e1 = 4;
		int r = 8;
		for (int i = 15; i >= 0; i--) {
			if (edge >= Util.Cnk[i][r]) {
				edge -= Util.Cnk[i][r--];
				cube.cubies[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				cube.cubies[i] = e1++;
			}
		}
		for (int i = 16; i < 24; ++i) {
			cube.cubies[i] = (byte)i;
		}
	}

	/* Pack a cube into the coord */
	public int pack(Cubies cube){
		int coord = 0;
		int r = 8;
		for (int i=15; i>=0; i--) {
			if (cube.cubies[i] < 4 || cube.cubies[i] >= 12) {
				coord += Util.Cnk[i][r--];
			}
		}
		return coord;
	}

}
