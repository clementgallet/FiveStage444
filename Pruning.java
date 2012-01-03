package fivestage444;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

abstract class Pruning {

	long num_positions;
	byte[] ptable;
	int num_moves;
	int num_solved;
	int[] psolved;
	int count = 0;
	File fname;

	void writeToFile(){
		try {
			FileOutputStream fos = new FileOutputStream (fname);
			BufferedOutputStream output = new BufferedOutputStream(fos);

			System.out.println ("Creating pruning table file '"+fname.getName()+"'.");
			output.write( ptable, 0, (int)(num_positions/4 + 1));
			output.flush();
			output.close();
			}
		catch(java.io.IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			System.out.println ("Warning: Failed to create pruning file " + fname);
		}
	}

	void readFromFile(){
		System.out.println("Read file "+fname.getName());
		try {
			FileInputStream fis = new FileInputStream (fname);
			BufferedInputStream input = new BufferedInputStream(fis);
			input.read (ptable, 0, (int)(num_positions/4 + 1));
			input.close();
		}
		catch(java.io.FileNotFoundException e)
		{
			System.out.println("File not found" + e);
			System.out.println("Error reading pruning table file '"+fname+"'");
		}
		catch(java.io.IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			System.out.println("Error reading pruning table file '"+fname+"'");
		}
	}


	abstract void init ();

	int get_dist (long idx){
		return (ptable[(int)(idx>>2)] >> ((idx & 0x3) << 1)) & 0x3;
	}

	void set_dist (long idx, int value){
		ptable[(int)(idx>>2)] |= (byte)(value << ((idx & 0x3) << 1));
		count++;
	}

	abstract long do_move (long idx, int move);

	public void analyse (){
		int i, dist;
		long idx;
		int max_dist = 25;	//MAX_DISTANCE;

		init ();

		if (fname.exists() ) {
			readFromFile();
			return;
		}

		int new_count = count;
		for (dist = 0; dist < max_dist && new_count > 0; ++dist) {
			System.out.println(" dist "+dist+": "+new_count+" positions.");
			int old_count = count;
			for (idx = 0; idx < num_positions; ++idx) {
				if (get_dist(idx) == (((dist + 2) % 3) + 1)){
					generate (idx, (dist % 3) + 1);
				}
			}
			new_count = count - old_count;
		}
		System.out.println("Generate "+count+" positions.");

		writeToFile();
	}

	void generate (long idx, int dist)
	{
		int i, j;

		for (i = 0; i < num_moves; ++i) {
			long idx2 = do_move (idx, i);
			if (get_dist(idx2) == 0){
				saveIdxAndSyms( idx2, dist );
			}
		}
	}

	abstract void saveIdxAndSyms (long idx, int dist);

}
