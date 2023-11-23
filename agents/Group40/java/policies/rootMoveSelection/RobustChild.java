package javaV.policies.rootMoveSelection;

import javaV.MCTSNode;

public class RobustChild {

    public static MCTSNode getBestChild(MCTSNode root){
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : root.children){
            double value = child.N;
            if (value > bestValue){
                bestChild = child;
                bestValue = value;
            }
        }
        return bestChild;
    }
}
