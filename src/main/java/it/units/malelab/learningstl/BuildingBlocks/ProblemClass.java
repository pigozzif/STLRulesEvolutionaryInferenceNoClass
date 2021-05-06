package it.units.malelab.learningstl.BuildingBlocks;

import it.units.malelab.learningstl.BuildingBlocks.FitnessFunctions.AbstractFitnessFunction;
import it.units.malelab.learningstl.TreeNodes.AbstractTreeNode;
import it.units.malelab.jgea.representation.grammar.Grammar;
import it.units.malelab.jgea.representation.grammar.GrammarBasedProblem;
import it.units.malelab.jgea.representation.tree.Tree;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;


public class ProblemClass<T> implements GrammarBasedProblem<String, AbstractTreeNode, Double> {

    private final Grammar<String> grammar;
    private final Function<Tree<String>, AbstractTreeNode> solutionMapper;
    private final AbstractFitnessFunction<T> fitnessFunction;

    public ProblemClass(String grammarPath, AbstractFitnessFunction<T> fitness, STLFormulaMapper mapper) throws IOException {
        this.grammar = Grammar.fromFile(new File(grammarPath));
        this.fitnessFunction = fitness;
        this.solutionMapper = mapper;
    }

    @Override
    public Grammar<String> getGrammar() {
        return this.grammar;
    }

    @Override
    public Function<Tree<String>, AbstractTreeNode> getSolutionMapper() {
        return this.solutionMapper;
    }

    @Override
    public AbstractFitnessFunction<T> getFitnessFunction() {
        return this.fitnessFunction;
    }

}
