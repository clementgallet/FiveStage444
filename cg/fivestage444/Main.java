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
		//SolverSlack s = new SolverSlack();
		Random gen = new Random(43);
		long time = System.currentTimeMillis();
		for (int i = 0; i < random_count; ++i) {
			//c.randomise(gen);
			//c.scramble("2R2 U2 2R2 u2 2R2 2U2");
			c.scramble("F2 U r2 U2 F2 2R2 F2 U2 r2 U' F2");
			//c.scramble("2R2 u2 2R2 2B2 U' 2R2 2B2 U 2B2 u2 2R2"); //O
			//c.scramble("R2 u2 B2 R2 u2 B2 R2 U R2 B2 R2 U B2 u2"); //W
			//c.scramble("R2 D' 2R2 F2 2R2 f2 2R2 f2 U F2 R2 U R2 U' R2 D"); //B
			//c.scramble("F2 L' U' L F2 R' D R' D' r2 U2 2R2 u2 2R2 2U2"); //P
			//c.scramble("F2 U r2 U' B2 U' F2 U B2 U' 2R2 F2 U2 r2 U' F2"); //C
			//c.scramble("r2 U' F2 R' D R' D' R U' R' U R U' 2R2 U2 F2 r2"); //M
			//c.scramble("r2 U' F2 U B2 U' F2 U' 2R2 U2 B2 r2"); //K
			//c.scramble("B2 L U L' B2 R D' 2R2 F2 2R2 f2 2R2 2F2 R D R2"); //17
			//c.scramble("F U F' R2 F U' F' U' R2 U R2 U r2 U2 2R2 u2 2R2 2U2"); //S
			//c.scramble("F U F' R2 F U' F' U' R2 U 2F2 R2 2F2 r2 2F2 r2 U R2"); //Q
			//c.scramble("d2 l2 U' B' U B U2 F U' F' U  F' L2 F l2 U2 l2 d2"); //X
			//c.scramble("r2 2F2 U2 f2 D r2 U2 f2 U' f2 L2 U2 B2 l2"); //22
			//c.scramble("");
			System.out.println(s.solve( c, true));
		}
		System.out.println(System.currentTimeMillis() - time);
	}
}
