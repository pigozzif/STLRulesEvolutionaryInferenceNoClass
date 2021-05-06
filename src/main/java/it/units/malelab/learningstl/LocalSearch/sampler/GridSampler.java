package it.units.malelab.learningstl.LocalSearch.sampler;


public interface GridSampler {

	double[][] sample(int n, double[] lbounds, double[] ubounds);
	double[][] sample(int n, Parameter[] params);

}
