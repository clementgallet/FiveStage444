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
		Tables.prune_table_edgcen2 = new PruningStage2EdgCen();
		Tables.prune_table_cen3 = new PruningStage3Cen();
		Tables.prune_table_edg3 = new PruningStage3Edg();
		Tables.prune_table_edgcen4 = new PruningStage4EdgCen();
		Tables.prune_table_edgcor5 = new PruningStage5EdgCor();
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
			Tables.prune_table_edgcen2.analyse();
			Tables.prune_table_cen3.analyse();
			Tables.prune_table_edg3.analyse();
			Tables.prune_table_edgcen4.analyse();
			Tables.prune_table_edgcor5.analyse();

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
		if( ! checkTables() ) System.out.println( "There might be a problem with tables integrity...");
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
			read(Tables.symHelper5, in);

			read(Tables.prunTable1, in);
			read(Tables.prune_table_edgcen2.ptable, in);
			read(Tables.prune_table_cen3.ptable, in);
			read(Tables.prune_table_edg3.ptable, in);
			read(Tables.prunTableEdgCor4, in);
			read(Tables.prune_table_edgcen4.ptable, in);
			read(Tables.prune_table_edgcor5.ptable, in);
			read(Tables.prunTableEdgCen5, in);

			inited = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void initTo(DataOutput out) throws IOException {
		write(Tables.sym2rawEdge1, out); //         62,328 B
		write(Tables.moveEdge1, out); //       + 2,243,808 B
		write(Tables.moveCorner1, out); //     +    78,732 B
		write(Tables.conjCorner1, out); //     +   209,952 B
		write(Tables.moveEdge2, out); //       +    23,520 B
		write(Tables.conjEdge2, out); //       +    13,440 B
		write(Tables.sym2rawCenter2, out); //  +     1,432 B
		write(Tables.moveCenter2, out); //     +    40,096 B
		write(Tables.sym2rawCenter3, out); //  +   227,920 B
		write(Tables.moveCenter3, out); //     + 4,558,400 B
		write(Tables.moveEdge3, out); //       +   514,800 B
		write(Tables.conjEdge3, out); //       +   411,840 B
		write(Tables.sym2rawEdge4, out); //    +    23,872 B
		write(Tables.moveEdge4, out); //       +   381,952 B
		write(Tables.moveCorner4, out); //     +    13,440 B
		write(Tables.conjCorner4, out); //     +    13,440 B
		write(Tables.moveCenter4, out); //     +     1,120 B
		write(Tables.conjCenter4, out); //     +     1,120 B
		write(Tables.moveCorner5, out); //     +     1,152 B
		write(Tables.conjCorner5, out); //     +    18,432 B
		write(Tables.moveCenter5, out); //     +    41,472 B
		write(Tables.conjCenter5, out); //     +   663,552 B
		write(Tables.sym2rawEdge5, out); //    +    29,776 B
		write(Tables.moveEdge5, out); //       +   357,312 B
		write(Tables.symHelper5, out); //      +   xxx,xxx B
		//                     all move tables = 9,932,908 B

		write(Tables.prunTable1, out); //                     6,815,567 B
		write(Tables.prune_table_edgcen2.ptable, out); //   +    300,720 B
		write(Tables.prune_table_cen3.ptable, out); //      +     56,980 B
		write(Tables.prune_table_edg3.ptable, out); //      +     25,740 B
		write(Tables.prunTableEdgCor4, out); //            +    501,313 B
		write(Tables.prune_table_edgcen4.ptable, out); //   +    417,760 B
		write(Tables.prune_table_edgcor5.ptable, out); //   +    714,624 B
		write(Tables.prunTableEdgCen5, out); //            +  2,572,647 B
		//                              all pruning tables = 11,405,351 B
	}
	
	public static boolean checkTables() {
		if( Arrays.deepHashCode(new Object[]{Tables.sym2rawEdge1}) != -1678978030 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveEdge1}) != -1836370822 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCorner1}) != 53409135 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjCorner1}) != 1899376863 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveEdge2}) != 1775527632 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjEdge2}) != -1576838944 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.sym2rawCenter2}) != -382050577 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCenter2}) != -1888070592 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.sym2rawCenter3}) != -1020604492 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCenter3}) != 622005811 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveEdge3}) != 1927805216 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjEdge3}) != 773090752 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.sym2rawEdge4}) != -190552920 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveEdge4}) != 938076253 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCorner4}) != 375889888 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjCorner4}) != -2082952064 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCenter4}) != 688119576 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjCenter4}) != -1065647936 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCorner5}) != -348572128 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjCorner5}) != 566954016 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveCenter5}) != 1392919328 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.conjCenter5}) != -1913154528 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.sym2rawEdge5}) != -1356505928 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.moveEdge5}) != -1518191552 ) return false;

		//if( Arrays.deepHashCode(new Object[]{CubeStage1.prune_table.ptable_packed}) != -1008636558 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.prune_table_edgcen2.ptable}) != -1962629242 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.prune_table_cen3.ptable}) != -1309504063 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.prune_table_edg3.ptable}) != -2007839488 ) return false;
		//if( Arrays.deepHashCode(new Object[]{CubeStage4.prune_table_edgcor.ptable_packed}) != 1520100235 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.prune_table_edgcen4.ptable}) != 1168357152 ) return false;
		//if( Arrays.deepHashCode(new Object[]{CubeStage5.prune_table_edgcen.ptable_packed}) != 1661693585 ) return false;
		if( Arrays.deepHashCode(new Object[]{Tables.prune_table_edgcor5.ptable}) != 1458653136 ) return false;

		return true;
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
