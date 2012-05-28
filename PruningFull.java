package fivestage444;

import static fivestage444.Constants.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

abstract class PruningFull {

	protected int num_positions;
	public byte[] ptable;
	protected int num_moves;
	protected int count = 0;
	protected int unique_count = 0;
	protected File fname;
	protected int back_dist = 16;

	private void writeToFile(){
		try {
			FileOutputStream fos = new FileOutputStream (fname);
			BufferedOutputStream output = new BufferedOutputStream(fos);

			System.out.println ("Creating pruning table file '"+fname.getName()+"'.");
			output.write( ptable, 0, num_positions);
			output.flush();
			output.close();
			}
		catch(java.io.IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			System.out.println ("Warning: Failed to create pruning file " + fname);
		}
	}

	private void readFromFile(){
		System.out.println("Read file "+fname.getName());
		try {
			FileInputStream fis = new FileInputStream (fname);
			BufferedInputStream input = new BufferedInputStream(fis);
			input.read (ptable, 0, num_positions);
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

	abstract int do_move (int idx, int move);

	public void analyse (){
		int i, dist;
		int idx, old_count = 0;
		int max_dist = 16;	//MAX_DISTANCE;

		init ();

		if (fname.exists() ) {
			readFromFile();
			return;
		}

		int new_count = count;
		for (dist = 0; dist < max_dist && new_count > 0 && dist < back_dist; ++dist) {
			System.out.println(" dist "+dist+": "+new_count+" positions.");
			old_count = count;
			for (idx = 0; idx < num_positions; ++idx) {
				if (ptable[idx] == dist){
					generate (idx, -1, dist + 1);
				}
			}
			new_count = count - old_count;
		}
		System.out.println("Switch to backward search");
		for (; dist < max_dist && new_count > 0; ++dist) {
			System.out.println(" dist "+dist+": "+new_count+" positions.");
			old_count = count;
			for (idx = 0; idx < num_positions; ++idx) {
				if (ptable[idx] == -1){
					generate (idx, dist, dist + 1);
				}
			}
			new_count = count - old_count;
		}

		System.out.println("Generate "+count+" positions and "+unique_count+" unique.");

		writeToFile();
	}

	protected void generate (int idx, int dist, int new_dist){
		int i, j;

		for (i = 0; i < num_moves; ++i) {
			int idx2 = do_move (idx, i);
			if (ptable[idx2] == dist){
				unique_count++;
				if ( dist == -1 )
					saveIdxAndSyms( idx2, new_dist );
				else {
					saveIdxAndSyms( idx, new_dist );
					break;
				}
			}
		}
	}

	abstract void saveIdxAndSyms (int idx, int dist);

}
