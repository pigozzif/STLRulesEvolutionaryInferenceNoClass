package it.units.malelab.learningstl.LocalSearch.gaussianProcesses.kernels;

import it.units.malelab.learningstl.LocalSearch.gaussianProcesses.GpDataset;

public interface KernelFunction {

	double calculate(final double[] x1, final double[] x2);

	double calculateDerivative(final double[] x1,
							   final double[] x2, final int derivativeI);

	double calculateSecondDerivative(final double[] x1,
									 final double[] x2, final int derivativeI, final int derivativeJ);

	double calculateHyperparamDerivative(final double[] x1,
										 final double[] x2, final int hyperparamIndex);

	double[] getHypeparameters();

	double[] getDefaultHyperarameters(GpDataset data);

	void setHyperarameters(double[] hyp);

}
