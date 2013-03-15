package cg.fivestage444.Stages;

import cg.fivestage444.Coordinates.SymCoordState;
import cg.fivestage444.CubeState;

abstract public class Stage {

	public long STAGE_SIZE; /* Size of the stage. */
	public SymCoordState symState;

	/**
	 * Convert a CubeState into the stage coordinates.
	 * @param cube cube to convert from.
	 */
	abstract public void pack(CubeState cube);

	/**
	 * Check if the state is solved.
	 * @return if solved.
	 */
	abstract public boolean isSolved();

	/**
	 * We apply a move on this stage, and storing the result on another stage.
	 * @param m the move to apply. It belongs to the own stage domain (0..this.getMovesNumber())
	 * @param s the stage to store the result of the move.
	 */
	abstract public void moveTo( int m, Stage s );

	/**
	 * We want to know given a certain state a lower bound of the minimum number of moves to solved that state.
	 * @return a lower bound of the distance to the closest solved state.
	 */
	abstract public int pruning();

	/**
	 * Returns what are the moves allowed in this stage.
	 * Because the moves of the different stages are all included in each other (building a chain),
	 * the move array Moves.stage2moves is structured so that the set of moves in a particular stage is always of the
	 * form 0..N, so that this function only have to return the integer N.
	 * @return how many moves are allowed in this stage.
	 */
	abstract public int getMovesNumber();

	/**
	 * Returns an integer representing the stage state.
	 * The state is symmetry-reduced using the symmetry in the SymState.
	 * @return the integer representing the state.
	 */
	abstract public int getId();

	/**
	 * Returns an integer representing the stage state.
	 * The state is symmetry-reduced using the given symmetry identifier.
	 * It is used when the SymState present some symmetries.
	 * @return the integer representing the state.
	 */
	abstract public int getId(int sym);

	/**
	 * Set the state corresponding to the given id.
	 * @param id integer representing the state.
	 */
	abstract public void setId(int id);

	/**
	 * Compute the canonical form of the state.
	 */
	abstract public void normalize();

	public Stage newOne(){
		Stage s = null;
		try {
			s = this.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IllegalAccessException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return s;
	}
}
