package it.units.malelab.learningstl.LocalSearch.gpOptimisation;

import it.units.malelab.learningstl.LocalSearch.numeric.algebra.NonPosDefMatrixException;
import it.units.malelab.learningstl.LocalSearch.numeric.optimization.DifferentiableObjective;
import it.units.malelab.learningstl.LocalSearch.gaussianProcesses.AbstractGP;
import it.units.malelab.learningstl.LocalSearch.gaussianProcesses.GpDataset;
import it.units.malelab.learningstl.LocalSearch.gaussianProcesses.regression.RegressionPosterior;

public class GPPosteriorQuantileFitness implements DifferentiableObjective {

	private final AbstractGP<RegressionPosterior> gp;
	private final double beta;
	private final GpDataset testDatapoint;

	public GPPosteriorQuantileFitness(AbstractGP<RegressionPosterior> gp,
			double beta) {
		this.gp = gp;
		this.beta = beta;
		testDatapoint = new GpDataset(gp.getTrainingSet().getDimension(), 1);
	}

	@Override
	public double getValueAt(double... point) {
		testDatapoint.set(point);
		double value = Double.MIN_VALUE;
		try {
			value = gp.getGpPosterior(testDatapoint).getUpperBound(beta)[0];
		} catch (NonPosDefMatrixException e) {
			System.out.println(e.getClass().getName());
		}
		return value;
	}

	@Override
	public double[] getGradientAt(double... point) {
		final double[] grad = new double[point.length];
		for (int i = 0; i < grad.length; i++) {

			// derivative of mean:
			// dmu/dx = d(kx)/dx * invC_y

			// derivative of variance:
			// dV/dx = dk(x,x)/dx - d(kx' * invC * kx)/dx
			// dV/dx = 0 - d(kx' * invC * kx)/dx
			// dV/dx = - ( d(kx)/dx * (invC * kx) + (kx' * invC) * d(kx)/dx )
			// dV/dx = - d(kx)/dx * invC * kx - kx' * invC * d(kx)/dx
		}
		throw new IllegalStateException("Not supported yet!");
	}

}
