package cg.fivestage444;

import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

public final class CosetSolver {

	private static final int N_SIZE5 = 12*12*12*96*7444;

	private static long unique;
	private static long pos;
	private static long done;

	private static final byte[] allPos5 = new byte[N_SIZE5>>>3];
	private static final byte[] allPos5_2 = new byte[N_SIZE5>>>3];

	public static void main(String[] args){
		Symmetry.init();
		Util.init();
		CoordsHandler.init();
		Stage4.init();
		Stage5.init();
		distTable5in4(0);
	}

	private static void distTable5in4(int coset){
		System.out.println("Start analysing coset "+coset);

		Stage4 s = new Stage4();
		s.setId(coset);

		CubeState cube = new CubeState();
		//s.unpack(cube);
		cube.init();

		done = 0;
		for (int length4 = 0; length4 < 19; ++length4) {
			unique = 0;
			pos = 0;
			if(length4<14)
				move_stage();
			else
				move_stage_backward();
			/* Copy from allPos5_2 to allPos5 */
			for( int idx=0; idx<N_SIZE5>>>3; idx++ )
				allPos5[idx] |= allPos5_2[idx];
			if(length4==0)
				search_stage(s, cube, length4, 0, Moves.N_STAGE_MOVES);
			/* Copy from allPos5 to allPos5_2 */
			for( int idx=0; idx<N_SIZE5>>>3; idx++ )
				allPos5_2[idx] |= allPos5[idx];
			System.out.println(String.format("%2d%12d%10d", length4, pos, unique));
		}
	}

	private static void search_stage(Stage4 s, CubeState cube, int depth, int moves_done, int last_move){
		if (depth == 0 && s.isSolved() ){
			save_stage(cube);
			return;
		}
		Stage4 t = new Stage4();
		CubeState cube2 = new CubeState();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage4.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			int dist4 = t.pruning();
			if (dist4 > depth-1) continue;
			if( ( (depth-1)!=dist4 ) && (depth+dist4<6) ) continue;
			cube.copyTo(cube2);
			cube2.move(Moves.stage2moves[move]);
			search_stage(t, cube2, depth - 1, moves_done + 1, move);
		}
	}

	private static void save_stage(CubeState cube){
		Stage5 s = new Stage5();
		s.pack(cube);
		save_stage(s, allPos5);
	}

	private static void save_stage(Stage5 s, byte[] array){
		int idx = s.getId();
		if(!Util.get1bit(array, idx)){
			save_stage(s, idx, array);
		}
	}

	private static void save_stage(Stage5 s, int idx, byte[] array){
		unique++;
		int nsym = 1;
		Util.set1bit(array, idx);
		done++;
		s.normalize();
		long[] symSs = s.edge.getSyms();
		for (int j=0; j<4; j++) {
			long symS = symSs[j];
			for (int k=0; symS != 0; symS>>=1, k++) {
				if ((symS & 0x1L) == 0) continue;
				int idx_sym = s.getId(k*4+j);
				if(!Util.get1bit(array, idx_sym)){
					Util.set1bit(array, idx_sym);
					done++;
				}
				if (idx_sym == idx)
					nsym++;
			}
		}
		pos += Stage5.N_SYM/nsym;
	}

	public static void move_stage (){
		Stage5 s1 = new Stage5();
		Stage5 s2 = new Stage5();

		for( int idx=0; idx<N_SIZE5; idx++){
			if(allPos5[idx>>>3] == 0){
				idx += 7;
				continue;
			}
			if (!Util.get1bit(allPos5, idx)) continue;
			s1.setId(idx);
			for (int m=0; m<Stage5.N_MOVES; m++){
				s1.moveTo( m, s2 );
				save_stage(s2, allPos5_2);
			}
		}	
	}

	public static void move_stage_backward (){
		Stage5 s1 = new Stage5();
		Stage5 s2 = new Stage5();

		for( int idx=0; idx<N_SIZE5; idx++){
			if(allPos5_2[idx>>>3] == 0xFF){
				idx += 7;
				continue;
			}
			if (Util.get1bit(allPos5_2, idx)) continue;
			s1.setId(idx);
			for (int m=0; m<Stage5.N_MOVES; m++){
				s1.moveTo( m, s2 );
				if(Util.get1bit(allPos5, s2.getId())){
					save_stage(s1, idx, allPos5_2);
					break;
				}
			}
		}
	}
}
