package fivestage444;

public final class Main {

	public static void main(String[] args){

		int random_count = 100;

		for (int i = 0; i < random_count; ++i) {
			System.out.println( new Search().solve( Tools.randomCube(), 1000, true));
		}
	}
}
