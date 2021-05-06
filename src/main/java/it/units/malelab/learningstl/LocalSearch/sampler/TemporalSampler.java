package it.units.malelab.learningstl.LocalSearch.sampler;

public class TemporalSampler implements GridSampler {

    @Override
    public double[][] sample(int n, double[] lbounds, double[] ubounds) {
        double[][] res = new double[n][lbounds.length];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < lbounds.length; j += 2) {
                res[i][j] = lbounds[j] + Math.random() * (ubounds[j] - lbounds[j]);
                res[i][j + 1] = res[i][j] + (Math.random()) * (ubounds[j] - res[i][j]);
            }
            for (int j = lbounds.length; j < res[i].length; j++) {
                res[i][j] = lbounds[j] + Math.random() * (ubounds[j] - lbounds[j]);
            }
        }
        return res;
    }

    @Override
    public double[][] sample(int n, Parameter[] params) {
        return new double[0][];
    }
}
