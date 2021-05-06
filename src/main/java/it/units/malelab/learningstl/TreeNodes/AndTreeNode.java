package it.units.malelab.learningstl.TreeNodes;

import it.units.malelab.learningstl.BuildingBlocks.STLFormulaMapper;
import eu.quanticol.moonlight.formula.DoubleDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.List;
import java.util.Objects;


public class AndTreeNode extends AbstractTreeNode {

    public AndTreeNode(List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        super();
        this.firstChild = STLFormulaMapper.parseSubTree(siblings.get(0), ancestors);
        this.secondChild = STLFormulaMapper.parseSubTree(siblings.get(1), ancestors);
        this.symbol = "AND";
        this.func = x -> TemporalMonitor.andMonitor(this.firstChild.getOperator().apply(x), new DoubleDomain(),
                this.secondChild.getOperator().apply(x));
    }

    @Override
    public int getNecessaryLength() {
        return Math.max(this.firstChild.getNecessaryLength(), this.secondChild.getNecessaryLength());
    }

    @Override
    public void getVariablesAux(List<String[]> temp) {
        this.firstChild.getVariablesAux(temp);
        this.secondChild.getVariablesAux(temp);
    }

    @Override
    public int getNumBounds() {
        return this.firstChild.getNumBounds() + this.secondChild.getNumBounds();
    }

    @Override
    public int[] propagateParametersAux(double[] parameters, int[] idxs) {
        idxs = this.firstChild.propagateParametersAux(parameters, idxs);
        idxs = this.secondChild.propagateParametersAux(parameters, idxs);
        return idxs;
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
