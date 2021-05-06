package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;
import it.units.malelab.learningstl.LocalSearch.LocalSearch;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AnomalyDetectionFitnessFunction extends AbstractFitnessFunction<Signal<Map<String, Double>>> {

    private final double alpha;
    private final List<Signal<Map<String, Double>>> positive;
    private final List<Signal<Map<String, Double>>> negative;
    private final double[] labels;

    public AnomalyDetectionFitnessFunction(double a, String path, boolean localSearch) throws IOException {
        super(localSearch);
        this.alpha = a;
        this.signalBuilder = new SupervisedSignalBuilder();
        List<Signal<Map<String, Double>>> signals = this.signalBuilder.parseSignals(path);
        this.labels = this.signalBuilder.readVectorFromFile(path + "/labels.csv");
        this.positive = IntStream.range(0, signals.size()).filter(x -> this.labels[x] > 0).mapToObj(signals::get).collect(Collectors.toList());
        this.negative = IntStream.range(0, signals.size()).filter(x -> this.labels[x] < 0).mapToObj(signals::get).collect(Collectors.toList());
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        if (this.isLocalSearch) {
            double[] newParams = LocalSearch.optimize(monitor, this, 1);
            monitor.propagateParameters(newParams);
        }
        return - this.computeFitness(monitor);
    }

    private double computeFitness(AbstractTreeNode monitor) {
        double robustness = 0.0;
        int average = 0;
        for (Signal<Map<String, Double>> s : this.positive) {
            double result = this.monitorSignal(s, monitor, false);
            if (result <= 0.0) {
                ++average;
            }
            robustness += Math.abs(result);
        }
        average /= this.positive.size();
        robustness /= this.positive.size();
        return this.alpha * average + robustness;
    }

    @Override
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            return this.computeFitness(node);};
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTest() {
        return this.positive;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTest() {
        return this.negative;
    }

}
