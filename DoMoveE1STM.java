package fivestage444;

public final class DoMoveE1STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		CubeStage1 cube1 = new CubeStage1();
		cube1.m_co = 0;
		cube1.m_edge_ud_combo8 = idx;
		cube1.do_move (move_code);
		return cube1.m_edge_ud_combo8;
	}
}
