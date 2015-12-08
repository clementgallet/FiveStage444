package cg.fivestage444.Coordinates;

import cg.fivestage444.Cubies.Cubies;
import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Symmetry;
import cg.fivestage444.Util;

public final class Edge4 extends SymCoord {

	public Edge4(){
		N_COORD = 5968;
		N_RAW_COORD = 88200*2;
		N_SYM = Stage4.N_SYM;
		SYM_SHIFT = 4;
		SYM_MASK = ( 1 << SYM_SHIFT ) - 1;
		N_MOVES = Stage4.N_MOVES;

		SolvedStates = new int[]{0};
		cubieType = new EdgeCubies();
		rightMultOrConjugate = CONJUGATE;
		HASHCODE_RAW2SYM = 1288425037;
		HASHCODE_SYM2RAW = -190552951;
		HASHCODE_MOVE = -207107456;
	}

	/* Unpack a raw coord to a cube */
	public void unpack(Cubies cube, int raw_coord)
	{
		int ledge4of8 = raw_coord % 70;
		int edge = raw_coord / 70;
		int redge4of8 = edge % 70;
		edge /= 70;
		int perm6_fb = edge % 6;
		int perm6_rl = edge / 6;
		byte[] t = new byte[4];

		int i1 = 0;
		int i2 = 0;
		int r = 4;
		Util.set4Perm( t, perm6_rl );
		for( int i=7; i >= 0; i-- ){
			if( ledge4of8 >= Util.Cnk[i][r] ){
				ledge4of8 -= Util.Cnk[i][r--];
				cube.cubies[i+4] = (byte)( t[i1++] + 4 );
			}
			else
				cube.cubies[i+4] = (byte)( (i2++) + 8);
		}

		i1 = 0;
		i2 = 0;
		r = 4;
		Util.set4Perm( t, perm6_fb );
		for( int i=7; i >= 0; i-- ){
			if( redge4of8 >= Util.Cnk[i][r] ){
				redge4of8 -= Util.Cnk[i][r--];
				cube.cubies[( i < 4 ) ? i : i + 8] = (byte)(i1++);
			}
			else
				cube.cubies[( i < 4 ) ? i : i + 8] = (byte)(t[i2++] + 12);
		}

		for( int i=16; i < 24; i++ )
			cube.cubies[i] = (byte)i;
	}

	/* Pack a cube into the raw coord */
	public int pack(Cubies cube){
		int redge4of8 = 0;
		int ledge4of8 = 0;
		byte[] edges_rl = new byte[8];
		byte[] edges_fb = new byte[8];

		int i_rl = 4;
		int i_fb = 0;
		int r = 4;
		for( int i=7; i>=0;i--){
			if( cube.cubies[i+4] < 8 ){
				ledge4of8 += Util.Cnk[i][r--];
				edges_rl[i_rl++] = cube.cubies[i+4];
			}
			else
				edges_fb[i_fb++] = (byte)(cube.cubies[i+4] - 8);
		}

		i_rl = 0;
		i_fb = 4;
		r = 4;
		for( int i=7; i>=0;i--){
			int u = (i < 4) ? i : i + 8;
			if( cube.cubies[u] < 4 ){
				redge4of8 += Util.Cnk[i][r--];
				edges_rl[i_rl++] = cube.cubies[u];
			}
			else
				edges_fb[i_fb++] = (byte)(cube.cubies[u] - 8);
		}

		int perm6_rl = Util.perm_to_420[Util.get8Perm (edges_rl, 0)]%6;
		int perm6_fb = Util.perm_to_420[Util.get8Perm (edges_fb, 0)]%6;

		return ((( perm6_rl * 6 + perm6_fb ) * 70 + redge4of8 ) * 70 + ledge4of8 );
	}

	@Override
	public void initSym2Raw (){
		sym2raw = new int[N_COORD];
		raw2sym = new int[N_RAW_COORD];

		int repIdx = 0;
		EdgeCubies cube1 = new EdgeCubies();
		EdgeCubies cube2 = new EdgeCubies();
		byte[] t = new byte[8];
		byte[] isRepTable = new byte[(N_RAW_COORD>>3) + 1];
		hasSym = new long[N_COORD];
		for (int u = 0; u < N_RAW_COORD; ++u) {
			if( Util.get1bit( isRepTable, u )) continue;
			raw2sym[u] = repIdx << SYM_SHIFT;
			unpack(cube1, u);

			/* Only retain configs without parity */
			/* TODO: Try in the pack/unpack functions to only produce correct positions,
			 * so that we would not have to do this filter.
			 */
			int ul = Util.get8Perm( cube1.cubies, 4 );
			for (int i=0; i<4; i++)
				t[i] = ( cube1.cubies[i] > 4 ) ? (byte)(cube1.cubies[i]-8) : cube1.cubies[i];
			for (int i=4; i<8; i++)
				t[i] = ( cube1.cubies[i+8] > 4 ) ? (byte)(cube1.cubies[i+8]-8) : cube1.cubies[i+8];
			int uh = Util.get8Perm( t, 0 );
			if( Util.parity_perm8_table[ul] != Util.parity_perm8_table[uh] ) continue; // getting rid of the parity.

			for (int s = 1; s < N_SYM; ++s) {
				cube1.conjugate (s, cube2);
				int raw_coord = pack(cube2);
				Util.set1bit( isRepTable, raw_coord );
				raw2sym[raw_coord] = ( repIdx << SYM_SHIFT ) + Symmetry.invSymIdx[s];
				if( raw_coord == u )
					hasSym[repIdx] |= (0x1L << s);
			}
			sym2raw[repIdx++] = u;
		}
	}
}
