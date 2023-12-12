package javaV.policies.selection;

import javaV.MCTSAgent;
import javaV.MCTSNode;
import javaV.common.RAVEStat;

public class RAVE {
    public static int cutoffFactor = 6;
    public static int V = (MCTSAgent.simulationsCntPerCore * MCTSAgent.cores) * cutoffFactor;
    public static double calculateValue(MCTSNode node, MCTSNode parent){
        final RAVEStat raveStat = parent.RAVEStats[node.move.y][node.move.x];
        final double raveFactor = Math.max(0, ((double)(V - raveStat.N) / V));
        return ( ((1 - raveFactor) * UCT.calculateValue(node, parent)) + (raveFactor * ((double) raveStat.Q / raveStat.N)));
    }
}
