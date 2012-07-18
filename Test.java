package cg.fivestage444;

import static cg.fivestage444.Constants.*; 

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public final class Test {


	public static void main(String[] args){

		Random gen = new Random();
		Tools.init();

		testCenter35();

		//testSolve2(10);
		//testSolve3(30);
		//testSolve4(30);
		//testMove4(10);
		//testMove5(10);
		
	}

	public static void testCenter35(){

	Set[] centerSets = new Set[34];

	for (int i=0; i < 34; i++)
		for (int j=i+1; j<35; j++){

			/* Initialisation */
			centerSets[0] = new HashSet<Integer>();
			centerSets[0].add(i);
			centerSets[0].add(j);
			int l=1;
			for (int k=0; k<35; k++)
				if( k!=i && k!=j ){
					centerSets[l] = new HashSet<Integer>();
					centerSets[l++].add(k);
				}

			int size = 34;
			boolean did=true;
			while(( size > 1 ) && did){
				did = false;
				//printSets(centerSets, size);
				/* Loop through all sets */
				for (int s=0; s<size; s++){
					/* We pass if the current set is a singleton */
					if( centerSets[s].size() < 2 ) continue;

					Set curr = new HashSet<Integer>(centerSets[s]);
					Iterator<Integer> it = curr.iterator();
					int first_elem = it.next();
					while (it.hasNext()) {
						int other_elem = it.next();
						for (int m=0; m<16; m++){
							if( merge2Sets(centerSets, size, Tables.moveCenter4[first_elem][m], Tables.moveCenter4[other_elem][m])){
								size--;
								did = true;
								//printSets(centerSets, size);
							}
						}
					}
				}
			}
			if( size == 1 ) System.out.print("");
			else System.out.println("Yeah !");
		}
	}

	public static boolean merge2Sets (Set[] centerSets, int size, int a, int b){
		int idx_a = -1;
		int idx_b = -1;
		for( int i=0; i < size; i++ ){
			if( centerSets[i].contains(a))
				idx_a = i;
			if( centerSets[i].contains(b))
				idx_b = i;
		}
		if(idx_a == idx_b)
			return false;
		centerSets[Math.min(idx_a,idx_b)].addAll(centerSets[Math.max(idx_a,idx_b)]);
		centerSets[Math.max(idx_a,idx_b)] = centerSets[size-1];
		return true;

	}

	public static void printSets (Set[] centerSets, int size){

		for (int s=0; s<size; s++){
		/*
			Iterator it = centerSets[s].iterator();
			
			System.out.println("{"+it.next());
			while (it.hasNext()) {
				System.out.println(","+it.next());
			}
			System.out.println("} ");*/
			System.out.print(centerSets[s]);
			System.out.print(",");
		}
	System.out.println("");
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
			int center = cube.convert_symcenters_to_stage3();
			int sym = center & 0x7;
			center >>>= 3;
			int edge = cube.convert_edges_to_stage3();
			boolean edge_odd = cube.edgeUD_parity_odd();

			System.out.println("edge: "+edge+", center: "+center+", sym:"+sym);

		}
	}

	public static void testSolve4(int n){

		Random gen = new Random();
		CubeState cube = new CubeState();

		for( int i=0; i < n; i++ ){

			/* Generate a random cube in stage 5 subgroup and convert to CubeStage4 coordinates */
			randomScramble( cube, Constants.stage5_slice_moves, Constants.N_STAGE5_MOVES, gen );
			int edge = cube.convert_symedges_to_stage4();
			int sym = edge & 0xF;
			edge >>>= 4;
			int corner = cube.convert_corners_to_stage4();
			int center = cube.convert_centers_to_stage4();

			System.out.println("edge: "+edge+", center: "+center+", corner:"+corner);

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
