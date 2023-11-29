package javaV.policies.selection;

import javaV.MCTSAgent;
import javaV.MCTSNode;

public class UCT{

    public static double calculateValue(MCTSNode node, MCTSNode parent){
        return ((double) node.Q / node.N) + (MCTSAgent.C * (Math.sqrt(Math.log(parent.N) / node.N)));
    }
}
