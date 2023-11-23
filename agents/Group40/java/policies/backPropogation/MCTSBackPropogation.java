package javaV.policies.backPropogation;

import javaV.MCTSNode;

public class MCTSBackPropogation {
    public static void update(MCTSNode node, int rWins, int bWins){
        int totalSimulations = rWins + bWins;
        node.N += totalSimulations;
        //Update using opposite colour. On blue nodes, add red wins. On red nodes, add blue wins
        //Because on blue colored nodes, the children are red, so we are choosing the move that results in red node that produces the best outcome for blue
        //So we add blue win on red nodes
        if (node.colour == 'R'){
            node.Q += bWins;
        }
        else{
            node.Q += rWins;
        }
    }
}
