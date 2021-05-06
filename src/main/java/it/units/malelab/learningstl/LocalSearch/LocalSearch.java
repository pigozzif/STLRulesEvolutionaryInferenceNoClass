package it.units.malelab.learningstl.LocalSearch;

import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import it.units.malelab.learningstl.LocalSearch.gpOptimisation.GPOptimisation;
import it.units.malelab.learningstl.LocalSearch.gpOptimisation.GpoOptions;
import it.units.malelab.learningstl.LocalSearch.numeric.optimization.ObjectiveFunction;
import it.units.malelab.learningstl.LocalSearch.sampler.GridSampler;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.LocalSearch.sampler.Parameter;

import java.util.List;
import java.util.stream.IntStream;


public class LocalSearch {

    public static double[] optimize(AbstractTreeNode monitor, AbstractFitnessFunction<?> ff, int maxIterations) {
        double[] timeBounds = ff.getSignalBuilder().getTemporalBounds();
        List<String[]> variables = monitor.getVariables();
        int numVariables = variables.size();
        int numBounds = monitor.getNumBounds();
        double[] lb = new double[numBounds + numVariables];  // variables' lower bounds
        double[] ub = new double[numBounds + numVariables];  // variables' upper bounds
        // bounds for interval variables
        for (int i = 0; i < numBounds; ++i) {
            lb[i] = (i % 2 == 0) ? timeBounds[0] : 1;  // interval start
            ub[i] = timeBounds[1];  // interval end
        }
        // numerical variables are already normalized :)
        for (int j = 0; j < numVariables; ++j) {
            lb[j + numBounds] = 0.0;
            ub[j + numBounds] = 1.0;
        }
        ObjectiveFunction function = point -> {
            final double[] p = point;
            point = IntStream.range(0, point.length).mapToDouble(i -> lb[i] + p[i] * (ub[i] - lb[i])).toArray();
            return ff.getObjective().apply(monitor, point);
        };
        GPOptimisation gpo = createOptimizer(numBounds, maxIterations);
        double[] lbU = IntStream.range(0, lb.length).mapToDouble(i -> 0).toArray();
        double[] ubU = IntStream.range(0, ub.length).mapToDouble(i -> 1).toArray();
        double[] v = gpo.optimise(function, lbU, ubU).getSolution();
        return IntStream.range(0, v.length).mapToDouble(i -> lb[i] + v[i] * (ub[i] - lb[i])).toArray();
    }

    public static GPOptimisation createOptimizer(int numBounds, int maxIterations) {
        GridSampler custom = createSampler(numBounds);
        GPOptimisation gpo = new GPOptimisation();
        GpoOptions options = new GpoOptions();
        options.setInitialSampler(custom);
        options.setMaxIterations(maxIterations);
        options.setHyperparamOptimisation(true);
        options.setUseNoiseTermRatio(true);
        options.setNoiseTerm(0);
        options.setGridSampler(custom);
        options.setGridSampleNumber(200);
        gpo.setOptions(options);
        return gpo;
    }

    public static GridSampler createSampler(int numBounds) {
        return new GridSampler() {
            @Override
            public double[][] sample(int n, double[] lbounds, double[] ubounds) {
                double[][] res = new double[n][lbounds.length];
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < numBounds; j += 2) {
                        res[i][j] = lbounds[j] + (Math.random() * (ubounds[j] - lbounds[j]));
                        res[i][j + 1] = Math.max(Math.random(), 0.00000001) * (ubounds[j + 1] - res[i][j]);
                    }
                    for (int j = numBounds; j < res[i].length; j++) {
                        res[i][j] = lbounds[j] + Math.random() * (ubounds[j] - lbounds[j]);
                    }
                }
                return res;
            }

            @Override
            public double[][] sample(int n, Parameter[] params) {
                return new double[0][];
            }
        };
    }

}
