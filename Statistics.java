package fivestage444;

public final class Statistics{

	static int[][] nodeNumber = new int[6][30];
	static int[][] leafNumber = new int[6][30];

	Statistics(){
		int i, j;
		for(i=0; i<5; i++)
			for(j=0; j<5; j++){
				nodeNumber[i][j] = 0;
				leafNumber[i][j] = 0;
			}
	}

	public synchronized static final void addNode( int stage, int depth ){

		nodeNumber[stage][depth]++;

	}

	public synchronized static final void addLeaf( int stage, int depth ){

		leafNumber[stage][depth]++;

	}

	public static final void print(){

		int i, j;

		System.out.println( "Node number:" );
		for (i=1; i<=5; i++){
			System.out.println("Stage "+i);
			System.out.println("Depth\tNumber");

			for (j=0; j<30; j++)
				if( nodeNumber[i][j] > 0 )
					System.out.println(j + "\t" + nodeNumber[i][j]);
			System.out.println("");
		}

		System.out.println("");
		System.out.println( "Leaf number:" );
		for (i=i; i<=5; i++){
			System.out.println("Stage "+i);
			System.out.println("Depth\tNumber");

			for (j=0; j<30; j++)
				if( leafNumber[i][j] > 0 )
					System.out.println(j + "\t" + leafNumber[i][j]);

			System.out.println("");
		}
	}
}
