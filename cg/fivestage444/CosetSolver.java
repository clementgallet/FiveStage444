package cg.fivestage444;

import cg.fivestage444.Stages.*;

public final class CosetSolver {
	public static int length;

	public static long unique;
	public static CubeState init_cube;
	public static Stage coset_stage;
	private static Stage subgroup_stage;
	private static int N_MOVES_COSET;
	private static int N_MOVES_SUBGROUP;

	private static byte[] visited;
	private static byte[] visited_copy;

	private static int current_starting_moves;
	private static long current_idx;

	public static void main(String[] args){
		Symmetry.init();
		Util.init();
		CoordsHandler.init();
		Stage4.init();
		Stage5.init();
		CosetSolver coset_solver = new CosetSolver();
		coset_solver.solve_coset(0, new Stage4(), new Stage5());
	}

	private void solve_coset(int coset, Stage cs, Stage ss){
		coset_stage = cs;
		N_MOVES_COSET = cs.getMovesNumber();
		subgroup_stage = ss;
		N_MOVES_SUBGROUP = ss.getMovesNumber();

		visited = new byte[(int)(ss.STAGE_SIZE>>>3)];
		visited_copy = new byte[(int)(ss.STAGE_SIZE>>>3)];

		System.out.println("Start analysing coset "+coset);

		//coset_stage.setId(coset);

		init_cube = new CubeState();
		//s.unpack(cube);
		init_cube.init();
		coset_stage.pack(init_cube);

		long count = 0;
		for (length = 0; (length < 20) && (count < ss.STAGE_SIZE); ++length) {
			unique = 0;
			move_stage_threaded();
			/* Copy from allPos5_2 to allPos5 */
			for( int idx=0; idx< subgroup_stage.STAGE_SIZE >>>3; idx++ )
				visited[idx] |= visited_copy[idx];
			if(length<1)
				search_stage(length);
			/* Copy from allPos5 to allPos5_2 */
			for( int idx=0; idx< subgroup_stage.STAGE_SIZE >>>3; idx++ )
				visited_copy[idx] |= visited[idx];
			count = Util.bitCount(visited);
			System.out.println(String.format("%2d%14d%14d", length, count, unique));
		}
		System.out.println(String.format("A %14d", count));
	}

	private class CosetSolverThread extends Thread{

		public void run(){
			if(length <= 3)
				search_stage(coset_stage, init_cube, length, 0, Moves.N_STAGE_MOVES);
			else{
				int starting_moves = get_starting_moves();
				while(starting_moves != -1){ //  TODO: Use a do-while instead.
					int move1 = starting_moves / N_MOVES_COSET;
					int move2 = starting_moves % N_MOVES_COSET;
					Stage s = coset_stage.newOne();
					Stage t = coset_stage.newOne();
					CubeState cube = new CubeState();
					init_cube.copyTo(cube);
					s.moveTo(move1, t);
					t.moveTo(move2, s);
					cube.move(Moves.stage2moves[move1]);
					cube.move(Moves.stage2moves[move2]);
					search_stage(s, cube, length-2, 2, move2);
					starting_moves = get_starting_moves();
				}
			}
		}

		private synchronized int get_starting_moves(){
			while(true){
				int starting_moves = current_starting_moves++;
				if(starting_moves >= N_MOVES_COSET*N_MOVES_COSET)
					return -1;
				int move1 = starting_moves / N_MOVES_COSET;
				int move2 = starting_moves % N_MOVES_COSET;
				long mask = Moves.moves_mask[move1];
				if(((mask >> move2) & 1) == 1)
					return starting_moves;
			}
		}
	}

	private class SubgroupSolverThread extends Thread{
		final long BATCH_SIZE = 7444;

		public void run(){
			Stage s1 = subgroup_stage.newOne();
			Stage s2 = subgroup_stage.newOne();

			long idxBatch=getNewIdxBatch();
			while (idxBatch >= 0){
				for( long idx=idxBatch; idx< Math.min(idxBatch + BATCH_SIZE,subgroup_stage.STAGE_SIZE); idx++){
					if(length<15){
						if(visited[(int)(idx>>>3)] == 0){
							idx = (((idx>>>3)+1)<<3)-1;
							continue;
						}
						if (!Util.get1bit(visited, idx)) continue;
						s1.setId(idx);
						for (int m=0; m<N_MOVES_SUBGROUP; m++){
							s1.moveTo( m, s2 );
							save_stage(s2, visited_copy);
						}
					}
					else{
						if(visited_copy[(int)(idx>>>3)] == 0xFF){
							idx = (((idx>>>3)+1)<<3)-1;
							continue;
						}
						if (Util.get1bit(visited_copy, idx)) continue;
						s1.setId(idx);
						for (int m=0; m<N_MOVES_SUBGROUP; m++){
							s1.moveTo( m, s2 );
							if(Util.get1bit(visited, s2.getId())){
								Util.set1bit(visited_copy, idx);
								break;
							}
						}
					}
				}
				idxBatch=getNewIdxBatch();
			}
		}

		public long getNewIdxBatch(){
			long idx;
			synchronized (visited){
				idx = current_idx;
				current_idx += BATCH_SIZE;
			}
			if(idx >= subgroup_stage.STAGE_SIZE)
				return -1;
			else
				return idx;
		}
	}

	private void search_stage(int depth){
		int N_THREADS = 4;
		Thread[] threads = new Thread[N_THREADS];
		current_starting_moves = 0;
		if(depth <= 3){
			threads[0] = new CosetSolverThread();
			threads[0].start();
			try {
				threads[0].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
			for (int t=0; t<N_THREADS; t++){
				threads[t] = new CosetSolverThread();
				threads[t].start();
			}
			try {
				for (int t=0; t<N_THREADS; t++)
					threads[t].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void search_stage(Stage s, CubeState cube, int depth, int moves_done, int last_move){
		if (depth == 0 && s.isSolved() ){
			save_stage(cube);
			return;
		}
		Stage t = s.newOne();
		CubeState cube2 = new CubeState();
		long mask = Moves.moves_mask[last_move];
		if(depth==1)
			mask &= (1 << N_MOVES_COSET) - (1 << N_MOVES_SUBGROUP); // We only try moves not in subgroup stage.
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

	private void save_stage(CubeState cube){
		Stage s = subgroup_stage.newOne();
		s.pack(cube);
		save_stage(s, visited);
	}

	private void save_stage(Stage s, byte[] array){
		long idx = s.getId();
		if(Util.get1bit(array, idx)) return;
		//unique++;
		//int nsym = 1;
		Util.set1bit(array, idx);
		//done++;
		s.normalize();
		long[] symSs = s.symState.getSyms();
		for (int j=0; j<symSs.length; j++) {
			long symS = symSs[j];
			for (int k=0; symS != 0; symS>>=1, k++) {
				if ((symS & 0x1L) == 0) continue;
				long idx_sym = s.getId(k*symSs.length+j);
				//	nsym++;
				//else if(!Util.get1bit(array, idx_sym)){
				if (idx_sym != idx)
					Util.set1bit(array, idx_sym);
				//	done++;
				//}
			}
		}
		//pos += s.symState.sc.N_SYM/nsym;
	}

	public void move_stage_threaded (){
		int N_THREADS = 4;
		Thread[] threads = new Thread[N_THREADS];
		current_idx = 0;
		for (int t=0; t<N_THREADS; t++){
			threads[t] = new SubgroupSolverThread();
			threads[t].start();
		}
		try {
			for (int t=0; t<N_THREADS; t++)
				threads[t].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
