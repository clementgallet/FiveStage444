package cg.fivestage444;

public final class CubeStage2 {

	public int centerF;
	public int symF;
	public int centerB;
	public int symB;
	public int edge;

	public static PruningStage2EdgCen prune_table_edgcen;

	public final void copyTo (CubeStage2 cube1){
		cube1.edge = edge;
		cube1.centerF = centerF;
		cube1.symF = symF;
		cube1.centerB = centerB;
		cube1.symB = symB;
	}
}
