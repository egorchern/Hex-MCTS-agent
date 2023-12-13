package javaV.policies.selection;

import javaV.MCTSAgent;
import javaV.MCTSNode;
import javaV.common.RAVEStat;

public class RAVE {

    public static double calculateValue(MCTSNode child, MCTSNode parent){
        final RAVEStat raveStat = parent.RAVEStats[child.move.y][child.move.x];
        return (( UCT.calculateValue(child, parent) ) + ( ((double) raveStat.Q / raveStat.N ) ));
    }
}
