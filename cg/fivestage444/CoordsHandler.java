package cg.fivestage444;

import cg.fivestage444.Coordinates.*;

public class CoordsHandler {
	public static final Center2 center2 = new Center2();
	public static final Center3 center3 = new Center3();
	public static final Center4 center4 = new Center4();
	public static final Center5 center5 = new Center5();
	public static final Corner1 corner1 = new Corner1();
	public static final Corner4 corner4 = new Corner4();
	public static final Corner5 corner5 = new Corner5();
	public static final Edge1 edge1 = new Edge1();
	public static final Edge2 edge2 = new Edge2();
	public static final Edge3 edge3 = new Edge3();
	public static final Edge4 edge4 = new Edge4();
	public static final Edge5 edge5 = new Edge5();

	public static void init(){
		center2.init();
		center3.init();
		center4.init();
		center5.init();
		corner1.init();
		corner4.init();
		corner5.init();
		edge1.init();
		edge2.init();
		edge3.init();
		edge4.init();
		edge5.init();
	}
}
