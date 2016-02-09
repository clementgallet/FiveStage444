package cg.fivestage444;

public class Worker implements Runnable {
	public CubeAndSolution cas;
	public Strategy st;
	public void run(){
		st.processSolution(cas);
	}
}
