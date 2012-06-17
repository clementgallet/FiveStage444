package cg.fivestage444;

public final class Test {

	public static void main(String[] args){

		Tools.init();

		CubeStage5 c = new CubeStage5();

		c.edge = 3110;
		c.sym = 2;
		c.cosym = 2;
		c.center = 13;
		c.corner = 2;

		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		/*
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		c.do_move(4);
		System.out.println("edge:"+c.edge+" - sym:"+c.sym+" - cosym:"+c.cosym+" - center:"+c.center+" - corner:"+c.corner);
		*/
	}
}
