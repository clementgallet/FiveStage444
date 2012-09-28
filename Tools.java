package cg.fivestage444;

import cg.fivestage444.Stages.Stage1;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class Tools {
	private static final Logger l = Logger.getLogger(Tools.class.getName());

	private static void read(byte[] arr, DataInput in) throws IOException {
		in.readFully(arr);
	}

	private static void write(byte[] arr, DataOutput out) throws IOException {
		out.write(arr);
	}

	public static synchronized void init() {
		init(true, null);
	}

	private static void prepareTables() {
		Symmetry.init();
		Util.init();
		CubePack.init();
	}

	private static volatile boolean inited = false;

	private static synchronized void init(boolean tryToReadFile, File fivephase_tables) {
		if( inited ) {
			return;
		}

		if(fivephase_tables == null) {
			fivephase_tables = new File("fivephase_tables.rbk");
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
			if( Util.FULL_PRUNING3 ){
				Stage3.init();
			}
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
		if( ! Util.FULL_PRUNING3 ){
			Stage3.init();
		}
		Stage5.init();
		inited = true;
	}

	private static boolean initFrom(DataInput in) {
		try {
			Stage1.prunTable = new byte[(Stage1.N_SIZE+1)/2];
			read(Stage1.prunTable, in);
			Stage1.init();
			Stage4.prunTable = new byte[(Stage4.N_SIZE+1)/2];
			read(Stage4.prunTable, in);
			Stage4.init();
			if( Util.FULL_PRUNING3 ){
				Stage3.prunTable = new byte[(Stage3.N_SIZE+1)/2];
				read(Stage3.prunTable, in);
				Stage3.init();
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Stage1.prunTable = null;
			Stage3.prunTable = null;
			Stage4.prunTable = null;
			return false;
		}
	}

	private static void initTo(DataOutput out) throws IOException {
		write(Stage1.prunTable, out); // 15582 * 2187          / 2  =  17,038,917 B
		write(Stage4.prunTable, out); // 420 * 35 * 5968       / 2  =  43,864,800 B
		if( Util.FULL_PRUNING3 )
			write(Stage3.prunTable, out); // 12870 * 56980 / 2  = 366,666,300 B
	}

}
