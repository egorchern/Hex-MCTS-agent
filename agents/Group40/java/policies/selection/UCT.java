package javaV.policies.selection;

import javaV.MCTSNode;

public class UCT{
    public static double C = 0.5;
    public static double calculateValue(MCTSNode node, MCTSNode parent){
        return (node.Q / node.N) + C * Math.sqrt(Math.log(parent.N) / node.N);
    }
}
