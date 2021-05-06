package it.units.malelab.learningstl.LocalSearch.gpOptimisation;

import it.units.malelab.learningstl.LocalSearch.numeric.optimization.ObjectiveFunction;

public interface NoisyObjectiveFunction extends ObjectiveFunction {
	
	double getVarianceAt(double... point);

}
