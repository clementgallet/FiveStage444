package cg.fivestage444.Coordinates;

import cg.fivestage444.CubeState;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Moves;
import cg.fivestage444.Stages.Stage5;

public final class Center5 {

	public final static int N_COORD = 12*12*12;
	private final static int N_SYM = Stage5.N_SYM;
	private final static int N_MOVES = Stage5.N_MOVES;
	private static final short squares_cen_map[] = { 15, 60, 85, 90, 102, 105, 150, 153, 165, 170, 195, 240 };
	private static final byte[] squares_cen_revmap = new byte[256];

	/* Coordinates */
	public int coord;

	/* Tables */
	private static short[][] move = new short[N_COORD][N_MOVES];
	private static short[][] conj = new short[N_COORD][N_SYM*4];

	/* Check if solved */
	public boolean isSolved( int sym ){
		return conj[coord][sym] == 0;
	}

	/* Move */
	public void moveTo( int m, Center5 c ){
		c.coord =  move[coord][m];
	}

	/* Get the conjugated coordinate */
	public int conjugate( int sym ){
		return conj[coord][sym];
	}

	/* Unpack a coord to a cube */
	private void unpack (CubeState cube)
	{
		int cen1 = this.coord % 12;
		int cen2 = (this.coord/12) % 12;
		int cen3 = this.coord/(12*12);
		int x = (squares_cen_map[cen1] << 16) | (squares_cen_map[cen2] << 8) | squares_cen_map[cen3];
		int b = 0x800000;
		for (int i = 0; i < 24; ++i) {
			cube.m_cen[i] = (byte) (2*(i/8) + ((x & b) == 0 ? 0 : 1));
			b >>= 1;
		}
	}

	/* Pack a cube into the coord */
	private void pack (CubeState cube){
		int x = 0;
		int b = 0x800000;
		for (int i = 0; i < 24; ++i) {
			if ((cube.m_cen[i] & 0x1) != 0) {
				x |= b;
			}
			b >>= 1;
		}
		int cen1 = squares_cen_revmap[(x >> 16) & 0xFF];
		int cen2 = squares_cen_revmap[(x >> 8) & 0xFF];
		int cen3 = squares_cen_revmap[x & 0xFF];
		this.coord = cen1 + 12*cen2 + 12*12*cen3;
	}

	/* Initialisations */
	public static void init(){
		for (int i = 0; i < 12; ++i) {
			squares_cen_revmap[squares_cen_map[i]] = (byte)i;
		}
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		Center5 c = new Center5();
		for (int u = 0; u < N_COORD; ++u) {
			c.coord = u;
			c.unpack( cube1 );
			for (int m = 0; m < N_MOVES; ++m) {
				cube1.rotate_sliceCENTER (Moves.stage2moves[m], cube2);
				c.pack( cube2 );
				move[u][m] = (short)(c.coord);
			}
			for (int s = 0; s < N_SYM; ++s) {
				for (int cs = 0; cs < 4; ++cs) {
					cube1.rightMultCenters(Symmetry.invSymIdx[Symmetry.symIdxMultiply[s][cs]], cube2);
					cube2.leftMultCenters(s);
					c.pack( cube2 );
					conj[u][(s<<2)|cs] = (short)(c.coord);
				}
			}
		}
	}
}
