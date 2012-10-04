package cg.fivestage444;

import cg.fivestage444.Stages.Stage1;
import cg.fivestage444.Stages.Stage2;
import cg.fivestage444.Stages.Stage3;
import cg.fivestage444.Stages.Stage4;
import cg.fivestage444.Stages.Stage5;

class Tools {

	public static synchronized void init() {
		Symmetry.init();
		Util.init();
		CubePack.init();
		Stage1.init();
		Stage2.init();
		Stage3.init();
		Stage4.init();
		Stage5.init();
	}
}
