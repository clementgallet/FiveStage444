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

	public static final String datafiles_path = "./cg/fivestage444/";
	public static final String tables_path = "./cg/fivestage444/";
	public static final int STM = 0;
	public static final int FTM = 1;
	public static int METRIC = FTM;
	public static String METRIC_STR = (METRIC == STM) ? "stm" : "ftm";

	public static final int N_SYM = 48;
	public static final int N_SYM_STAGE1 = 48;
	public static final int N_SYM_STAGE2 = 16;
	public static final int N_SYM_STAGE3 = 8;
	public static final int N_SYM_STAGE4 = 16;
	public static final int N_SYM_STAGE5 = 48;

	public static final int N_CORNER_ORIENT = 2187;

	public static final int N_EDGE_COMBO8 = 735471;	// 24!/(16!*8!)
	public static final int N_SYMEDGE_COMBO8 = 15582;

	public static final int N_STAGE2_EDGE_CONFIGS = 420;
	public static final int N_STAGE2_CENTER_CONFIGS = 51482970;	// 24!/(16!*24*24)
	public static final int N_CENTER_COMBO4 = 10626;
	public static final int N_SYMCENTER_COMBO4 = 716;

	public static final int N_STAGE3_CENTER_CONFIGS = 900900;	//(16*15*14*13/24)*(12*11*10*9/24)
	public static final int N_STAGE3_SYMCENTER_CONFIGS = 56980;
	public static final int N_COMBO_16_8 = 12870;				//16!/(8!*8!)
	public static final int N_STAGE3_EDGE_PAR = 2;
	public static final int N_STAGE3_EDGE_CONFIGS = N_COMBO_16_8;	//16!/(8!*8!), does not include parity info

	public static final int N_STAGE4_CENTER_CONFIGS = 70;	//8!/(4!*4!)
	public static final int N_STAGE4_EDGE_CONFIGS = 88200;	//420*420/2
	public static final int N_STAGE4_SYMEDGE_CONFIGS = 5968;
	public static final int N_STAGE4_CORNER_CONFIGS = 420;	//8!/96

	public static final int N_STAGE5_EDGE_PERM = 96*96*96;
	public static final int N_STAGE5_SYMEDGE_PERM = 7444;
	public static final int N_STAGE5_CENTER_PERM = 12*12*12;
	public static final int N_STAGE5_CORNER_PERM = 96;

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


	/** Filter certain combinaisons of moves:
	  * Same slice. For example, Rf Rf2 is not permitted because Rf Rf2 = Rf3.
	  * Same face, only a specific order is allowed. Rf Ls2 is allowed but not Ls2 Rf.
	  * For STM only, if two successive moves are from the same face and the same rotation, the first one should be either Ufx, Rfx or Ffx.
	  *   for example Rf Ls3 is ok but Lf2 Rs2 is not permitted because Lf2 Rs2 = Rf2 Ls2.
	  **/

	public static final boolean slice_moves_to_try [][] = new boolean[N_MOVES][N_MOVES];
	static{
		for (int i=0; i<N_MOVES; i++) {
			for (int j=0; j<N_MOVES; j++) {
				slice_moves_to_try[i][j] = (i/3 == j/3) || ((i/18 == j/18) && (i>j));
				if( METRIC == STM )
					slice_moves_to_try[i][j] |= (i/16 == j/16) && ((i%3) == (j%3)) && ((i%12) >= 3);
			}
		}
	}

	public static final int N_STAGE1_MOVES = 36;
	public static final int N_STAGE1_SEARCH = ( METRIC == STM ) ? 36 : 27;
	public static final int N_STAGE1_LAST = ( METRIC == STM ) ? 8 : 12;

	public static final byte stage1_slice_moves[];
	static{
		if( METRIC == STM )
			stage1_slice_moves = new byte[]{
				Lf, Rf, Ff, Bf, Lf3, Rf3, Ff3, Bf3, // moves that will be tried for the last move
				Uf, Uf3, Uf2, Us, Us3, Us2, Df, Df3, Df2, Ds, Ds3, Ds2,
				         Lf2, Ls, Ls3, Ls2,          Rf2, Rs, Rs3, Rs2,
				         Ff2, Fs, Fs3, Fs2,          Bf2, Bs, Bs3, Bs2
			};
		else
			stage1_slice_moves = new byte[]{
				Lf, Rf, Ff, Bf, Lf3, Rf3, Ff3, Bf3, Lw, Lw3, Fw, Fw3, // moves that will be tried for the last move
				Uf, Uf3, Uf2, Uw, Uw3, Uw2, Df, Df3, Df2,
				         Lf2,          Lw2,          Rf2,
				         Ff2,          Fw2,          Bf2,
				Dw, Dw3, Dw2, Rw, Rw3, Rw2, Bw, Bw3, Bw2
			};
	}

	public static final int N_FACE_MOVES = 18;
	public static final int basic_to_face[] = new int[N_MOVES];
	static {
		for( int i = 0; i < N_STAGE1_MOVES; i++ ){
			byte m = stage1_slice_moves[i];
			basic_to_face[i] = ( m / 9 ) * 3 + ( m % 3 );
		}
	};

	public static final int stage1_inv_slice_moves[] = new int[N_MOVES];
	static {
		for (int i=0; i<N_STAGE1_MOVES; i++) {
			stage1_inv_slice_moves[stage1_slice_moves[i]] = i;
		}
	}

	public static final boolean stage1_slice_moves_to_try [][] = new boolean[N_STAGE1_MOVES + 1][N_STAGE1_MOVES];
	static{
		for (int i=0; i<N_STAGE1_MOVES; i++) {
			for (int j=0; j<N_STAGE1_MOVES; j++) {
				stage1_slice_moves_to_try[i][j] = slice_moves_to_try[stage1_slice_moves[i]][stage1_slice_moves[j]];
			}
			stage1_slice_moves_to_try[N_STAGE1_MOVES][i] = false;
		}
	}

	public static final int N_STAGE2_MOVES = 28;
	public static final int N_STAGE2_SEARCH = ( METRIC == STM ) ? 28 : 23;
	public static final int N_STAGE2_LAST = ( METRIC == STM ) ? 8 : 6;

	public static final byte stage2_slice_moves[];
	static {
		if( METRIC == STM )
			stage2_slice_moves = new byte[]{
				Us, Ds, Ls, Rs, Us3, Ds3, Ls3, Rs3,
				Uf, Uf3, Uf2,          Us2, Df, Df3, Df2,          Ds2,
				         Lf2,          Ls2,          Rf2,          Rs2,
				         Ff2, Fs, Fs3, Fs2,          Bf2, Bs, Bs3, Bs2
			};
		else
			stage2_slice_moves = new byte[]{
				Uw, Ls, Rs, Uw3, Ls3, Rs3,
				Uf, Uf3, Uf2,          Uw2, Df, Df3, Df2,
				         Lf2,          Lw2,          Rf2,
				         Ff2, Fs, Fs3, Fw2,          Bf2, Bs, Bs3,
				Dw, Dw3, Dw2, Rw2, Bw2
			};
	}

	public static final int stage2_inv_slice_moves[] = new int[N_MOVES];
	static {
		for (int i=0; i<N_STAGE2_MOVES; i++) {
			stage2_inv_slice_moves[stage2_slice_moves[i]] = i;
		}
	}

	public static final boolean stage2_slice_moves_to_try [][] = new boolean[N_STAGE2_MOVES + 1][N_STAGE2_MOVES];
	static{
		for (int i=0; i<N_STAGE2_MOVES; i++) {
			for (int j=0; j<N_STAGE2_MOVES; j++) {
				stage2_slice_moves_to_try[i][j] = slice_moves_to_try[stage2_slice_moves[i]][stage2_slice_moves[j]];
			}
			stage2_slice_moves_to_try[N_STAGE2_MOVES][i] = false;
		}
	}

	public static final int N_STAGE3_MOVES = 20;
	public static final int N_STAGE3_SEARCH = ( METRIC == STM ) ? 20 : 17;
	public static final int N_STAGE3_LAST = 4;

	public static final byte stage3_slice_moves[];
	static {
		if( METRIC == STM )
			stage3_slice_moves = new byte[]{
				Fs, Bs, Fs3, Bs3,
				Uf, Uf3, Uf2, Us2, Df, Df3, Df2, Ds2,
				         Lf2, Ls2,          Rf2, Rs2,
				         Ff2, Fs2,          Bf2, Bs2
			};
		else
			stage3_slice_moves = new byte[]{
				Fs, Bs, Fs3, Bs3,
				Uf, Uf3, Uf2, Uw2, Df, Df3, Df2,
				         Lf2, Lw2,          Rf2,
				         Ff2, Fw2,          Bf2,
				Dw2, Rw2, Bw2
			};
	}

	public static final int stage3_inv_slice_moves[] = new int[N_MOVES];
	static {
		for (int i=0; i<N_STAGE3_MOVES; i++) {
			stage3_inv_slice_moves[stage3_slice_moves[i]] = i;
		}
	}

	public static final boolean stage3_slice_moves_to_try [][] = new boolean[N_STAGE3_MOVES + 1][N_STAGE3_MOVES];
	static{
		for (int i=0; i<N_STAGE3_MOVES; i++) {
			for (int j=0; j<N_STAGE3_MOVES; j++) {
				stage3_slice_moves_to_try[i][j] = slice_moves_to_try[stage3_slice_moves[i]][stage3_slice_moves[j]];
			}
			stage3_slice_moves_to_try[N_STAGE3_MOVES][i] = false;
		}
	}

	public static boolean stage3_move_parity[] = new boolean[N_STAGE3_MOVES];
	static {
		for( int i = 0; i < N_STAGE3_MOVES; i++)
			stage3_move_parity[i] = ((( stage3_slice_moves[i] / 3 ) % 3 ) == 1 ) && (( stage3_slice_moves[i] % 3 ) < 2 );
	}

	public static final int N_STAGE4_MOVES = 16;
	public static final int N_STAGE4_SEARCH = ( METRIC == STM ) ? 16 : 13;
	public static final int N_STAGE4_LAST = 4;

	public static final byte stage4_slice_moves[];
	static {
		if( METRIC == STM )
			stage4_slice_moves = new byte[]{
				Uf, Df,	Uf3, Df3,
				Uf2, Us2, Df2, Ds2,
				Lf2, Ls2, Rf2, Rs2,
				Ff2, Fs2, Bf2, Bs2
			};
		else
			stage4_slice_moves = new byte[]{
				Uf, Df,	Uf3, Df3,
				Uf2, Uw2, Df2,
				Lf2, Lw2, Rf2,
				Ff2, Fw2, Bf2,
				Dw2, Rw2, Bw2
			};
	}

	public static final int stage4_inv_slice_moves[] = new int[N_MOVES];
	static {
		for (int i=0; i<N_STAGE4_MOVES; i++) {
			stage4_inv_slice_moves[stage4_slice_moves[i]] = i;
		}
	}

	public static final boolean stage4_slice_moves_to_try [][] = new boolean[N_STAGE4_MOVES + 1][N_STAGE4_MOVES];
	static{
		for (int i=0; i<N_STAGE4_MOVES; i++) {
			for (int j=0; j<N_STAGE4_MOVES; j++) {
				stage4_slice_moves_to_try[i][j] = slice_moves_to_try[stage4_slice_moves[i]][stage4_slice_moves[j]];
			}
			stage4_slice_moves_to_try[N_STAGE4_MOVES][i] = false;
		}
	}

	public static final int N_STAGE5_MOVES = 12;
	public static final int N_STAGE5_SEARCH = ( METRIC == STM ) ? 12 : 9;

	public static final byte stage5_slice_moves[];
	static {
		if( METRIC == STM )
			stage5_slice_moves = new byte[]{
				Uf2, Us2, Df2, Ds2, Lf2, Ls2, Rf2, Rs2, Ff2, Fs2, Bf2, Bs2
			};
		else
			stage5_slice_moves = new byte[]{
				Uf2, Uw2, Df2, Lf2, Lw2, Rf2, Ff2, Fw2, Bf2,
				Dw2, Rw2, Bw2
			};
	}

	public static final int stage5_inv_slice_moves[] = new int[N_MOVES];
	static {
		for (int i=0; i<N_STAGE5_MOVES; i++) {
			stage5_inv_slice_moves[stage5_slice_moves[i]] = i;
		}
	}

	public static final boolean stage5_slice_moves_to_try [][] = new boolean[N_STAGE5_MOVES + 1][N_STAGE5_MOVES];
	static{
		for (int i=0; i<N_STAGE5_MOVES; i++) {
			for (int j=0; j<N_STAGE5_MOVES; j++) {
				stage5_slice_moves_to_try[i][j] = slice_moves_to_try[stage5_slice_moves[i]][stage5_slice_moves[j]];
			}
			stage5_slice_moves_to_try[N_STAGE5_MOVES][i] = false;
		}
	}

	public static final int Cnk [][] = new int[25][25];
	static {
		for (int i=0; i<25; i++) {
			Cnk[i][i] = 1;
			Cnk[i][0] = 1;
		}
		for (int i=1; i<25; i++) {
			for (int j=1; j<=i; j++) {
				Cnk[i][j] = Cnk[i-1][j] + Cnk[i-1][j-1];
			}
		}
	}

	/**
	 * Extract an element from a table that stores eight numbers per byte.
	 * @param x	table index
	 * @param p	table
	 * @return	extracted value
	 */
	public static final byte get_value_1bit (int x, byte[] p)
	{
		int x2 = x >> 3;
		int j = x & 0x7;
		return (byte)((p[x2] >> j) & 0x1);
	}

	/**
	 * Write a 1 into a table.
	 * @param x	table index
	 * @param p	table
	 */
	public static final void set_1_1bit (int x, byte[] p)
	{
		int x2 = x >> 3;
		int j = x & 0x7;
		p[x2] |= (0x1 << j);
	}

	/**
	 * Converts an array of integers from 0 to n-1 into a corresponding number from 0 to n!-1.
	 * @param n		cardinal of the permutation
	 * @param array_in	permutation
	 * @param offset	index of the first element where the permutation starts in the table (can be >0)
	 * @return		an integer representing the permutation
	 */
	public static final int perm_n_pack (int n, byte[] array_in, int offset)
	{
		int idx;
		int i, j;

		idx = 0;

		for (i = 0; i < n; ++i) {
			idx *= (n - i);

			for (j = i + 1; j < n; ++j) {
				if (array_in[j+offset] < array_in[i+offset]) {
					++idx;
				}
			}
		}
		return idx;
	}

	/**
	 * Converts an integer into a permutation represented as an array of integers from 0 to n-1.
	 * @param n		cardinal of the permutation
	 * @param idx		an integer representing the permutation
	 * @param array_out	the permutation coded as an array of integers
	 * @param offset	index of the first element where the permutation is written in the table (can be >0)
	 */
	public static final void perm_n_unpack (int n, int idx, byte[] array_out, int offset)
	{
		int i, j;

		for (i = n - 1; i >= 0; --i) {
			array_out[i+offset] = (byte)(idx % (n - i));
			idx /= (n - i);

			for (j = i + 1; j < n; ++j) {
				if (array_out[j+offset] >= array_out[i+offset]) {
					array_out[j+offset]++;
				}
			}
		}
	}

	/**
	 * Compute the next permutation of an array of integers.
	 * @param array		array of integers representing the permutation
	 * @param length	length of the array
	 * @param offset	index of the first element where the permutation starts in the table (can be >0)
	 */

	public static final void nextPerm (byte[] array, int length, int offset){
		int j = length - 2;
		while (j >= 0 && ( array[j+offset] >= array[j+1+offset] ))
			--j;
			
		if (j < 0) return; // Already next perm.
		
		int m = length - 1;
		while (array[j+offset] >= array[m+offset])
			m--;
		byte temp = array[j+offset];
		array[j+offset] = array[m+offset];
		array[m+offset] = temp;
		
		int k = j + 1;
		m = length - 1;
		while (k < m) {
			temp = array[k+offset];
			array[k+offset] = array[m+offset];
			array[m+offset] = temp;
			k++;
			m--;
		}
	}

	public static final int STAGE2_NUM_SOLVED_SYMCENTER_CONFIGS = 6;
	public static final int stage2_solved_symcenters[] = {
	582, 606, 631, 641, 664, 673
	};

	public static final int STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS = 6;
	public static final int stage3_solved_sym_centers[] = {
	56966, 56974, 56975, 56977, 56978, 56979
	};
	// True solved centers, better not deleting. Edit: I'm so wise !
	// 900830, 900844, 900850, 900853, 900857, 900858, 900871, 900872, 900876, 900879, 900885, 900899

	public static final int STAGE4_NUM_SOLVED_CENTER_CONFIGS = 12;
	public static final short stage4_solved_centers_bm[] = {
	0x0F, 0xF0, 0x55, 0xAA, 0x5A, 0xA5, 0x69, 0x96, 0x66, 0x99, 0x3C, 0xC3
	};

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
