package fivestage444;

import static fivestage444.Constants.*;

public final class StageController{


	static public int currentStage = 12;
	static public int currentBest = 142;
	static final public int goalStage12 = 15;
	static final public int goalStage123 = 25;
	static final public int goalStage1234 = 35;
	static final public int goalStage12345 = 45;

	static public void updateBest( int newBest ){
		currentBest = newBest;
	}

	static public void nextStage (){
		currentStage += 11;
		currentBest = 142;
	}
}
