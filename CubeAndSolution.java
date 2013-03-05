package cg.fivestage444;

import cg.fivestage444.Stages.*;

import java.util.Comparator;
import java.util.logging.Logger;

public class CubeAndSolution implements Cloneable, Comparable<CubeAndSolution> {
	private static final Logger l = Logger.getLogger(StageSolver.class.getName());

	CubeState cube; /* the cube structure */
	byte[] move_list = new byte[100]; /* the list of moves that has been applied to the cube */
	int move_length = 0;
	int[] stage_length = new int[5]; /* the length of the move list at each stage */
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
		System.arraycopy(move_list, 0, cas.move_list, 0, move_list.length);
		cas.stage_length = new int[stage_length.length];
		System.arraycopy(stage_length, 0, cas.stage_length, 0, stage_length.length);
		return cas;
	}

	public static class QueueComparator implements Comparator<CubeAndSolution> {
		public int compare(CubeAndSolution c1, CubeAndSolution c2) {
			return c2.comparator - c1.comparator;
		}
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
		if (current_stage == 1){
			switch (cube.corners.cubies[0] >> 3) {
				case 0:
					rotate12 = 0;
					break;	//no whole cube rotation
				case 1:
					rotate12 = 32;
					break;
				case 2:
					rotate12 = 16;
					break;
				default:
					l.severe("Invalid cube rotation state.");
			}
			cube.rightMult(rotate12);
		}
		if (current_stage == 2){
			if (cube.centers.cubies[16] < 4) {
				cube.rightMult(8);
				rotate23 = 8;
			}
		}
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
				if(!cube.isInSubgroup2()){
					l.severe("Convertion to Stage2 not allowed");
					l.info(cube.toString());
				}
				Stage2 stage2 = new Stage2();
				stage2.pack(cube);
				return stage2;
			case 3:
				if(!cube.isInSubgroup3()){
					l.severe("Convertion to Stage3 not allowed");
					l.info(cube.toString());
				}
				Stage3 stage3 = new Stage3();
				stage3.pack(cube);
				return stage3;
			case 4:
				if(!cube.isInSubgroup4()){
					l.severe("Convertion to Stage4 not allowed");
					l.info(cube.toString());
				}
				Stage4 stage4 = new Stage4();
				stage4.pack(cube);
				return stage4;
			case 5:
				if(!cube.isInSubgroup5()){
					l.severe("Convertion to Stage5 not allowed");
					l.info(cube.toString());
				}
				Stage5 stage5 = new Stage5();
				stage5.pack(cube);
				return stage5;
		}
		return null;
	}

	public Stage toNextStage(){
		stage_length[current_stage-1] = move_length;
		current_stage++;
		return toCurrentStage();
	}

	public boolean isSolved(){
		return cube.isSolvedAndOrientation() >= 0;
	}

	public void processSolution(){
		/** Because of the cube rotations between some stages, we need to modify the rotations
		 * to take into accounts these cube rotations.
		 */
		for (int l=stage_length[0]; l<stage_length[1]; l++)
			move_list[l] = Symmetry.moveConjugate[move_list[l]][rotate12];
		for (int l=stage_length[1]; l<move_length; l++)
			move_list[l] = Symmetry.moveConjugate[move_list[l]][rotate12+rotate23];
	}

	public String outputSolution(){
		processSolution();
		return Moves.print_move_list(move_length, move_list, false);
	}

	public String outputGenerator(CubeState initialCube){
		processSolution();

		/** As noted by Bruce, the solved cube has a arbitrary orientation.
		 *  However, if we want to output a generator,
		 *  it will be applied to a solved state with a fixed orientation
		 *  (e.g. white on top/green on front according to WCA regulations 2013)
		 *  Because of that, we need to transform all moves according to the orientation of the solved state.
		 */
		initialCube.scramble(move_length, move_list);
		int finalOrientation = initialCube.isSolvedAndOrientation();
		if( finalOrientation == -1 ){ // Not solved
			l.severe("The restructured solution is not working!");
			return "";
		}
		for (int l=0; l<move_length; l++)
			move_list[l] = Symmetry.moveConjugate[move_list[l]][finalOrientation];

		return Moves.print_move_list(move_length, move_list, true);
	}

	public String debugOutputMoves(){
		return Moves.print_move_list(move_length, move_list, false);
	}
}
