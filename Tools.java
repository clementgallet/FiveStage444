package cg.fivestage444;

import static cg.fivestage444.Constants.*;

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

//import net.gnehzr.tnoodle.utils.Utils;
//import net.gnehzr.tnoodle.utils.TimedLogRecordStart;

public class Tools {
	private static final Logger l = Logger.getLogger(Tools.class.getName());

	static void read(byte[] arr, DataInput in) throws IOException {
		in.readFully(arr);
	}

	static void read(short[] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			arr[i] = in.readShort();
		}
	}

	static void read(int[] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			arr[i] = in.readInt();
		}
	}

	static void read(byte[][] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (char i=0; i<length; i++) {
			in.readFully(arr[i]);
		}
	}

	static void read(short[][] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			final int len = arr[i].length;
			for (int j=0; j<len; j++) {
				arr[i][j] = in.readShort();
			}
		}	
	}

	static void read(int[][] arr, DataInput in) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			final int len = arr[i].length;
			for (int j=0; j<len; j++) {
				arr[i][j] = in.readInt();
			}
		}	
	}

	static void write(byte[] arr, DataOutput out) throws IOException {
		out.write(arr);
	}

	static void write(short[] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			out.writeShort(arr[i]);
		}
	}

	static void write(int[] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			out.writeInt(arr[i]);
		}
	}

	static void write(byte[][] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (char i=0; i<length; i++) {
			out.write(arr[i]);
		}
	}

	static void write(short[][] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			final int len = arr[i].length;
			for (int j=0; j<len; j++) {
				out.writeShort(arr[i][j]);
			}
		}	
	}
	
	static void write(int[][] arr, DataOutput out) throws IOException {
		final int length = arr.length;
		for (int i=0; i<length; i++) {
			final int len = arr[i].length;
			for (int j=0; j<len; j++) {
				out.writeInt(arr[i][j]);
			}
		}	
	}
	
	public static synchronized void init() {
		init(true, null);
	}

	private static void prepareTables() {
		Symmetry.init();
		Tables.init();
	}

	public static enum InitializationState {
		UNINITIALIZED,
		INITING_TABLES,
		INITIALIZED;
	}

	public static InitializationState getInitializationState() {
		return inited;
	}

	static volatile InitializationState inited = InitializationState.UNINITIALIZED;

	private static synchronized void init(boolean tryToReadFile, File fivephase_tables) {
		if(inited != InitializationState.UNINITIALIZED) {
			return;
		}

		if(fivephase_tables == null) {
			//fivephase_tables = new File(Utils.getResourceDirectory(), "fivephase_tables");
			fivephase_tables = new File("cg/fivestage444/fivephase_tables");
		}

		prepareTables();
		if(tryToReadFile) {
			try {
				FileInputStream is = new FileInputStream(fivephase_tables);
				if(initFrom(new DataInputStream(is))) {
					inited = InitializationState.INITIALIZED;
				}
			} catch (FileNotFoundException e) {
				l.info("Couldn't find " + fivephase_tables + ", going to create it.");
			}
		}
		if(inited == InitializationState.UNINITIALIZED) {
			//TimedLogRecordStart start = new TimedLogRecordStart("Generating fivephase tables");
			//l.log(start);

			inited = InitializationState.INITING_TABLES;
			Tables.init_tables();

			try {
				l.info("Writing to " + fivephase_tables);
				FileOutputStream out = new FileOutputStream(fivephase_tables);
				DataOutputStream dataOut = new DataOutputStream(out);
				initTo(dataOut);
			} catch(IOException e) {
				l.log(Level.INFO, "Failed to write to " + fivephase_tables, e);
			}
			
			//l.log(start.finishedNow());
		}
		inited = InitializationState.INITIALIZED;
		// if( ! checkTables() ) System.out.println( "There might be a problem with tables integrity...");
	}
	
	public static boolean initFrom(DataInput in) {
		try {
			read(Tables.prunTableEdgCor5, in);
			read(Tables.prunTableEdgCen5, in);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void initTo(DataOutput out) throws IOException {
		write(Tables.prunTableEdgCen4, out); //            +    104,440 B
		write(Tables.prunTableEdgCor5, out); //            +    357,312 B
	}

	public static void main(String[] args) throws IOException {
		System.out.println(Arrays.toString(args));
		if(args.length != 1) {
			System.out.println("Please provide 1 argument: the file to store the tables in");
			System.exit(1);
		}
		init(false, new File(args[0]));
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
