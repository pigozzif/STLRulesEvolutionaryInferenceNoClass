package it.units.malelab.learningstl.LocalSearch.gpOptimisation.tranformations;

public class EmptyTransformer implements Transformer {

	@Override
	public double[] applyTransformation(double[] x) {
		return x;
	}

	@Override
	public double[] invertTransformation(double[] x) {
		return x;
	}

}
