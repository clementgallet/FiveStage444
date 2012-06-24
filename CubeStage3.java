package cg.fivestage444;

public final class CubeStage3 {

	public int center;
	public int sym;
	public int cosym;
	public int edge; //edge coordinate (12870)
	public boolean edge_odd; //odd parity of edges

	public static PruningStage3Cen prune_table_cen;
	public static PruningStage3Edg prune_table_edg;

	public final void copyTo (CubeStage3 cube1){
		cube1.center = center;
		cube1.sym = sym;
		cube1.cosym = cosym;
		cube1.edge = edge;
		cube1.edge_odd = edge_odd;
	}

	public boolean is_solved (){
		if (edge_odd)
			return false;	//not solved if odd edge parity

		if (edge != 494)
			return false;	//not solved if wrong edge value

		for (int i = 0; i < Constants.STAGE3_NUM_SOLVED_SYM_CENTER_CONFIGS; ++i)
			if ( center == Constants.stage3_solved_sym_centers[i])
				return true;	//If we found a matching center value, then it is solved.

		return false;
	}

	/* Convert functions */

	public void convert_centers_to_std_cube (int center2, CubeState result_cube){
		int i;
		int cenbm = Tables.eloc2e16bm[center2/70];
		int cenbm4of8 = Tables.bm4of8[center2 % 70];
		int ud = 0;
		int pos4of8 = 0;
		for (i = 0; i < 16; ++i) {
			if ((cenbm & (1 << i)) == 0) {
				result_cube.m_cen[i] = (byte)(ud++/4);
			} else {
				if ((cenbm4of8 & (1 << pos4of8++)) == 0) {
					result_cube.m_cen[i] = 3;
				} else {
					result_cube.m_cen[i] = 2;
				}
			}
		}
		for (i = 16; i < 24; ++i) {
			result_cube.m_cen[i] = (byte)(i/4);
		}
	}

	public void convert_edges_to_std_cube (CubeState result_cube){
		int i;
		int edge_bm = Tables.eloc2e16bm[edge];
		byte e0 = 0;
		byte e1 = 4;
		for (i = 0; i < 16; ++i) {
			if ((edge_bm & (1 << i)) != 0) {
				result_cube.m_edge[i] = e0++;
				if (e0 == 4) {
					e0 = 12;		//skip numbers 4..11; those are used for e1
				}
			} else {
				result_cube.m_edge[i] = e1++;
			}
		}
		for (i = 16; i < 24; ++i) {
			result_cube.m_edge[i] = (byte)i;
		}
	}
}
