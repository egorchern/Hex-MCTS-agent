package javaV.policies.selection;

import javaV.MCTSAgent;
import javaV.MCTSNode;
import javaV.common.RAVEStat;

public class RAVE {
    public static int cutoffFactor = 7;
    public static int V = (MCTSAgent.simulationsCntPerCore * MCTSAgent.cores) * cutoffFactor;
    public static double calculateValue(MCTSNode child, MCTSNode parent){
        final RAVEStat raveStat = parent.RAVEStats[child.move.y][child.move.x];
        final double raveFactor = Math.max(0, ((double)(V - child.N) / V));
        return ( ((1 - raveFactor) * UCT.calculateValue(child, parent)) + (raveFactor * ((double) raveStat.Q / raveStat.N)) );
    }
}
