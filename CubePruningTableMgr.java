package fivestage444;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//Class to create and clean up all pruning tables
public final class CubePruningTableMgr {

	private static String metric_names [] = { "stm", "ttm", "btm" };
	private static String metric_long_names[] = { "slice", "twist", "block" };

	private static byte switch_list[][] = {
		{ 17, 19, 20, 22 },
		{ 17, 19, 21, 23 },
		{ 17, 18, 21, 22 },
		{ 17, 18, 20, 23 },
		{ 18, 19, 22, 23 }
	};

	public static CubePruningTable pcpt_edgcen2;
	public static CubePruningTable pcpt_cen3;
	public static CubePruningTable pcpt_edg3;

	private static void writeToFile( File fname, byte[] array, int length ){
		try {
			FileOutputStream fos = new FileOutputStream (fname);
			BufferedOutputStream output = new BufferedOutputStream(fos);

			System.out.println ("Creating pruning table file '"+fname.getName()+"'.");
			output.write( array, 0, length);
			output.flush();
			output.close();
			}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			System.out.println ("Warning: Failed to create pruning file " + fname);
		}
	}

	private static void readFromFile( File fname, byte[] array, int length ){
		try {
			FileInputStream fis = new FileInputStream (fname);
			BufferedInputStream input = new BufferedInputStream(fis);
			input.read (array, 0, length);
			input.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("File not found" + e);
			System.out.println("Error reading pruning table file '"+fname+"'");
		}
		catch(IOException ioe){
			System.out.print("Erreur : ");
			ioe.printStackTrace();
			System.out.println("Error reading pruning table file '"+fname+"'");
		}
	}

	public static void init_pruning_tables (int metric){
		int i;
		int[] solved_table = new int[24];
		int[] tmp_list = new int[64*3];
		File fname;
		CubeStage2 stage2_solved = new CubeStage2();
		CubeStage2 stage2_solved2 = new CubeStage2();
		CubeStage3 stage3_solved = new CubeStage3();
		CubeState cs1 = new CubeState();

		/*** Stage 2 ***/
		System.out.println("Stage2...");
		int clocfx;
		fname = new File( Constants.datafiles_path, "stage2_" + metric_names[metric] + "_edgcen_prune.rbk" );
		if (! fname.exists() ) {
			stage2_solved.init ();
			clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved.m_centerFB);
			solved_table[0] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved.m_edge;

			stage2_solved2.m_centerFB = stage2_solved.m_centerFB;
			stage2_solved2.m_edge = stage2_solved.m_edge;
			stage2_solved2.do_whole_cube_move (1);
			clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
			solved_table[1] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

			cs1.init ();
			cs1.invert_fbcen ();
			cs1.convert_to_stage2 (stage2_solved2);
			clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
			solved_table[2] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

			stage2_solved2.do_whole_cube_move (1);
			clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
			solved_table[3] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

			for (i = 0; i < 5; ++i) {
				int j;
				cs1.init ();
				for (j = 0; j < 4; ++j) {
					cs1.m_cen[switch_list[i][j]] ^= 1;
				}
				cs1.convert_to_stage2 (stage2_solved2);
				clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
				solved_table[4*i + 4] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

				stage2_solved2.do_whole_cube_move (1);
				clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
				solved_table[4*i + 5] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

				cs1.invert_fbcen ();
				cs1.convert_to_stage2 (stage2_solved2);
				clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
				solved_table[4*i + 6] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;

				stage2_solved2.do_whole_cube_move (1);
				clocfx = Tables.stage2_cen_to_cloc4sf (stage2_solved2.m_centerFB);
				solved_table[4*i + 7] = Constants.N_STAGE2_EDGE_CONFIGS*clocfx + stage2_solved2.m_edge;
			}

			pcpt_edgcen2 = new CubePruningTable (Constants.N_CENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS, CubeStage2.prune_table_edgcen2, new DoMoveEC2STM());
			switch (metric) {
			case 0:
				pcpt_edgcen2.init_move_list (0, Constants.N_STAGE2_SLICE_MOVES, tmp_list);
				break;
			case 1:
				for (i = 0; i < Constants.N_STAGE2_TWIST_MOVES; ++i) {
					tmp_list[2*i] = Constants.stage2_twist_moves[i][0];
					tmp_list[2*i+1] = Constants.stage2_twist_moves[i][1];
				}
				pcpt_edgcen2.init_move_list (2, Constants.N_STAGE2_TWIST_MOVES, tmp_list);
				for (i = 0; i < Constants.N_STAGE2_2TWIST_MOVES; ++i) {
					tmp_list[2*i] = Constants.stage2_2twist_moves[i][0];
					tmp_list[2*i+1] = Constants.stage2_2twist_moves[i][1];
				}
				pcpt_edgcen2.init_move_list2 (2, Constants.N_STAGE2_2TWIST_MOVES, tmp_list);
				break;
			case 2:
				for (i = 0; i < Constants.N_STAGE2_BLOCK_MOVES; ++i) {
					tmp_list[2*i] = Constants.stage2_block_moves[i][0];
					tmp_list[2*i+1] = Constants.stage2_block_moves[i][1];
				}
				pcpt_edgcen2.init_move_list (2, Constants.N_STAGE2_BLOCK_MOVES, tmp_list);
			}
			pcpt_edgcen2.init_solved_list (24, solved_table);
			pcpt_edgcen2.analyze ();

			writeToFile( fname, CubeStage2.prune_table_edgcen2, Constants.N_CENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS/2);
		} else {
			readFromFile( fname, CubeStage2.prune_table_edgcen2, Constants.N_CENTER_COMBO4*Constants.N_STAGE2_EDGE_CONFIGS/2);
		}
	
		/*** Stage 3 ***/
		System.out.println ("Stage3...");
		for (i = 0; i < Constants.STAGE3_NUM_SOLVED_CENTER_CONFIGS; ++i) {
			solved_table[i] = Constants.stage3_solved_centers[i];
		}

		pcpt_cen3 = new CubePruningTable (Constants.N_STAGE3_CENTER_CONFIGS, CubeStage3.prune_table_cen3, new DoMoveC3STM());
		switch (metric) {
		case 0:
			pcpt_cen3.init_move_list (0, Constants.N_STAGE3_SLICE_MOVES, tmp_list);
			break;
		case 1:
			for (i = 0; i < Constants.N_STAGE3_TWIST_MOVES; ++i) {
				tmp_list[2*i] = Constants.stage3_twist_moves[i][0];
				tmp_list[2*i+1] = Constants.stage3_twist_moves[i][1];
			}
			pcpt_cen3.init_move_list (2, Constants.N_STAGE3_TWIST_MOVES, tmp_list);
			for (i = 0; i < Constants.N_STAGE3_2TWIST_MOVES; ++i) {
				tmp_list[2*i] = Constants.stage3_2twist_moves[i][0];
				tmp_list[2*i+1] = Constants.stage3_2twist_moves[i][1];
			}
			pcpt_cen3.init_move_list2 (2, Constants.N_STAGE3_2TWIST_MOVES, tmp_list);
	
			break;
		case 2:
			for (i = 0; i < Constants.N_STAGE3_BLOCK_MOVES; ++i) {
				tmp_list[2*i] = Constants.stage3_block_moves[i][0];
				tmp_list[2*i+1] = Constants.stage3_block_moves[i][1];
			}
			pcpt_cen3.init_move_list (2, Constants.N_STAGE3_BLOCK_MOVES, tmp_list);
			break;
		}
		pcpt_cen3.init_solved_list (Constants.STAGE3_NUM_SOLVED_CENTER_CONFIGS, solved_table);
		pcpt_cen3.analyze ();
	
		stage3_solved.init ();
		solved_table[0] = stage3_solved.m_edge;

		pcpt_edg3 = new CubePruningTable (Constants.N_STAGE3_EDGE_PAR*Constants.N_STAGE3_EDGE_CONFIGS, CubeStage3.prune_table_edg3, new DoMoveE3STM());
		switch (metric) {
		case 0:
			break;
		case 1:
			for (i = 0; i < Constants.N_STAGE3_TWIST_MOVES; ++i) {
				tmp_list[2*i] = Constants.stage3_twist_moves[i][0];
				tmp_list[2*i+1] = Constants.stage3_twist_moves[i][1];
			}
			pcpt_edg3.init_move_list (2, Constants.N_STAGE3_TWIST_MOVES, tmp_list);
			for (i = 0; i < Constants.N_STAGE3_2TWIST_MOVES; ++i) {
				tmp_list[2*i] = Constants.stage3_2twist_moves[i][0];
				tmp_list[2*i+1] = Constants.stage3_2twist_moves[i][1];
			}
			pcpt_edg3.init_move_list2 (2, Constants.N_STAGE3_2TWIST_MOVES, tmp_list);
			break;
		case 2:
			pcpt_edg3.init_move_list (2, Constants.N_STAGE3_BLOCK_MOVES, tmp_list);
			break;
		}
		pcpt_edg3.init_move_list (0, Constants.N_STAGE3_SLICE_MOVES, tmp_list);
		pcpt_edg3.init_solved_list (1, solved_table);
		pcpt_edg3.analyze ();	
	}
};


