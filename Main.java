package cg.fivestage444;
import java.util.Random;

public final class Main {

	public static void main(String[] args){

		int random_count = 10;
		Tools.init();
		CubeState c = new CubeState();
		Search s = new Search();
		Random gen = new Random(43);
		long time = System.currentTimeMillis();
		for (int i = 0; i < random_count; ++i) {
			c.randomise(gen);
			System.out.println(s.solve( c, true));
		}
		System.out.println(System.currentTimeMillis() - time);
	}
}
