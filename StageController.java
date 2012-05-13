package fivestage444;

import static fivestage444.Constants.*;

public final class StageController{


	static public int[] currentStage;
	static public int[] currentBest;
	static final public int goalStage12 = 16;
	static final public int goalStage123 = 26;
	static final public int goalStage1234 = 37;
	static final public int goalStage12345 = 48;

	static public void init( int count ){
		currentStage = new int[count+1];
		currentBest = new int[count+1];
		for (int i=0; i<count+1; i++){
			currentStage[i] = 12;
			currentBest[i] = 142;
		}
	}

	static public void updateBest( int id, int newBest ){
		currentBest[id] = newBest;
	}

	static public void nextStage (int id){
		currentStage[id] += 11;
		currentBest[id] = 142;
	}
}
