package fivestage444;

public final class CubePruningTable {

	private	int m_num_positions;
	private	byte[] m_ptable;
	private Constants.DoMove m_do_move_func;
	private int m_num_moves;
	private int[] m_move_list;
	private int m_num_moves2;
	private int[] m_move_list2;
	private int m_num_solved;
	private int[] m_psolved;
	private int m_count;

	CubePruningTable (int num_positions, byte[] ptable, Constants.DoMove move_func){
		m_num_positions = num_positions;
		m_ptable = ptable;
		m_do_move_func = move_func;
		m_num_moves = 0;
		//m_move_list = NULL;
		m_num_moves2 = 0;
		//m_move_list2 = NULL;
		m_num_solved = 0;
		//m_psolved = NULL;
		m_count = 0;
	}

	public void init_move_list (int dim2, int num_moves, int[] move_list){
		int i;
		m_num_moves = num_moves;
		m_move_list = new int[3*m_num_moves];
		switch (dim2) {
		case 0:
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = i;
				m_move_list[3*i+1] = -1;
				m_move_list[3*i+2] = -1;
			}
			break;
		case 1:
			for (i = 0; i < m_num_moves; ++i) {
				m_move_list[3*i] = move_list[i];
				m_move_list[3*i+1] = -1;
				m_move_list[3*i+2] = -1;
			}
			break;
		case 2:
			{
				int p = 0;
				for (i = 0; i < m_num_moves; ++i) {
					m_move_list[3*i] = move_list[p++];
					m_move_list[3*i+1] = move_list[p++];
					m_move_list[3*i+2] = -1;
				}
			}
			break;
		case 3:
			{
				int p = 0;
				for (i = 0; i < m_num_moves; ++i) {
					m_move_list[3*i] = move_list[p++];
					m_move_list[3*i+1] = move_list[p++];
					m_move_list[3*i+2] = move_list[p++];
				}
			}
			break;
		default:
			System.out.println ("CubePruningTable::init_move_list call ignored");
		}
	}

	public void init_move_list2 (int dim2, int num_moves, int[] move_list){
		int i;
		m_num_moves2 = num_moves;
		m_move_list2 = new int[3*m_num_moves];
		switch (dim2) {
		case 0:
			for (i = 0; i < m_num_moves2; ++i) {
				m_move_list2[3*i] = i;
				m_move_list2[3*i+1] = -1;
				m_move_list2[3*i+2] = -1;
			}
			break;
		case 1:
			for (i = 0; i < m_num_moves2; ++i) {
				m_move_list2[3*i] = move_list[i];
				m_move_list2[3*i+1] = -1;
				m_move_list2[3*i+2] = -1;
			}
			break;
		case 2:
			for (i = 0; i < m_num_moves2; ++i) {
				m_move_list2[3*i] = move_list[2*i];
				m_move_list2[3*i+1] = move_list[2*i + 1];
				m_move_list2[3*i+2] = -1;
			}
			break;
		default:
			System.out.println ("CubePruningTable::init_move_list2 call ignored\n");
		}
	}

	public void init_solved_list (int count, int[] solved_list){
		m_num_solved = count;
		m_psolved = solved_list;	// (old comment:) Assume points to statically allocated array. (Me) Don't know if it's the right thing to do...
	}

	public void init (){
		int i;
		int n = m_num_positions/2 + (m_num_positions & 0x1);
		m_count = 0;
		for (i = 0; i < n; ++i) {
			m_ptable[i] = (byte)0xFF;
		}
	}

	public void analyze (){
		int i;
		int idx;
		int dist;
		int max_dist = 14;	//MAX_DISTANCE;
		init ();
		for (i = 0; i < m_num_solved; ++i) {
			add_to_table (m_psolved[i], 0);
		}
		int new_count = m_count;
		for (dist = 1; dist <= max_dist && new_count > 0; ++dist) {
			int old_count = m_count;
			for (idx = 0; idx < m_num_positions; ++idx) {
				int dx = Constants.get_dist_4bit (idx, m_ptable);
				if (m_num_moves2 > 0 && dist >= 2 && dx == dist - 2) {
					generate2 (idx, dist);
				}
				if (dx == dist - 1) {
					generate1 (idx, dist);
				}
			}
			new_count = m_count - old_count;
			//special case: distance 1 could have 0 positions when there are "moves" that count as 2 moves.
			if (new_count == 0 && m_num_moves2 > 0 && dist == 1) {
				new_count = 1;	//fake new count to prevent exiting loop prematurely.
			}
		}
	}

	public void generate1 (int idx, int dist)
	{
		int i, j;

		for (i = 0; i < m_num_moves; ++i) {
			int idx2 = m_do_move_func.do_move (idx, m_move_list[3*i]);
			for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
				idx2 = m_do_move_func.do_move (idx2, m_move_list[3*i + j]);
			}
			add_to_table (idx2, dist);
		}
	}

	public void generate2 (int idx, int dist)
	{
		int i, j;

		for (i = 0; i < m_num_moves2; ++i) {
			int idx2 = m_do_move_func.do_move (idx, m_move_list2[3*i]);
			for (j = 1; j < 3 && m_move_list[3*i + j] >= 0; ++j) {
				idx2 = m_do_move_func.do_move (idx2, m_move_list2[3*i + j]);
			}
			add_to_table (idx2, dist);
		}
	}

	public void add_to_table (int idx, int dist){
		if (Constants.get_dist_4bit (idx, m_ptable) == 0xF) {
			set_dist_4bit (idx, dist, m_ptable);
			++m_count;
		}
	}

	public void set_dist_4bit (int x, int dist, byte[] p)
	{
		int x2 = x >> 1;
		int j = x & 0x1;
		if (j == 0) {
			p[x2] &= 0xF0;
			p[x2] |= dist & 0xF;
			return;
		}
		p[x2] &= 0x0F;
		p[x2] |= (dist & 0xF) << 4;
	}

}
