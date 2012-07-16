package cg.fivestage444;

import static cg.fivestage444.Constants.*; 

import java.util.Random;

public final class Test {


	public static void main(String[] args){

		Random gen = new Random();
		Tools.init();

		//testSolve2(10);
		//testSolve3(30);
		testSolve4(30);
		//testMove4(10);
		//testMove5(10);
		
	}

	public static void randomScramble(CubeState cube, Random gen){

		cube.init();
		for( int i=0; i < 100; i++ ){
			cube.do_move(gen.nextInt(N_MOVES));
		}		
	}

	public static void randomScramble(CubeState cube, byte[] moves, int length, Random gen){

		cube.init();
		for( int i=0; i < 100; i++ ){
			cube.do_move(moves[gen.nextInt(length)]);
		}		
	}

	public static void testSolve2(int n){

		Random gen = new Random();
		CubeState cube = new CubeState();

		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 3 subgroup and convert to CubeStage2 coordinates */
			randomScramble( cube, Constants.stage3_slice_moves, Constants.N_STAGE3_MOVES, gen );
			int rr = gen.nextInt(4);
			for( int j=0; j<rr; j++){
				cube.do_move(Uw);
				cube.do_move(Dw3);
			}
			//cube.convert_to_stage2(cs1);

			//System.out.println("edge: "+cs1.edge+", centerF: "+cs1.centerF+", centerB:"+cs1.centerB+", symF:"+cs1.symF+", symB:"+cs1.symB);

		}
	}

	public static void testSolve3(int n){

		Random gen = new Random();
		CubeState cube = new CubeState();

		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 4 subgroup and convert to CubeStage3 coordinates */
			randomScramble( cube, Constants.stage4_slice_moves, Constants.N_STAGE4_MOVES, gen );
			//cube.convert_to_stage3(cs1);

			//System.out.println("edge: "+cs1.edge+", center: "+cs1.center+", sym:"+cs1.sym);

		}
	}

	public static void testSolve4(int n){

		Random gen = new Random();
		CubeState cube = new CubeState();

		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 5 subgroup and convert to CubeStage4 coordinates */
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES, gen );
			//cube.convert_to_stage4(cs1);

			//System.out.println("edge: "+cs1.edge+", center: "+cs1.center+", corner:"+cs1.corner);

		}
	}

	public static void testMove4(int n){

		int move;
		Random gen = new Random();
		CubeState cube = new CubeState();

		boolean error = false;
		for( int i=0; i < 100; i++ ){

			/* Generate a random cube in stage 4 subgroup and convert to CubeStage5 coordinates */
			randomScramble( cube, Constants.stage4_slice_moves, Constants.N_STAGE4_MOVES, gen );
			//cube.convert_to_stage4(cs1);

			/* Apply n random moves from both the cube and the CubeStage5 coordinates */
			for( int j=0; j<n; j++ ){
				move = gen.nextInt(Constants.N_STAGE4_MOVES);
				cube.do_move( Constants.stage4_slice_moves[move] );
				//cs1.do_move(move);
			}

			/* Test if the two CubeStage4 are identical
			   Don't check for syms because they might be different due to symmetrical positions */
			//cube.convert_to_stage4(cs2);
			//if(( cs1.edge != cs2.edge ) || (cs1.corner != cs2.corner) || (cs1.center != cs2.center)){
				error = true;
				break;
			//}
		}

		if( error ){
			System.out.println("Found a difference between (move + convert4) and (convert4 + move)");
			//System.out.println("edge1:"+cs1.edge+"-corner1:"+cs1.corner+"-center1:"+cs1.center);
			//System.out.println("edge2:"+cs2.edge+"-corner2:"+cs2.corner+"-center2:"+cs2.center);
		}
		else {
			System.out.println("Found no difference between (move + convert4) and (convert4 + move)");
		}
	}

	public static void testMove5(int n){

		int move;
		Random gen = new Random();
		CubeState cube = new CubeState();

		boolean error = false;
		for( int i=0; i < 100; i++ ){

			/* Generate a random cube in stage 5 subgroup and convert to CubeStage5 coordinates */
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES, gen );
			//cube.convert_to_stage5(cs1);

			/* Apply n random moves from both the cube and the CubeStage5 coordinates */
			for( int j=0; j<n; j++ ){
				move = gen.nextInt(Constants.N_STAGE5_MOVES);
				cube.do_move( Constants.stage5_slice_moves[move] );
				//cs1.do_move(move);
			}

			/* Test if the two CubeStage5 are identical
			   Don't check for syms because they might be different due to symmetrical positions */
			//cube.convert_to_stage5(cs2);
			//if(( cs1.edge != cs2.edge ) || (cs1.corner != cs2.corner) || (cs1.center != cs2.center)){
				error = true;
				break;
			//}
		}

		if( error ){
			System.out.println("Found a difference between (move + convert5) and (convert5 + move)");
			//System.out.println("edge1:"+cs1.edge+"-corner1:"+cs1.corner+"-center1:"+cs1.center);
			//System.out.println("edge2:"+cs2.edge+"-corner2:"+cs2.corner+"-center2:"+cs2.center);
		}
		else {
			System.out.println("Found no difference between (move + convert5) and (convert5 + move)");
		}
	}
}
