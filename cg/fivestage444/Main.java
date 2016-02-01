package cg.fivestage444;

import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {
	private static final Level DEFAULT_LOG_LEVEL = Level.WARNING;

	public static void main(String[] args){
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setLevel(DEFAULT_LOG_LEVEL);
		for(Handler h : rootLogger.getHandlers()) {
			h.setLevel(DEFAULT_LOG_LEVEL);
		}
		int random_count = 1;
		Tools.init();
		CubeState c = new CubeState();
		SolverOptimal s = new SolverOptimal();
		Random gen = new Random(43);
		long time = System.currentTimeMillis();
		for (int i = 0; i < random_count; ++i) {
			//c.randomise(gen);
			c.scramble("F2 U r2 U2 F2 2R2 F2 U2 r2 U' F2");
			System.out.println(s.solve( c, true));
		}
		System.out.println(System.currentTimeMillis() - time);
	}
}
