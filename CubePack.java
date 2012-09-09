package cg.fivestage444;

import cg.fivestage444.Coordinates.Corner4;
import java.util.Arrays;

/* CubePack structure: a (almost) full representation of the cube using a set of coordinates.
 * Some code is taken from cube20 */

public final class CubePack{

	/** Coordinates **/

	/* Corners */
	byte corner_top_loc; // Location of the top four corners (70).
	byte corner_top_perm, corner_bottom_perm; // Permutation of top and bottom corners (24).
	/* Centers */
	short[] centers = new short[6]; // Location of each center (10626).
	short[] edges_loc = new short[6]; // Location of each group of 4 edges (10626).
	byte[] edges_perm = new byte[6]; // Permutation of each group of 4 edges (24).
	final static int CENTER_U = 0;
	final static int CENTER_D = 1;
	final static int CENTER_L = 2;
	final static int CENTER_R = 3;
	final static int CENTER_F = 4;
	final static int CENTER_B = 5;

	public CubePack(){
		corner_top_loc = 0;
		corner_top_perm = 0;
		corner_bottom_perm = 0;
	}

	public void copyTo( CubePack cp ){
		cp.corner_top_loc = this.corner_top_loc;
		cp.corner_top_perm = this.corner_top_perm;
		cp.corner_bottom_perm = this.corner_bottom_perm;
		for( int i=0; i<6; i++ ){
			cp.centers[i] = this.centers[i];
			cp.edges_loc[i] = this.edges_loc[i];
			cp.edges_perm[i] = this.edges_perm[i];
		}
	}

	/** Tables **/

	static final int[][] move_cperm = new int[Util.C8_4][Moves.N_STAGE_MOVES];
	static final int[][] conj_cperm = new int[Util.C8_4][Symmetry.N_SYM];
	static final int[][] move_edges = new int[10626][Moves.N_STAGE_MOVES];
	static final int[][] conj_edges = new int[10626][Symmetry.N_SYM];
	static final short[][] move_centers = new short[10626][Moves.N_STAGE_MOVES];
	static final short[][] conj_centers = new short[10626][Symmetry.N_SYM];

	/** Packing and Unpacking **/

	void pack( CubeState cube ){
		packCorners( cube );
		for( int i=0; i<6; i++){
			packCenters( cube, i );
			packEdges( cube, i );
		}
	}

	void unpack( CubeState cube ){
		unpackCorners( cube );
		for( int i=0; i<6; i++){
			unpackCenters( cube, i );
			unpackEdges( cube, i );
		}
	}

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

	void packCenters (CubeState cube, int c_idx){
		this.centers[c_idx] = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (cube.m_cen[i] == c_idx) {
				this.centers[c_idx] += Util.Cnk[i][r--];
			}
		}
	}

	void unpackCenters (CubeState cube, int c_idx){
		int center = this.centers[c_idx];
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (center >= Util.Cnk[i][r]) {
				center -= Util.Cnk[i][r--];
				cube.m_cen[i] = (byte)c_idx;
			}
			else
				cube.m_cen[i] = -1;
		}
	}

	void packEdges( CubeState cube, int e_idx ){
		int r = 4;
		int perm = 0;
		this.edges_loc[e_idx] = 0;

		for (int i=23; i>=0; i--) {
			if (( cube.m_edge[i] / 4 ) == e_idx ){
				this.edges_loc[e_idx] += Util.Cnk[i][r--];
				perm = 4 * perm + ( cube.m_edge[i] & 0x3 );
			}
		}
		this.edges_perm[e_idx] = Util.s4compress[perm];
	}

	void unpackEdges( CubeState cube, int e_idx ){
		int perm = Util.s4expand[this.edges_perm[e_idx]];
		int loc = this.edges_loc[e_idx];
		int r = 4;
		for (int i=23; i>=0; i--)
			if (loc >= Util.Cnk[i][r]) {
				loc -= Util.Cnk[i][r--];
				cube.m_edge[i] = (byte)( 4*e_idx + (( perm >>> (2*r)) & 0x3 ));
			}
			else
				cube.m_edge[i] = -1;
	}

	/** Init tables **/

	static void init(){
		init_moveCorners();
		init_conjCorners();
		init_moveEdges();
		init_conjEdges();
		init_moveCenters();
		init_conjCenters();
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
				move_cperm[i][mv] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm;
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
				conj_cperm[i][sym] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm;
			}
		}
	}

	static void init_moveEdges(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<10626; i++) {
			cp1.edges_loc[2] = (short)i;
			cp1.unpackEdges( cube1, 2 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES; mv++) {
				cube1.rotate_sliceEDGE( Moves.stage2moves[mv], cube2 );
				cp2.packEdges( cube2, 2 );
				move_edges[i][mv] = (cp2.edges_loc[2] << 5) + cp2.edges_perm[2];
			}
		}
	}

	static void init_conjEdges(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<10626; i++) {
			cp1.edges_loc[2] = (short)i;
			cp1.unpackEdges( cube1, 2 );
			for (int sym=0; sym<Symmetry.N_SYM; sym++) {
				cube1.rightMultEdges( sym, cube2 );
				cp2.packEdges( cube2, 2 );
				conj_edges[i][sym] = (cp2.edges_loc[2] << 5) + cp2.edges_perm[2];
			}
		}
	}

	static void init_moveCenters(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<10626; i++) {
			cp1.centers[3] = (short)i;
			cp1.unpackCenters( cube1, 3 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES; mv++) {
				cube1.rotate_sliceCENTER( Moves.stage2moves[mv], cube2 );
				cp2.packCenters( cube2, 3 );
				move_centers[i][mv] = cp2.centers[3];
			}
		}
	}

	static void init_conjCenters(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int i=0; i<10626; i++) {
			cp1.centers[2] = (short)i;
			cp1.unpackCenters( cube1, 2 );
			for (int sym=0; sym<Symmetry.N_SYM; sym++) {
				cube1.rightMultCenters( sym, cube2 );
				cp2.packCenters( cube2, 2 );
				conj_centers[i][sym] = cp2.centers[2];
			}
		}
	}

	/** Convert to Coordinates **/

	public void toCorner4( Corner4 c ){
		c.coord = 6 * corner_top_loc + Util.perms_to_6[corner_top_perm][corner_bottom_perm];
	}

	/** Move functions **/

	public final void move(int m){
		moveCorners(m);
		for (int i=0; i<6; i++){
			moveEdges(m, i);
			moveCenters(m, i);
		}
	}

	public final void moveCorners(int m){
		int t = move_cperm[corner_top_loc][m];
		corner_top_loc = (byte)(t >> 10);
		corner_top_perm = Util.s4mul[corner_top_perm][(t >> 5) & 31];
		corner_bottom_perm = Util.s4mul[corner_bottom_perm][t & 31];
	}

	public final void moveEdges(int m, int e_idx){
		int t = move_edges[this.edges_loc[e_idx]][m];
		this.edges_loc[e_idx] = (short)(t >> 5);
		this.edges_perm[e_idx] = Util.s4mul[this.edges_perm[e_idx]][t & 31];
	}

	public final void moveCenters(int m, int c_idx){
		this.centers[c_idx] = move_centers[this.centers[c_idx]][m];
	}

	public final void conjCorners(int sym){
		int t = conj_cperm[corner_top_loc][sym] ;
		corner_top_loc = (byte)(t >> 10);
		corner_top_perm = Util.s4mul[corner_top_perm][(t >> 5) & 31];
		corner_bottom_perm = Util.s4mul[corner_bottom_perm][t & 31];
	}

	/** Debug **/

	public void print(){

		System.out.println("c_top_loc:"+corner_top_loc+" - c_top_perm:"+corner_top_perm+" - c_bot_perm:"+corner_bottom_perm);
		System.out.print("centers: ");
		for (int i=0; i<6; i++){
			System.out.print(centers[i]+" - ");
		}
		System.out.println();
		System.out.print("edges_loc: ");
		for (int i=0; i<6; i++){
			System.out.print(edges_loc[i]+" - ");
		}
		System.out.println();
		System.out.print("edges_perm: ");
		for (int i=0; i<6; i++){
			System.out.print(edges_perm[i]+" - ");
		}
		System.out.println();
	}
}

