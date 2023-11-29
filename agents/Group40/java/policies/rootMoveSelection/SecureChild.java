package javaV.policies.rootMoveSelection;

import javaV.MCTSAgent;
import javaV.MCTSNode;

public class SecureChild {

    public static MCTSNode getBestChild(MCTSNode root){
        MCTSNode bestChild = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : root.children){
            double value = ((double) child.Q / child.N) - (MCTSAgent.C * (Math.sqrt((Math.log(root.N) / child.N))));
            if (value > bestValue){
                bestChild = child;
                bestValue = value;
            }
        }
        return bestChild;
    }
}
