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

	static int DEBUG_LEVEL = 0;

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

			cube.scramble(length1_sub, move_list_sub_stage1);
			cube.scramble(length2_sub, move_list_sub_stage2);
			cube.scramble(length3_sub, move_list_sub_stage3);
			cube.scramble(length4_sub, move_list_sub_stage4);
			cube.scramble(length5_sub, move_list_sub_stage5);

			int final_ori = cube.is_solved();
			System.out.println(final_ori);
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

			sb.append(print_move_list (length5_sub, move_list_sub_stage5, true));
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, true));
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, true));
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, true));
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, true));
		}
		else{
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, false));
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, false));
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, false));
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, false));
			sb.append(print_move_list (length5_sub, move_list_sub_stage5, false));
		}
		//System.out.print(length1_sub + length2_sub + length3_sub + length4_sub + length5_sub + "\t");
		return sb.toString();
	}

	public void init_stage1 ( int max_turns ) {
		int edge1 = init_cube[0].convert_symedges_to_stage1();
		int edge2 = init_cube[1].convert_symedges_to_stage1();
		int edge3 = init_cube[2].convert_symedges_to_stage1();

		int sym1 = edge1 & 0x3F;
		int sym2 = edge2 & 0x3F;
		int sym3 = edge3 & 0x3F;

		edge1 >>>= 6;
		edge2 >>>= 6;
		edge3 >>>= 6;

		int corner1 = init_cube[0].convert_corners_to_stage1();
		int corner2 = init_cube[1].convert_corners_to_stage1();
		int corner3 = init_cube[2].convert_corners_to_stage1();

		int d1 = Tables.prunDist1(edge1, sym1, corner1);
		int d2 = Tables.prunDist1(edge2, sym2, corner2);
		int d3 = Tables.prunDist1(edge3, sym3, corner3);
		int d = Math.min(Math.min(d1, d2), d3);

		total_length = max_turns+1;
		found_sol = false;
		for (length1 = d; length1 < total_length; ++length1) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "Stage 1 - length "+length1 );
			if ( search_stage1 (edge1, sym1, corner1, length1, 0, N_STAGE1_MOVES, d1, 0 )
			  || search_stage1 (edge2, sym2, corner2, length1, 0, N_STAGE1_MOVES, d2, 1 )
			  || search_stage1 (edge3, sym3, corner3, length1, 0, N_STAGE1_MOVES, d3, 2 ))
				return;
		}
	}

	public boolean search_stage1 (int edge, int sym, int corner, int depth, int moves_done, int last_move, int dist, int r){
		int mov_idx, j;
		if ( ( edge == 0 ) && Tables.conjCorner1[corner][sym] == 1906 ){
			if (depth == 0)
				return init_stage2 (r);
			else
				return false;
		}
		int end = ( depth == 1 ) ? N_STAGE1_LAST : N_STAGE1_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage1_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move */
			int mm = basic_to_face[mov_idx];
			int cornerx = ( mm >= 0 ) ? Tables.moveCorner1[corner][mm] : corner;
			int edgex = Tables.moveEdge1[edge][Symmetry.moveConjugate1[mov_idx][sym]];
			int symx = Symmetry.symIdxMultiply[edgex & 0x3F][sym];
			edgex >>>= 6;

			/* Compute new distance */
			int newDist = Tables.new_dist(Tables.prunTable1, N_STAGE1_CORNERS * edgex + Tables.conjCorner1[cornerx][symx], dist);
			if (newDist > depth-1) continue;
			move_list_stage1[moves_done] = (byte)mov_idx;
			if (search_stage1 (edgex, symx, cornerx, depth - 1, moves_done + 1, mov_idx, newDist, r)) return true;
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

		int edge1 = c1.convert_edges_to_stage2();
		int centerF1 = c1.convert_symcenters_to_stage2(4);
		int centerB1 = c1.convert_symcenters_to_stage2(5);
		int symF1 = centerF1 & 0xF;
		int symB1 = centerB1 & 0xF;
		centerF1 >>>= 4;
		centerB1 >>>= 4;

		int d21 = Math.max(Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES*centerF1+Tables.conjEdge2[edge1][symF1]), Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES * centerB1 + Tables.conjEdge2[edge1][symB1]));
		if( d21 >= min2 ) return false;

		c1.copyTo (c1r);
		c1r.leftMult  ( 8 );
		int edge2 = c1r.convert_edges_to_stage2();
		int centerF2 = c1r.convert_symcenters_to_stage2(4);
		int centerB2 = c1r.convert_symcenters_to_stage2(5);
		int symF2 = centerF2 & 0xF;
		int symB2 = centerB2 & 0xF;
		centerF2 >>>= 4;
		centerB2 >>>= 4;

		int d22 = Math.max(Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES * centerF2 + Tables.conjEdge2[edge2][symF2]), Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES * centerB2 + Tables.conjEdge2[edge2][symB2]));

		for (length2 = Math.min(d21, d22); length2 < min2; ++length2) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "  Stage 2 - length "+length2 );
			if((( length2 >= d21 ) && search_stage2 (edge1, centerF1, symF1, centerB1, symB1, length2, 0, N_STAGE2_MOVES, 0 )) ||
			   (( length2 >= d22 ) && search_stage2 (edge2, centerF2, symF2, centerB2, symB2, length2, 0, N_STAGE2_MOVES, 1 ))){
				return true;
			}
			min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);
		}
		return false;
	}

	public boolean search_stage2 (int edge, int centerF, int symF, int centerB, int symB, int depth, int moves_done, int last_move, int r ){
		int mov_idx, mc, j;

		if (depth == 0){
		if( ( centerF == centerB ) && (( symF & 0x8 ) == ( symB & 0x8 )) && ((( edge == 0 ) && (( symF & 0x8 ) != 0 )) || (( edge == 414 ) && (( symF & 0x8 ) == 0 ))))
			for (int i=0; i < Constants.stage2_solved_symcenters.length; i++)
				if (centerF == Constants.stage2_solved_symcenters[i])
					//if (depth == 0) 
						return init_stage3 (r);
		return false;
		}

		int end = ( depth == 1 ) ? N_STAGE2_LAST : N_STAGE2_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage2_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to list2[depth] */
			int edgex = Tables.moveEdge2[edge][mov_idx];
			int centerFx = Tables.moveCenter2[centerF][Symmetry.moveConjugate2[mov_idx][symF]];
			int symFx = Symmetry.symIdxMultiply[centerFx & 0xF][symF];
			centerFx >>= 4;

			int newDistCenF = Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES*centerFx+Tables.conjEdge2[edgex][symFx]);
			if (newDistCenF > depth-1) continue;

			int centerBx = Tables.moveCenter2[centerB][Symmetry.moveConjugate2[mov_idx][symB]];
			int symBx = Symmetry.symIdxMultiply[centerBx & 0xF][symB];
			centerBx >>= 4;

			int newDistCenB = Tables.getPrun2(Tables.prunTableEdgCen2, N_STAGE2_EDGES*centerBx+Tables.conjEdge2[edgex][symBx]);
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

		int center = c2.convert_symcenters_to_stage3();
		int sym = center & 0xF;
		center >>>= 4;
		int edge = c2.convert_edges_to_stage3();
		boolean edge_odd = c2.edgeUD_parity_odd();

		int min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );

		int cubeDistCen = Tables.prune_table_cen3.ptable[(center<<1)+(edge_odd?1:0)];
		if( cubeDistCen >= min3 ) return false;
		int cubeDistEdg = Tables.prune_table_edg3.ptable[( edge<<1 ) + (edge_odd?1:0)];
		int d3 = Math.max(cubeDistCen, cubeDistEdg);

		for (length3 = d3; length3 < min3; ++length3) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "    Stage 3 - length "+length3 );
			if( search_stage3 (center, sym, edge, edge_odd, length3, 0, N_STAGE3_MOVES )){
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

			int newDistCen = Tables.prune_table_cen3.ptable[(centerx<<1)+(edge_oddx?1:0)];
			if (newDistCen > depth-1) continue;
			int newDistEdg = Tables.prune_table_edg3.ptable[( edgex<<1 ) + (edge_oddx?1:0)];
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

		int edge = c3.convert_symedges_to_stage4();
		int sym = edge & 0xF;
		edge >>>= 4;
		int corner = c3.convert_corners_to_stage4();
		int center = c3.convert_centers_to_stage4();

		int min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );

		int cubeDistEdgCen = Tables.getPrun2(Tables.prunTableEdgCen4, edge * N_STAGE4_CENTERS + Tables.conjCenter4[center][sym]);
		if( cubeDistEdgCen >= min4 ) return false;
		int cubeDistEdgCor = FULL_PRUNING_STAGE4 ? Tables.prunDist4(edge, sym, corner, center) : Tables.prunDistEdgCor4(edge, sym, corner);
		int d4 = Math.max(cubeDistEdgCen, cubeDistEdgCor);


		for (length4 = d4; length4 < min4; ++length4) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "      Stage 4 - length "+length4 );
			/** if( search_stage4 (s1, length4, 0, N_STAGE4_MOVES, d4 )) { **/
			if( search_stage4 (center, corner, edge, sym, length4, 0, N_STAGE4_MOVES, cubeDistEdgCor )) {
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
			int newDistEdgCen = Tables.getPrun2(Tables.prunTableEdgCen4, edgex*N_STAGE4_CENTERS+Tables.conjCenter4[centerx][symx]);
			if (newDistEdgCen > depth-1) continue;
			int newDist = FULL_PRUNING_STAGE4 ? 
			  Tables.new_dist(Tables.prunTable4, (long)(edgex*Constants.N_STAGE4_CORNERS+Tables.conjCorner4[cornerx][symx])*N_STAGE4_CENTERS+Tables.conjCenter4[centerx][symx], dist) :
			  Tables.new_dist(Tables.prunTableEdgCor4, edgex*Constants.N_STAGE4_CORNERS+Tables.conjCorner4[cornerx][symx], dist);
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

		int edge = c4.convert_symedges_to_stage5();
		int sym = edge & 0xFF;
		edge >>>= 8;
		int corner = c4.convert_corners_to_stage5();
		int center = c4.convert_centers_to_stage5();

		int cubeDistEdgCor = Tables.getPrun2(Tables.prunTableEdgCor5, edge * N_STAGE5_CORNERS + Tables.conjCorner5[corner][sym]);
		if( cubeDistEdgCor >= total_length-length4-length3-length2-length1 ) return false;
		int cubeDistEdgCen = Tables.prunDistEdgCen5(edge, sym, center);

		int d5 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

		for (length5 = d5; length5 < total_length-length4-length3-length2-length1; ++length5) {
			if( search_stage5 (edge, sym, center, corner, length5, 0, N_STAGE5_MOVES, cubeDistEdgCen)){
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

			int newDistEdgCor = Tables.getPrun2(Tables.prunTableEdgCor5, edgex * N_STAGE5_CORNERS + Tables.conjCorner5[cornerx][symx]);
			if (newDistEdgCor > depth-1) continue;
			int newDistEdgCen = Tables.new_dist(Tables.prunTableEdgCen5, edgex * Constants.N_STAGE5_CENTERS + Tables.conjCenter5[centerx][symx], distEdgCen);
			if (newDistEdgCen > depth-1) continue;
			move_list_stage5[moves_done] = (byte)mov_idx;
			if (search_stage5 (edgex, symx, centerx, cornerx, depth - 1, moves_done + 1, mov_idx, newDistEdgCen)) return true;
		}
		return false;
	}
}
