package it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions;

import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.learningstl.BuildingBlocks.SignalBuilders.SupervisedSignalBuilder;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.learningstl.LocalSearch.LocalSearch;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;


public class SupervisedFitnessFunction extends AbstractFitnessFunction<Signal<Map<String, Double>>> {

    private final List<Signal<Map<String, Double>>> positiveTraining = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> positiveTest = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> negativeTraining = new ArrayList<>();
    private final List<Signal<Map<String, Double>>> negativeTest = new ArrayList<>();
    private final BiFunction<double[], double[], Double> function = (x, y) -> (x[0] - y[0]) / (Math.abs(x[1] + y[1]));
    private final double[] labels;
    private final long numPositive;
    private final long numNegative;
    private int fitnessEval = 0;

    public SupervisedFitnessFunction(String path, boolean localSearch, Random random) throws IOException {
        super(localSearch);
        this.signalBuilder = new SupervisedSignalBuilder();
        List<Signal<Map<String, Double>>> signals = this.signalBuilder.parseSignals(path);
        this.labels = this.signalBuilder.readVectorFromFile(path + "/labels.csv");
        this.splitSignals(signals, 0.8, random);
        this.numPositive = Arrays.stream(this.labels).filter(x -> x > 0).count();
        this.numNegative = Arrays.stream(this.labels).filter(x -> x < 0).count();
    }

    @Override
    public Double apply(AbstractTreeNode monitor) {
        ++this.fitnessEval;
        if (this.isLocalSearch || this.fitnessEval > 25000) {
            this.optimizeAndUpdateParams(monitor, 1);
        }
        double[] positiveResult = this.computeRobustness(monitor, this.positiveTraining, this.numPositive, false);
        double[] negativeResult = this.computeRobustness(monitor, this.negativeTraining, this.numNegative, false);
        return - this.function.apply(positiveResult, negativeResult);
    }

    public double[] computeRobustness(AbstractTreeNode monitor, List<Signal<Map<String, Double>>> data, long num, boolean isNegative) {
        double[] result = new double[3];
        double robustness;
        for (Signal<Map<String, Double>> signal : data) {
            robustness = this.monitorSignal(signal, monitor, isNegative);
            result[0] += robustness;
            result[1] += robustness * robustness;
        }
        result[1] = this.standardDeviation(result[1], result[0], num);
        result[0] /= num;
        return result;
    }
    // TODO: they use plain old variance
    private double standardDeviation(double partialSumSquared, double partialSum, long num) {
        double mean = partialSum / (num - 1);
        return Math.sqrt((partialSumSquared / (num - 1)) - (mean * mean));
    }

    private void splitSignals(List<Signal<Map<String, Double>>> signals, double fold, Random random) {
        List<Integer> positiveIndexes = new ArrayList<>();
        List<Integer> negativeIndexes = new ArrayList<>();
        for (int i=0; i < this.labels.length; ++i) {
            if (this.labels[i] < 0) {
                negativeIndexes.add(i);
            } else {
                positiveIndexes.add(i);
            }
        }
        Collections.shuffle(positiveIndexes, random);
        Collections.shuffle(negativeIndexes, random);
        int posFold = (int) (positiveIndexes.size() * fold);
        int negFold = (int) (negativeIndexes.size() * fold);
        for (int i=0; i < posFold; ++i) {
            this.positiveTraining.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=posFold; i < positiveIndexes.size(); ++i) {
            this.positiveTest.add(signals.get(positiveIndexes.get(i)));
        }
        for (int i=0; i < negFold; ++i) {
            this.negativeTraining.add(signals.get(negativeIndexes.get(i)));
        }
        for (int i=negFold; i < negativeIndexes.size(); ++i) {
            this.negativeTest.add(signals.get(negativeIndexes.get(i)));
        }
    }

    public void optimizeAndUpdateParams(AbstractTreeNode monitor, int maxIterations) {
        double[] newParams = LocalSearch.optimize(monitor, this, maxIterations);
        monitor.propagateParameters(newParams);
        double[] p1u1 = this.computeRobustness(monitor, this.getPositiveTraining(), numPositive, false);
        double[] p2u2 = this.computeRobustness(monitor, this.getNegativeTraining(), numNegative, false);
        double value;
        if (p1u1[0] > p2u2[0]) {
            value = ((p1u1[0] - p1u1[1]) + (p2u2[0] + p2u2[1])) / 2;
        } else {
            value = ((p2u2[0] - p2u2[1]) + (p1u1[0] + p1u1[1])) / 2;
        }
        int numBounds = monitor.getNumBounds();
        List<String[]> variables = monitor.getVariables();
        for (int i = numBounds; i < newParams.length; i++) {
            if (variables.get(i - numBounds)[1].equals(">")) {
                newParams[i] = Math.max(newParams[i] + value, 0);
            } else {
                newParams[i] = Math.max(newParams[i] - value, 0);
            }
        }
        monitor.propagateParameters(newParams);
    }

    @Override
    public BiFunction<AbstractTreeNode, double[], Double> getObjective() {
        return (AbstractTreeNode node, double[] params) -> {node.propagateParameters(params);
            double[] value1 = this.computeRobustness(node, this.positiveTraining, this.numPositive, false);
            double[] value2 = this.computeRobustness(node, this.negativeTraining, this.numNegative, false);
            return this.function.apply(value1, value2);};
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTraining() {
        return this.positiveTraining;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTraining() {
        return this.negativeTraining;
    }

    @Override
    public List<Signal<Map<String, Double>>> getPositiveTest() {
        return this.positiveTest;
    }

    @Override
    public List<Signal<Map<String, Double>>> getNegativeTest() {
        return this.negativeTest;
    }

}
