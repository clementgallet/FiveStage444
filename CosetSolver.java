package cg.fivestage444;

import cg.fivestage444.Stages.*;

public final class CosetSolver {

	private static long unique;
	private static long pos;
	private static long done;

	private static Stage subgroup_stage;
	private static int N_MOVES_COSET;
	private static int N_MOVES_SUBGROUP;

	private static byte[] visited;
	private static byte[] visited_copy;

	public static void main(String[] args){
		Symmetry.init();
		Util.init();
		CoordsHandler.init();
		Stage3.init();
		Stage4.init();
		solve_coset(0, new Stage3(), new Stage4());
	}

	private static void solve_coset(int coset, Stage cs, Stage ss){
		Stage coset_stage = cs;
		N_MOVES_COSET = cs.getMovesNumber();
		subgroup_stage = ss;
		N_MOVES_SUBGROUP = ss.getMovesNumber();

		visited = new byte[(int)(ss.STAGE_SIZE>>>3)];
		visited_copy = new byte[(int)(ss.STAGE_SIZE>>>3)];

		System.out.println("Start analysing coset "+coset);

		coset_stage.setId(coset);

		CubeState cube = new CubeState();
		//s.unpack(cube);
		cube.init();
		coset_stage.pack(cube);

		done = 0;
		long sum_pos = 0;
		long sum_unique = 0;
		for (int length = 0; (length < 20) && (done < ss.STAGE_SIZE); ++length) {
			unique = 0;
			pos = 0;
			if(length<13)
				move_stage();
			else
				move_stage_backward();
			/* Copy from allPos5_2 to allPos5 */
			for( int idx=0; idx< subgroup_stage.STAGE_SIZE >>>3; idx++ )
				visited[idx] |= visited_copy[idx];
			if(length<13)
				search_stage(coset_stage, cube, length, 0, Moves.N_STAGE_MOVES);
			/* Copy from allPos5 to allPos5_2 */
			for( int idx=0; idx< subgroup_stage.STAGE_SIZE >>>3; idx++ )
				visited_copy[idx] |= visited[idx];
			System.out.println(String.format("%2d%14d%12d", length, pos, unique));
			sum_pos += pos;
			sum_unique += unique;
		}
		System.out.println(String.format("A %14d%12d", sum_pos, sum_unique));
	}

	private static void search_stage(Stage s, CubeState cube, int depth, int moves_done, int last_move){
		if (depth == 0 && s.isSolved() ){
			save_stage(cube);
			return;
		}
		Stage t = s.newOne();
		CubeState cube2 = new CubeState();
		long mask = Moves.moves_mask[last_move];
		//if(depth==1)
		//	mask &= 0xF000; // We only try moves not in stage 5.
		for (int move = 0; mask != 0 && move < N_MOVES_COSET; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			int dist = t.pruning();
			if (dist > depth-1) continue;
			if( ( (depth-1)!=dist ) && (depth+dist<6) ) continue;
			cube.copyTo(cube2);
			cube2.move(Moves.stage2moves[move]);
			search_stage(t, cube2, depth - 1, moves_done + 1, move);
		}
	}

	private static void save_stage(CubeState cube){
		subgroup_stage.pack(cube);
		save_stage(subgroup_stage, visited);
	}

	private static void save_stage(Stage s, byte[] array){
		long idx = s.getId();
		if(!Util.get1bit(array, idx)){
			save_stage(s, idx, array);
		}
	}

	private static void save_stage(Stage s, long idx, byte[] array){
		unique++;
		int nsym = 1;
		Util.set1bit(array, idx);
		done++;
		s.normalize();
		long[] symSs = s.symState.getSyms();
		for (int j=0; j<symSs.length; j++) {
			long symS = symSs[j];
			for (int k=0; symS != 0; symS>>=1, k++) {
				if ((symS & 0x1L) == 0) continue;
				long idx_sym = s.getId(k*symSs.length+j);
				if(!Util.get1bit(array, idx_sym)){
					Util.set1bit(array, idx_sym);
					done++;
				}
				if (idx_sym == idx)
					nsym++;
			}
		}
		pos += s.symState.sc.N_SYM/nsym;
	}

	public static void move_stage (){
		Stage s1 = subgroup_stage.newOne();
		Stage s2 = subgroup_stage.newOne();

		for( long idx=0; idx< subgroup_stage.STAGE_SIZE; idx++){
			if(visited[(int)(idx>>>3)] == 0){
				idx += 7;
				continue;
			}
			if (!Util.get1bit(visited, idx)) continue;
			s1.setId(idx);
			for (int m=0; m<N_MOVES_SUBGROUP; m++){
				s1.moveTo( m, s2 );
				save_stage(s2, visited_copy);
			}
		}	
	}

	public static void move_stage_backward (){
		Stage s1 = subgroup_stage.newOne();
		Stage s2 = subgroup_stage.newOne();

		for( long idx=0; idx< subgroup_stage.STAGE_SIZE; idx++){
			if(visited_copy[(int)(idx>>>3)] == 0xFF){
				idx += 7;
				continue;
			}
			if (Util.get1bit(visited_copy, idx)) continue;
			s1.setId(idx);
			for (int m=0; m<N_MOVES_SUBGROUP; m++){
				s1.moveTo( m, s2 );
				if(Util.get1bit(visited, s2.getId())){
					save_stage(s1, idx, visited_copy);
					break;
				}
			}
		}
	}
}
