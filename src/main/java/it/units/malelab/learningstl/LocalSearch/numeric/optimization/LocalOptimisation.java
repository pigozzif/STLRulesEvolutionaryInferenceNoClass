package it.units.malelab.learningstl.LocalSearch.numeric.optimization;


public abstract class LocalOptimisation {

	abstract public PointValue optimise(ObjectiveFunction func,
			double[] init);

}
