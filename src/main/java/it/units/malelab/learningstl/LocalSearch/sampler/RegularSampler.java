package it.units.malelab.learningstl.LocalSearch.sampler;

import java.util.stream.IntStream;

/**
 * Sample a regular grid, with N'>N points, where N' is the smallest number >=N
 * whose dim-root is an integer, where dim is the dimension of the parameter
 * space.
 */
public class RegularSampler implements GridSampler {

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	@Override
	public double[][] sample(int n, double[] lbounds, double[] ubounds) {
		if (lbounds.length != ubounds.length)
			throw new IllegalArgumentException(
					"lbounds.length != ubounds.length");
		for (int i = 0; i < ubounds.length; i++)
			if (lbounds[i] >= ubounds[i])
				throw new IllegalArgumentException("lbound >= ubound");

		final int dim = lbounds.length;
		final int pointsPerDim = (int) Math.ceil(Math.pow(n, 1d / dim));
		final int nPrime = (int) Math.pow(pointsPerDim, dim);

		final double[][] grid = new double[nPrime][dim];

		for (int d = dim - 1; d >= 0; d--) {
			final double[] lin = linspace(pointsPerDim, lbounds[d], ubounds[d]);

			final int repsRequired = (int) Math.pow(pointsPerDim, dim - d - 1);
			int j = 0;
			int reps = 0;
			for (int i = 0; i < nPrime; i++) {
				grid[i][d] = lin[j];
				if (++reps >= repsRequired) {
					reps = 0;
					if (++j >= pointsPerDim)
						j = 0;
				}
			}
		}
		return grid;
	}

//	@Override
//	public double[][] sample(int n, Parameter[] params) {
//		final int dim = params.length;
//		final int pointsPerDim = (int) Math.ceil(Math.pow(n, 1d / dim));
//		final int nPrime = (int) Math.pow(pointsPerDim, dim);
//
//		final double[][] grid = new double[nPrime][dim];
//
//		for (int d = dim - 1; d >= 0; d--) {
//			final double[] lin = linspace(pointsPerDim,
//					params[d].getLowerBound(), params[d].getUpperBound());
//
//			final int repsRequired = (int) Math.pow(pointsPerDim, dim - d - 1);
//			int j = 0;
//			int reps = 0;
//			for (int i = 0; i < nPrime; i++) {
//				grid[i][d] = lin[j];
//				if (++reps >= repsRequired) {
//					reps = 0;
//					if (++j >= pointsPerDim)
//						j = 0;
//				}
//			}
//		}
//		return grid;
//	}

    @Override
    public double[][] sample(int n, Parameter[] params) {
        double[] lb = IntStream.range(0,params.length).mapToDouble(s -> params[s].getLowerBound()).toArray();
        double[] ub = IntStream.range(0,params.length).mapToDouble(s -> params[s].getUpperBound()).toArray();
        return sample(n,lb,ub);
    }

	private static double[] linspace(final int n, final double a, final double b) {
		final double step = (b - a) / (n - 1);
		double current = a;
		double[] x = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = current;
			current += step;
		}
		return x;
	}

}