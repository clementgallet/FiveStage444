package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Corner5 {

	final static int N_COORD = 96;
	final static int N_SYM = 48;
	final static int N_MOVES = 12;

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[MAX_COORD][N_MOVES];
	public static short[][] conj = new short[MAX_COORD][N_SYM*4];

	/* Check if solved */
	public boolean isSolved( int sym ){
		return conj[coord][sym] == 0;
	}

	/* Move */
	public void moveTo( int m, Corner5 c ){
		c.coord =  move[coord][m];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int i;
		byte[] old_m_cor = new byte[8];
		byte[] t = new byte[4];
		Constants.set4Perm (old_m_cor, this.coord/4);
		Constants.set4Perm (t, sqs_rep_to_perm[sqs_perm_to_rep[corner/4]][this.coord % 4]);
		for (i = 0; i < 4; ++i) {
			old_m_cor[i+4] = (byte)(t[i]+4);
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		static final byte sqs_to_std_cor[] = { 0, 2, 5, 7, 1, 3, 4, 6 };
		for (i = 0; i < 8; ++i) {
			cube.m_cor[sqs_to_std_cor[i]] = sqs_to_std_cor[old_m_cor[i]];
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.

		static final byte std_to_sqs_cor[] = { 0, 4, 1, 5, 6, 2, 7, 3 };
		byte[] new_m_cor = new byte[8];
		for (i = 0; i < 8; ++i) {
			new_m_cor[std_to_sqs_cor[i]] = std_to_sqs_cor[cube.m_cor[i]];
		}

		this.coord = 4*Constants.get4Perm (new_m_cor, 0) + (new_m_cor[4] - 4);
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
				for (int cosym = 0; cosym < 4; ++cosym) {
					cube1.rightMultCorners(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultCorners(sym);
					this.pack( cube2 );
					conj[u][(sym<<2)|cosym] = coord;
				}
			}
		}
	}
}
