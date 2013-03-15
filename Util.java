package cg.fivestage444;

public final class Util {

	public static void init (){
		initCnk();
		initParityTable();
		initMap96();
		initPerm420();
	}

	/**
	 * We need to compute the binomial coefficients and the factorials for further functions.
	 * Cnk[n][k] = n! / (k! (n-k)! )
	 * fact[n] = n!
	 */
	public static final int Cnk [][] = new int[25][25];
	private static final int[] fact  = new int[26];

	private static void initCnk() {
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
	 * @param off	index of the first element where the permutation starts in the table
	 * @return		an integer representing the permutation
	 */
	public static int get4Perm (byte[] array_in, int off){
		int idx = 0;
		int val = 0x3210;
		for (int i=0; i<3; i++) {
			int v = (array_in[i+off]-off) << 2;
			idx = (4 - i) * idx + ((val >> v) & 0x7);
			val -= 0x1110 << v;
		}
		return idx;
	}

	/**
	 * Converts an array of integers from off to off+8-1 into a corresponding number from 0 to 8!-1.
	 * Faster version. Taken from Chen Shuang (min2phase).
	 * @param array_in	permutation
	 * @param off	index of the first element where the permutation starts in the table
	 * @return		an integer representing the permutation
	 */
	public static int get8Perm (byte[] array_in, int off){
		int idx = 0;
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int v = (array_in[i+off]-off) << 2;
			idx = (8 - i) * idx + ((val >> v) & 0x7);
			val -= 0x11111110 << v;
		}
		return idx;
	}

	/**
	 * Converts an integer into a permutation represented as an array of integers from 0 to 3.
	 * @param arr		the permutation coded as an array of integers
	 * @param idx		an integer representing the permutation
	 */
	public static void set4Perm (byte[] arr, int idx) {
		int val = 0x3210;
		for (int i=0; i<3; i++) {
			int p = fact[3-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (byte) ((val >> v) & 0x7);
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
	private static void set8Perm(byte[] arr, int idx) {
		int val = 0x76543210;
		for (int i=0; i<7; i++) {
			int p = fact[7-i];
			int v = idx / p;
			idx -= v*p;
			v <<= 2;
			arr[i] = (byte) ((val >> v) & 0x7);
			int m = (1 << v) - 1;
			val = (val & m) + ((val >> 4) & ~m);
		}
		arr[7] = (byte)val;
	}

	/**
	 * Set one bit to 1 in the table at a certain index.
	 * @param table table where the bit is set
	 * @param index index at which the bit is set
	 */
	public static void set1bit(byte[] table, long index) {
		table[(int)(index>>>3)] |= (byte)( 1 << ( index & 0x7L ));
	}

	/**
	 * Get the state of one bit in the table at a certain index.
	 * @param table table where the bit is fetched
	 * @param index index at which the bit is fetched
	 * @return the state of the bit
	 */
	public static boolean get1bit(final byte[] table, long index) {
		return (( table[(int)(index>>>3)] >>> ( index & 0x7L )) & 1 ) != 0;
	}

	/*** init_parity_table ***/
	/* TODO: We could encode the parity state into one of the bits in {get,set}8Perm functions,
	 * which would remove the need for the following code.
	 */
	public static final boolean[] parity_perm8_table = new boolean[40320];

	/**
	 * Determine the parity of a permutation of 8 elements.
	 * @param x the integer encoding for the permutation of 8 elements, usually encoded with get8Perm.
	 * @return 0 if the parity is even, 1 if the parity is odd.
	 */
	private static int get_parity8 (int x){
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

	/**
	 * Store the parity of each permutation of 8 elements into an array.
	 */
	private static void initParityTable(){
		for (int x = 0; x < 40320; ++x) {
			parity_perm8_table[x] = (get_parity8 (x) != 0);
		}
	}

	/**
	 * The next two functions are helpers to decompose the S_8 group (set of permutations of 8 elements).
	 * If you consider corners, the square subgroup (generated by half turns only) has 96 elements.
	 * The number of cosets related to this subgroup is then 40320/96 = 420 elements.
	 * We need to be able to tell in which coset an element of S_8 belongs.
	 * Currently, we are doing it in a rather straightforward way.
	 */

	 /* The map96 lists all the elements of the square subgroup. */
	private static final byte[][] map96 = new byte[96][8];

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

	/** We produce every permutation of 8 elements by composing every element of the square subgroup with every coset.
	 * The elements of the square group has already been enumerated above.
	 * The cosets can be decomposed into a permutation of 3 elements (6 cases)
	 * and a 4-element subset among 8 elements (C(8,4) = 70 cases).
	 * Then, we map every permutation to the corresponding coset.
	 */
	public static final short[] perm_to_420 = new short[40320];

	private static void initPerm420(){
		int i;
		int u, v, w, u2;
		byte[] perm3 = new byte[8];
		byte[] square_perm = new byte[8];
		byte[] perm8 = new byte[8];

		for (v = 0; v < 6; ++v) {
			set8Perm (perm3, v); /* A permutation of the first 3 elements. */
			for (w = 0; w < 96; ++w) {
				for (i = 0; i < 8; ++i)
					square_perm[i] = map96[w][perm3[i]]; /* We compose with an element of the square group */
				for (u = 0; u < 70; ++u) {
					int f = 0;
					int b = 4;
					int p = u;
					int r = 4;
					for (i = 7; i >= 0; --i) {
						if (p >= Cnk[i][r]) {
							p -= Cnk[i][r--];
							perm8[i] = square_perm[f++];
						} else {
							perm8[i] = square_perm[b++];
						} /* We compose again with a 4-combination */
					}
					u2 = get8Perm (perm8, 0);
					perm_to_420[u2] = (short)(6*u + v); /* The encoding coset */
				}
			}
		}
	}

	/**
	 * Perform the permutation cycle (a b c d) on the tab array.
	 * @param tab the array where elements are permuted.
	 * @param a first element to permute
	 * @param b second element to permute
	 * @param c third element to permute
	 * @param d forth element to permute
	 * @param times number of times to apply the permutation
	 */
	public static void cycle(byte[] tab, int a, int b, int c, int d, int times){
		if(times <= 0) return;
		byte temp = tab[d];
		tab[d] = tab[c];
		tab[c] = tab[b];
		tab[b] = tab[a];
		tab[a] = temp;
		if(times > 1)
			cycle(tab, a, b, c, d, times - 1);
	}

	/**
	 * Perform the permutation cycle (a b c d) on the tab array.
	 * Also orient clockwise first and third elements and counter-clockwise the other ones.
	 * @param tab the array where elements are permuted.
	 * @param a first element to permute
	 * @param b second element to permute
	 * @param c third element to permute
	 * @param d forth element to permute
	 * @param times number of times to apply the permutation
	 */
	public static void cycleAndOrient(byte[] tab, int a, int b, int c, int d, int times){
		if(times <= 0) return;
		byte temp = tab[d];
		tab[d] = (byte)((tab[c] + 8) % 24);
		tab[c] = (byte)((tab[b] + 16) % 24);
		tab[b] = (byte)((tab[a] + 8) % 24);
		tab[a] = (byte)((temp + 16) % 24);
		if(times > 1)
			cycleAndOrient(tab, a, b, c, d, times - 1);
	}
}
