package cg.fivestage444;

import java.util.Arrays;

public final class Util {

	public static final boolean FULL_PRUNING3 = true;

	public static final void init (){
		initCnk();
		initC24to8();
		initC16to16();
		initShiftC16();
		initC24_4to12();
		initParityTable();
		initMap96();

		init_s4();
		initPerm420();
	}

	public static final int Cnk [][] = new int[25][25];
	public static final int fact [] = new int[26];

	public static final void initCnk() {
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

        public static final void set1bit(byte[] table, int index) {
		table[index>>>3] |= (byte)( 1 << ( index & 0x7 ));
	}

        public static final boolean get1bit(byte[] table, int index) {
		return (( table[index>>>3] >>> ( index & 0x7 )) & 1 ) != 0;
        }

	public static final void setPrun2(byte[] table, int index, int value) {
		table[index >> 1] ^= (0x0f ^ value) << ((index & 1) << 2);
	}

        public static final int getPrun2(byte[] table, int index) {
                return (table[index >> 1] >> ((index & 1) << 2)) & 0x0f;
        }

	/* We need to be able to convert a number between 0 and C^24_4 - 1 that represents the location of four elements among 24,
	 * who in practice are only taking place in the 8 last places, into the number coding to the location of those four elements
	 * among the 8 last places */
	static final int C24_4 = 10626;
	public static final byte[] C24to8 = new byte[C24_4];

	public static final void initC24to8() {
		byte[] t = new byte[8];
		for (int p=0; p<40320; p++){
			int r = 4;
			int c24 = 0;
			int c8 = 0;
			set8Perm( t, p );
			for (int i=7; i>=0; i--){
				if( t[i] < 4 ){
					c24 += Cnk[i+16][r];
					c8 += Cnk[i][r--];
				}
			}
			C24to8[c24] = (byte)c8;
		}
	}

	/* We need to be able to convert two numbers between 0 and C^16_4 - 1 representing the location of four elements among 16,
	 * where the elements from both numbers are not intersecting, into a number between 0 and C^16_8 - 1 representing the location of
	 * all eight elements among 16 and a number between 0 and C^8_4 - 1 representing how the four elements from the one of the input numbers
	 * are disposed among 8. As we don't care of which element come from each number, we can swap the numbers so that the second number is 
	 * always below C^15_4, which reduce some space.
	 * We also keep track of how many inversions there are between the four elements from the first input number and those from the second input number.
	 * Because of the swap thing from earlier, we need two arrays, the second array for when the input numbers are swapped.
	 * Finally, we also compute the number of inversions between the eight elements coming from the two input numbers and the other eight elements.
	 * We don't need to keep the exact number of inversion, just the parity. */
	static final int C16_4 = 1820;
	static final int C15_4 = 1365;
	static final int C16_8 = 12870;
	public static final int[][] C16to16 = new int[C16_4][C15_4];
	public static final byte[] parityC16_4 = new byte[C16_4*C15_4/8+1];
	public static final byte[] paritySwapC16_4 = new byte[C16_4*C15_4/8+1];
	public static final byte[] parityC16_8 = new byte[C16_4*C15_4/8+1];

	public static final void initC16to16() {
		for (int i_loc8=0; i_loc8<C16_8; i_loc8++){
			for (int i_loc4=0; i_loc4<C8_4/2; i_loc4++){
				int loc8 = i_loc8;
				int loc4 = i_loc4;
				int r8 = 8;
				int r4 = 4;

				int loc4a = 0;
				int loc4b = 0;

				int j = 7;

				int inva = 0;
				int invb = 0;
				int inv = 0;

				for (int i=15; i>=0; i--){
					if( loc8 >= Cnk[i][r8] ){
						loc8 -= Cnk[i][r8];
						
						if( loc4 >= Cnk[j][r4] ){
							loc4 -= Cnk[j][r4];
							loc4a += Cnk[i][r4];
							inva += r8-r4;
							r4--;
						}
						else{
							loc4b += Cnk[i][r8-r4];
							invb += r4;
						}
						r8--;
						j--;
					}
					else
						inv += r8;
				}
				if( loc4b > loc4a ){
					int temp = loc4a;
					loc4a = loc4b;
					loc4b = temp;
				}

				C16to16[loc4a][loc4b] = 35*i_loc8 + i_loc4;

				if(( inva & 0x1 ) != 0 )
					set1bit( parityC16_4, loc4a * C15_4 + loc4b );
				if(( invb & 0x1 ) != 0 )
					set1bit( paritySwapC16_4, loc4a * C15_4 + loc4b );
				if(( inv & 0x1 ) != 0 )
					set1bit( parityC16_8, loc4a * C15_4 + loc4b );
			}
		}
	}

	public static final int[] shiftC16 = new int[C16_4];

	public static final void initShiftC16() {
		for (int p=0; p<C8_4; p++){
			int t = p;
			int r = 4;
			int c12 = 0;
			int c16 = 0;
			for (int i=7; i>=0; i--){
				if( t >= Cnk[i][r] ){
					t -= Cnk[i][r];
					c12 += Cnk[i+4][r];
					c16 += Cnk[(i<4)?i:i+8][r--];
				}	
			}
			shiftC16[c12] = p;
			shiftC16[c16] = p;
		}
	}

	public static final byte[] C24_4to12 = new byte[C24_4];
	public static final void initC24_4to12(){
		final short squares_cen_map[] = { 15, 60, 85, 90, 102, 105, 150, 153, 165, 170, 195, 240 };
		for (int i=0; i<12; i++){
			int x = squares_cen_map[i];
			int r = 4;
			int c8 = 0;
			int c16 = 0;
			int c24 = 0;
			for (int j=7; j>=0; j--){
				if((( x >>> (7-j) ) & 0x1 ) != 0 ){
					c8 += Cnk[j][r];
					c16 += Cnk[j+8][r];
					c24 += Cnk[j+16][r--];
				}
			}
			C24_4to12[c8] = (byte)i;
			C24_4to12[c16] = (byte)i;
			C24_4to12[c24] = (byte)i;
		}
	}

	/*** init_parity_table ***/
	public static final boolean[] parity_perm8_table = new boolean[40320];

	private static final int get_parity8 (int x){
		int i, j;
		int parity = 0;
		byte[] t = new byte[8];
		set8Perm (t, x);
		for (i = 0; i < 7; ++i) {
			if (t[i] == i) {
				continue;
			}
			for (j = i + 1; j < 8; ++j) {
				if (t[j] == i) {
					//"swap" the i & j elements, but don't bother updating the "i"-element
					//as it isn't needed anymore.
					t[j] = t[i];
				}
			}
			parity ^= 1;
		}
		return parity;
	}

	public static void initParityTable (){
		int x;

		for (x = 0; x < 40320; ++x) {
			parity_perm8_table[x] = (get_parity8 (x) != 0);
		}
	}

	/*** init map96 ***/
	public static final byte[][] map96 = new byte[96][8];

	private static void initMap96 (){
		int a1, i;
		byte[] t = new byte[8];
		byte f;
		for (a1 = 0; a1 < 24; ++a1) {
			set4Perm (t, a1);
			for (i = 0; i < 4; ++i) {
				t[i+4] = (byte)(t[i] + 4);
			}
			for (i = 0; i < 8; ++i) {
				map96[4*a1][i] = t[i];
			}
			f = t[4]; t[4]= t[5]; t[5] = f;
			f = t[6]; t[6]= t[7]; t[7] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 1][i] = t[i];
			}
			f = t[4]; t[4]= t[7]; t[7] = f;
			f = t[5]; t[5]= t[6]; t[6] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 2][i] = t[i];
			}
			f = t[4]; t[4]= t[5]; t[5] = f;
			f = t[6]; t[6]= t[7]; t[7] = f;
			for (i = 0; i < 8; ++i) {
				map96[4*a1 + 3][i] = t[i];
			}
		}
	}

	/*** init_perm_to_420 ***/
	public static final short[] perm_to_420 = new short[40320]; // (420)
	public static final byte[][] perms_to_6 = new byte[24][24]; // (6)

	public static void initPerm420 (){
		int i;
		int u, v, w, u2;
		byte[] t = new byte[8];
		byte[] t2 = new byte[8];
		byte[] t3 = new byte[8];

		for (v = 0; v < 6; ++v) {
			set8Perm (t, v);
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i)
					t2[i] = map96[w][t[i]];
				perms_to_6[get4Perm(t2, 0)][get4Perm(t2, 4)] = (byte)v;
				for (u = 0; u < 70; ++u) {
					int f = 0;
					int b = 4;
					int p = u;
					int r = 4;
					for (i = 7; i >= 0; --i) {
						if (p >= Cnk[i][r]) {
							p -= Cnk[i][r--];
							t3[i] = t2[f++];
						} else {
							t3[i] = t2[b++];
						}
					}
					u2 = get8Perm (t3, 0);
					perm_to_420[u2] = (short)(6*u + v);
				}
			}
		}
	}
	
	static final int FACT4 = 24;
	static final int C8_4 = 70;

	static final byte[][] s4mul = new byte[FACT4][FACT4];
	static final byte[] parity_s4 = new byte[FACT4/8+1];

	static void init_s4(){
		byte[] t1 = new byte[4];
		byte[] t2 = new byte[4];
		byte[] t3 = new byte[4];
		for (int i = 0; i < FACT4; i++ ){
			for (int j = 0; j < FACT4; j++ ){
				set4Perm( t1, i );
				set4Perm( t2, j );
				for( int k = 0; k < 4; k++ )
					t3[k] = t2[t1[k]];
				s4mul[j][i] = (byte) get4Perm( t3, 0 );
			}
			int parity = 0;
			for (int a = 0; a < 3; a++ )
				for (int b = a+1; b < 4; b++ )
					if( t1[a] > t1[b] )
						parity ^= 1;
			if(( parity & 0x1 ) != 0)
				set1bit( parity_s4, i );
		}
	}
}
