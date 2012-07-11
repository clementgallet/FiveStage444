package cg.fivestage444;

import static cg.fivestage444.Constants.*;

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
	int r1, r2;
	int r1_sub, r2_sub;

	static int MAX_STAGE2 = (METRIC == STM) ? 6 : 7;
	static int MAX_STAGE3 = (METRIC == STM) ? 9 : 10;
	static int MAX_STAGE4 = (METRIC == STM) ? 11 : 12;

	static int MIN_STAGE3 = 7;
	static int MIN_STAGE4 = 8;
	static int MIN_STAGE5 = 8;

	CubeState[] init_cube = new CubeState[3];
	CubeState c1 = new CubeState();
	CubeState c1r = new CubeState();
	CubeState c2 = new CubeState();
	CubeState c3 = new CubeState();
	CubeState c4 = new CubeState();

	CubeStage1[] list1 = new CubeStage1[20];
	// TODO: Use it for all stages or don't use it.

	static int DEBUG_LEVEL = 0;

	public String solve (CubeState cube, boolean inverse) {
		int i, j;

		Tools.init();
		for( i=0; i<20; i++ ){
			list1[i] = new CubeStage1();
		}

		StringBuffer sb = new StringBuffer();
		String sol = "";
		CubeState c = new CubeState();
		cube.copyTo( c );
		
		for( j=0; j<3; j++){
			init_cube[j] = new CubeState();
			c.copyTo( init_cube[j] );
			c.leftMult ( 16 );
		}

		init_stage1 ();

		/* Transform rotations before outputing the solution */
		for (i = 0; i < length1_sub; ++i)
			move_list_sub_stage1[i] = stage1_slice_moves[move_list_sub_stage1[i]];
		for (i = 0; i < length2_sub; ++i)
			move_list_sub_stage2[i] = Symmetry.moveConjugate[stage2_slice_moves[move_list_sub_stage2[i]]][rotate_sub];
		for (i = 0; i < length3_sub; ++i)
			move_list_sub_stage3[i] = Symmetry.moveConjugate[stage3_slice_moves[move_list_sub_stage3[i]]][rotate2_sub];
		for (i = 0; i < length4_sub; ++i)
			move_list_sub_stage4[i] = Symmetry.moveConjugate[stage4_slice_moves[move_list_sub_stage4[i]]][rotate2_sub];
		for (i = 0; i < length5_sub; ++i)
			move_list_sub_stage5[i] = Symmetry.moveConjugate[stage5_slice_moves[move_list_sub_stage5[i]]][rotate2_sub];

		if( inverse ){
			sb.append(print_move_list (length5_sub, move_list_sub_stage5, true));
			//sb.append("* ");
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, true));
			//sb.append("* ");
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, true));
			//sb.append("* ");
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, true));
			//sb.append("* ");
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, true));
		}
		else{
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, false));
			//sb.append("* ");
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, false));
			//sb.append("* ");
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, false));
			//sb.append("* ");
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, false));
			//sb.append("* ");
			sb.append(print_move_list (length5_sub, move_list_sub_stage5, false));
		}
		//System.out.print(length1_sub + length2_sub + length3_sub + length4_sub + length5_sub);
		//System.out.print("\t");
		/* Check the solution */
		if( ! Tools.checkSolution(cube, sb.toString()))
			System.out.println("Not a solution !!!!!!");
		return sb.toString();
	}

	public void init_stage1 () {
		CubeStage1 s1 = new CubeStage1();
		CubeStage1 s2 = new CubeStage1();
		CubeStage1 s3 = new CubeStage1();

		init_cube[0].convert_to_stage1 (s1);
		init_cube[1].convert_to_stage1 (s2);
		init_cube[2].convert_to_stage1 (s3);

		int d1 = s1.getDistance();
		int d2 = s2.getDistance();
		int d3 = s3.getDistance();
		int d = Math.min(Math.min(d1, d2), d3);

		total_length = 48;
		found_sol = false;
		for (length1 = d; length1 < total_length; ++length1) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "Stage 1 - length "+length1 );
			if ( search_stage1 (s1, length1, 0, N_STAGE1_MOVES, d1, 0 )
			  || search_stage1 (s2, length1, 0, N_STAGE1_MOVES, d2, 1 )
			  || search_stage1 (s3, length1, 0, N_STAGE1_MOVES, d3, 2 ))
				return;
		}
	}

	public boolean search_stage1 (CubeStage1 cube1, int depth, int moves_done, int last_move, int dist, int r){
		int mov_idx, j;
		if (cube1.is_solved ()) {
			if (depth == 0)
				return init_stage2 (r);
			else
				return false;
		}
		int end = ( depth == 1 ) ? N_STAGE1_LAST : N_STAGE1_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage1_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to list1[depth] */
			if (( stage1_slice_moves[mov_idx]/3)%3 != 1 )
				list1[depth].corner = Tables.moveCorner1[cube1.corner][basic_to_face[mov_idx]];
			else
				list1[depth].corner = cube1.corner;
			int newEdge = Tables.moveEdge1[cube1.edge][Symmetry.moveConjugate1[mov_idx][cube1.sym]];
			list1[depth].sym = Symmetry.symIdxMultiply[newEdge & 0x3F][cube1.sym];
			list1[depth].edge = newEdge >> 6 ;

			/* Compute new distance */
			int newDist = Tables.new_dist(CubeStage1.prune_table.ptable_packed, N_STAGE1_CORNERS * list1[depth].edge + Tables.conjCorner1[list1[depth].corner][list1[depth].sym], dist);
			if (newDist > depth-1) continue;
			move_list_stage1[moves_done] = (byte)mov_idx;
			if (search_stage1 (list1[depth], depth - 1, moves_done + 1, mov_idx, newDist, r)) return true;
		}
		return false;
	}

	public boolean init_stage2 (int r){
		int i;
		if( found_sol ) return true;
		r1 = r;
		init_cube[r1].copyTo(c1);

		c1.scramble( length1, move_list_stage1, stage1_slice_moves );

		rotate = c1.m_cor[0] >> 3;
		switch (rotate) {
		case 0:
			break;	//no whole cube rotation
		case 1:
			c1.rightMult ( 32 );
			rotate = 32;
			break;
		case 2:
			c1.rightMult ( 16 );
			rotate = 16;
			break;
		default:
			System.out.println ("Invalid cube rotation state.");
		}

		int min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);

		CubeStage2 s1 = new CubeStage2();
		CubeStage2 s2 = new CubeStage2();

		c1.convert_to_stage2 (s1);
		int d21 = Math.max(CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * s1.centerF + Tables.conjEdge2[s1.edge][s1.symF]], CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * s1.centerB + Tables.conjEdge2[s1.edge][s1.symB]]);
		if( d21 >= min2 ) return false;

		c1.copyTo (c1r);
		c1r.leftMult  ( 8 );
		c1r.convert_to_stage2 (s2);
		int d22 = Math.max(CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * s2.centerF + Tables.conjEdge2[s2.edge][s2.symF]], CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * s2.centerB + Tables.conjEdge2[s2.edge][s2.symB]]);

		for (length2 = Math.min(d21, d22); length2 < min2; ++length2) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "  Stage 2 - length "+length2 );
			if((( length2 >= d21 ) && search_stage2 (s1.edge, s1.centerF, s1.symF, s1.centerB, s1.symB, length2, 0, N_STAGE2_MOVES, 0 )) ||
			   (( length2 >= d22 ) && search_stage2 (s2.edge, s2.centerF, s2.symF, s2.centerB, s2.symB, length2, 0, N_STAGE2_MOVES, 1 ))){
				return true;
			}
			min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);
		}
		return false;
	}

	public boolean search_stage2 (int edge, int centerF, int symF, int centerB, int symB, int depth, int moves_done, int last_move, int r ){
		int mov_idx, mc, j;

		if( ( centerF == centerB ) && (( symF & 0x8 ) == ( symB & 0x8 )) && ((( edge == 0 ) && (( symF & 0x8 ) != 0 )) || (( edge == 414 ) && (( symF & 0x8 ) == 0 ))))
			for (int i=0; i < Constants.stage2_solved_symcenters.length; i++)
				if (centerF == Constants.stage2_solved_symcenters[i]){
					if (depth == 0) 
						return init_stage3 (r);
					else
						return false;
				}

		int end = ( depth == 1 ) ? N_STAGE2_LAST : N_STAGE2_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage2_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to list2[depth] */
			int centerFx = Tables.moveCenter2[centerF][Symmetry.moveConjugate2[mov_idx][symF]];
			int symFx = Symmetry.symIdxMultiply[centerFx & 0xF][symF];
			centerFx >>= 4;
			int centerBx = Tables.moveCenter2[centerB][Symmetry.moveConjugate2[mov_idx][symB]];
			int symBx = Symmetry.symIdxMultiply[centerBx & 0xF][symB];
			centerBx >>= 4;
			int edgex = Tables.moveEdge2[edge][mov_idx];

			int newDistCenF = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * centerFx + Tables.conjEdge2[edgex][symFx]];
			if (newDistCenF > depth-1) continue;
			int newDistCenB = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGES * centerBx + Tables.conjEdge2[edgex][symBx]];
			if (newDistCenB > depth-1) continue;
			move_list_stage2[moves_done] = (byte)mov_idx;
			if (search_stage2 (edgex, centerFx, symFx, centerBx, symBx, depth - 1, moves_done + 1, mov_idx, r)) return true;
		}
		return false;
	}

	public boolean init_stage3 (int r){
		int i;
		if ( found_sol ) return true;

		r2 = r;
		switch (r) {
		case 0:
			c1.copyTo(c2);
			break;	//no whole cube rotation
		case 1:
			c1r.copyTo(c2);
			break;	//no whole cube rotation
		default:
			System.out.println ("Invalid cube rotation state.");
		}

		c2.scramble( length2, move_list_stage2, stage2_slice_moves );

		rotate2 = rotate;

		if (c2.m_cen[16] < 4) {
			c2.rightMult ( 8 );
			rotate2 += 8;
		}

		CubeStage3 s1 = new CubeStage3();
		c2.convert_to_stage3 (s1);

		int min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );

		int cubeDistCen = CubeStage3.prune_table_cen.ptable[s1.center];
		if( cubeDistCen >= min3 ) return false;
		int cubeDistEdg = CubeStage3.prune_table_edg.ptable[( s1.edge<<1 ) + (s1.edge_odd?1:0)];
		int d3 = Math.max(cubeDistCen, cubeDistEdg);

		for (length3 = d3; length3 < min3; ++length3) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "    Stage 3 - length "+length3 );
			if( search_stage3 (s1.center, s1.sym, s1.edge, s1.edge_odd, length3, 0, N_STAGE3_MOVES )){
				return true;
			}
			min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage3 (int center, int sym, int edge, boolean edge_odd, int depth, int moves_done, int last_move){
		int mov_idx, j;
		if(( !edge_odd ) && ( edge == 12375 ))
			for (int i = 0; i < Constants.stage3_solved_sym_centers.length; ++i)
				if ( center == Constants.stage3_solved_sym_centers[i]){
					if (depth == 0)
						return init_stage4 ();
					else
						return false;
				}

		int end = ( depth == 1 ) ? N_STAGE3_LAST : N_STAGE3_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage3_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to cube2 */
			int edgex = Tables.moveEdge3[edge][mov_idx];
			boolean edge_oddx = edge_odd ^ stage3_move_parity[mov_idx];
			int centerx = Tables.moveCenter3[center][Symmetry.moveConjugate3[mov_idx][sym]];
			int symx = Symmetry.symIdxCo2Multiply[sym][centerx&0xF];
			centerx >>= 4;

			int newDistCen = CubeStage3.prune_table_cen.ptable[centerx];
			if (newDistCen > depth-1) continue;
			int newDistEdg = CubeStage3.prune_table_edg.ptable[( edgex<<1 ) + (edge_oddx?1:0)];
			if (newDistEdg > depth-1) continue;
			move_list_stage3[moves_done] = (byte)mov_idx;
			if (search_stage3 (centerx, symx, edgex, edge_oddx, depth - 1, moves_done + 1, mov_idx)) return true;
		}
		return false;
	}

	public boolean init_stage4 (){
		int i;
		if ( found_sol ) return true;

		c2.copyTo(c3);
		c3.scramble( length3, move_list_stage3, stage3_slice_moves );
		CubeStage4 s1 = new CubeStage4();
		c3.convert_to_stage4 (s1);

		/** int d4 = s1.getDistance(); **/

		int min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );

		int cubeDistEdgCen = s1.prune_table_edgcen.ptable[s1.edge * N_STAGE4_CENTERS + Tables.conjCenter4[s1.center][s1.sym]];
		if( cubeDistEdgCen >= min4 ) return false;
		int cubeDistEdgCor = s1.getDistanceEdgCor();
		int d4 = Math.max(cubeDistEdgCen, cubeDistEdgCor);


		for (length4 = d4; length4 < min4; ++length4) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "      Stage 4 - length "+length4 );
			/** if( search_stage4 (s1, length4, 0, N_STAGE4_MOVES, d4 )) { **/
			if( search_stage4 (s1.center, s1.corner, s1.edge, s1.sym, length4, 0, N_STAGE4_MOVES, cubeDistEdgCor )) {
				return true;
			}
			min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage4 (int center, int corner, int edge, int sym, int depth, int moves_done, int last_move, int dist){
		int mov_idx, j;
		if( corner == 0 && edge == 0 )
			for (int i = 0; i < Constants.stage4_solved_centers_bm.length; ++i)
				if (center == Constants.stage4_solved_centers_bm[i]){
					if (depth == 0)
						return init_stage5 ();
					else
						return false;
				}

		int end = ( depth == 1 ) ? N_STAGE4_LAST : N_STAGE4_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage4_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move */
			int centerx = Tables.moveCenter4[center][mov_idx];
			int cornerx = Tables.moveCorner4[corner][mov_idx];
			int edgex = Tables.moveEdge4[edge][Symmetry.moveConjugate4[mov_idx][sym]];
			int symx = Symmetry.symIdxMultiply[edgex & 0xF][sym];
			edgex >>= 4;

			/* Compute new distance */
			/** int newDist = cube2.prune_table.new_dist((( cube2.edge * N_STAGE4_CORNERS + Tables.conjCorner4[cube2.corner][cube2.sym] ) * N_STAGE4_CENTERS ) + Tables.conjCenter4[cube2.center][cube2.sym], dist);
			if (newDist > depth-1) continue; **/
			int newDistEdgCen = CubeStage4.prune_table_edgcen.ptable[edgex*N_STAGE4_CENTERS+Tables.conjCenter4[centerx][symx]];
			if (newDistEdgCen > depth-1) continue;
			int newDist = Tables.new_dist(CubeStage4.prune_table_edgcor.ptable_packed, edgex*Constants.N_STAGE4_CORNERS+Tables.conjCorner4[cornerx][symx], dist);
			if (newDist > depth-1) continue;
			move_list_stage4[moves_done] = (byte)mov_idx;
			if (search_stage4 (centerx, cornerx, edgex, symx, depth - 1, moves_done + 1, mov_idx, newDist)) return true;
		}
		return false;
	}

	public boolean init_stage5 (){
		int i;

		c3.copyTo(c4);
		c4.scramble( length4, move_list_stage4, stage4_slice_moves );

		CubeStage5 s1 = new CubeStage5();
		c4.convert_to_stage5 (s1);

		int cubeDistEdgCor = CubeStage5.prune_table_edgcor.ptable[s1.edge * N_STAGE5_CORNERS + Tables.conjCorner5[s1.corner][s1.sym]];
		if( cubeDistEdgCor >= total_length-length4-length3-length2-length1 ) return false;
		int cubeDistEdgCen = s1.getDistanceEdgCen();

		int d5 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

		for (length5 = d5; length5 < total_length-length4-length3-length2-length1; ++length5) {
			if( search_stage5 (s1.edge, s1.sym, s1.center, s1.corner, length5, 0, N_STAGE5_MOVES, cubeDistEdgCen)){
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
				r1_sub = r1;
				r2_sub = r2;
				rotate_sub = rotate;
				rotate2_sub = rotate2;
				return true;
			}
		}
		return false;
	}

	public boolean search_stage5 (int edge, int sym, int center, int corner, int depth, int moves_done, int last_move, int distEdgCen){
		int mov_idx, j;
		if (depth == 0) {
			return( edge == 0 && Tables.conjCenter5[center][sym] == 0 && Tables.conjCorner5[corner][sym] == 0 );
		}
		for (mov_idx = 0; mov_idx < N_STAGE5_SEARCH; ++mov_idx) {
			if (stage5_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to cube2 */
			int centerx = Tables.moveCenter5[center][mov_idx];
			int cornerx = Tables.moveCorner5[corner][mov_idx];
			int edgex = Tables.moveEdge5[edge][Symmetry.moveConjugate5[mov_idx][sym]];
			int symx = Symmetry.symIdxCo4Multiply[sym][edgex & 0xFF];
			edgex >>= 8;

			int newDistEdgCor = CubeStage5.prune_table_edgcor.ptable[edgex * N_STAGE5_CORNERS + Tables.conjCorner5[cornerx][symx]];
			if (newDistEdgCor > depth-1) continue;
			int newDistEdgCen = Tables.new_dist(CubeStage5.prune_table_edgcen.ptable_packed, edgex * Constants.N_STAGE5_CENTERS + Tables.conjCenter5[centerx][symx], distEdgCen);
			if (newDistEdgCen > depth-1) continue;
			move_list_stage5[moves_done] = (byte)mov_idx;
			if (search_stage5 (edgex, symx, centerx, cornerx, depth - 1, moves_done + 1, mov_idx, newDistEdgCen)) return true;
		}
		return false;
	}
}
