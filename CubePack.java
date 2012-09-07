package cg.fivestage444;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Coordinates.Corner4;
import java.util.Arrays;

/* CubePack structure: a (almost) full representation of the cube using a set of coordinates.
 * Some code is taken from cube20 */

public final class CubePack{

	static final int N_MOVES = 36;

	static final int C8_4 = 70;
	static final int FACT4 = 24;

	/** Coordinates **/

	/* Corners */
	byte corner_top_loc; // Location of the top four corners.
	byte corner_top_perm, corner_bottom_perm; // Permutation of top and bottom corners.

	public CubePack(){
		corner_top_loc = 0;
		corner_top_perm = 0;
		corner_bottom_perm = 0;
	}

	public void copyTo( CubePack cp ){
		cp.corner_top_loc = this.corner_top_loc;
		cp.corner_top_perm = this.corner_top_perm;
		cp.corner_bottom_perm = this.corner_bottom_perm;
	}

	/** Tables **/
	static final byte[] s4inv = new byte[FACT4];
	static final byte[][] s4mul = new byte[FACT4][FACT4];
	static final byte[] s4compress = new byte[256];
	static final byte[] s4expand = new byte[FACT4];

	static final byte[] c8_4_compact = new byte[256];
	static final byte[] c8_4_expand = new byte[C8_4];
//	static final byte c8_4_parity[C8_4];

	static final int[][] move_cperm = new int[C8_4][N_MOVES];

	/** Init tables **/

	static void init_s4(){
		int cc = 0;
		for (int a = 0; a < 4; a++ )
			for (int b = 0; b < 4; b++ )
				if (a == b)
					for (int c = 0; c < 4; c++ )
						if (( a == c ) && ( b == c )) {
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

	static void init_moveCorners(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<C8_4; i++) {
			cp1.corner_top_loc = (byte)i;
			cp1.unpackCorners( cube1 );
			for (int mv=0; mv<N_MOVES; mv++) {
				cube1.rotate_sliceCORNER( stage2moves[mv], cube2 );
				cp2.packCorners( cube2 );
				move_cperm[i][mv] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm ;
			}
		}
	}

	/** Packing and Unpacking **/

	void packCorners( CubeState cube ){
		final byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		final byte std_to_sqs[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		int ctl = 0;
		int ctp = 0;
		int cbp = 0;
		for (int i=7; i>=0; i--) {
			int perm = std_to_sqs[cube.m_cor[sqs_to_std[i]] & 0x7];
			if (( perm & 4 ) != 0) { // bottom layer
				cbp = 4 * cbp + (perm & 3);
			} else {
				ctl |= 1<<i;
				ctp = 4 * ctp + (perm & 3);
			}
		}
		corner_top_loc = c8_4_compact[ctl];
		corner_top_perm = s4compress[ctp];
		corner_bottom_perm = s4compress[cbp];
	}

	void unpackCorners( CubeState cube ){
		final byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		final byte std_to_sqs[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		int c8_4_bits = c8_4_expand[corner_top_loc];
		int ct_perm = s4expand[corner_top_perm];
		int cb_perm = s4expand[corner_bottom_perm];
		for (int i=0; i<8; i++)
			if (((c8_4_bits >> i) & 1 ) != 0) { // top layer
				cube.m_cor[sqs_to_std[i]] = sqs_to_std[3 & ct_perm];
				ct_perm >>= 2 ;
			} else {
				cube.m_cor[sqs_to_std[i]] = sqs_to_std[(3 & cb_perm) + 4];
				cb_perm >>= 2 ;
			}
	}

	/** Convert to Coordinates **/

	public void toCorner4( Corner4 c ){
		c.coord = 6 * corner_top_loc; // + ...
	}


	/** Move functions **/
	
	public final void moveCorners(int m){
		int t = move_cperm[corner_top_loc][m] ;
		corner_top_loc = (byte)(t >> 10);
		corner_top_perm = s4mul[corner_top_perm][(t >> 5) & 31];
		corner_bottom_perm = s4mul[corner_bottom_perm][t & 31];
	}

}

