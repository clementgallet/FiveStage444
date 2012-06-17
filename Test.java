package cg.fivestage444;

import java.util.Random;

public final class Test {

	public static void main(String[] args){
		Tools.init();
		testMove5Hack(2);
	}

	public static void randomScramble(CubeState cube, byte[] moves, int length){

		Random gen = new Random();
		cube.init();
		for( int i=0; i < 1000; i++ ){
			cube.do_move(moves[gen.nextInt(length)]);
		}		
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
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES );
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
	public static void testMove5Hack(int n){

		int move, move1;
		Random gen = new Random();
		CubeState cube = new CubeState();
		CubeStage5 cs1 = new CubeStage5();
		CubeStage5 cs2 = new CubeStage5();
		CubeStage5 cs3 = new CubeStage5();
		CubeStage5 cs4 = new CubeStage5();
		CubeStage5 cs5 = new CubeStage5();

		boolean error = false;
		for( int i=0; i < 10;){

			/* Generate a random cube in stage 5 subgroup and convert to CubeStage5 coordinates */
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES );
			cube.convert_to_stage5(cs1);
			cube.convert_to_stage5(cs4);
			cube.convert_to_stage5(cs5);

			if( cs1.sym != 1 || cs1.cosym != 1) continue;
			move = gen.nextInt(Constants.N_STAGE5_MOVES);
			move1 = move;
			int newEdge = Tables.move_table_symEdgeSTAGE5[cs5.edge][Symmetry.moveConjugate5[move1][Symmetry.symIdxMultiply[cs5.cosym*2][cs5.sym]]];
			int newSym = ( newEdge & 0xFF ) >> 2;
			int newCosym = newEdge & 0x03;
			if( newCosym != 1 ) continue;
			cube.do_move( Constants.stage5_slice_moves[move] );
			cs1.do_move(move);
			cs4.do_move(move);
			move = gen.nextInt(Constants.N_STAGE5_MOVES);
			cube.do_move( Constants.stage5_slice_moves[move] );
			cube.convert_to_stage5(cs2);
			cs4.do_move(move);
			//if(( cs4.edge == cs2.edge ) && (cs4.corner == cs2.corner) && (cs4.center == cs2.center))
			//	continue;
			i++;
			error = true;
			System.out.println("sym: "+cs5.sym+" - cosym: "+cs5.cosym+" - newCosym:"+newCosym+" - newSym:"+newSym);
			for( int k=0; k<4; k++){
				cs3.edge = cs1.edge;
				cs3.sym = cs1.sym;
				cs3.corner = cs1.corner;
				cs3.center = cs1.center;

				cs3.cosym = k;
				cs3.do_move(move);
				if(( cs3.edge == cs2.edge ) && (cs3.corner == cs2.corner) && (cs3.center == cs2.center)){
					System.out.println("Good cosym: "+k+" - Obtained cosym: "+cs1.cosym);
					error = false;
				}
			}
			if( error ){
				System.out.println("Could not find any good cosym !!!");

			}
		}
	}
}
