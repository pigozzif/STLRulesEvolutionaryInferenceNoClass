package it.units.malelab.learningstl.LocalSearch.numeric.algebra;

public class NonPosDefMatrixException extends Exception {

	private static final long serialVersionUID = -3169783459808245883L;

	public NonPosDefMatrixException() {
		super("Non positive definite matrix detected!");
	}

}
