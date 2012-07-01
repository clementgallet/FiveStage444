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

	static int MAX_STAGE2 = (METRIC == STM) ? 6 : 8; // FTM
	static int MAX_STAGE3 = 9;
	//static int MAX_STAGE4 = 12;
	static int MAX_STAGE4 = 11;

	static int MIN_STAGE3 = 7;
	static int MIN_STAGE4 = 8;
	static int MIN_STAGE5 = 8;

	CubeState[] init_cube = new CubeState[6];
	CubeState c1 = new CubeState();
	CubeState c1r = new CubeState();
	CubeState c2 = new CubeState();
	CubeState c3 = new CubeState();
	CubeState c4 = new CubeState();

	CubeStage1[] list1 = new CubeStage1[20];
	CubeStage2[] list2 = new CubeStage2[20];
	// TODO: Use it for all stages or don't use it.

	static int DEBUG_LEVEL = 0;

	public String solve (CubeState cube, int timeOut, boolean inverse) {
		int i, j;

		Tools.init();
		for( i=0; i<20; i++ ){
			list1[i] = new CubeStage1();
			list2[i] = new CubeStage2();
		}

		StringBuffer sb = new StringBuffer();
		String sol = "";
		
		for( j=0; j<6; j++){
			init_cube[j] = new CubeState();
			cube.copyTo( init_cube[j] );
			cube.leftMultEdges  ( 16 );
			cube.leftMultCenters( 16 );
			cube.leftMultCorners( 16 );
			if( j == 2 ) cube.inverse();
		}

		init_stage1 ();

		/* Transform rotations before outputing the solution */
		for (i = 0; i < length1_sub; ++i)
			move_list_sub_stage1[i] = stage1_slice_moves[move_list_sub_stage1[i]];
		for (i = 0; i < length2_sub; ++i)
			move_list_sub_stage2[i] = xlate_r6[stage2_slice_moves[move_list_sub_stage2[i]]][rotate];
		for (i = 0; i < length3_sub; ++i)
			move_list_sub_stage3[i] = xlate_r6[stage3_slice_moves[move_list_sub_stage3[i]]][rotate2];
		for (i = 0; i < length4_sub; ++i)
			move_list_sub_stage4[i] = xlate_r6[stage4_slice_moves[move_list_sub_stage4[i]]][rotate2];
		for (i = 0; i < length5_sub; ++i)
			move_list_sub_stage5[i] = xlate_r6[stage5_slice_moves[move_list_sub_stage5[i]]][rotate2];

		if( inverse ^ (r1_sub>2) ){
			sb.append(print_move_list (length5_sub, move_list_sub_stage5, true));
			//sb.append("* ");
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, true));
			//sb.append("* ");
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, rotate2+3, true));
			//sb.append("* ");
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, rotate, true));
			//sb.append("* ");
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, true));
		}
		else{
			sb.append(print_move_list (length1_sub, move_list_sub_stage1, false));
			//sb.append("* ");
			sb.append(print_move_list (length2_sub, move_list_sub_stage2, rotate, false));
			//sb.append("* ");
			sb.append(print_move_list (length3_sub, move_list_sub_stage3, rotate2+3, false));
			//sb.append("* ");
			sb.append(print_move_list (length4_sub, move_list_sub_stage4, false));
			//sb.append("* ");
			sb.append(print_move_list (length5_sub, move_list_sub_stage5, false));
		}

		//System.out.print(length1_sub + length2_sub + length3_sub + length4_sub + length5_sub);
		//System.out.print("\t");
		return sb.toString();
	}

	public void init_stage1 () {
		CubeStage1 s1 = new CubeStage1();
		CubeStage1 s2 = new CubeStage1();
		CubeStage1 s3 = new CubeStage1();
		CubeStage1 s4 = new CubeStage1();
		CubeStage1 s5 = new CubeStage1();
		CubeStage1 s6 = new CubeStage1();

		init_cube[0].convert_to_stage1 (s1);
		init_cube[1].convert_to_stage1 (s2);
		init_cube[2].convert_to_stage1 (s3);
		init_cube[3].convert_to_stage1 (s4);
		init_cube[4].convert_to_stage1 (s5);
		init_cube[5].convert_to_stage1 (s6);

		int d1 = s1.getDistance();
		int d2 = s2.getDistance();
		int d3 = s3.getDistance();
		int d4 = s4.getDistance();
		int d5 = s5.getDistance();
		int d6 = s6.getDistance();
		int d = Math.min(Math.min(Math.min(d1, d2), d3), Math.min(Math.min(d4, d5), d6));

		total_length = 48;
		found_sol = false;
		for (length1 = d; length1 < total_length; ++length1) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "Stage 1 - length "+length1 );
			if ( search_stage1 (s1, length1, 0, N_BASIC_MOVES, d1, 0 )
			  || search_stage1 (s2, length1, 0, N_BASIC_MOVES, d2, 1 )
			  || search_stage1 (s3, length1, 0, N_BASIC_MOVES, d3, 2 )
			  || search_stage1 (s4, length1, 0, N_BASIC_MOVES, d4, 3 )
			  || search_stage1 (s5, length1, 0, N_BASIC_MOVES, d5, 4 )
			  || search_stage1 (s6, length1, 0, N_BASIC_MOVES, d6, 5 ))
				return;
		}
	}

	public boolean search_stage1 (CubeStage1 cube1, int depth, int moves_done, int last_move, int dist, int r){
		//CubeStage1 cube2 = new CubeStage1();
		int mov_idx, j;
		if (depth == 0){
			if (! cube1.is_solved ()) {
				return false;
			}
			return init_stage2 (r);
		}
		int end = ( depth == 1 ) ? N_STAGE1_LAST : N_STAGE1_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage1_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to list1[depth] */
			if (( METRIC == FTM ) || (( stage1_slice_moves[mov_idx] % 6 ) < 3 ))
				list1[depth].corner = Tables.move_table_co[cube1.corner][basic_to_face[mov_idx]];
			else
				list1[depth].corner = cube1.corner;
			int newEdge = Tables.move_table_symEdgeSTAGE1[cube1.edge][Symmetry.moveConjugate1[mov_idx][cube1.sym]];
			list1[depth].sym = Symmetry.symIdxMultiply[newEdge & 0x3F][cube1.sym];
			list1[depth].edge = newEdge >> 6 ;

			/* Compute new distance */
			int newDist = CubeStage1.prune_table.new_dist(N_CORNER_ORIENT * list1[depth].edge + Tables.move_table_co_conj[list1[depth].corner][list1[depth].sym], dist);
			if (newDist > depth-1) continue;
			move_list_stage1[moves_done] = (byte)mov_idx;
			if (search_stage1 (list1[depth], depth - 1, moves_done + 1, mov_idx, newDist, r)) return true;
		}
		return false;
	}

	public boolean init_stage2 (int r){
		int i;
		int cubeDistCenF1 = 0;
		int cubeDistCenB1 = 0;
		int d21 = 999;
		int cubeDistCenF2 = 0;
		int cubeDistCenB2 = 0;
		int d22 = 999;
		if( found_sol ) return true;
		r1 = r;
		init_cube[r1].copyTo(c1);

		c1.scramble( length1, move_list_stage1, stage1_slice_moves );

		rotate = c1.m_cor[0] >> 3;
		switch (rotate) {
		case 0:
			break;	//no whole cube rotation
		case 1:
			c1.do_move (Ls3, FTM);
			c1.do_move (Rs, FTM);
			c1.do_move (Us3, FTM);
			c1.do_move (Ds, FTM);
			break;
		case 2:
			c1.do_move (Fs, FTM);
			c1.do_move (Bs3, FTM);
			c1.do_move (Us, FTM);
			c1.do_move (Ds3, FTM);
			break;
		default:
			System.out.println ("Invalid cube rotation state.");
		}

		int min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);

		CubeStage2 s1 = new CubeStage2();
		CubeStage2 s2 = new CubeStage2();

		c1.convert_to_stage2 (s1);
		cubeDistCenF1 = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s1.centerF + Tables.move_table_edge_conjSTAGE2[s1.edge][s1.symF]];
		if( cubeDistCenF1 < min2 ){
			cubeDistCenB1 = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s1.centerB + Tables.move_table_edge_conjSTAGE2[s1.edge][s1.symB]];
			if( cubeDistCenB1 < min2 ){
				d21 = Math.max(cubeDistCenF1, cubeDistCenB1);
			}
		}

		c1.copyTo (c1r);
		c1r.leftMultEdges  ( 8 );
		c1r.leftMultCenters( 8 );
		c1r.leftMultCorners( 8 );
		c1r.convert_to_stage2 (s2);
		cubeDistCenF2 = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s2.centerF + Tables.move_table_edge_conjSTAGE2[s2.edge][s2.symF]];
		if( cubeDistCenF2 < min2 ){
			cubeDistCenB2 = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * s2.centerB + Tables.move_table_edge_conjSTAGE2[s2.edge][s2.symB]];
			if( cubeDistCenB2 < min2 ){
				d22 = Math.max(cubeDistCenF2, cubeDistCenB2);
			}
		}

		for (length2 = Math.min(d21, d22); length2 < min2; ++length2) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "  Stage 2 - length "+length2 );
			if((( length2 >= d21 ) && search_stage2 (s1, length2, 0, N_STAGE2_MOVES, 0 )) ||
			   (( length2 >= d22 ) && search_stage2 (s2, length2, 0, N_STAGE2_MOVES, 1 ))){
				return true;
			}
			min2 = Math.min( MAX_STAGE2 + 1, total_length - length1 - MIN_STAGE3 - MIN_STAGE4 - MIN_STAGE5);
		}
		return false;
	}

	public boolean search_stage2 (CubeStage2 cube1, int depth, int moves_done, int last_move, int r ){
		//CubeStage2 cube2 = new CubeStage2();
		int mov_idx, mc, j;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			return init_stage3 (r);
		}
		int end = ( depth == 1 ) ? N_STAGE2_LAST : N_STAGE2_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage2_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to list2[depth] */
			int newCen;
			newCen = Tables.move_table_symCenterSTAGE2[cube1.centerF][Symmetry.moveConjugate2[mov_idx][cube1.symF]];
			list2[depth].symF = Symmetry.symIdxMultiply[newCen & 0xF][cube1.symF];
			list2[depth].centerF = newCen >> 4;
			newCen = Tables.move_table_symCenterSTAGE2[cube1.centerB][Symmetry.moveConjugate2[mov_idx][cube1.symB]];
			list2[depth].symB = Symmetry.symIdxMultiply[newCen & 0xF][cube1.symB];
			list2[depth].centerB = newCen >> 4;
			list2[depth].edge = Tables.move_table_edgeSTAGE2[cube1.edge][mov_idx];

			int newDistCenF = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * list2[depth].centerF + Tables.move_table_edge_conjSTAGE2[list2[depth].edge][list2[depth].symF]];
			if (newDistCenF > depth-1) continue;
			int newDistCenB = CubeStage2.prune_table_edgcen.ptable[N_STAGE2_EDGE_CONFIGS * list2[depth].centerB + Tables.move_table_edge_conjSTAGE2[list2[depth].edge][list2[depth].symB]];
			if (newDistCenB > depth-1) continue;
			move_list_stage2[moves_done] = (byte)mov_idx;
			if (search_stage2 (list2[depth], depth - 1, moves_done + 1, mov_idx, r)) return true;
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

		c2.scramble( length2, move_list_stage2, stage2_slice_moves, Lf );

		rotate2 = rotate;

		if (c2.m_cen[16] < 4) {
			c2.do_move (Us, FTM);
			c2.do_move (Ds3, FTM);
			rotate2 += 3;
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
			if( search_stage3 (s1, length3, 0, N_STAGE3_MOVES )){
				return true;
			}
			min3 = Math.min( MAX_STAGE3 + 1, total_length - length1 - length2 - MIN_STAGE4 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage3 (CubeStage3 cube1, int depth, int moves_done, int last_move){
		CubeStage3 cube2 = new CubeStage3();
		int mov_idx, j;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			return init_stage4 ();
		}
		int end = ( depth == 1 ) ? N_STAGE3_LAST : N_STAGE3_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage3_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to cube2 */
			cube2.edge = Tables.move_table_edgeSTAGE3[cube1.edge][mov_idx];
			cube2.edge_odd = cube1.edge_odd ^ stage3_move_parity[mov_idx];
			int newCen = Tables.move_table_symCenterSTAGE3[cube1.center][Symmetry.moveConjugate3[mov_idx][Symmetry.symIdxMultiply[cube1.sym][cube1.cosym]]];
			int newSym = ( newCen & 0xF ) >> 1;
			int newCosym = newCen & 0x01;
			cube2.cosym = Symmetry.symIdxMultiply[Symmetry.symIdxMultiply[Symmetry.invSymIdx[cube1.sym]][newCosym]][Symmetry.symIdxMultiply[cube1.sym][cube1.cosym]];
			cube2.sym = Symmetry.symIdxMultiply[newSym][cube1.sym];
			cube2.center = newCen >> 4;

			int newDistCen = CubeStage3.prune_table_cen.ptable[cube2.center];
			if (newDistCen > depth-1) continue;
			int newDistEdg = CubeStage3.prune_table_edg.ptable[( cube2.edge<<1 ) + (cube2.edge_odd?1:0)];
			if (newDistEdg > depth-1) continue;
			move_list_stage3[moves_done] = (byte)mov_idx;
			if (search_stage3 (cube2, depth - 1, moves_done + 1, mov_idx)) return true;
		}
		return false;
	}

	public boolean init_stage4 (){
		int i;
		if ( found_sol ) return true;

		c2.copyTo(c3);
		c3.scramble( length3, move_list_stage3, stage3_slice_moves, Ff );
		CubeStage4 s1 = new CubeStage4();
		c3.convert_to_stage4 (s1);

		/** int d4 = s1.getDistance(); **/

		int cubeDistEdgCen = s1.prune_table_edgcen.ptable[s1.edge * N_STAGE4_CENTER_CONFIGS + Tables.move_table_cen_conjSTAGE4[s1.center][s1.sym]];
		if( cubeDistEdgCen >= total_length-length3-length2 ) return false;
		int cubeDistEdgCor = s1.getDistanceEdgCor();
		int d4 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

		int min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );

		for (length4 = d4; length4 < min4; ++length4) {
			if( DEBUG_LEVEL >= 1 ) System.out.println( "      Stage 4 - length "+length4 );
			/** if( search_stage4 (s1, length4, 0, N_STAGE4_MOVES, d4 )) { **/
			if( search_stage4 (s1, length4, 0, N_STAGE4_MOVES, cubeDistEdgCor )) {
				return true;
			}
			min4 = Math.min( MAX_STAGE4 + 1, total_length - length1 - length2 - length3 - MIN_STAGE5 );
		}
		return false;
	}

	public boolean search_stage4 (CubeStage4 cube1, int depth, int moves_done, int last_move, int dist){
		CubeStage4 cube2 = new CubeStage4();
		int mov_idx, j;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			return init_stage5 ();
		}
		int end = ( depth == 1 ) ? N_STAGE4_LAST : N_STAGE4_SEARCH;
		for (mov_idx = 0; mov_idx < end; ++mov_idx) {
			if (stage4_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move to cube2 */
			cube2.center = Tables.move_table_cenSTAGE4[cube1.center][mov_idx];
			cube2.corner = Tables.move_table_cornerSTAGE4[cube1.corner][mov_idx];
			int newEdge = Tables.move_table_symEdgeSTAGE4[cube1.edge][Symmetry.moveConjugate4[mov_idx][cube1.sym]];
			cube2.sym = Symmetry.symIdxMultiply[newEdge & 0xF][cube1.sym];
			cube2.edge = newEdge >> 4;

			/* Compute new distance */
			/** int newDist = cube2.prune_table.new_dist((( cube2.edge * N_STAGE4_CORNER_CONFIGS + Tables.move_table_corner_conjSTAGE4[cube2.corner][cube2.sym] ) * N_STAGE4_CENTER_CONFIGS ) + Tables.move_table_cen_conjSTAGE4[cube2.center][cube2.sym], dist);
			if (newDist > depth-1) continue; **/
			int newDistEdgCen = cube2.prune_table_edgcen.ptable[cube2.edge * N_STAGE4_CENTER_CONFIGS + Tables.move_table_cen_conjSTAGE4[cube2.center][cube2.sym]];
			if (newDistEdgCen > depth-1) continue;
			int newDist = cube2.new_dist_edgcor(dist);
			if (newDist > depth-1) continue;
			move_list_stage4[moves_done] = (byte)mov_idx;
			if (search_stage4 (cube2, depth - 1, moves_done + 1, mov_idx, newDist)) return true;
		}
		return false;
	}

	public boolean init_stage5 (){
		int i;

		c3.copyTo(c4);
		c4.scramble( length4, move_list_stage4, stage4_slice_moves );

		CubeStage5 s1 = new CubeStage5();
		c4.convert_to_stage5 (s1);

		int cubeDistEdgCor = CubeStage5.prune_table_edgcor.ptable[s1.edge * N_STAGE5_CORNER_PERM + Tables.move_table_corner_conjSTAGE5[s1.corner][(s1.sym<<2)+s1.cosym]];
		if( cubeDistEdgCor >= total_length-length4-length3-length2-length1 ) return false;
		int cubeDistEdgCen = s1.getDistanceEdgCen();
		int d5 = Math.max(cubeDistEdgCen, cubeDistEdgCor);

		for (length5 = d5; length5 < total_length-length4-length3-length2-length1; ++length5) {
			if( search_stage5 (s1, length5, 0, N_STAGE5_MOVES, cubeDistEdgCen)){
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
				return true;
			}
		}
		return false;
	}

	public boolean search_stage5 (CubeStage5 cube1, int depth, int moves_done, int last_move, int distEdgCen){
		CubeStage5 cube2 = new CubeStage5();
		int mov_idx, j;
		if (depth == 0) {
			if (! cube1.is_solved ()) {
				return false;
			}
			return true;
		}
		for (mov_idx = 0; mov_idx < N_STAGE5_SEARCH; ++mov_idx) {
			if (stage5_slice_moves_to_try[last_move][mov_idx])
				continue;

			/* Move cube1 to cube2 */
			cube2.center = Tables.move_table_cenSTAGE5[cube1.center][mov_idx];
			cube2.corner = Tables.move_table_cornerSTAGE5[cube1.corner][mov_idx];
			int newEdge = Tables.move_table_symEdgeSTAGE5[cube1.edge][Symmetry.moveConjugate5[mov_idx][Symmetry.symIdxMultiply[cube1.sym][cube1.cosym]]];
			int newSym = ( newEdge & 0xFF ) >> 2;
			int newCosym = newEdge & 0x03;
			cube2.cosym = Symmetry.symIdxMultiply[Symmetry.symIdxMultiply[Symmetry.invSymIdx[cube1.sym]][newCosym]][Symmetry.symIdxMultiply[cube1.sym][cube1.cosym]];
			cube2.sym = Symmetry.symIdxMultiply[newSym][cube1.sym];
			cube2.edge = newEdge >> 8;

			int newDistEdgCor = CubeStage5.prune_table_edgcor.ptable[cube2.edge * N_STAGE5_CORNER_PERM + Tables.move_table_corner_conjSTAGE5[cube2.corner][(cube2.sym<<2)+cube2.cosym]];
			if (newDistEdgCor > depth-1) continue;
			int newDistEdgCen = cube2.new_dist_edgcen(distEdgCen);
			if (newDistEdgCen > depth-1) continue;
			move_list_stage5[moves_done] = (byte)mov_idx;
			if (search_stage5 (cube2, depth - 1, moves_done + 1, mov_idx, newDistEdgCen)) return true;
		}
		return false;
	}
}
