package fivestage444;

public final class DoMoveC3STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		CubeStage3 cube1 = new CubeStage3();
		cube1.m_centerLR = idx;
		cube1.m_edge = 0;
		cube1.m_edge_odd = false;
		cube1.do_move (move_code);
		return cube1.m_centerLR;
	}
}
