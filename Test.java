package cg.fivestage444;

import static cg.fivestage444.Constants.*; 

import java.util.Random;

public final class Test {

	public static void main(String[] args){
		Tools.init();
		Random gen = new Random(44);
		//testMove5(10);
		System.out.println("Search 1 takes "+( timeSearch1(1000, gen) / 1000.0 )+" ms" );
		System.out.println("Search 2 takes "+( timeSearch2(1000, gen) / 1000.0 )+" ms" );
		System.out.println("Search 3 takes "+( timeSearch3(1000, gen) / 1000.0 )+" ms" );
		System.out.println("Search 4 takes "+( timeSearch4(1000, gen) / 1000.0 )+" ms" );
		System.out.println("Search 5 takes "+( timeSearch5(1000, gen) / 1000.0 )+" ms" );
	}

	public static void randomScramble(CubeState cube, byte[] moves, int length, Random gen){

		cube.init();
		for( int i=0; i < 100; i++ ){
			cube.do_move(moves[gen.nextInt(length)], STM);
		}		
	}

	public static long timeSearch1(int n, Random gen){

		Search s = new Search();
		CubeStage1 s1 = new CubeStage1();
		/* Set up s for stopping after finding a solution */
		s.endtime = System.currentTimeMillis() - 1;
		s.found1 = true;
		for( int i=0; i<20; i++ )
			s.list1[i] = new CubeStage1();

		long time = 0;
		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 1 subgroup */
			s1.corner = gen.nextInt(Constants.N_CORNER_ORIENT);
			s1.edge = gen.nextInt(Constants.N_SYMEDGE_COMBO8);
			s1.sym = gen.nextInt(Constants.N_SYM_STAGE1);
			int d = s1.getDistance();

			long itime = System.currentTimeMillis();
			for (int length1 = d; length1 < 100; ++length1)
				if( s.search_stage1 (s1, length1, 0, Constants.N_BASIC_MOVES, d, 0 ))
					break;
			time += System.currentTimeMillis() - itime;
		}

		return time;
	}

	public static long timeSearch2(int n, Random gen){

		Search s = new Search();
		CubeState cube = new CubeState();
		CubeStage2 s1 = new CubeStage2();
		/* Set up s for stopping after finding a solution */
		s.endtime = System.currentTimeMillis() - 1;
		s.found1 = true;
		s.found2 = true;
		for( int i=0; i<20; i++ )
			s.list2[i] = new CubeStage2();

		long time = 0;
		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 2 subgroup */
			randomScramble(cube, stage2_slice_moves, N_STAGE2_SLICE_MOVES, gen);
			cube.convert_to_stage2( s1 );

			int cubeDistCenF1 = s1.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s1.centerF + Tables.move_table_edge_conjSTAGE2[s1.edge][s1.symF]];
			int cubeDistCenB1 = s1.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s1.centerB + Tables.move_table_edge_conjSTAGE2[s1.edge][s1.symB]];
			int d2 = Math.max(cubeDistCenF1, cubeDistCenB1);

			long itime = System.currentTimeMillis();
			for (int length2 = d2; length2 < 100; ++length2){
				if( s.search_stage2 (s1, length2, 0, N_STAGE2_SLICE_MOVES, 0 ))
					break;
			}
			time += System.currentTimeMillis() - itime;
		}

		return time;
	}
	public static long timeSearch3(int n, Random gen){

		Search s = new Search();
		CubeState cube = new CubeState();
		CubeStage3 s1 = new CubeStage3();
		/* Set up s for stopping after finding a solution */
		s.solver_mode = s.SUB_123;

		long time = 0;
		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 3 subgroup */
			randomScramble(cube, stage3_slice_moves, N_STAGE3_SLICE_MOVES, gen);
			cube.convert_to_stage3( s1 );

			int cubeDistCen = s1.prune_table_cen.ptable[s1.center];
			int cubeDistEdg = s1.prune_table_edg.ptable[( s1.edge<<1 ) + (s1.edge_odd?1:0)];
			int d3 = Math.max(cubeDistCen, cubeDistEdg);

			long itime = System.currentTimeMillis();
			for (int length3 = d3; length3 < 100; ++length3){
				if( s.search_stage3 (s1, length3, 0, N_STAGE3_SLICE_MOVES ))
					break;
			}
			time += System.currentTimeMillis() - itime;
		}

		return time;
	}
	public static long timeSearch4(int n, Random gen){

		Search s = new Search();
		CubeState cube = new CubeState();
		CubeStage4 s1 = new CubeStage4();
		/* Set up s for stopping after finding a solution */
		s.solver_mode = s.SUB_234;

		long time = 0;
		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 4 subgroup */
			randomScramble(cube, stage4_slice_moves, N_STAGE4_SLICE_MOVES, gen);
			cube.convert_to_stage4( s1 );

			int d4 = s1.getDistance();

			long itime = System.currentTimeMillis();
			for (int length4 = d4; length4 < 100; ++length4){
				if( s.search_stage4 (s1, length4, 0, N_STAGE4_SLICE_MOVES, d4 ))
					break;
			}
			time += System.currentTimeMillis() - itime;
		}

		return time;
	}
	public static long timeSearch5(int n, Random gen){

		Search s = new Search();
		CubeState cube = new CubeState();
		CubeStage5 s1 = new CubeStage5();

		long time = 0;
		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 5 subgroup */
			randomScramble(cube, stage5_slice_moves, N_STAGE5_MOVES, gen);
			cube.convert_to_stage5( s1 );

			int cubeDistEdgCor = s1.prune_table_edgcor.ptable[s1.edge * N_STAGE5_CORNER_PERM + Tables.move_table_corner_conjSTAGE5[s1.corner][(s1.sym<<2)+s1.cosym]];
			int cubeDistEdgCen = s1.getDistanceEdgCen();
			int d5 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

			long itime = System.currentTimeMillis();
			for (int length5 = d5; length5 < 100; ++length5){
				if( s.search_stage5 (s1, length5, 0, N_STAGE5_MOVES, cubeDistEdgCen ))
					break;
			}
			time += System.currentTimeMillis() - itime;
		}

		return time;
	}

	public static void testMove5(int n){

		int move;
		Random gen = new Random();
		CubeState cube = new CubeState();
		CubeStage5 cs1 = new CubeStage5();
		CubeStage5 cs2 = new CubeStage5();

		boolean error = false;
		for( int i=0; i < 100; i++ ){

			/* Generate a random cube in stage 5 subgroup and convert to CubeStage5 coordinates */
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES, gen );
			cube.convert_to_stage5(cs1);

			/* Apply n random moves from both the cube and the CubeStage5 coordinates */
			for( int j=0; j<n; j++ ){
				move = gen.nextInt(Constants.N_STAGE5_MOVES);
				cube.do_move( Constants.stage5_slice_moves[move] );
				cs1.do_move(move);
			}

			/* Test if the two CubeStage5 are identical
			   Don't check for syms because they might be different due to symmetrical positions */
			cube.convert_to_stage5(cs2);
			if(( cs1.edge != cs2.edge ) || (cs1.corner != cs2.corner) || (cs1.center != cs2.center)){
				error = true;
				break;
			}
		}

		if( error ){
			System.out.println("Found a difference between (move + convert5) and (convert5 + move)");
			System.out.println("edge1:"+cs1.edge+"-corner1:"+cs1.corner+"-center1:"+cs1.center);
			System.out.println("edge2:"+cs2.edge+"-corner2:"+cs2.corner+"-center2:"+cs2.center);
		}
		else {
			System.out.println("Found no difference between (move + convert5) and (convert5 + move)");
		}
	}
}
