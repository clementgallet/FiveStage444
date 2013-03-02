package cg.fivestage444;

import cg.fivestage444.Stages.*;

public class CubeAndSolution implements Cloneable, Comparable<CubeAndSolution> {

	CubeState cube; /* the cube structure */
	byte[] move_list = new byte[100]; /* the list of moves that has been applied to the cube */
	int move_length = 0; /* the length of the current move list */
	int comparator = 0; /* the value used to sort objects */
	int current_stage = 1; /* at which stage are we. TODO: use a better struct than an int */
	int rotate12 = 0; /* which rotation was applied between stage 1 and 2. TODO: same */
	int rotate23 = 0; /* which rotation was applied between stage 2 and 3. TODO: same */

	public CubeAndSolution(CubeState cube){
		this.cube = cube;
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		CubeAndSolution cas = (CubeAndSolution) super.clone();
		cas.cube = (CubeState)cube.clone();
		cas.move_list = new byte[move_list.length];
		System.arraycopy(move_list, 0, cas.move_list, 0, move_length);
		return cas;
	}

	@Override
	public int compareTo(CubeAndSolution o) {
		return this.comparator - o.comparator;
	}

	/**
	 * Apply a move on the cube structure, and update the move list and the move length.
	 * @param move the move to apply, in the move domain (0..N_MOVES)
	 */
	public void move(int move){
		cube.move(move);
		move_list[move_length] = (byte)move;
		move_length++;
	}

	/**
	 * At the end of stages 1 and 2, we need to rotate the cube to be able to convert to the next stage.
	 * We also keep track of the rotations to be able to print the correct move sequence at the end.
	 */
	public void rotate(){

	}

	/**
	 * Convert the current cube state to one of the stage structure, according to current_stage.
	 * TODO: Check that the conversion is possible and raise an exception otherwise.
	 * @return the stage structure
	 */
	public Stage toCurrentStage(){
		switch (current_stage){
			case 1:
				Stage1 stage1 = new Stage1();
				stage1.pack(cube);
				return stage1;
			case 2:
				Stage2 stage2 = new Stage2();
				stage2.pack(cube);
				return stage2;
			case 3:
				Stage3 stage3 = new Stage3();
				stage3.pack(cube);
				return stage3;
			case 4:
				Stage4 stage4 = new Stage4();
				stage4.pack(cube);
				return stage4;
			case 5:
				Stage5 stage5 = new Stage5();
				stage5.pack(cube);
				return stage5;
		}
		return null;
	}

	public Stage toNextStage(){
		current_stage++;
		return toCurrentStage();
	}

}
