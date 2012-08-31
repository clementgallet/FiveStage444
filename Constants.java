package cg.fivestage444;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


/**
 * Class containing all the constants used in the program, and some useful functions.
 */
public final class Constants{

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

	/** Filter certain combinaisons of moves:
	  **/

	public static final long moves_mask [] = new long[N_STAGE_MOVES+1];
	static{
		for (int i=0; i<N_STAGE_MOVES; i++) {
			moves_mask[i] = ( 1L << N_STAGE_MOVES ) - 1L;
			for (int j=0; j<N_STAGE_MOVES; j++) {
				int m = stage2moves[i];
				int n = stage2moves[j];

	  			/* Same slice. For example, Rf Rf2 is not permitted because Rf Rf2 = Rf3. */
				if (m/3 == n/3)
					moves_mask[i] &= -1L ^ ( 1L << j );

	  			/* Same face, only a specific order is allowed. Rf Ls2 is allowed but not Ls2 Rf. */
				if ( (m/18 == n/18) && (m>n) )
					moves_mask[i] &= -1L ^ ( 1L << j );

	  			/* For STM only, if two successive moves are from the same face and the same rotation, the first one should be either Ufx, Rfx or Ffx.
	  			 *   for example Rf Ls3 is ok but Lf2 Rs2 is not permitted because Lf2 Rs2 = Rf2 Ls2. */
				if( METRIC == STM )
					if ((m/16 == n/16) && ((m%3) == (n%3)) && ((m%12) >= 3))
						moves_mask[i] &= -1L ^ ( 1L << j );

				/* One of each double layer turn of the same plane is allowed, the other one not (Uw is ok, not Dw). */
				if(( n%18 ) >= 15 )
					moves_mask[i] &= -1L ^ ( 1L << j );
			}
		}

		/* The index N_STAGE_MOVES correspond to the beginning of the solve, where no moves has been done yet.
		 * Everything is allowed except for the double layer thing. */
		moves_mask[N_STAGE_MOVES] = ( 1L << N_STAGE_MOVES ) - 1L;
		for (int j=0; j<N_STAGE_MOVES; j++)
			if(( stage2moves[j]%18 ) >= 15 )
				moves_mask[N_STAGE_MOVES] &= -1L ^ ( 1L << j );
	}

	public static final int Cnk [][] = new int[25][25];
	public static final int fact [] = new int[26];
	static {
		fact[0] = 1;
		for (int i=0; i<25; i++) {
			Cnk[i][i] = 1;
			Cnk[i][0] = 1;
			fact[i+1] = fact[i] * (i+1);
		}
		for (int i=1; i<25; i++) {
			for (int j=1; j<=i; j++) {
				Cnk[i][j] = Cnk[i-1][j] + Cnk[i-1][j-1];
			}
		}
	}

	/**
	 * Converts an array of integers from off to off+4-1 into a corresponding number from 0 to 4!-1.
	 * Faster version. Taken from Chen Shuang (min2phase).
	 * @param array_in	permutation
	 * @param offset	index of the first element where the permutation starts in the table
	 * @return		an integer representing the permutation
	 */
	public static final int get4Perm (byte[] array_in, int off){
		int idx = 0;
		int val = 0x3210;
		for (int i=0; i<3; i++) {
			int v = (array_in[i+off]-off) << 2;
			idx = (4 - i) * idx + ((val >> v) & 07);
			val -= 0x1110 << v;
		}
		return idx;
	}

	/**
	 * Converts an array of integers from off to off+8-1 into a corresponding number from 0 to 8!-1.
	 * Faster version. Taken from Chen Shuang (min2phase).
	 * @param array_in	permutation
	 * @param offset	index of the first element where the permutation starts in the table
	 * @return		an integer representing the permutation
	 */
	public static final int get8Perm (byte[] array_in, int off){
		int idx = 0;
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int v = (array_in[i+off]-off) << 2;
			idx = (8 - i) * idx + ((val >> v) & 07);
			val -= 0x11111110 << v;
		}
		return idx;
	}

	/**
	 * Converts an integer into a permutation represented as an array of integers from 0 to 3.
	 * @param arr		the permutation coded as an array of integers
	 * @param idx		an integer representing the permutation
	 */
	public static final void set4Perm (byte[] arr, int idx) {
		int val = 0x3210;
		for (int i=0; i<3; i++) {
			int p = fact[3-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (byte) ((val >> v) & 07);
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[3] = (byte)val;
	}

	/**
	 * Converts an integer into a permutation represented as an array of integers from 0 to 7.
	 * @param arr		the permutation coded as an array of integers
	 * @param idx		an integer representing the permutation
	 */
	public static final void set8Perm (byte[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (byte) ((val >> v) & 07);
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = (byte)val;
	}

        public static final void setPrun2(int[] table, int index, int value) {
                table[index >> 3] ^= (0x0f ^ value) << ((index & 7) << 2);
        }

        public static final int getPrun2(int[] table, int index) {
                return (table[index >> 3] >> ((index & 7) << 2)) & 0x0f;
        }

	public static String move_strings[] = {
	"U", "U'", "U2", "u", "u'", "u2", "Uw", "Uw'", "Uw2",
	"D", "D'", "D2", "d", "d'", "d2", "Dw", "Dw'", "Dw2",
	"L", "L'", "L2", "l", "l'", "l2", "Lw", "Lw'", "Lw2",
	"R", "R'", "R2", "r", "r'", "r2", "Rw", "Rw'", "Rw2",
	"F", "F'", "F2", "f", "f'", "f2", "Fw", "Fw'", "Fw2",
	"B", "B'", "B2", "b", "b'", "b2", "Bw", "Bw'", "Bw2"
	};

	public static String print_move_list (int count, byte[] move_list, boolean inverse){
		int j, m;
		StringBuffer sb = new StringBuffer();
		if( inverse ){
			for (j = count-1; j >= 0; --j) {
				m = move_list[j];
				m = m + ((( m + 2 ) % 3 ) - 1); // inverse
				sb.append(move_strings[m] + ' ');
			}
		}
		else {
			for (j = 0; j < count; ++j) {
				m = move_list[j];
				sb.append(move_strings[m] + ' ');
			}
		}
		return sb.toString();
	}
}
