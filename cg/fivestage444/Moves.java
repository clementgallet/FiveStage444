package cg.fivestage444;

/**
 * Class containing all the constants used in the program, and some useful functions.
 */
public final class Moves{

//slice rotate codes
	public static final int Uf  = 0;	//Up "face" (top slice) clockwise wrt top
	public static final int Uf3 = 1;	//Up "face" counter-clockwise
	public static final int Uf2 = 2;	//Up "face" 180 degrees

	public static final int Us  = 3;	//Up "slice" (upper inner slice) clockwise wrt top
	public static final int Us3 = 4;	//Up "slice" counter-clockwise
	public static final int Us2 = 5;	//Up "slice" 180 degrees

	public static final int Uw  = 6;	//Up "wide" (double outer slice) clockwise wrt top
	public static final int Uw3 = 7;	//Up "wide" counter-clockwise
	public static final int Uw2 = 8;	//Up "wide" 180 degrees

	public static final int Df  = 9;	//Down "face" (bottom slice) clockwise wrt bottom
	public static final int Df3 = 10;	//Down "face" counter-clockwise
	public static final int Df2 = 11;	//Down "face" 180 degrees

	public static final int Ds  = 12;	//Down "slice" (lower inner slice) clockwise wrt bottom
	public static final int Ds3 = 13;	//Down "slice" counter-clockwise
	public static final int Ds2 = 14;	//Down "slice" 180 degrees

	public static final int Dw  = 15;	//Down "wide" (double outer slice) clockwise wrt bottom
	public static final int Dw3 = 16;	//Down "wide" counter-clockwise
	public static final int Dw2 = 17;	//Down "wide" 180 degrees

	public static final int Lf  = 18;	//Left "face" (left-hand outer slice) clockwise wrt left side
	public static final int Lf3 = 19;	//Left "face" counter-clockwise
	public static final int Lf2 = 20;	//Left "face" 180 degrees

	public static final int Ls  = 21;	//Left "slice" (left-hand inner slice) clockwise wrt left side
	public static final int Ls3 = 22;	//Left "slice" counter-clockwise
	public static final int Ls2 = 23;	//Left "slice" 180 degrees

	public static final int Lw  = 24;	//Left "wide" (left-hand double outer slice) clockwise wrt left side
	public static final int Lw3 = 25;	//Left "wide" counter-clockwise
	public static final int Lw2 = 26;	//Left "wide" 180 degrees

	public static final int Rf  = 27;	//Right "face" (right-hand outer slice) clockwise wrt right side
	public static final int Rf3 = 28;	//Right "face" counter-clockwise
	public static final int Rf2 = 29;	//Right "face" 180 degrees

	public static final int Rs  = 30;	//Right "slice" (right-hand inner slice) clockwise wrt right side
	public static final int Rs3 = 31;	//Right "slice" counter-clockwise
	public static final int Rs2 = 32;	//Right "slice" 180 degrees

	public static final int Rw  = 33;	//Right "wide" (right-hand double outer slice) clockwise wrt right side
	public static final int Rw3 = 34;	//Right "wide" counter-clockwise
	public static final int Rw2 = 35;	//Right "wide" 180 degrees

	public static final int Ff  = 36;	//Front "face" (front outer slice) clockwise wrt front
	public static final int Ff3 = 37;	//Front "face" counter-clockwise
	public static final int Ff2 = 38;	//Front "face" 180 degrees

	public static final int Fs  = 39;	//Front "slice" (front inner slice) clockwise wrt front
	public static final int Fs3 = 40;	//Front "slice" counter-clockwise
	public static final int Fs2 = 41;	//Front "slice" 180 degrees

	public static final int Fw  = 42;	//Front "wide" (front double outer slice) clockwise wrt front
	public static final int Fw3 = 43;	//Front "wide" counter-clockwise
	public static final int Fw2 = 44;	//Front "wide" 180 degrees

	public static final int Bf  = 45;	//Back "face" (rear outer slice) clockwise wrt back side
	public static final int Bf3 = 46;	//Back "face" counter-clockwise
	public static final int Bf2 = 47;	//Back "face" 180 degrees

	public static final int Bs  = 48;	//Back "slice" (rear inner slice) clockwise wrt back side
	public static final int Bs3 = 49;	//Back "slice" counter-clockwise
	public static final int Bs2 = 50;	//Back "slice" 180 degrees

	public static final int Bw  = 51;	//Back "wide" (rear double outer slice) clockwise wrt back side
	public static final int Bw3 = 52;	//Back "wide" counter-clockwise
	public static final int Bw2 = 53;	//Back "wide" 180 degrees

	public static final int N_MOVES  = Bw2 + 1;	//last rotate code plus one

	/** Don't use the actual numbering of rotation, but use another one sorted by the different stages. **/

	public static final int N_STAGE_MOVES = 36;

	public static final byte[] stage2moves = {
		Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2, // Stage 5 moves
		Uf, Df,	Uf3, Df3, // Stage 4 moves
		Fs, Bs, Fs3, Bs3, // Stage 3 moves
		Us, Ds, Ls, Rs, Us3, Ds3, Ls3, Rs3, // Stage 2 moves
		Lf, Rf, Ff, Bf, Lf3, Rf3, Ff3, Bf3 // Stage 1 moves
	};

	public static final byte moves2stage[] = new byte[N_MOVES];
	static {
		for (byte i=0; i<N_STAGE_MOVES; i++) {
			moves2stage[stage2moves[i]] = i;
		}
	}

	/** Initialize stage2face table **/
	public final static int N_FACE_MOVES = 18;
	public static final byte[] stage2face = new byte[N_MOVES];
	public static final byte[] face2moves = new byte[N_FACE_MOVES];
	static{
		int fm = 0; // TODO: Only works for STM, because face moves are unique.
		for( int s = 0; s < N_STAGE_MOVES; s++ ){
			byte m = stage2moves[s];
			if((( m / 3 ) % 3 ) == 1 ) // inner slice move
				stage2face[s] = -1;
			else{
				stage2face[s] = (byte) fm;
				face2moves[fm] = m;
				fm++;
			}
		}
	}

	/** Filter certain combinaisons of moves **/

	public static final long moves_mask [] = new long[N_STAGE_MOVES+1];
	static{
		for (int i=0; i<N_STAGE_MOVES; i++) {
			moves_mask[i] = ( 1L << N_STAGE_MOVES ) - 1L;
			for (int j=0; j<N_STAGE_MOVES; j++) {
				int m = stage2moves[i];
				int n = stage2moves[j];

	  			/* Same slice. For example, Rf Rf2 is not permitted because Rf Rf2 = Rf3. */
				if (m/3 == n/3)
					moves_mask[i] &= ~(1L << j);

	  			/* Same face, only a specific order is allowed. Rf Ls2 is allowed but not Ls2 Rf. */
				if ( (m/18 == n/18) && (m>n) )
					moves_mask[i] &= ~(1L << j);

	  			/* If two successive moves are from the same face and the same rotation, the first one should be either Ufx, Rfx or Ffx.
	  			 * For example Rf Ls3 is ok but Lf2 Rs2 is not permitted because Lf2 Rs2 = Rf2 Ls2. */
				if ((m/18 == n/18) && ((m%3) == (n%3)) && ((m%18) >= 3))
					moves_mask[i] &= ~(1L << j);
			}
		}

		/* The index N_STAGE_MOVES correspond to the beginning of the solve, where no moves has been done yet. */
		moves_mask[N_STAGE_MOVES] = ( 1L << N_STAGE_MOVES ) - 1L;
	}

	public static final String[] move_strings = {
	"U", "U'", "U2", "2U", "2U'", "2U2", "u", "u'", "u2",
	"D", "D'", "D2", "2D", "2D'", "2D2", "d", "d'", "d2",
	"L", "L'", "L2", "2L", "2L'", "2L2", "l", "l'", "l2",
	"R", "R'", "R2", "2R", "2R'", "2R2", "r", "r'", "r2",
	"F", "F'", "F2", "2F", "2F'", "2F2", "f", "f'", "f2",
	"B", "B'", "B2", "2B", "2B'", "2B2", "b", "b'", "b2"
	};

	public static String print_move_list (int count, byte[] move_list, boolean inverse){
		int j, m;
		StringBuilder sb = new StringBuilder();
		if( inverse ){
			for (j = count-1; j >= 0; --j) {
				m = move_list[j];
				m = m + ((( m + 2 ) % 3 ) - 1); // inverse
				sb.append(move_strings[m]).append(' ');
			}
		}
		else {
			for (j = 0; j < count; ++j) {
				m = move_list[j];
				sb.append(move_strings[m]).append(' ');
			}
		}
		return sb.toString().trim();
	}
}
