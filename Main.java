package cg.fivestage444;
import java.util.Random;

public final class Main {

	public static void main(String[] args){

		int random_count = 30;
		CubeState c = new CubeState();
		Search s = new Search();
		Random gen;
		for (int max2=6; max2<7; max2++)
		for (int min3=7; min3<8; min3++)
		for (int max3=9; max3<10; max3++)
		for (int min4=7; min4<8; min4++)
		for (int max4=11; max4<12; max4++)
		for (int min5=7; min5<8; min5++){
			gen = new Random(100);
			Search.MAX_STAGE2 = max2;
			Search.MIN_STAGE3 = min3;
			Search.MAX_STAGE3 = max3;
			Search.MIN_STAGE4 = min4;
			Search.MAX_STAGE4 = max4;
			Search.MIN_STAGE5 = min5;
			System.out.print(max2+"\t"+min3+"\t"+max3+"\t"+min4+"\t"+max4+"\t"+min5+"\t");
			long time = System.currentTimeMillis();	
			for (int i = 0; i < random_count; ++i) {
				c = Tools.randomCube(gen);
				s.solve( c, 1000, true);
			}
			System.out.println(System.currentTimeMillis() - time);
		}
	}
}
