package javaV.policies.rootMoveSelection;

import javaV.MCTSAgent;
import javaV.MCTSNode;
import javaV.common.Move;

public class SecureChild {
    public static MCTSNode getBestChild(MCTSNode root) {
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (final MCTSNode child : root.children) {
            double value = calculateNodeValue(child, root);
            if (value > bestValue) {
                bestChild = child;
                bestValue = value;
            }
        }
        return bestChild;
    }

    private static double calculateNodeValue(MCTSNode node, MCTSNode parent) {
        double traditionalUCT = (double) node.Q / node.N - (MCTSAgent.C * Math.sqrt((Math.log(parent.N) / node.N)));
        double raveValue = node.raveN > 0 ? (double) node.raveQ / node.raveN : 0;

        double beta = calculateBeta(node);
        return beta * raveValue + (1 - beta) * traditionalUCT;
    }

    private static double calculateBeta(MCTSNode node) {
        double k = 1.0; // tunning
        return node.N > 0 ? k / (node.N + k + node.N * k) : 0;
    }
}