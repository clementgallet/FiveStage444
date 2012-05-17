package fivestage444;

public final class CubeStage2 {

	public short m_centerF;
	public short m_centerB;
	public short m_edge;

	public static PruningStage2EdgCen prune_table_edgcen;

	public void copyTo (CubeStage2 cube1){
		cube1.m_edge = m_edge;
		cube1.m_centerF = m_centerF;
		cube1.m_centerB = m_centerB;
	}

	public boolean edges_center_solved (boolean front){
		int i;
		short cen;
		if (front) cen = m_centerF;
		else cen = m_centerB;

		if (( m_edge != 0 ) && ( m_edge != 414 ))
			return false;
		for (i=0; i < Constants.STAGE2_NUM_SOLVED_SYMCENTER_CONFIGS; i++)
			if ((cen>>4) == Constants.stage2_solved_symcenters[i])
				return true;

		return false;
	}

	public boolean is_solved (){
		int i;

		if (( m_edge != 0 ) && ( m_edge != 414 ))
			return false;

		for (i=0; i < Constants.STAGE2_NUM_SOLVED_SYMCENTER_CONFIGS; i++)
			if (((m_centerF>>4) == Constants.stage2_solved_symcenters[i]) && ((m_centerB>>4) == Constants.stage2_solved_symcenters[i]) && ((m_centerF&0x8) == (m_centerB&0x8)) && ( ((m_centerF&0x8) == 0 && ( m_edge == 414 )) || ((m_centerF&0x8) != 0 && ( m_edge == 0 )) ))
				return true;

		return false;
	}

	public void do_move (int move_code){

		int sym, moveConj, newSym, newRep;
		short rep, newCen;

		sym = m_centerF & 0xF;
		rep = (short)(m_centerF >> 4);
		moveConj = Constants.stage2_inv_slice_moves[Symmetry.moveConjugate[Constants.stage2_slice_moves[move_code]][sym]];
		newCen = Tables.move_table_symCenterSTAGE2[rep][moveConj];
		newSym = newCen & 0xF;
		newRep = newCen >> 4;
		m_centerF = (short)(( newRep << 4 ) + Symmetry.symIdxMultiply[newSym][sym]);

		sym = m_centerB & 0xF;
		rep = (short)(m_centerB >> 4);
		moveConj = Constants.stage2_inv_slice_moves[Symmetry.moveConjugate[Constants.stage2_slice_moves[move_code]][sym]];
		newCen = Tables.move_table_symCenterSTAGE2[rep][moveConj];
		newSym = newCen & 0xF;
		newRep = newCen >> 4;
		m_centerB = (short)(( newRep << 4 ) + Symmetry.symIdxMultiply[newSym][sym]);

		m_edge = Tables.move_table_edgeSTAGE2[m_edge][move_code];
	}

	/* Convert functions */

	public void convert_centers_to_std_cube (int u, CubeState result_cube){
		int i;
		int cbmb = Tables.cloc_to_bm[u];
		int udlrf = 0;
		for (i = 0; i < 24; ++i) {
			if ((cbmb & (1 << i)) == 0) {
				result_cube.m_cen[i] = (byte)(udlrf++/4);
			} else {
				result_cube.m_cen[i] = 5;
			}
		}
	}

	public void convert_edges_to_std_cube (CubeState result_cube){
		int i;
		byte[] t6 = new byte[4];
		int edgeFbm = Tables.bm4of8[m_edge / 6];
		Constants.perm_n_unpack (4, m_edge % 6, t6, 0);
		for (i = 0; i < 16; ++i)
			result_cube.m_edge[i] = (byte)i;

		byte f = 16;
		int b = 0;
		for (i = 0; i < 8; ++i) {
			if ((edgeFbm & (1 << i)) == 0) {
				result_cube.m_edge[16 + i] = (byte)(20 + t6[b++]);
			} else {
				result_cube.m_edge[16 + i] = f++;
			}
		}
	}

	/* Pruning functions */

	private int get_idx(boolean front){
		short cen;
		if (front) cen = m_centerF;
		else cen = m_centerB;
		return Constants.N_STAGE2_EDGE_CONFIGS * (cen >> 4 ) + Tables.move_table_edge_conjSTAGE2[m_edge][cen & 0xF];
	}

	public int get_dist_edgcen (boolean front){
		return prune_table_edgcen.get_dist_packed(get_idx(front));
	}

	public int new_dist_edgcen (boolean front, int dist){
		return prune_table_edgcen.new_dist(get_idx(front), dist);
	}

	public int getDistance (boolean front){
		return Math.max(getDistanceEdgCen(false), getDistanceEdgCen(true));
	}

	public int getDistanceEdgCen (boolean front){
		CubeStage2 cube1 = new CubeStage2();
		CubeStage2 cube2 = new CubeStage2();
		int mov_idx, mc, j, dist1, dist2;
		int nDist = 0;

		copyTo (cube1);
		dist1 = cube1.get_dist_edgcen(front);

		while (! cube1.edges_center_solved(front)) {

			boolean noMoves=true;
			for (mov_idx = 0; mov_idx < Constants.N_STAGE2_SLICE_MOVES; ++mov_idx) {
				cube1.copyTo(cube2);
				cube2.do_move (mov_idx);
				dist2 = cube2.get_dist_edgcen(front);

				if (((dist2+1) % 3) != dist1) continue;
				cube2.copyTo(cube1);
				nDist++;
				dist1 = dist2;
				noMoves=false;
				break;
			}
			if( noMoves){
				System.out.println("Could not find a move that lowers the distance !!");
				break;
			}
		}
		return nDist;
	}
}
