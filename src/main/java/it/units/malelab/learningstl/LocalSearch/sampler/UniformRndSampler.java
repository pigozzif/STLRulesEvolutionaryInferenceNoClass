package it.units.malelab.learningstl.LocalSearch.sampler;

import java.util.Random;
import java.util.stream.IntStream;

public class UniformRndSampler implements GridSampler {
	private Random rand ;

	public UniformRndSampler(int seed) {
		this.rand = new Random(seed);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public double[][] sample(int n, double[] lbounds, double[] ubounds) {
		final int dim = lbounds.length;
		final double[][] inputVals = new double[n][];
		for (int i = 0; i < n; i++) {
			inputVals[i] = new double[dim];
			for (int j = 0; j < dim; j++) {
				final double l = lbounds[j];
				final double u = ubounds[j];
				inputVals[i][j] = rand.nextDouble() * (u - l) + l;
			}
		}
		return inputVals;
	}

//	@Override
//	public double[][] sample(int n, Parameter[] params) {
//		final int dim = params.length;
//		final double[][] inputVals = new double[n][];
//		for (int i = 0; i < n; i++) {
//			inputVals[i] = new double[dim];
//			for (int j = 0; j < dim; j++) {
//				final double l = params[j].getLowerBound();
//				final double u = params[j].getUpperBound();
//				inputVals[i][j] = rand.nextDouble() * (u - l) + l;
//			}
//		}
//		return inputVals;
//	}

	@Override
	public double[][] sample(int n, Parameter[] params) {
		double[] lb = IntStream.range(0,params.length).mapToDouble(s -> params[s].getLowerBound()).toArray();
		double[] ub = IntStream.range(0,params.length).mapToDouble(s -> params[s].getUpperBound()).toArray();
		return sample(n,lb,ub);
	}

}
