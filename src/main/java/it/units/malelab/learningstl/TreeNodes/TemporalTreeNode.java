package it.units.malelab.learningstl.TreeNodes;

import eu.quanticol.moonlight.formula.Interval;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.stream.Collectors;


public abstract class TemporalTreeNode extends AbstractTreeNode {

    protected int start;
    protected int end;

    public TemporalTreeNode(List<Tree<String>> siblings, String message) {
        super();
        this.equipTemporalOperator(siblings, message);
    }

    public Interval createInterval() {
        return new Interval(this.start, this.end);
    }

    public void setInterval(int s, int e) {
        this.start = s;
        this.end = e;
    }

    public String getSymbol() {
        return super.getSymbol() + " I=[" + this.start + ", " + this.end + "]";
    }

    private void equipTemporalOperator(List<Tree<String>> siblings, String message) {
        int start;
        int end;
        if (!siblings.get(1).content().equals("const_num")) {
            start = (int) this.parseIntervalBound(siblings.get(1).childStream().collect(Collectors.toList()));
            end = start + (int) Math.max(1.0, this.parseIntervalBound(siblings.get(2).childStream().collect(Collectors.toList())));
        }
        else {
            start = 0;
            end = 0;
        }
        this.setInterval(start, end);
        this.symbol = message.toUpperCase().replace(".", "");
    }

    private double parseIntervalBound(List<Tree<String>> leaves) {
        int k = leaves.size() - 1;
        double value = 0.0;
        for (Tree<String> leaf : leaves) {
            value += Double.parseDouble(leaf.child(0).content()) * Math.pow(10, k--);
        }
        return value;
    }

}
