package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.UnsupervisedSignalBuilder;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.LocalSearch.LocalSearch;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class UnsupervisedFitnessFunction extends AbstractFitnessFunction<Signal<Map<String, Double>>[]> {

    private final List<Signal<Map<String, Double>>[]> signals;
    private final int numFragments;

    public UnsupervisedFitnessFunction(String path, boolean localSearch) throws IOException {
        super(localSearch);
        this.signalBuilder = new UnsupervisedSignalBuilder();
        this.signals = this.signalBuilder.parseSignals(path);
        this.numFragments = this.signals.stream().mapToInt(x -> x.length).sum();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        double count = 0.0;
            if (this.isLocalSearch) {
                double[] newParams = LocalSearch.optimize(monitor, this, 15);
                monitor.propagateParameters(newParams);
            }
        for (Signal<Map<String, Double>>[] l : this.signals) {
            for (Signal<Map<String, Double>> s : l) {
                    if (s.size() <= monitor.getNecessaryLength()) {
                        count += PENALTY_VALUE;
                    }
                    else {
                        count +=  Math.abs(monitor.getOperator().apply(s).monitor(s).valueAt(s.end()));
                    }
            }
        }
        return count / this.numFragments;
    }

    @Override
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            double count = 0.0;
            for (Signal<Map<String, Double>>[] l : this.signals) {
                for (Signal<Map<String, Double>> s : l) {
                    if (s.size() <= node.getNecessaryLength()) {
                        count += PENALTY_VALUE;
                    } else {
                        count += Math.abs(node.getOperator().apply(s).monitor(s).valueAt(s.end()));
                    }
                }
            }
            return count / this.numFragments;};
    }

}
