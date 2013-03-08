package cg.fivestage444;

import cg.fivestage444.Coordinates.RawCoordState;
import cg.fivestage444.Coordinates.SymCoordState;
import cg.fivestage444.Cubies.CenterCubies;
import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

/* CubePack structure: a (almost) full representation of the cube using a set of coordinates.
 * Some code and ideas are taken from cube20 */

final class CubePack{

	/** Coordinates **/

	/* Corners */
	private byte corner_top_loc; // Location of the top four corners (70).
	private byte corner_top_perm;
	private byte corner_bottom_perm; // Permutation of top and bottom corners (24).
	/* Centers */
	private final short[] centers = new short[6]; // Location of each center (10626).
	private final short[] edges_loc = new short[6]; // Location of each group of 4 edges (10626).
	private final byte[] edges_perm = new byte[6]; // Permutation of each group of 4 edges (24).

	/** Tables **/

	private static final int N_ROT = 3;
	private static final int ROTATE_U = Moves.N_STAGE_MOVES + 0;
	private static final int ROTATE_UR3 = Moves.N_STAGE_MOVES + 1;
	private static final int ROTATE_RU3 = Moves.N_STAGE_MOVES + 2;
	private static final int[] rotations = {8, 16, 32};

	private static final int[][] move_cperm = new int[Util.C8_4][Moves.N_STAGE_MOVES+N_ROT];
	private static final int[][] move_edges = new int[Util.C24_4][Moves.N_STAGE_MOVES+N_ROT];
	private static final short[][] move_centers = new short[Util.C24_4][Moves.N_STAGE_MOVES+N_ROT];


	/** Packing and Unpacking **/

	void pack( CubeState cube ){
		packCorners( cube.corners );
		for( int i=0; i<6; i++){
			packCenters( cube.centers, i );
			packEdges( cube.edges, i );
		}
	}

	void packCorners( CornerCubies cube ){
		int ctl = 0;
		byte[] ctp = new byte[4];
		byte[] cbp = new byte[4];
		int itp = 3;
		int ibp = 3;
		int r = 4;
		for (int i=7; i>=0; i--) {
			int perm = cube.cubies[i] & 0x7;
			if (( perm & 0x4 ) != 0) { // bottom layer
				cbp[ibp--] = (byte)( perm & 0x3 );
			} else {
				ctl += Util.Cnk[i][r--];
				ctp[itp--] = (byte)( perm & 0x3 );
			}
		}
		corner_top_loc = (byte) ctl;
		corner_top_perm = (byte) Util.get4Perm(ctp, 0);
		corner_bottom_perm = (byte) Util.get4Perm(cbp, 0);
	}

	void unpackCorners( CornerCubies cube ){
		int c8_4_bits = corner_top_loc;
		int r = 4;
		byte[] ct_perm = new byte[4];
		byte[] cb_perm = new byte[4];
		int itp = 3;
		int ibp = 3;
		Util.set4Perm(ct_perm, corner_top_perm);
		Util.set4Perm(cb_perm, corner_bottom_perm);
		for (int i = 7; i >=0; i--)
			if ( c8_4_bits >= Util.Cnk[i][r] ) { // top layer
				c8_4_bits -= Util.Cnk[i][r--];
				cube.cubies[i] = ct_perm[itp--];
			} else {
				cube.cubies[i] = (byte)( cb_perm[ibp--] + 4 );
			}
	}

	void packCenters (CenterCubies cube, int c_idx){
		this.centers[c_idx] = 0;
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (cube.cubies[i] == c_idx) {
				this.centers[c_idx] += Util.Cnk[i][r--];
			}
		}
	}

	void unpackCenters (CenterCubies cube, int c_idx){
		int center = this.centers[c_idx];
		int r = 4;
		for (int i=23; i>=0; i--) {
			if (center >= Util.Cnk[i][r]) {
				center -= Util.Cnk[i][r--];
				cube.cubies[i] = (byte) c_idx;
			}
			else
				cube.cubies[i] = -1;
		}
	}

	void packEdges( EdgeCubies cube, int e_idx ){
		int r = 4;
		this.edges_loc[e_idx] = 0;
		byte[] t = new byte[4];
		int it = 3;

		for (int i=23; i>=0; i--) {
			if (( cube.cubies[i] / 4 ) == e_idx ){
				this.edges_loc[e_idx] += Util.Cnk[i][r--];
				t[it--] = (byte)( cube.cubies[i] & 0x3 );
			}
		}
		this.edges_perm[e_idx] = (byte) Util.get4Perm( t, 0 );
	}

	void unpackEdges( EdgeCubies cube, int e_idx ){
		byte[] t = new byte[4];
		Util.set4Perm( t, this.edges_perm[e_idx] );
		int it = 3;
		int loc = this.edges_loc[e_idx];
		int r = 4;
		for (int i=23; i>=0; i--)
			if (loc >= Util.Cnk[i][r]) {
				loc -= Util.Cnk[i][r--];
				cube.cubies[i] = (byte)( 4*e_idx + t[it--] );
			}
			else
				cube.cubies[i] = -1;
	}

	/** Init tables **/

	static void init(){
		init_moveCorners();
		init_moveEdges();
		init_moveCenters();
	}

	private static void init_moveCorners(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CornerCubies cube1 = new CornerCubies();
		CornerCubies cube2 = new CornerCubies();
		for (int i=0; i<Util.C8_4; i++) {
			cp1.corner_top_loc = (byte)i;
			cp1.unpackCorners( cube1 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES + N_ROT; mv++) {
				if( mv >= Moves.N_STAGE_MOVES)
					cube1.rightMult( rotations[mv - Moves.N_STAGE_MOVES], cube2 );
				else
					cube1.move( Moves.stage2moves[mv], cube2 );
				cp2.packCorners( cube2 );
				move_cperm[i][mv] = (cp2.corner_top_loc << 10) + (cp2.corner_top_perm << 5) + cp2.corner_bottom_perm;
			}
		}
	}

	private static void init_moveEdges(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		for (int i=0; i<Util.C24_4; i++) {
			cp1.edges_loc[2] = (short)i;
			cp1.unpackEdges( cube1, 2 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES + N_ROT; mv++) {
				if( mv >= Moves.N_STAGE_MOVES)
					cube1.rightMult( rotations[mv - Moves.N_STAGE_MOVES], cube2 );
				else
					cube1.move( Moves.stage2moves[mv], cube2 );
				cp2.packEdges( cube2, 2 );
				move_edges[i][mv] = (cp2.edges_loc[2] << 5) + cp2.edges_perm[2];
			}
		}
	}

	private static void init_moveCenters(){
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CenterCubies cube1 = new CenterCubies();
		CenterCubies cube2 = new CenterCubies();
		for (int i=0; i<Util.C24_4; i++) {
			cp1.centers[3] = (short)i;
			cp1.unpackCenters( cube1, 3 );
			for (int mv=0; mv<Moves.N_STAGE_MOVES + N_ROT; mv++) {
				if( mv >= Moves.N_STAGE_MOVES)
					cube1.rightMult( rotations[mv - Moves.N_STAGE_MOVES], cube2 );
				else
					cube1.move( Moves.stage2moves[mv], cube2 );
				cp2.packCenters( cube2, 3 );
				move_centers[i][mv] = cp2.centers[3];
			}
		}
	}

	/** Convert to Coordinates **/

	void toEdge2( RawCoordState e ){
		e.coord = 6 * Util.C24to8[edges_loc[4]] + Util.perms_to_6[edges_perm[4]][edges_perm[5]];
	}

	void toCenter2( SymCoordState c, int c_idx ){
		c.raw_coord = centers[c_idx];
		c.computeSym();
	}

	public void toStage2( Stage2 s ){
		toEdge2( s.edge );
		toCenter2( s.centerF, 4 );
		toCenter2( s.centerB, 5 );
	}

	void toCenter3( SymCoordState c ){
		if( centers[2] > centers[3] )
			c.raw_coord = Util.C16to16[centers[2]][centers[3]];
		else
			c.raw_coord = Util.C16to16[centers[3]][centers[2]];
		c.computeSym();
	}

	void toEdge3( RawCoordState e ){
		if( edges_loc[0] > edges_loc[3] )
			e.coord = Util.C16to16[edges_loc[0]][edges_loc[3]]/35;
		else
			e.coord = Util.C16to16[edges_loc[3]][edges_loc[0]]/35;
	}

	public void toStage3( Stage3 s ){
		toEdge3( s.edge );
		toCenter3( s.center );
		/* Parity. Yes, it is not simple... */
		boolean parity = false;
		for( int i=0; i<4; i++ )
			parity ^= Util.get1bit( Util.parity_s4, edges_perm[i] );
		if( edges_loc[0] < edges_loc[1] ){
			parity ^= Util.get1bit( Util.parityC16_4, edges_loc[1] * Util.C15_4 + edges_loc[0] );
			parity ^= Util.get1bit( Util.parityC16_8, edges_loc[1] * Util.C15_4 + edges_loc[0] );
		}
		else {
			parity ^= Util.get1bit( Util.paritySwapC16_4, edges_loc[0] * Util.C15_4 + edges_loc[1] );
			parity ^= Util.get1bit( Util.parityC16_8, edges_loc[0] * Util.C15_4 + edges_loc[1] );
		}
		if( edges_loc[2] < edges_loc[3] )
			parity ^= Util.get1bit( Util.parityC16_4, edges_loc[3] * Util.C15_4 + edges_loc[2] );
		else
			parity ^= Util.get1bit( Util.paritySwapC16_4, edges_loc[2] * Util.C15_4 + edges_loc[3] );
		s.parity = (byte)( parity ? 1 : 0 );
	}

	void toCorner4(RawCoordState c){
		c.coord = 6 * corner_top_loc + Util.perms_to_6[corner_top_perm][corner_bottom_perm];
	}

	void toCenter4(RawCoordState c){
		if( centers[0] < centers[1] )
			c.coord = centers[0];
		else
			c.coord = centers[1];
	}

	void toEdge4(SymCoordState e){
		e.raw_coord = (( Util.perms_to_6[edges_perm[0]][edges_perm[1]]   * 6  +
					 Util.perms_to_6[edges_perm[2]][edges_perm[3]] ) * 70 +
					 Util.shiftC16[edges_loc[0]]				   ) * 70 +
					 Util.shiftC16[edges_loc[1]];
		e.computeSym();
	}

	public void toStage4( Stage4 s ){
		toEdge4( s.edge );
		toCenter4( s.center );
		toCorner4( s.corner );
	}

	void toEdge5(SymCoordState e){
		e.raw_coord = (( 4 * edges_perm[4] + edges_perm[5] / 6 ) * 96 +
						 4 * edges_perm[2] + edges_perm[3] / 6 ) * 96 +
						 4 * edges_perm[0] + edges_perm[1] / 6;
		e.computeSym();
	}

	void toCorner5(RawCoordState c){
		c.coord = 4 * corner_top_perm + corner_bottom_perm / 6;
	}

	void toCenter5(RawCoordState c){
		c.coord = ( Util.C24_4to12[centers[5]] * 12 + Util.C24_4to12[centers[3]] ) * 12 + Util.C24_4to12[centers[1]];
	}

	public void toStage5( Stage5 s ){
		toEdge5( s.edge );
		toCenter5( s.center );
		toCorner5( s.corner );
	}

	/** Move functions **/

	public final void moveTo(int m, CubePack cp){
		moveCorners(m, cp);
		for (int i=0; i<6; i++){
			moveEdges(m, i, cp);
			moveCenters(m, i, cp);
		}
	}

	final void moveCorners(int m, CubePack cp){
		int t = move_cperm[corner_top_loc][m];
		cp.corner_top_loc = (byte)(t >> 10);
		cp.corner_top_perm = Util.s4mul[corner_top_perm][(t >> 5) & 31];
		cp.corner_bottom_perm = Util.s4mul[corner_bottom_perm][t & 31];
	}

	final void moveEdges(int m, int e_idx, CubePack cp){
		int t = move_edges[this.edges_loc[e_idx]][m];
		cp.edges_loc[e_idx] = (short)(t >> 5);
		cp.edges_perm[e_idx] = Util.s4mul[this.edges_perm[e_idx]][t & 31];
	}

	final void moveCenters(int m, int c_idx, CubePack cp){
		cp.centers[c_idx] = move_centers[this.centers[c_idx]][m];
	}

	/** Orientation **/
	public final int rotateStage2(){
		if( edges_loc[5] > Util.Cnk[17][4] )
			return 0;
		if( edges_loc[5] > Util.Cnk[9][4] ){
			moveTo( ROTATE_RU3, this );
			return 32;
		}
		moveTo( ROTATE_UR3, this );
		return 16;
	}

	public final int rotateStage3(){
		if( centers[5] > Util.Cnk[17][4] )
			return 0;
		moveTo( ROTATE_U, this );
		return 8;
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

