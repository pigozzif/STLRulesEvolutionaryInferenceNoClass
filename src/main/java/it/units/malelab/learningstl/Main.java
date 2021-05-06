package it.units.malelab.learningstl;

import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.SupervisedFitnessFunction;
import it.units.malelab.learningstl.BuildingBlocks.ProblemClass;
import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.Worker;
import it.units.malelab.jgea.core.Individual;
import it.units.malelab.jgea.core.evolver.StandardWithEnforcedDiversityEvolver;
import it.units.malelab.jgea.core.evolver.stopcondition.Iterations;
import it.units.malelab.jgea.core.listener.Listener;
import it.units.malelab.jgea.core.listener.PrintStreamListener;
import it.units.malelab.jgea.core.listener.collector.*;
import it.units.malelab.jgea.core.operator.GeneticOperator;
import it.units.malelab.jgea.core.order.PartialComparator;
import it.units.malelab.jgea.core.selector.Tournament;
import it.units.malelab.jgea.core.selector.Worst;
import it.units.malelab.jgea.core.util.Args;
import it.units.malelab.jgea.core.util.Misc;
import it.units.malelab.jgea.representation.grammar.cfggp.GrammarBasedSubtreeMutation;
import it.units.malelab.jgea.representation.grammar.cfggp.GrammarRampedHalfAndHalf;
import it.units.malelab.jgea.representation.tree.SameRootSubtreeCrossover;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class Main extends Worker {

    private static int seed;
    private static PrintStream out;
    private static String grammarPath;
    private static String outputPath;
    private static String inputPath;
    private static boolean isLocalSearch;

    public static void main(String[] args) throws IOException {
        String errorMessage = "notFound";
        String random = Args.a(args, "seed", errorMessage);
        if (random.equals(errorMessage)) {
            throw new IllegalArgumentException("Random Seed not Valid");
        }
        seed = Integer.parseInt(random);
        grammarPath = Args.a(args, "grammar", null);
        outputPath = Args.a(args, "output", null) + seed + ".csv";
        out = new PrintStream(new FileOutputStream(outputPath, true), true);
        inputPath = Args.a(args, "input", null);
        isLocalSearch = Boolean.parseBoolean(Args.a(args, "local_search", null));
        new Main(args);
    }

    public Main(String[] args) {
        super(args);
    }

    @Override
    public void run() {
        try {
            evolution();
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void evolution() throws IOException, ExecutionException, InterruptedException {
        Random r = new Random(seed);
        SupervisedFitnessFunction f = new /*I80FitnessFunction();*/SupervisedFitnessFunction(inputPath, isLocalSearch, r);
        STLFormulaMapper m = new STLFormulaMapper();
        final ProblemClass<Signal<Map<String, Double>>> p = new ProblemClass<>(grammarPath, f, m);
        Map<GeneticOperator<Tree<String>>, Double> operators = new LinkedHashMap<>();
        operators.put(new GrammarBasedSubtreeMutation<>(12, p.getGrammar()), 0.2d);
        operators.put(new SameRootSubtreeCrossover<>(12), 0.8d);
        StandardWithEnforcedDiversityEvolver<Tree<String>, AbstractTreeNode, Double> evolver = new StandardWithEnforcedDiversityEvolver<>(
                    p.getSolutionMapper(),
                    new GrammarRampedHalfAndHalf<>(0, 12, p.getGrammar()),
                    PartialComparator.from(Double.class).comparing(Individual::getFitness),
                    500,
                    operators,
                    new Tournament(5),
                    new Worst(),
                 500,
                    true,
                100
        );
        Collection<AbstractTreeNode> solutions = evolver.solve(Misc.cached(p.getFitnessFunction(), 10), new Iterations(50),
                r, this.executorService, Listener.onExecutor(new PrintStreamListener<>(out, false, 10,
                        ",", ",",  new Basic(), new Population(), new Diversity(), new BestInfo("%5.3f")), this.executorService));
        AbstractTreeNode bestFormula = solutions.iterator().next();
        f.optimizeAndUpdateParams(bestFormula, 1);
        Files.write(Paths.get(outputPath), (bestFormula.toString() + "\n").getBytes(), StandardOpenOption.APPEND);
        this.postProcess(bestFormula, p.getFitnessFunction());
    }

    public void postProcess(AbstractTreeNode bestFormula, AbstractFitnessFunction<Signal<Map<String, Double>>> f) throws IOException {
        double result;
        double count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getPositiveTest()) {
            result = f.monitorSignal(signal, bestFormula, false);
            if (result <= 0.0) {
                ++count;
            }
        }
        Files.write(Paths.get(outputPath), ("Positive Test Misclassification Rate: " + count / f.getPositiveTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
        count = 0.0;
        for (Signal<Map<String, Double>> signal : f.getNegativeTest()) {
            result = f.monitorSignal(signal, bestFormula, true);
            if (result <= 0.0) {
                ++count;
            }
        }
        Files.write(Paths.get(outputPath), ("Negative Test Misclassification Rate: " + count / f.getNegativeTest().size() + "\n").getBytes(), StandardOpenOption.APPEND);
    }

}
