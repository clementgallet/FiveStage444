package cg.fivestage444;

//import static cg.fivestage444.Constants.*;
import cg.fivestage444.Stages.Stage1;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

import java.util.Random;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.File;

public final class Search {

	byte[] move_list_stage1 = new byte[50];
	byte[] move_list_stage2 = new byte[50];
	byte[] move_list_stage3 = new byte[50];
	byte[] move_list_stage4 = new byte[50];
	byte[] move_list_stage5 = new byte[50];
	int length1, length2, length3, length4, length5;
	int rotate, rotate2;
	int rotate_sub, rotate2_sub;
	int total_length;

	byte[] move_list_sub_stage1 = new byte[50];
	byte[] move_list_sub_stage2 = new byte[50];
	byte[] move_list_sub_stage3 = new byte[50];
	byte[] move_list_sub_stage4 = new byte[50];
	byte[] move_list_sub_stage5 = new byte[50];
	int length1_sub, length2_sub, length3_sub, length4_sub, length5_sub;
	boolean found_sol;

	static int MAX_STAGE2 = 6;
	static int MAX_STAGE3 = 9;
	static int MAX_STAGE4 = 11;

	static int MIN_STAGE3 = 7;
	static int MIN_STAGE4 = 8;
	static int MIN_STAGE5 = 8;

	CubeState[] init_cube = new CubeState[3];

	static int DEBUG_LEVEL = 0;
	static int PRINT_LENGTH = 0;

	public String solve (CubeState cube, int max_turns, boolean inverse) {
		int i, j;

		Tools.init();
		StringBuffer sb = new StringBuffer();
		String sol = "";
		CubeState c = new CubeState();
		cube.copyTo( c );
		
		for( j=0; j<3; j++){
			init_cube[j] = new CubeState();
			c.copyTo( init_cube[j] );
			c.leftMult ( 16 );
		}

		init_stage1 ( max_turns );

		/* Transform rotations before outputing the solution */
		for (i = 0; i < length1_sub; ++i)
			move_list_sub_stage1[i] = Moves.stage2moves[move_list_sub_stage1[i]];
		for (i = 0; i < length2_sub; ++i)
			move_list_sub_stage2[i] = Symmetry.moveConjugate[Moves.stage2moves[move_list_sub_stage2[i]]][rotate_sub];
		for (i = 0; i < length3_sub; ++i)
			move_list_sub_stage3[i] = Symmetry.moveConjugate[Moves.stage2moves[move_list_sub_stage3[i]]][rotate2_sub];
		for (i = 0; i < length4_sub; ++i)
			move_list_sub_stage4[i] = Symmetry.moveConjugate[Moves.stage2moves[move_list_sub_stage4[i]]][rotate2_sub];
		for (i = 0; i < length5_sub; ++i)
			move_list_sub_stage5[i] = Symmetry.moveConjugate[Moves.stage2moves[move_list_sub_stage5[i]]][rotate2_sub];

		if( inverse ){

			cube.scramble(length1_sub, move_list_sub_stage1);
			cube.scramble(length2_sub, move_list_sub_stage2);
			cube.scramble(length3_sub, move_list_sub_stage3);
			cube.scramble(length4_sub, move_list_sub_stage4);
			cube.scramble(length5_sub, move_list_sub_stage5);

			int final_ori = cube.is_solved();
			if( final_ori == -1 ){
				System.out.println("Not a solution !!!!!!");
				return "";
			}
			for (i = 0; i < length1_sub; ++i)
				move_list_sub_stage1[i] = Symmetry.moveConjugate[move_list_sub_stage1[i]][final_ori];
			for (i = 0; i < length2_sub; ++i)
				move_list_sub_stage2[i] = Symmetry.moveConjugate[move_list_sub_stage2[i]][final_ori];
			for (i = 0; i < length3_sub; ++i)
				move_list_sub_stage3[i] = Symmetry.moveConjugate[move_list_sub_stage3[i]][final_ori];
			for (i = 0; i < length4_sub; ++i)
				move_list_sub_stage4[i] = Symmetry.moveConjugate[move_list_sub_stage4[i]][final_ori];
			for (i = 0; i < length5_sub; ++i)
				move_list_sub_stage5[i] = Symmetry.moveConjugate[move_list_sub_stage5[i]][final_ori];

			sb.append(Moves.print_move_list (length5_sub, move_list_sub_stage5, true));
			sb.append(Moves.print_move_list (length4_sub, move_list_sub_stage4, true));
			sb.append(Moves.print_move_list (length3_sub, move_list_sub_stage3, true));
			sb.append(Moves.print_move_list (length2_sub, move_list_sub_stage2, true));
			sb.append(Moves.print_move_list (length1_sub, move_list_sub_stage1, true));
		}
		else{
			sb.append(Moves.print_move_list (length1_sub, move_list_sub_stage1, false));
			sb.append(Moves.print_move_list (length2_sub, move_list_sub_stage2, false));
			sb.append(Moves.print_move_list (length3_sub, move_list_sub_stage3, false));
			sb.append(Moves.print_move_list (length4_sub, move_list_sub_stage4, false));
			sb.append(Moves.print_move_list (length5_sub, move_list_sub_stage5, false));
		}
		System.out.print(length1_sub + length2_sub + length3_sub + length4_sub + length5_sub + "\t");
		return sb.toString();
	}

	public void init_stage1 ( int max_turns ) {
		Stage1 s1 = new Stage1();
		Stage1 s2 = new Stage1();
		Stage1 s3 = new Stage1();

		s1.pack(init_cube[0]);
		s2.pack(init_cube[1]);
		s3.pack(init_cube[2]);

		CubePack cp1 = new CubePack();
		CubePack cp2 = new CubePack();
		CubePack cp3 = new CubePack();

		cp1.pack(init_cube[0]);
		cp2.pack(init_cube[1]);
		cp3.pack(init_cube[2]);

		int d1 = s1.pruning();
		int d2 = s2.pruning();
		int d3 = s3.pruning();
		int d = Math.min(Math.min(d1, d2), d3);

		total_length = max_turns+1;
		found_sol = false;
		for (length1 = d; length1 < total_length; ++length1) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "Stage 1 - length "+length1 );
			if ( search_stage1 (s1, cp1, length1, 0, Moves.N_STAGE_MOVES )
			  || search_stage1 (s2, cp2, length1, 0, Moves.N_STAGE_MOVES )
			  || search_stage1 (s3, cp3, length1, 0, Moves.N_STAGE_MOVES ))
				return;
		}
	}

	public boolean search_stage1 (Stage1 s, CubePack cp, int depth, int moves_done, int last_move){
		if ( s.isSolved() ){
			if (depth == 0)
				return init_stage2 (cp);
			else
				return false;
		}
		Stage1 t = new Stage1();
		CubePack cp2 = new CubePack();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage1.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1L ) == 0)
				continue;
			s.moveTo( move, t );
			if (t.pruning() > depth-1) continue;
			cp.moveTo( move, cp2 );
			move_list_stage1[moves_done] = (byte)move;
			if (search_stage1 (t, cp2, depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	public boolean init_stage2 (CubePack cp){
		if( found_sol ) return true;

		rotate = cp.rotateStage2();

		int min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);

		Stage2 s1 = new Stage2();
		cp.toStage2( s1 );

		int d2 = s1.pruning();
		if( d2 >= min2 ) return false;

		for (length2 = d2; length2 < min2; ++length2) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "  Stage 2 - length "+length2 );
			if( search_stage2 (s1, cp, length2, 0, Moves.N_STAGE_MOVES ))
				return true;
			min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);
		}
		return false;
	}

	public boolean search_stage2 (Stage2 s, CubePack cp, int depth, int moves_done, int last_move ){
		if (depth == 0){
			if( s.isSolved() )
				return init_stage3 (cp);
			return false;
		}

		Stage2 t = new Stage2();
		CubePack cp2 = new CubePack();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage2.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			/* Move cube1 to list2[depth] */
			s.moveTo( move, t );
			if (t.pruning() > depth-1) continue;
			cp.moveTo( move, cp2 );
			move_list_stage2[moves_done] = (byte)move;
			if (search_stage2 (t, cp2, depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	public boolean init_stage3 (CubePack cp){
		if ( found_sol ) return true;

		rotate2 = rotate + cp.rotateStage3();

		Stage3 s = new Stage3();
		cp.toStage3( s );

		int min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		int d3 = s.pruning();

		for (length3 = d3; length3 < min3; ++length3) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "    Stage 3 - length "+length3 );
			if( search_stage3 (s, cp, length3, 0, Moves.N_STAGE_MOVES )){
				return true;
			}
			min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage3 (Stage3 s, CubePack cp, int depth, int moves_done, int last_move){
		if( s.isSolved() ){
			if (depth == 0)
				return init_stage4 (cp);
			else
				return false;
		}

		Stage3 t = new Stage3();
		CubePack cp2 = new CubePack();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage3.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			if (t.pruning() > depth-1) continue;
			cp.moveTo( move, cp2 );
			move_list_stage3[moves_done] = (byte)move;
			if (search_stage3 (t, cp2, depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	public boolean init_stage4 (CubePack cp){
		if ( found_sol ) return true;

		Stage4 s = new Stage4();
		cp.toStage4( s );

		int min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		int d4 = s.pruning();

		for (length4 = d4; length4 < min4; ++length4) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "      Stage 4 - length "+length4 );
			if( search_stage4 (s, cp, length4, 0, Moves.N_STAGE_MOVES )) {
				return true;
			}
			min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage4 (Stage4 s, CubePack cp, int depth, int moves_done, int last_move){
		if( s.isSolved() ){
			if (depth == 0)
				return init_stage5 (cp);
			else
				return false;
		}

		Stage4 t = new Stage4();
		CubePack cp2 = new CubePack();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage4.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			if (t.pruning() > depth-1) continue;
			cp.moveTo( move, cp2 );
			move_list_stage4[moves_done] = (byte)move;
			if (search_stage4 (t, cp2, depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	public boolean init_stage5 (CubePack cp){
		int i;

		Stage5 s = new Stage5();
		cp.toStage5( s );

		int d5 = s.pruning();

		for (length5 = d5; length5 < total_length-length4-length3-length2-length1; ++length5) {
			if( search_stage5 (s, length5, 0, Moves.N_STAGE_MOVES)){
				if( DEBUG_LEVEL >= 1 ) System.out.println( "        Stage 5 - length "+length5 );
				total_length = length1+length2+length3+length4+length5;
				/* Save current solution */
				found_sol = true;
				System.arraycopy(move_list_stage1, 0, move_list_sub_stage1, 0, length1);
				System.arraycopy(move_list_stage2, 0, move_list_sub_stage2, 0, length2);
				System.arraycopy(move_list_stage3, 0, move_list_sub_stage3, 0, length3);
				System.arraycopy(move_list_stage4, 0, move_list_sub_stage4, 0, length4);
				System.arraycopy(move_list_stage5, 0, move_list_sub_stage5, 0, length5);
				length1_sub = length1;
				length2_sub = length2;
				length3_sub = length3;
				length4_sub = length4;
				length5_sub = length5;
				rotate_sub = rotate;
				rotate2_sub = rotate2;
				return true;
			}
		}
		return false;
	}

	public boolean search_stage5 (Stage5 s, int depth, int moves_done, int last_move){
		if (depth == 0)
			return( s.isSolved() );

		Stage5 t = new Stage5();
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage5.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s.moveTo( move, t );
			if (t.pruning() > depth-1) continue;
			move_list_stage5[moves_done] = (byte)move;
			if (search_stage5 (t, depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}
}
