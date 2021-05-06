package it.units.malelab.learningstl.LocalSearch.numeric.rsm;

/**
 * Created by ssilvetti on 05/01/17.
 */
public class KernelRBF implements Kernel {

    private double[] hyp;
    private double amplitude2;
    private double invLengthscale2;


    public KernelRBF() {
    }

    @Override
    public void setHyp(double[] hyp) {
        this.hyp = hyp;
        amplitude2 = hyp[0] * hyp[0];
        invLengthscale2 = 1. / (hyp[1] * hyp[1]);

    }

    @Override
    public double[] getHyp() {
        return hyp;
    }

    @Override
    public double dot(double[] x, double[] y) {
        final int n = x.length;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            final double v = x[i] - y[i];
            sum += v * v;
        }
        return amplitude2 * Math.exp(-0.5 * sum * invLengthscale2);
    }

    @Override
    public double dotEuclideanDistance(double distance) {
        return amplitude2 * Math.exp(-0.5 * distance * invLengthscale2);
    }

    @Override
    public double dotDistances(double[] distances) {
        double distance = 0;
        for (final double v : distances) {
            distance += v * v;
        }
        return distance;
    }
}