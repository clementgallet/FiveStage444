package fivestage444;

public final class Timer {
	long t;
	// constructor
	public Timer() {
		reset();
	}
	// reset timer
	public void reset() {
		t = System.currentTimeMillis();
	}
	// return elapsed time
	public long elapsed() {
		return System.currentTimeMillis() - t;
	}
	// print explanatory string and elapsed time
	public void print(String s) {
		System.out.println(s + ": " + elapsed());
	}
}
