package cg.fivestage444;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public final class Test {


	public static void main(String[] args){

		//Tools.init();
		Symmetry.init();
		Util.init();
		CubePack.init();

		testPack();

	}

	public static void randomScramble(CubeState cube, Random gen){

		cube.init();
		for( int i=0; i < 100; i++ ){
			cube.do_move(gen.nextInt(Moves.N_MOVES));
		}		
	}

	public static void randomScramble(CubeState cube, byte[] moves, int length, Random gen){

		cube.init();
		for( int i=0; i < 100; i++ ){
			cube.do_move(moves[gen.nextInt(length)]);
		}		
	}

	public static void testPack(){
		Random gen = new Random(42);
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubePack cp3 = new CubePack();


		/* Do one move on the cube and on the cubepack, and compare the two */
		randomScramble( cube1, Moves.stage2moves, 28, gen );
		cp1.pack( cube1 );

		int move = gen.nextInt(Moves.N_STAGE_MOVES);
		cube1.do_move(Moves.stage2moves[move]);
		cp1.moveTo(move, cp2);

		cp3.pack( cube1 );
		cp2.print();
		cp3.print();
	}


}
