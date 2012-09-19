package cg.fivestage444;
import java.util.Random;

public final class Main {

	public static void main(String[] args){

		int random_count = 100;
		Tools.init();
		CubeState c = new CubeState();
		Search s = new Search();
		Random gen = new Random(43);
/*		for (int j=6; j<7; j++)
		for (int k=10; k<12; k++)
		for (int l=12; l<15; l++)
		for (int m=6; m<10; m++)
		for (int n=6; n<10; n++)
		for (int o=8; o<9; o++){
		s.MAX_STAGE2 = j;
		s.MAX_STAGE3 = k;
		s.MAX_STAGE4 = l;
		s.MIN_STAGE3 = m;
		s.MIN_STAGE4 = n;
		s.MIN_STAGE5 = o;*/
		long time = System.currentTimeMillis();	
		for (int i = 0; i < random_count; ++i) {
			c.randomise(gen);
			System.out.println(s.solve( c, 45, true));
			//s.solve( c, 45, true);
		}
		System.out.println(System.currentTimeMillis() - time);
		//System.out.println(j+"-"+k+"-"+l+"-"+m+"-"+n+"-"+o+"-"+(System.currentTimeMillis() - time));
		//}
	}
}
