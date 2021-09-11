package it.units.malelab.learningstl.TreeNodes;

import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;
import it.units.malelab.jgea.core.util.Sized;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


public abstract class AbstractTreeNode implements Sized {

    protected Function<Signal<Map<String, Double>>, TemporalMonitor<Map<String, Double>, Double>> func;
    protected AbstractTreeNode firstChild;
    protected AbstractTreeNode secondChild;
    protected String symbol;

    public Function<Signal<Map<String, Double>>, TemporalMonitor<Map<String, Double>, Double>> getOperator() {
        return this.func;
    }

    public AbstractTreeNode getSecondChild() {
        return this.secondChild;
    }

    public AbstractTreeNode getFirstChild() {
        return this.firstChild;
    }

    public abstract int getNecessaryLength();

    public String getSymbol() {
        return this.symbol;
    }

    public List<String[]> getVariables() {
        List<String[]> ans  = new ArrayList<>();
        this.getVariablesAux(ans);
        return ans;
    }

    public abstract void getVariablesAux(List<String[]> temp);

    public abstract int getNumBounds();

    public void propagateParameters(double[] parameters) {
        this.propagateParametersAux(parameters, new int[] {0, this.getNumBounds()});
    }

    public abstract int[] propagateParametersAux(double[] parameters, int[] idxs);

    @Override
    public int size() {
        int ans = 1;
        ans += (this.firstChild != null) ? this.firstChild.size() : 0;
        ans += (this.secondChild != null) ? this.secondChild.size() : 0;
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        else if (o.getClass() != this.getClass()) {
            return false;
        }
        final AbstractTreeNode other = (AbstractTreeNode) o;
        return Objects.equals(this.getSymbol(), other.getSymbol());
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + (this.symbol == null ? 0 : this.symbol.hashCode());
        return result;
    }

    @Override
    public String toString() {
        String children;
        if (this.firstChild == null && this.secondChild == null) {
            children = "";
        }
        else if (this.firstChild == null) {
            children = "[" + this.secondChild.toString() + "]";
        }
        else if (this.secondChild == null) {
            children = "[" + this.firstChild.toString() + "]";
        }
        else {
            children = "[" + this.firstChild.toString() + "," + this.secondChild.toString() + "]";
        }
        return this.getSymbol() + children;
    }

}
