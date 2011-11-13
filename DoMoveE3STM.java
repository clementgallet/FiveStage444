package fivestage444;

public class DoMoveE3STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		CubeStage3 cube1 = new CubeStage3();
		cube1.m_centerLR = 0;
		cube1.m_edge = (short)(idx % Constants.N_STAGE3_EDGE_CONFIGS);
		cube1.m_edge_odd = (idx >= Constants.N_STAGE3_EDGE_CONFIGS);
		cube1.do_move (move_code);
		int x = cube1.m_edge;
		if (cube1.m_edge_odd) {
			x += Constants.N_STAGE3_EDGE_CONFIGS;
		}
		return x;
	}
}
