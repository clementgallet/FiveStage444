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

	static boolean inited = false;
	
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
		CubeStage1.prune_table = new PruningStage1();
		CubeStage2.prune_table_edgcen = new PruningStage2EdgCen();
		CubeStage3.prune_table_cen = new PruningStage3Cen();
		CubeStage3.prune_table_edg = new PruningStage3Edg();
		/** CubeStage4.prune_table = new PruningStage4(); **/
		CubeStage4.prune_table_edgcen = new PruningStage4EdgCen();
		CubeStage4.prune_table_edgcor = new PruningStage4EdgCor();
		CubeStage5.prune_table_edgcen = new PruningStage5EdgCen();
		CubeStage5.prune_table_edgcor = new PruningStage5EdgCor();
	}

	private static synchronized void init(boolean tryToReadFile, File fivephase_tables) {
		if (inited)
			return;
		
		if(fivephase_tables == null) {
			//fivephase_tables = new File(Utils.getResourceDirectory(), "fivephase_tables");
			fivephase_tables = new File("cg/fivestage444/fivephase_tables_"+METRIC_STR);
		}

		prepareTables();
		if(tryToReadFile) {
			try {
				FileInputStream is = new FileInputStream(fivephase_tables);
				inited = initFrom(new DataInputStream(is));
			} catch (FileNotFoundException e) {
				l.info("Couldn't find " + fivephase_tables + ", going to create it.");
			}
		}
		if(!inited) {
			//TimedLogRecordStart start = new TimedLogRecordStart("Generating fivephase tables");
			//l.log(start);

			Tables.init_tables();
			CubeStage1.prune_table.analyse();
			CubeStage2.prune_table_edgcen.analyse();
			CubeStage3.prune_table_cen.analyse();
			CubeStage3.prune_table_edg.analyse();
			/** CubeStage4.prune_table.analyse(); **/
			CubeStage4.prune_table_edgcen.analyse();
			CubeStage4.prune_table_edgcor.analyse();
			CubeStage5.prune_table_edgcen.analyse();
			CubeStage5.prune_table_edgcor.analyse();

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
		inited = true;
	}
	
	public static boolean initFrom(DataInput in) {
		try {
			read(Tables.sym2rawEdge1, in);
			read(Tables.moveEdge1, in);
			read(Tables.moveCorner1, in);
			read(Tables.conjCorner1, in);
			read(Tables.moveEdge2, in);
			read(Tables.conjEdge2, in);
			read(Tables.sym2rawCenter2, in);
			read(Tables.moveCenter2, in);
			read(Tables.sym2rawCenter3, in);
			read(Tables.moveCenter3, in);
			read(Tables.moveEdge3, in);
			read(Tables.conjEdge3, in);
			read(Tables.sym2rawEdge4, in);
			read(Tables.moveEdge4, in);
			read(Tables.moveCorner4, in);
			read(Tables.conjCorner4, in);
			read(Tables.moveCenter4, in);
			read(Tables.conjCenter4, in);
			read(Tables.moveCorner5, in);
			read(Tables.conjCorner5, in);
			read(Tables.moveCenter5, in);
			read(Tables.conjCenter5, in);
			read(Tables.sym2rawEdge5, in);
			read(Tables.moveEdge5, in);

			read(CubeStage1.prune_table.ptable_packed, in);
			read(CubeStage2.prune_table_edgcen.ptable, in);
			read(CubeStage3.prune_table_cen.ptable, in);
			read(CubeStage3.prune_table_edg.ptable, in);
			/** read(CubeStage4.prune_table.ptable_packed, in); **/
			read(CubeStage4.prune_table_edgcor.ptable_packed, in);
			read(CubeStage4.prune_table_edgcen.ptable, in);
			read(CubeStage5.prune_table_edgcen.ptable_packed, in);
			read(CubeStage5.prune_table_edgcor.ptable, in);

			inited = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void initTo(DataOutput out) throws IOException {
		write(Tables.sym2rawEdge1, out);
		write(Tables.moveEdge1, out);
		write(Tables.moveCorner1, out);
		write(Tables.conjCorner1, out);
		write(Tables.moveEdge2, out);
		write(Tables.conjEdge2, out);
		write(Tables.sym2rawCenter2, out);
		write(Tables.moveCenter2, out);
		write(Tables.sym2rawCenter3, out);
		write(Tables.moveCenter3, out);
		write(Tables.moveEdge3, out);
		write(Tables.conjEdge3, out);
		write(Tables.sym2rawEdge4, out);
		write(Tables.moveEdge4, out);
		write(Tables.moveCorner4, out);
		write(Tables.conjCorner4, out);
		write(Tables.moveCenter4, out);
		write(Tables.conjCenter4, out);
		write(Tables.moveCorner5, out);
		write(Tables.conjCorner5, out);
		write(Tables.moveCenter5, out);
		write(Tables.conjCenter5, out);
		write(Tables.sym2rawEdge5, out);
		write(Tables.moveEdge5, out);

		write(CubeStage1.prune_table.ptable_packed, out);
		write(CubeStage2.prune_table_edgcen.ptable, out);
		write(CubeStage3.prune_table_cen.ptable, out);
		write(CubeStage3.prune_table_edg.ptable, out);
		/** write(CubeStage4.prune_table.ptable_packed, out); **/
		write(CubeStage4.prune_table_edgcor.ptable_packed, out);
		write(CubeStage4.prune_table_edgcen.ptable, out);
		write(CubeStage5.prune_table_edgcen.ptable_packed, out);
		write(CubeStage5.prune_table_edgcor.ptable, out);
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

	public static boolean checkSolution(CubeState cube, String scramble){

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
				return false;
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
