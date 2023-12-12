package javaV.policies.selection;

import javaV.MCTSAgent;
import javaV.MCTSNode;

public class UCT {

    public static double calculateValue(MCTSNode node, MCTSNode parent) {
        double traditionalUCT = (double) node.Q / node.N + MCTSAgent.C * Math.sqrt(Math.log(parent.N) / node.N);
        double raveValue = node.raveN > 0 ? (double) node.raveQ / node.raveN : 0;

        double beta = calculateBeta(node);

        return beta * raveValue + (1 - beta) * traditionalUCT;
    }

    private static double calculateBeta(MCTSNode node) {
        double k = 1.0; // tuning parameter
        return node.N > 0 ? k / (node.N + k + node.N * k) : 0;
    }
}