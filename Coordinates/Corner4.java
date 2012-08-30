package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Corner4 {

	final static int N_COORD = 420;
	final static int N_SYM = 16;
	final static int N_MOVES = 16;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM];

	/* Check if solved */
	public boolean isSolved(){
		return coord == 0;
	}

	/* Move */
	public void moveTo( int m, Corner4 c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int i;
		byte[] t6 = new byte[4];
		byte[] t8 = new byte[8];
		int cor_bm = this.coord / 6;
		Constants.set4Perm (t6, this.coord % 6);
		int a = 0;
		int b = 0;
		int r = 4;
		for (i = 7; i >= 0; i--) {
			if (cor_bm >= Cnk[i][r] ) {
				cor_bm -= Cnk[i][r--];
				t8[i] = (byte)a++;
			} else {
				t8[i] = (byte)(4 + t6[b++]);
			}
		}

		//Note: for corners, "squares" style mapping is used in creating the "coordinate" value.
		//But the do_move function for cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		static final byte sqs_to_std[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		for (i = 0; i < 8; ++i) {
			cube.m_cor[sqs_to_std[i]] = sqs_to_std[t8[i]];
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		int i;
		byte[] t6 = new byte[8];

		//Note: for corners, use of perm_to_420 array requires "squares" style mapping.
		//But the do_move function for std_cube assumes "standard" mapping.
		//Therefore the m_cor array must be converted accordingly using this conversion array.
		static final byte std_to_sqs[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		for (i = 0; i < 8; ++i) {
			t6[std_to_sqs[i]] = std_to_sqs[cube.m_cor[i]];
		}
		int u = Constants.get8Perm (t6, 0);
		this.coord = Tables.perm_to_420[u];
	}

	/* Initialisations */
	public static void init(){
		initMove();
		initConj();
	}

	public static void initMove (){

		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = u;
			this.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCORNER (stage2moves[m], cube2);
				this.pack( cube2 );
				move[u][m] = coord;
			}
		}
	}

	public static void initConj (){
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		for (int u = 0; u < N_COORD; ++u) {
			this.coord = u;
			this.unpack( cube1 );
			for (int sym = 0; sym < N_SYM; ++sym) {
				cube1.conjugateCorners (sym, cube2);
				this.pack( cube2 );
				conj[u][sym] = coord;
			}
		}
	}
}
