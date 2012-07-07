package cg.fivestage444;

public final class CubeStage3 {

	public int center;
	public int sym;
	public int edge; //edge coordinate (12870)
	public boolean edge_odd; //odd parity of edges

	public static PruningStage3Cen prune_table_cen;
	public static PruningStage3Edg prune_table_edg;

	public final void copyTo (CubeStage3 cube1){
		cube1.center = center;
		cube1.sym = sym;
		cube1.edge = edge;
		cube1.edge_odd = edge_odd;
	}
}
