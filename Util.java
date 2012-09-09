package cg.fivestage444;

import java.util.Arrays;

public final class Util {

	public static final void init (){
		initCnk();
		initC24to8();
		initC16to16();
		initParityTable();
		initMap96();

		init_s4();
		init_c8_4();
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

	static final int C16_4 = 1820;
	static final int C15_4 = 1365;
	public static final int[][] C16to16 = new int[C16_4][C15_4];

	public static final void initC16to16() {
		for (int p=0; p<C16_4; p++){
			for (int q=0; q<C15_4; q++){
				int tp = p;
				int tq = q;
				int rp = 4;
				int rq = 4;
				int c8 = 0;
				int r = 8;
				int c4 = 0;
				int s = 4;
				int j = 7;
				int first_cen = 0;
				for (int i=15; i>=0; i--){
					if( tp >= Cnk[i][rp] ){
						tp -= Cnk[i][rp--];
						c8 += Cnk[i][r--];
						if( first_cen == 0 ) first_cen = 1;
						if( first_cen != 1 )
							c4 += Cnk[j][s--];
						j--;
					}	
					if( tq >= Cnk[i][rq] ){
						tq -= Cnk[i][rq--];
						c8 += Cnk[i][r--];
						if( first_cen == 0 ) first_cen = -1;
						if( first_cen != -1 )
							c4 += Cnk[j][s--];
						j--;
					}	
				}
				C16to16[p][q] = 35*c8 + c4;
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
				int topexp = ((t2[3]*4+t2[2])*4+t2[1])*4+t2[0];
				int botexp = (((t2[7]-4)*4+t2[6]-4)*4+t2[5]-4)*4+t2[4]-4;
				perms_to_6[s4compress[topexp]][s4compress[botexp]] = (byte)v;
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

	static final byte[] s4inv = new byte[FACT4];
	static final byte[][] s4mul = new byte[FACT4][FACT4];
	static final byte[] s4compress = new byte[256];
	static final byte[] s4expand = new byte[FACT4];

	static final byte[] c8_4_compact = new byte[256];
	static final byte[] c8_4_expand = new byte[C8_4];
//	static final byte c8_4_parity[C8_4];

	static void init_s4(){
		int cc = 0;
		for (int a = 0; a < 4; a++ )
			for (int b = 0; b < 4; b++ )
				if (a != b)
					for (int c = 0; c < 4; c++ )
						if (( a != c ) && ( b != c )) {
							int d = 0 + 1 + 2 + 3 - a - b - c;
							int coor = cc ^ ((cc >> 1) & 1);
							int expanded = (1 << (2 * b)) + (2 << (2 * c)) + (3 << (2 * d));
							s4compress[expanded] = (byte)coor;
							s4expand[coor] = (byte)expanded;
							cc++;
						}
		for (int i = 0; i < FACT4; i++ )
			for (int j = 0; j < FACT4; j++ ) {
				int k = s4compress[muls4(s4expand[i], s4expand[j])];
				s4mul[j][i] = (byte)k;
				if (k == 0) s4inv[i] = (byte)j;
			}
	}

	static int muls4(int a, int b) {
		int r = 3 & (b >> (2 * (a & 3)));
		r += (3 & (b >> (2 * ((a >> 2) & 3)))) << 2;
		r += (3 & (b >> (2 * ((a >> 4) & 3)))) << 4;
		r += (3 & (b >> (2 * ((a >> 6) & 3)))) << 6;
		return r ;
	}

	static void init_c8_4(){
		int c = 0;
		for (int i=0; i<256; i++)
			if (bc(i) == 4) {
/*				int parity = 0 ;
				for (int j=0; j<8; j++)
					if (1 & (i >> j))
						for (int k=0; k<j; k++)
							if (0 == (1 & (i >> k)))
								parity++;
				c8_4_parity[c] = parity & 1; */
				c8_4_compact[i] = (byte)c;
				c8_4_expand[c] = (byte)i;
				c++;
			}
	}

	static int bc(int v) {
		int r = 0;
		while (v != 0) {
			v &= v - 1;
			r++;
		}
		return r;
	}
}
