package agents.Group40.java.policies.selection;

import agents.Group40.java.MCTSNode;

public class UCT{
    public static double C = 0.5;
    public static double calculateValue(MCTSNode node, MCTSNode parent){
        return (node.Q / node.N) + C * Math.sqrt(Math.log(parent.N) / node.N);
    }
}
