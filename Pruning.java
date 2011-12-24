package fivestage444;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

abstract class Pruning {

	int num_positions;
	byte[] ptable;
	int num_moves;
	int[] move_list;
	int num_solved;
	int[] psolved;
	int count = 0;
	File fname;

	void writeToFile(){
		try {
			FileOutputStream fos = new FileOutputStream (fname);
			BufferedOutputStream output = new BufferedOutputStream(fos);

			System.out.println ("Creating pruning table file '"+fname.getName()+"'.");
			output.write( ptable, 0, num_positions/4 + 1);
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
			input.read (ptable, 0, num_positions/4 + 1);
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

	int get_dist (int idx){
		return (ptable[idx>>2] >> ((idx & 0x3) << 1)) & 0x3;
	}

	void set_dist (int idx, int value){
		ptable[idx>>2] |= (byte)(value << ((idx & 0x3) << 1));
		count++;
	}

	abstract int do_move (int idx, int move);

	public void analyse (){
		int i;
		int idx;
		int dist;
		int max_dist = 20;	//MAX_DISTANCE;
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

		writeToFile();
	}

	void generate (int idx, int dist)
	{
		int i, j;

		//if( idx == 20626457 ) System.out.println("Init_dist:"+get_dist(idx)+"set_dist:"+dist);

		for (i = 0; i < num_moves; ++i) {
			int idx2 = do_move (idx, move_list[3*i]);
			for (j = 1; j < 3 && move_list[3*i + j] >= 0; ++j) {
				idx2 = do_move (idx2, move_list[3*i + j]);
			}
			if (get_dist(idx2) == 0){
				saveIdxAndSyms( idx2, dist );
				/*
				if( idx2 == 20626457 ){
					//System.out.println("From idx "+idx+" with distance "+get_dist(idx)+" I set cur_idx to "+dist+" with move "+i);
					System.out.println("From edge "+(idx/Constants.N_SQS_CENTER_PERM)+" I set cur_edge "+(20626457/48));
					System.out.println("Applying the same move gives idx "+(do_move(idx2,i)));
					System.out.println("Applying the same move again gives idx "+(do_move(do_move(idx2,i),i)));
				}*/
				//set_dist (idx2, dist);
				//count++;
			}
		}
	}

	abstract void saveIdxAndSyms (int idx, int dist);

}
