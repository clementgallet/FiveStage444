package fivestage444;

public final class SolverState implements java.io.Serializable{

	public CubeState cube;
	public int metric;
	public int[] move_list = new int[100];
	public int move_count;
	public int rotate;

	SolverState( CubeState cube, int metric, int[] move_list, int move_count, int rotate ){
		int i;
		this.cube = cube;
		this.metric = metric;
		for (i=0; i < 100; ++i)
			this.move_list[i] = 0;
		for (i=0; i < move_count; ++i)
			this.move_list[i] = move_list[i];
		this.move_count = move_count;
		this.rotate = rotate;
	}

}
