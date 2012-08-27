package cg.fivestage444.Coordinates;

import static cg.fivestage444.Constants.*;

public final class Center5 {

	final static int N_COORD = 12*12*12;
	final static int N_SYM = 48;
	final static int N_MOVES = 12;
	static final short squares_cen_map[] = { 0x0F, 0x33, 0x3C, 0x55, 0x5A, 0x66, 0x99, 0xA5, 0xAA, 0xC3, 0xCC, 0xF0 };
	static final byte[] squares_cen_revmap = new byte[256];

	/* Coordinates */
	int coord;

	/* Tables */
	public static short[][] move = new short[N_COORD][N_MOVES];
	public static short[][] conj = new short[N_COORD][N_SYM*4];

	/* Check if solved */
	public boolean isSolved( int sym ){
		return conj[coord][sym] == 0;
	}

	/* Move */
	public void moveTo( int m, Center5 c ){
		c.coord =  move[coord][m];
	}

	/* Unpack a coord to a cube */
	public void unpack (CubeState cube)
	{
		int i;
		byte[] old_m_cen = new byte[24];

		int cen1 = this.coord % 12;
		int cen2 = (this.coord/12) % 12;
		int cen3 = this.coord/(12*12);
		int x = (Tables.squares_cen_map[cen1] << 16) | (Tables.squares_cen_map[cen2] << 8) | Tables.squares_cen_map[cen3];
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			old_m_cen[i] = (byte) (2*(i/8) + ((x & b) == 0 ? 0 : 1));
			b >>= 1;
		}

		//We must convert between "standard"-style cubie numbering and the "square"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.
		static final byte sqs_to_std_cen[] = {
			0,  2,  3,  1,  6,  4,  5,  7,
			8, 10, 11,  9, 14, 12, 13, 15,
			16, 18, 19, 17, 22, 20, 21, 23
		};
		for (i = 0; i < 24; ++i) {
			cube.m_cen[sqs_to_std_cen[i]] = (byte)(sqs_to_std_cen[4*old_m_cen[i]]/4);
		}
	}

	/* Pack a cube into the coord */
	public void pack (CubeState cube){
		int i;
		//We must convert between "squares"-style cubie numbering and the "standard"-style
		//cubie numbering for the corner and center cubies. Edge cubies need no such translation.

		static final byte std_to_sqs_cen[] = {
			0,  3,  1,  2,  5,  6,  4,  7,
			8, 11,  9, 10, 13, 14, 12, 15,
			16, 19, 17, 18, 21, 22, 20, 23
		};
		byte[] new_m_cen = new byte[24];
		for (i = 0; i < 24; ++i) {
			new_m_cen[std_to_sqs_cen[i]] = (byte)(std_to_sqs_cen[4*m_cen[i]]/4);
		}

		int x = 0;
		int b = 0x800000;
		for (i = 0; i < 24; ++i) {
			if ((new_m_cen[i] & 0x1) != 0) {
				x |= b;
			}
			b >>= 1;
		}
		short cen1 = (short)Tables.squares_cen_revmap[(x >> 16) & 0xFF];
		short cen2 = (short)Tables.squares_cen_revmap[(x >> 8) & 0xFF];
		short cen3 = (short)Tables.squares_cen_revmap[x & 0xFF];
		this.coord = cen1 + 12*cen2 + 12*12*cen3;
	}

	/* Initialisations */
	public static void init(){
		for (i = 0; i < 12; ++i) {
			squares_cen_revmap[squares_cen_map[i]] = (byte)i;
		}
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
				cube1.rotate_sliceCENTER (stage2moves[m], cube2);
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
					cube1.rightMultCenters(Symmetry.invSymIdx[Symmetry.symIdxMultiply[sym][cosym]], cube2);
					cube2.leftMultCenters(sym);
					this.pack( cube2 );
					conj[u][(sym<<2)|cosym] = coord;
				}
			}
		}
	}
}
