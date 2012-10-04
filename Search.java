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

final class Search {

	private final byte[] move_list_stage1 = new byte[50];
	private final byte[] move_list_stage2 = new byte[50];
	private final byte[] move_list_stage3 = new byte[50];
	private final byte[] move_list_stage4 = new byte[50];
	private final byte[] move_list_stage5 = new byte[50];
	private int length1;
	private int length2;
	private int length3;
	private int length4;
	private int length5;
	private int rotate;
	private int rotate2;
	private int rotate_sub;
	private int rotate2_sub;
	private int total_length;

	private final byte[] move_list_sub_stage1 = new byte[50];
	private final byte[] move_list_sub_stage2 = new byte[50];
	private final byte[] move_list_sub_stage3 = new byte[50];
	private final byte[] move_list_sub_stage4 = new byte[50];
	private final byte[] move_list_sub_stage5 = new byte[50];
	private int length1_sub;
	private int length2_sub;
	private int length3_sub;
	private int length4_sub;
	private int length5_sub;
	private boolean found_sol;

	private static final int MAX_STAGE2 = 6;
	private static final int MAX_STAGE3 = 10;
	private static final int MAX_STAGE4 = 14;

	private static final int MIN_STAGE3 = 8;
	private static final int MIN_STAGE4 = 7;
	private static final int MIN_STAGE5 = 8;

	private final CubeState[] init_cube = new CubeState[3];

	private final Stage1[] s1_list = new Stage1[20];
	private final Stage2[] s2_list = new Stage2[20];
	private final Stage3[] s3_list = new Stage3[20];
	private final Stage4[] s4_list = new Stage4[20];
	private final Stage5[] s5_list = new Stage5[20];

	private final CubePack[] cp1_list = new CubePack[20];
	private final CubePack[] cp2_list = new CubePack[20];
	private final CubePack[] cp3_list = new CubePack[20];
	private final CubePack[] cp4_list = new CubePack[20];

	private int min1_list;
	private int min2_list;
	private int min3_list;
	private int min4_list;

	private static final int DEBUG_LEVEL = 0;
	private static final boolean PRINT_LENGTH = true;

	public String solve (CubeState cube, int max_turns, boolean inverse) {
		int i, j;

		StringBuilder sb = new StringBuilder();
		CubeState c = new CubeState();
		cube.copyTo( c );

		for( j=0; j<3; j++){
			init_cube[j] = new CubeState();
			c.copyTo( init_cube[j] );
			c.leftMult ( 16 );
		}

		for (i=0; i<20; i++){
			s1_list[i] = new Stage1();
			s2_list[i] = new Stage2();
			s3_list[i] = new Stage3();
			s4_list[i] = new Stage4();
			s5_list[i] = new Stage5();
			cp1_list[i] = new CubePack();
			cp2_list[i] = new CubePack();
			cp3_list[i] = new CubePack();
			cp4_list[i] = new CubePack();
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
		if( PRINT_LENGTH )
			System.out.print(length1_sub + length2_sub + length3_sub + length4_sub + length5_sub + "\t");
		return sb.toString();
	}

	void init_stage1(int max_turns) {
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
			s1_list[0] = s1;
			cp1_list[0] = cp1;
			min1_list = 0;
			if ( search_stage1 (length1, 0, Moves.N_STAGE_MOVES ))
				return;
			s1_list[0] = s2;
			cp1_list[0] = cp2;
			min1_list = 0;
			if ( search_stage1 (length1, 0, Moves.N_STAGE_MOVES ))
				return;
			s1_list[0] = s3;
			cp1_list[0] = cp3;
			min1_list = 0;
			if ( search_stage1 (length1, 0, Moves.N_STAGE_MOVES ))
				return;
		}
	}

	boolean search_stage1(int depth, int moves_done, int last_move){
		if ( s1_list[moves_done].isSolved() ){
			return depth == 0 && init_stage2();
		}
		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage1.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1L ) == 0)
				continue;
			s1_list[moves_done].moveTo( move, s1_list[moves_done+1] );
			if (s1_list[moves_done+1].pruning() > depth-1) continue;
			move_list_stage1[moves_done] = (byte)move;
			min1_list = Math.min( min1_list, moves_done );
			if (search_stage1 (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	boolean init_stage2(){
		if( found_sol ) return true;

		for (;min1_list<length1; min1_list++)
			cp1_list[min1_list].moveTo( move_list_stage1[min1_list], cp1_list[min1_list+1] );
		CubePack cp = cp1_list[length1];
		rotate = cp.rotateStage2();

		int min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);

		Stage2 s = new Stage2();
		cp.toStage2( s );

		int d2 = s.pruning();
		if( d2 >= min2 ) return false;

		s2_list[0] = s;
		cp2_list[0] = cp;
		min2_list = 0;
		for (length2 = d2; length2 < min2; ++length2) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "  Stage 2 - length "+length2 );
			if( search_stage2 (length2, 0, Moves.N_STAGE_MOVES ))
				return true;
			min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);
		}
		return false;
	}

	boolean search_stage2(int depth, int moves_done, int last_move){
		if( s2_list[moves_done].isSolved() ){
			return depth == 0 && init_stage3();
		}

		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage2.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			/* Move cube1 to list2[depth] */
			s2_list[moves_done].moveTo( move, s2_list[moves_done+1] );
			if (s2_list[moves_done+1].pruning() > depth-1) continue;
			move_list_stage2[moves_done] = (byte)move;
			min2_list = Math.min( min2_list, moves_done );
			if (search_stage2 (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	boolean init_stage3(){
		if ( found_sol ) return true;

		for (;min2_list<length2; min2_list++)
			cp2_list[min2_list].moveTo( move_list_stage2[min2_list], cp2_list[min2_list+1] );
		CubePack cp = cp2_list[length2];
		rotate2 = rotate + cp.rotateStage3();

		Stage3 s = new Stage3();
		cp.toStage3( s );

		int min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		int d3 = s.pruning();

		s3_list[0] = s;
		cp3_list[0] = cp;
		min3_list = 0;
		for (length3 = d3; length3 < min3; ++length3) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "    Stage 3 - length "+length3 );
			if( search_stage3 (length3, 0, Moves.N_STAGE_MOVES )){
				return true;
			}
			min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		}
		return false;
	}

	boolean search_stage3(int depth, int moves_done, int last_move){
		if( s3_list[moves_done].isSolved() ){
			return depth == 0 && init_stage4();
		}

		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage3.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s3_list[moves_done].moveTo( move, s3_list[moves_done+1] );
			if (s3_list[moves_done+1].pruning() > depth-1) continue;
			move_list_stage3[moves_done] = (byte)move;
			min3_list = Math.min( min3_list, moves_done );
			if (search_stage3 (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	boolean init_stage4(){
		if ( found_sol ) return true;

		for (;min3_list<length3; min3_list++)
			cp3_list[min3_list].moveTo( move_list_stage3[min3_list], cp3_list[min3_list+1] );
		CubePack cp = cp3_list[length3];
		Stage4 s = new Stage4();
		cp.toStage4( s );

		int min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		int d4 = s.pruning();

		s4_list[0] = s;
		cp4_list[0] = cp;
		min4_list = 0;
		for (length4 = d4; length4 < min4; ++length4) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "      Stage 4 - length "+length4 );
			if( search_stage4 (length4, 0, Moves.N_STAGE_MOVES )) {
				return true;
			}
			min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		}
		return false;
	}

	boolean search_stage4(int depth, int moves_done, int last_move){
		if( s4_list[moves_done].isSolved() ){
			return depth == 0 && init_stage5();
		}

		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage4.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s4_list[moves_done].moveTo( move, s4_list[moves_done+1] );
			if (s4_list[moves_done+1].pruning() > depth-1) continue;
			move_list_stage4[moves_done] = (byte)move;
			min4_list = Math.min( min4_list, moves_done );
			if (search_stage4 (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}

	boolean init_stage5(){

		for (;min4_list<length4; min4_list++)
			cp4_list[min4_list].moveTo( move_list_stage4[min4_list], cp4_list[min4_list+1] );
		CubePack cp = cp4_list[length4];
		Stage5 s = new Stage5();
		cp.toStage5( s );

		int d5 = s.pruning();

		s5_list[0] = s;
		for (length5 = d5; length5 < total_length-length4-length3-length2-length1; ++length5) {
			if( search_stage5 (length5, 0, Moves.N_STAGE_MOVES)){
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

	boolean search_stage5(int depth, int moves_done, int last_move){
		if (depth == 0)
			return( s5_list[moves_done].isSolved() );

		long mask = Moves.moves_mask[last_move];
		for (int move = 0; mask != 0 && move < Stage5.N_MOVES; move++, mask >>>= 1) {
			if (( mask & 1 ) == 0)
				continue;

			s5_list[moves_done].moveTo( move, s5_list[moves_done+1] );
			if (s5_list[moves_done+1].pruning() > depth-1) continue;
			move_list_stage5[moves_done] = (byte)move;
			if (search_stage5 (depth - 1, moves_done + 1, move)) return true;
		}
		return false;
	}
}
