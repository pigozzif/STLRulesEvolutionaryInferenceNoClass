package it.units.malelab.learningstl.TreeNodes;

import it.units.malelab.learningstl.BuildingBlocks.MonitorExpressions;
import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.Objects;


public class BinaryTemporalTreeNode extends TemporalTreeNode {

    public BinaryTemporalTreeNode(MonitorExpressions op, List<Tree<String>> siblings, String message, List<Tree<String>> ancestors) {
        super(siblings, message);
        this.firstChild = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
        this.secondChild = STLFormulaMapper.parseSubTree(siblings.get(3), ancestors);
        switch (op) {
            case SINCE -> this.func = x -> TemporalMonitor.sinceMonitor(this.firstChild.getOperator().apply(x),
                    this.createInterval(), this.secondChild.getOperator().apply(x),
                    new DoubleDomain());
            case UNTIL -> this.func = x -> TemporalMonitor.untilMonitor(this.firstChild.getOperator().apply(x),
                    this.createInterval(), this.secondChild.getOperator().apply(x),
                    new DoubleDomain());
        }
    }

    @Override
    public int getNecessaryLength() {
        return Math.max(this.firstChild.getNecessaryLength(), this.secondChild.getNecessaryLength()) + this.end;
    }

    @Override
    public void getVariablesAux(List<String[]> temp) {
        this.firstChild.getVariablesAux(temp);
        this.secondChild.getVariablesAux(temp);
    }

    @Override
    public int getNumBounds() {
        int ans = 0;
        ans += 2;
        ans += this.firstChild.getNumBounds();
        ans += this.secondChild.getNumBounds();
        return ans;
    }

    @Override
    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        if (idxs[1] >= parameters.length && idxs[0] >= this.getNumBounds()) {
            return idxs;
        }
        // first parameter is start, second is offset with respect to start (to get interval end)
        int start = (int) Math.round(parameters[idxs[0]]);
        int length = (int) Math.round(parameters[idxs[0] + 1]);
        this.setInterval(start, start + length);
        idxs[0] += 2;
        idxs = this.secondChild.propagateParametersAux(parameters, idxs);
        return this.firstChild.propagateParametersAux(parameters, idxs);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final AbstractTreeNode other = (AbstractTreeNode) o;
        if (!Objects.equals(this.firstChild, other.getFirstChild())) {
            return false;
        }
        return Objects.equals(this.secondChild, other.getSecondChild());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result += 31 * result + (this.firstChild == null ? 0 : this.firstChild.hashCode());
        result += 31 * result + (this.secondChild == null ? 0 : this.secondChild.hashCode());
        return result;
    }

}
