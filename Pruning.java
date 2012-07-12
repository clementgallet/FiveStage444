package cg.fivestage444;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

abstract class Pruning {

	protected long num_positions;
	protected int n_packed;
	protected byte[] ptable;
	public byte[] ptable_packed;
	protected int num_moves;
	protected long count = 0;
	protected int unique_count = 0;
	protected int back_dist = 30;



	abstract void init ();

	public int get_dist (long idx){
		return (ptable[(int)(idx>>2)] >> ((idx & 0x3) << 1)) & 0x3;
	}

	protected void set_dist (long idx, int value){
		ptable[(int)(idx>>2)] |= (byte)(value << ((idx & 0x3) << 1));
		count++;
	}

	abstract long do_move (long idx, int move);

	public void analyse (){
		int i, dist;
		long idx, old_count = 0;
		int old_dist, new_dist;
		int max_dist = 30;	//MAX_DISTANCE;

		init();

		long new_count = count;
		for (dist = 0; dist < max_dist && new_count > 0 && dist < back_dist; ++dist) {
			System.out.println(" dist "+dist+": "+count+" positions.");
			old_count = count;
			old_dist = ((dist + 2) % 3) + 1;
			//new_dist = (dist % 3) + 1;
			for (idx = 0; idx < num_positions; ++idx) {
				if (get_dist(idx) == old_dist){
					//generate (idx, 0, new_dist);
					generate (idx, 0, (dist%3)+1);
				}
			}
			new_count = count - old_count;
		}
		System.out.println("Switch to backward search");
		for (; dist < max_dist && new_count > 0; ++dist) {
			System.out.println(" dist "+dist+": "+count+" positions.");
			old_count = count;
			old_dist = ((dist + 2) % 3) + 1;
			new_dist = (dist % 3) + 1;
			for (idx = 0; idx < num_positions; ++idx) {
				if (get_dist(idx) == 0){
					generate (idx, old_dist, new_dist);
				}
			}
			new_count = count - old_count;
		}

		System.out.println("Generate "+count+" positions and "+unique_count+" unique.");

		System.out.println("Packing table: "+(num_positions/4+1)+" -> "+n_packed);
		pack();
		ptable = null;
		System.gc();
	}

	protected void generate (long idx, int dist, int new_dist){
		int i, j;

		for (i = 0; i < num_moves; ++i) {
			long idx2 = do_move (idx, i);
			if (get_dist(idx2) == dist){
				unique_count++;
				if ( dist == 0 )
					saveIdxAndSyms( idx2, new_dist );
				else {
					set_dist( idx, new_dist );
					break;
				}
			}
		}
	}

	abstract void saveIdxAndSyms (long idx, int dist);

	private void pack (){ /* Taken from Chen Shuang's 8 step solver */
		for (int i=0; i<n_packed; i++) {
			int n = 1;
			int value = 0;
			for (int j=0; j<4; j++){
				value += n * (get_dist(4L*i+j) % 3);
				n *= 3;
			}
			if ((n_packed*4L+i) < num_positions)
				value += n * (get_dist(n_packed*4L+i) % 3);
			ptable_packed[i] = (byte)value;
		}
		ptable = null;
		System.gc();
	}
}
