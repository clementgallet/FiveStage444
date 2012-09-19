package cg.fivestage444;

import cg.fivestage444.Cubies.EdgeCubies;
import cg.fivestage444.Cubies.CornerCubies;
import cg.fivestage444.Cubies.CenterCubies;
import java.util.Arrays;
import java.util.Random;

//CubeState structure: a cubie-level representation of the cube.
public final class CubeState{

	public EdgeCubies edges = new EdgeCubies();
	public CornerCubies corners = new CornerCubies();
	public CenterCubies centers = new CenterCubies();

	public void init (){
		edges.init();
		corners.init();
		centers.init();
	}

	public int is_solved (){
		CubeState cube = new CubeState();
		for (int sym=0; sym<Symmetry.N_SYM; sym++){
			copyTo( cube );
			cube.leftMult( sym );
			if( cube.edges.is_solved() && cube.corners.is_solved() && cube.centers.is_solved() )
				return sym;
		}
		return -1;
	}

	/**
	 * Generates a random cube.
	 * @return A random cube in the string representation. Each cube of the cube space has the same probability.
	 */
	public void randomise() {
		Random r = new Random();
		randomise(r);
	}
	
	public void randomise(Random r) {
		init ();

		/* Randomise corners */
		randomPerm(r, corners.cubies, 8);
		int os = 0;
		for (int i=0; i<7; i++){
			int o = r.nextInt(3);
			corners.cubies[i] += 8*o;
			os += o;
		}
		corners.cubies[7] += 8*((15 - os) % 3);

		/* Randomize centers */
		randomPerm(r, centers.cubies, 24);

		/* Randomize edges */
		randomPerm(r, edges.cubies, 24);
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

	public final void move (int move_code){
		edges.move (move_code);
		corners.move (move_code);
		centers.move (move_code);
	}

	public void scramble (int move_count, byte[] move_arr){
		int i;
		for (i = 0; i < move_count; ++i) {
			move (move_arr[i]);
		}
	}

	public void scramble (int move_count, byte[] move_arr, byte[] move_trans){
		int i;
		for (i = 0; i < move_count; ++i) {
			move (move_trans[move_arr[i]]);
		}
	}

	public void copyTo (CubeState cube){
		edges.copyTo(cube.edges);
		corners.copyTo(cube.corners);
		centers.copyTo(cube.centers);
	}

	public void leftMult (int symIdx){
		edges.leftMult (symIdx);
		corners.leftMult (symIdx);
		centers.leftMult (symIdx);
	}

	/*
	public void rightMult (int symIdx){
		CubeState c = new CubeState();
		copyTo(c);
		c.rightMultEdges (symIdx, this);
		c.rightMultCenters (symIdx, this);
		c.rightMultCorners (symIdx, this);
	}*/

	public void print (){
		edges.print();
		corners.print();
		centers.print();
	}
}

