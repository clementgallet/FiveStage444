package cg.fivestage444;

import static cg.fivestage444.Constants.*;
import cg.fivestage444.Stage1;
import cg.fivestage444.Stage2;
import cg.fivestage444.Stage3;
import cg.fivestage444.Stage4;
import cg.fivestage444.Stage5;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Tools {
	private static final Logger l = Logger.getLogger(Tools.class.getName());

	static void read(int[] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			arr[i] = in.readInt();
		}
	}

	static void write(int[] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			out.writeInt(arr[i]);
		}
	}
	
	public static synchronized void init() {
		init(true, null);
	}

	private static void prepareTables() {
		Symmetry.init();
		Tables.init();
	}

	static volatile boolean inited = false;

	private static synchronized void init(boolean tryToReadFile, File fivephase_tables) {
		if( inited ) {
			return;
		}

		if(fivephase_tables == null) {
			fivephase_tables = new File("cg/fivestage444/fivephase_tables");
		}

		prepareTables();
		if(tryToReadFile) {
			try {
				FileInputStream is = new FileInputStream(fivephase_tables);
				if(initFrom(new DataInputStream(is))) {
					inited = true;
				}
			} catch (FileNotFoundException e) {
				l.info("Couldn't find " + fivephase_tables + ", going to create it.");
			}
		}
		if( ! inited ) {
			Stage1.init();
			Stage4.init();

			try {
				l.info("Writing to " + fivephase_tables);
				FileOutputStream out = new FileOutputStream(fivephase_tables);
				DataOutputStream dataOut = new DataOutputStream(out);
				initTo(dataOut);
			} catch(IOException e) {
				l.log(Level.INFO, "Failed to write to " + fivephase_tables, e);
			}
			
		}
		Stage2.init();
		Stage3.init();
		Stage5.init();
		inited = true;
	}
	
	public static boolean initFrom(DataInput in) {
		try {
			Stage1.prunTable = new int[(Edge1.N_COORD * Corner1.N_COORD+7)/8];
			read(Stage1.prunTable, in);
			Stage1.init();
			Stage4.prunTable = new int[(Edge4.N_COORD * Corner4.N_COORD * Center4.N_COORD+7)/8];
			read(Stage4.prunTable, in);
			Stage4.init();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Stage1.prunTable = null;
			Stage4.prunTable = null;
			return false;
		}
	}

	public static void initTo(DataOutput out) throws IOException {
		write(Stage1.prunTable, out); //            +    104,440 B
		write(Stage4.prunTable, out); //            +    357,312 B
	}

	/**
	 * Generates a random cube.
	 * @return A random cube in the string representation. Each cube of the cube space has the same probability.
	 */
	public static CubeState randomCube() {
		Random gen = new Random();
		return randomCube(gen);
	}
	
	public static CubeState randomCube(Random r) {
		int i, o, os;
		CubeState cube = new CubeState();
		cube.init ();

		/* Randomize corners */
		randomPerm(r, cube.m_cor, 8);
		os = 0;
		for (i=0; i<7; i++){
			o = r.nextInt(3);
			cube.m_cor[i] += 8*o;
			os += o;
		}
		cube.m_cor[7] += 8*((15 - os) % 3);

		/* Randomize centers */
		randomPerm(r, cube.m_cen, 24);

		/* Randomize edges */
		randomPerm(r, cube.m_edge, 24);

		return cube;
	}

	public static int checkSolution(CubeState cube, byte[] moves){

		for (int i=moves.length-1; i>=0; i--){
			int themove = moves[i] + ((( moves[i] + 2 ) % 3 ) - 1);
			cube.do_move(themove);
		}
		return cube.is_solved();
	}

	public static int checkSolution(CubeState cube, String scramble){

		String[] moves = scramble.split("\\s+");
		for (int i=moves.length-1; i>=0; i--){
			if( moves[i].isEmpty() ) continue;
			/* Do it the lazy way... */
			int themove = -1;
			for(int m=0; m < Constants.N_MOVES; m++){
				if( moves[i].equals( Constants.move_strings[m] )){
					themove = m;
					break;
				}
			}
			if( themove == -1 ){
				System.out.println("Unknown move");
				return -1;
			}
			themove = themove + ((( themove + 2 ) % 3 ) - 1);
			cube.do_move(themove);
		}
		return cube.is_solved();
	}

	/* Fisher-Yates shuffle */
	private static void randomPerm(Random r, byte[] array, int n) {
		int i, j;
		byte t;
		for (i = n-1; i > 0; i--){
			j = r.nextInt(i+1);
			t = array[i];
			array[i] = array[j];
			array[j] = t;
		}
	}
}
