package cg.fivestage444;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public final class Test {


	public static void main(String[] args){

		//Tools.init();
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
		Random gen = new Random();
		CubeState cube1 = new CubeState();
		CubeState cube2 = new CubeState();
		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();

		randomScramble( cube1, Moves.stage2moves, 28, gen );
		cp1.packCorners( cube1 );
		int move = gen.nextInt(Moves.N_STAGE_MOVES);
		cube1.do_move(Moves.stage2moves[move]);
		cp1.moveCorners(move);
		cp2.packCorners( cube1 );
		System.out.println(cp1.corner_top_loc + " - " + cp1.corner_top_perm + " - " + cp1.corner_bottom_perm);
		System.out.println(cp2.corner_top_loc + " - " + cp2.corner_top_perm + " - " + cp2.corner_bottom_perm);
		//cube1.print();
		//cube2.print();
	}


}
