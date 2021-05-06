package it.units.malelab.learningstl.LocalSearch.numeric.optimization.methods;

import it.units.malelab.learningstl.LocalSearch.numeric.optimization.ObjectiveFunction;
import it.units.malelab.learningstl.LocalSearch.numeric.optimization.PointValue;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateFunctionMappingAdapter;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer;

/**
 * Created by ssilvetti on 04/01/17.
 */
public class PowellMethodApacheConstrained {


    public PointValue optimise(final ObjectiveFunction func, double[] init, double[] lb, double[] ub) {

        // Just keep track of the best solution manually
        // To be used as last resort if the Powell method fails
        final double[] bestSoFar = new double[init.length];
        final double[] bestValueSoFar = { -Double.MAX_VALUE };
        System.arraycopy(init, 0, bestSoFar, 0, init.length);

        final MultivariateFunction f = point -> {
            final double value = func.getValueAt(point);
            if (value > bestValueSoFar[0]) {
                bestValueSoFar[0] = value;
                System.arraycopy(point, 0, bestSoFar, 0, point.length);
            }
            return value;
        };

        MultivariateFunctionMappingAdapter ff = new MultivariateFunctionMappingAdapter(f,lb,ub);
        MultivariateOptimizer optim = new PowellOptimizer(1e-1, 1e-1);
        final OptimizationData[] optimData = new OptimizationData[3];
        optimData[0] = new org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction(
                ff);
        optimData[1] = new MaxEval(500);

        optimData[2] = new InitialGuess(init);

        final PointValuePair pair;
        double[] optimum;
        double value;

        try {
            pair = optim.optimize(optimData);
            optimum = pair.getPoint();
            value = pair.getValue();
        } catch (TooManyEvaluationsException e) {
            // last resort if the Powell method fails
            optimum = bestSoFar;
            value = bestValueSoFar[0];
        }

        return new PointValue(ff.unboundedToBounded(optimum), value);
    }
}
