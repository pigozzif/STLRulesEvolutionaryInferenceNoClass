package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SignalBuilder;
import eu.quanticol.moonlight.signal.Signal;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


public abstract class AbstractFitnessFunction<T> implements Function<AbstractTreeNode, Double> {

    public final static double PENALTY_VALUE = 1.0;
    protected SignalBuilder<T> signalBuilder;
    protected final boolean isLocalSearch;

    public AbstractFitnessFunction(boolean localSearch) {
        this.isLocalSearch = localSearch;
    }

    public SignalBuilder<T> getSignalBuilder() {return this.signalBuilder;}

    public abstract BiFunction<AbstractTreeNode, double[], Double> getObjective();

    public List<T> getPositiveTraining() {
        return null;
    }

    public List<T> getNegativeTraining() {
        return null;
    }

    public List<T> getPositiveTest() {
        return null;
    }

    public List<T> getNegativeTest() {
        return null;
    }

    public double monitorSignal(Signal<Map<String, Double>> signal, AbstractTreeNode solution, boolean isNegative) {
        if (signal.size() <= solution.getNecessaryLength()) {
            return - PENALTY_VALUE;
        }
        double temp = solution.getOperator().apply(signal).monitor(signal).valueAt(signal.start());
        return (isNegative) ? - temp : temp;
    }

}
