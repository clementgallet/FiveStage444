package cg.fivestage444;

import cg.fivestage444.Stages.Stage;

public class CubeAndSolution implements Cloneable {

	CubeState cube;
	byte[] move_list = new byte[100];
	int move_length = 0;
	int current_stage = 1;

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

	public void move(int move){
		cube.move(move);
		move_list[move_length] = (byte)move;
		move_length++;
	}

	public Stage toNextStage(){

	}
}
