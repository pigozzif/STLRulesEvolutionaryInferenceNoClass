package it.units.malelab.learningstl.TreeNodes;

import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.Objects;


public class NotTreeNode extends AbstractTreeNode {

    public NotTreeNode(List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        super();
        this.firstChild = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
        this.symbol = "NOT";
        this.func = x -> TemporalMonitor.notMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain());
    }

    @Override
    public int getNecessaryLength() {
        return this.firstChild.getNecessaryLength();
    }

    @Override
    public void getVariablesAux(List<String[]> temp) {
        this.firstChild.getVariablesAux(temp);
    }

    @Override
    public int getNumBounds() {
        return this.firstChild.getNumBounds();
    }

    @Override
    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        if (idxs[1] >= parameters.length && idxs[0] >= this.getNumBounds()) return idxs;
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
