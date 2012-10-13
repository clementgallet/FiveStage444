package cg.fivestage444;
import java.util.Random;

public final class Main {

	private final static boolean OPTIMAL = true;
	private final static int SCRAMBLE_LENGTH = 5;

	public static void main(String[] args){

		int random_count = 100;
		Tools.init();
		CubeState c = new CubeState();
		Search s = new Search();
		Random gen = new Random();
		String sol = "";
		int sol_length;
		if (OPTIMAL){
			s.MAX_STAGE2 = 100;
			s.MAX_STAGE3 = 100;
			s.MAX_STAGE4 = 100;
			s.MIN_STAGE3 = 0;
			s.MIN_STAGE4 = 0;
			s.MIN_STAGE5 = 0;
		}
		long time = System.currentTimeMillis();	
		for (int i = 0; i < random_count; ++i) {
			if (OPTIMAL){
				c.randomise(gen, SCRAMBLE_LENGTH);
				sol_length = 0;
				sol = "";
				while ("".equals(sol)){
					System.out.println("length "+sol_length);
					sol = s.solve( c, sol_length++, true);
				}
				System.out.println(sol);
			}
			else {
				c.randomise(gen);
				System.out.println(s.solve( c, 45, true));
			}
		}
		System.out.println(System.currentTimeMillis() - time);
	}
}
