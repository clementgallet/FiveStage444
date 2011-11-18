package fivestage444;

public final class DoMoveEC4STM implements Constants.DoMove{
	public int do_move(int idx, int move_code){
		CubeStage4 cube1 = new CubeStage4();
		cube1.m_centerUD = (byte)(idx % Constants.N_STAGE4_CENTER_CONFIGS);
		cube1.m_corner = 0;
		cube1.m_edge = idx / Constants.N_STAGE4_CENTER_CONFIGS;
		cube1.do_move (move_code);
		return Constants.N_STAGE4_CENTER_CONFIGS*cube1.m_edge + cube1.m_centerUD;
	}
}
