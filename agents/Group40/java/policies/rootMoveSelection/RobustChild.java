package javaV.policies.rootMoveSelection;

import javaV.MCTSNode;
import javaV.common.Move;

public class RobustChild {

    public static MCTSNode getBestChild(MCTSNode root) {
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : root.children) {
            double value = calculateNodeValue(child);
            if (value > bestValue) {
                bestChild = child;
                bestValue = value;
            }
        }
        return bestChild;
    }

    private static double calculateNodeValue(MCTSNode node) {
        double beta = calculateBeta(node);
        double raveValue = node.raveN > 0 ? (double) node.raveQ / node.raveN : 0;
        double traditionalValue = (double) node.N; 

        return beta * raveValue + (1 - beta) * traditionalValue;
    }

    private static double calculateBeta(MCTSNode node) {
        double k = 1.0; // need tunning
        return node.N > 0 ? k / (node.N + k + node.N * k) : 0;
    }
}
