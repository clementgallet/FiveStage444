package cg.fivestage444;

import cg.fivestage444.Coordinates.Edge5;
import cg.fivestage444.Coordinates.Corner5;
import cg.fivestage444.Coordinates.Center5;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

public final class Analyze {

	static final int N_SIZE5 = Edge5.N_COORD*Corner5.N_COORD*Center5.N_COORD;

	static int length4;
	static CubeState cube = new CubeState();
	static long unique, pos, done;
	static byte[] move_list_stage4 = new byte[30];

	static byte[] allPos5 = new byte[N_SIZE5>>>3];
	static byte[] allPos5_2 = new byte[N_SIZE5>>>3];

	public static void main(String[] args){
		Tools.init();
		distTable5in4(0);
	}

	public static void distTable5in4 (int coset){
		System.out.println("Start analysing coset "+coset);

		Stage4 s = new Stage4();
		s.set( coset );

		cube.init();
		int cubeDist;
		cubeDist = s.pruning();

		done = 0;
		for (length4 = 0; length4 < 19; ++length4) {
			unique = 0;
			pos = 0;
			move_stage5();
			/* Copy from allPos5_2 to allPos5 */
			for( int idx=0; idx<N_SIZE5>>>3; idx++ )
				allPos5[idx] |= allPos5_2[idx];
			//if(length4==0)
			search_stage4 (s, length4, 0, Moves.N_STAGE_MOVES);
			/* Copy from allPos5 to allPos5_2 */
			for( int idx=0; idx<N_SIZE5>>>3; idx++ )
				allPos5_2[idx] |= allPos5[idx];
			System.out.println(String.format("%2d%12d%10d", length4, pos, unique));
		}
	}

	public static void search_stage4 (Stage4 s, int depth, int moves_done, int last_move){
		int mov_idx, j;
		if (depth == 0 && s.isSolved() ){
			save_stage5 ();
			return;
		}
		Stage4 t = new Stage4();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage4.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			int dist4 = t.pruning();
			if (dist4 > depth-1) continue;
			if( ( (depth-1)!=dist4 ) && (depth+dist4<6) ) continue;
			move_list_stage4[moves_done] = (byte)move;
			search_stage4 (t, depth - 1, moves_done + 1, move);
		}
	}

	public static void save_stage5 (){
		int i;

		CubeState c = new CubeState();
		cube.copyTo(c);
		c.scramble( length4, move_list_stage4, Moves.stage2moves );

		Stage5 s = new Stage5();
		s.pack( c );
		s.center.coord = s.center.conjugate(s.edge.sym);
		s.corner.coord = s.corner.conjugate(s.edge.sym);
		s.edge.sym = 0;

		long idx = ((long)s.edge.coord * Corner5.N_COORD + s.corner.coord) * Center5.N_COORD + s.center.coord;
		if(( allPos5[(int)(idx>>>3)] >>> (int)(idx & 0x7) & 1 ) == 0 ){
			unique++;
			int nsym = 1;
			allPos5[(int)(idx>>>3)] |= 1 << (idx & 0x7);
			done++;
			for (int j=0; j<4; j++) {
				long symS = Edge5.hasSym[s.edge.coord][j];
				for (int k=0; symS != 0; symS>>=1, k++) {
					if ((symS & 0x1L) == 0) continue;
					long idxx = ((long)s.edge.coord * Corner5.N_COORD + s.corner.conjugate((k<<2)+j)) * Center5.N_COORD + s.center.conjugate((k<<2)+j);
					if(( allPos5[(int)(idxx>>>3)] >>> (int)(idxx & 0x7) & 1 ) == 0 ){
						allPos5[(int)(idxx>>>3)] |= 1 << (idxx & 0x7);
						done++;
					}
					if (idxx == idx)
						nsym++;
				}
			}
			pos += 192/nsym;
		}
	}

	public static void move_stage5 (){
		int i;
		Stage5 s1 = new Stage5();
		Stage5 s2 = new Stage5();

		for( long idx=0; idx<N_SIZE5;){
			int val = allPos5[(int)(idx>>>3)];
			if( val == 0){
				idx += 8;
				continue;
			}
			for (long end=Math.min(idx+8, N_SIZE5); idx<end; idx++, val>>=1){
				if ((val&1) == 0) continue;
				s1.center.coord = (int)(idx % Center5.N_COORD);
				s1.edge.coord = (int)(idx / (Corner5.N_COORD*Center5.N_COORD));
				s1.corner.coord = (int)(( idx / Center5.N_COORD ) % Corner5.N_COORD);
				for (int m=0; m<Stage5.N_MOVES; m++){
					s1.moveTo( m, s2 );
					s2.center.coord = s2.center.conjugate(s2.edge.sym);
					s2.corner.coord = s2.corner.conjugate(s2.edge.sym);
					s2.edge.sym = 0;
					long idxx = ((long)s2.edge.coord*Corner5.N_COORD+s2.corner.coord)*Center5.N_COORD+s2.center.coord;
					if(( allPos5_2[(int)(idxx>>>3)] >>> (int)(idxx & 0x7L) & 1 ) == 0 ){
						unique++;
						done++;
						int nsym = 1;
						allPos5_2[(int)(idxx>>>3)] |= 1 << (idxx & 0x7L);
						for (int j=0; j<4; j++) {
							long symS = Edge5.hasSym[s2.edge.coord][j];
							for (int k=0; symS != 0; symS>>=1, k++) {
								if ((symS & 0x1L) == 0) continue;
								long idxxx = ((long)s2.edge.coord*Corner5.N_COORD+s2.corner.conjugate((k<<2)+j))*Center5.N_COORD+s2.center.conjugate((k<<2)+j);
								if(( allPos5_2[(int)(idxxx>>>3)] >>> (int)(idxxx & 0x7) & 1 ) == 0 ){
									allPos5_2[(int)(idxxx>>>3)] |= 1 << (idxxx & 0x7);
									done++;
								}
								if( idxxx == idxx)
									nsym++;
							}
						}
						pos += 192/nsym;
					}
				}
			}
		}

	
	}


}
