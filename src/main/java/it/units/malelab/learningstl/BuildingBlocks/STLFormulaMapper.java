package it.units.malelab.learningstl.BuildingBlocks;

import it.units.malelab.learningstl.TreeNodes.*;
import it.units.malelab.jgea.representation.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class STLFormulaMapper implements Function<Tree<String>, AbstractTreeNode> {

    @Override
    public AbstractTreeNode apply(Tree<String> root) {
        return parseSubTree(root, new ArrayList<>() {{ add(null); }});
    }

    public static AbstractTreeNode parseSubTree(Tree<String> currentNode, List<Tree<String>> ancestors) {
        List<Tree<String>> children = currentNode.childStream().collect(Collectors.toList());
        Tree<String> testChild = children.get(0);
        for (MonitorExpressions op : MonitorExpressions.values()) {
            if (op.toString().equals(testChild.content())) {
                ancestors.add(currentNode);
                return createMonitor(op, getSiblings(testChild, ancestors), ancestors);
            }
        }
        ancestors.add(currentNode);
        return parseSubTree(testChild, ancestors);
    }

    private static List<Tree<String>> getSiblings(Tree<String> node, List<Tree<String>> ancestors) {
        Tree<String> parent = ancestors.get(ancestors.size() - 1);
        if (parent == null) {
            return Collections.emptyList();
        }
        List<Tree<String>> res = parent.childStream().collect(Collectors.toList());
        res.remove(node);
        if (res.isEmpty()) {
            return getSiblings(parent, ancestors.stream().filter(x -> x != parent).collect(Collectors.toList()));
        }
        return res;
    }

    private static AbstractTreeNode createMonitor(MonitorExpressions op, List<Tree<String>> siblings, List<Tree<String>> ancestors) {
        return switch (op) {
            case PROP -> new NumericTreeNode(siblings);
            case NOT -> new NotTreeNode(siblings, ancestors);
            case AND -> new AndTreeNode(siblings, ancestors);
            case UNTIL, SINCE -> new BinaryTemporalTreeNode(op, siblings, op.toString(), ancestors);
            default -> new UnaryTemporalTreeNode(op, siblings, op.toString(), ancestors);
        };
    }

}
