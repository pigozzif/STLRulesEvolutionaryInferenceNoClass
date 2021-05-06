package it.units.malelab.learningstl.TreeNodes;

import it.units.malelab.learningstl.BuildingBlocks.CompareSign;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;


public class NumericTreeNode extends AbstractTreeNode {

    private final String var;
    private CompareSign cs;
    private Double number;

    public NumericTreeNode(List<Tree<String>> siblings) {
        super();
        this.var = siblings.get(0).child(0).content();
        String test = siblings.get(1).child(0).content();
        for (CompareSign c : CompareSign.values()) {
            if (c.toString().equals(test)) {
                this.cs = c;
                break;
            }
        }
        if (siblings.get(2).content().equals("const_num")) {
            this.number = null;
        }
        else {
            this.number = this.parseNumber(siblings.get(2).childStream().collect(Collectors.toList()));
        }
        this.func = x -> TemporalMonitor.atomicMonitor(y -> this.cs.getValue().apply(y.get(this.var), this.number));
    }

    private double parseNumber(List<Tree<String>> leaves) {
        double number = 0.0;
        for (int i=0; i < leaves.size(); ++i) {
            number += Double.parseDouble(leaves.get(i).child(0).content()) * Math.pow(10, - (i + 1));
        }
        return number;
    }

    @Override
    public int getNecessaryLength() {
        return 0;
    }

    public String getSymbol() {
        return this.var + " " + this.cs.toString() + " " + this.number;
    }

    @Override
    public void getVariablesAux(List<String[]> temp) {
        temp.add(new String[] {this.var, this.cs.toString(), "null"});
    }

    @Override
    public int getNumBounds() {
        return 0;
    }

    @Override
    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        if (idxs[1] >= parameters.length && idxs[0] >= this.getNumBounds()) return idxs;
        this.number = parameters[idxs[1]];
        idxs[1]++;
        return idxs;
    }

}
