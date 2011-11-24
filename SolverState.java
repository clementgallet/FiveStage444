package fivestage444;

public final class SolverState implements java.io.Serializable{

	public CubeState cube;
	public int metric;
	public int[] move_list;
	public int move_count;
	public int rotate;

	SolverState( CubeState cube, int metric, int[] move_list, int move_count, int rotate ){
		this.cube = cube;
		this.metric = metric;
		this.move_list = move_list;
		this.move_count = move_count;
		this.rotate = rotate;
	}

}
