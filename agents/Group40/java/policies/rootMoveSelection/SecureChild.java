package javaV.policies.rootMoveSelection;

import javaV.MCTSNode;

public class SecureChild {
    private static double C = 0.5;
    public static MCTSNode getBestChild(MCTSNode root){
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : root.children){
            double value = ((double) child.Q / child.N) - (C * (Math.sqrt((Math.log(root.N) / child.N))));
            if (value > bestValue){
                bestChild = child;
                bestValue = value;
            }
        }
        return bestChild;
    }
}
