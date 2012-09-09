package cg.fivestage444;

import cg.fivestage444.Coordinates.Corner4;
import java.util.Arrays;

/* CubePack structure: a (almost) full representation of the cube using a set of coordinates.
 * Some code is taken from cube20 */

public final class CubePack{

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

	static final int[][] move_cperm = new int[Util.C8_4][Moves.N_STAGE_MOVES];
	static final int[][] conj_cperm = new int[Util.C8_4][Symmetry.N_SYM];

	/** Init tables **/

	static void init(){
		init_moveCorners();
		init_conjCorners();
	}

	static void init_moveCorners(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<Util.C8_4; i++) {
			cp1.corner_top_loc = (byte)i;
			cp1.unpackCorners( cube1 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES; mv++) {
				cube1.rotate_sliceCORNER( Moves.stage2moves[mv], cube2 );
				cp2.packCorners( cube2 );
				move_cperm[i][mv] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm ;
			}
		}
	}

	static void init_conjCorners(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<Util.C8_4; i++) {
			cp1.corner_top_loc = (byte)i;
			cp1.unpackCorners( cube1 );
			for (int sym=0; sym<Symmetry.N_SYM; sym++) {
				cube1.rightMultCorners( sym, cube2 );
				cp2.packCorners( cube2 );
				conj_cperm[i][sym] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm ;
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
		corner_top_loc = Util.c8_4_compact[ctl];
		corner_top_perm = Util.s4compress[ctp];
		corner_bottom_perm = Util.s4compress[cbp];
	}

	void unpackCorners( CubeState cube ){
		final byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		final byte std_to_sqs[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		int c8_4_bits = Util.c8_4_expand[corner_top_loc];
		int ct_perm = Util.s4expand[corner_top_perm];
		int cb_perm = Util.s4expand[corner_bottom_perm];
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
		c.coord = 6 * corner_top_loc + Util.perms_to_6[corner_top_perm][corner_bottom_perm];
	}

	/** Move functions **/
	
	public final void moveCorners(int m){
		int t = move_cperm[corner_top_loc][m];
		corner_top_loc = (byte)(t >> 10);
		corner_top_perm = Util.s4mul[corner_top_perm][(t >> 5) & 31];
		corner_bottom_perm = Util.s4mul[corner_bottom_perm][t & 31];
	}

	public final void conjCorners(int sym){
		int t = conj_cperm[corner_top_loc][sym] ;
		corner_top_loc = (byte)(t >> 10);
		corner_top_perm = Util.s4mul[corner_top_perm][(t >> 5) & 31];
		corner_bottom_perm = Util.s4mul[corner_bottom_perm][t & 31];
	}

}

