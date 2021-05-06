package it.units.malelab.learningstl.TreeNodes;

import it.units.malelab.learningstl.BuildingBlocks.MonitorExpressions;
import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.Objects;


public class UnaryTemporalTreeNode extends TemporalTreeNode {

    public UnaryTemporalTreeNode(MonitorExpressions op, List<Tree<String>> siblings, String message, List<Tree<String>> ancestors) {
        super(siblings, message);
        this.firstChild = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
        switch (op) {
            case ONCE -> this.func = x -> TemporalMonitor.onceMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain(), this.createInterval());
            case EVENTUALLY -> this.func = x -> TemporalMonitor.eventuallyMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain(), this.createInterval());
            case HISTORICALLY -> this.func = x -> TemporalMonitor.historicallyMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain(), this.createInterval());
            case GLOBALLY -> this.func = x -> TemporalMonitor.globallyMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain(), this.createInterval());
        }
    }

    @Override
    public int getNecessaryLength() {
        return this.firstChild.getNecessaryLength() + this.end;
    }

    @Override
    public void getVariablesAux(List<String[]> temp) {
        this.firstChild.getVariablesAux(temp);
    }

    @Override
    public int getNumBounds() {
        int ans = 0;
        ans += 2;
        ans += this.firstChild.getNumBounds();
        return ans;
    }

    @Override
    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        if (idxs[1] >= parameters.length && idxs[0] >= this.getNumBounds()) return idxs;
        // first parameter is start, second is offset with respect to start (to get interval end)
        int start = (int) parameters[idxs[0]];
        int length = (int) parameters[idxs[0] + 1];
        this.setInterval(start, start + length);
        idxs[0] += 2;
        return this.firstChild.propagateParametersAux(parameters, idxs);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        final AbstractTreeNode other = (AbstractTreeNode) o;
        return Objects.equals(this.firstChild, other.getFirstChild());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result += 31 * result + (this.firstChild == null ? 0 : this.firstChild.hashCode());
        return result;
    }

}
