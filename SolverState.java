package fivestage444;

public final class SolverState implements java.io.Serializable{

	public int id;
	public CubeState cube;
	public int metric;
	public byte[] move_list = new byte[100];
	public int move_count;
	public int rotate;

	SolverState( int id, CubeState cube, int metric, byte[] move_list, int move_count, int rotate ){
		this.id = id;
		this.cube = cube;
		this.metric = metric;
		this.move_list = move_list;
		this.move_count = move_count;
		this.rotate = rotate;
	}

}
