package javaV.policies.backPropogation;

import javaV.MCTSNode;
import javaV.common.Common;
import javaV.common.RAVEStat;
import javaV.common.SimulationResult;
import javaV.common.SimulationWins;

public class RAVEBackPropogation {
    public static void update(MCTSNode node, SimulationResult simulationResult){
        final int totalSimulations = simulationResult.rWins + simulationResult.bWins;
        node.N += totalSimulations;
        //Update using opposite colour. On blue nodes, add red wins. On red nodes, add blue wins
        //Because on blue colored nodes, the children are red, so we are choosing the move that results in red node that produces the best outcome for blue
        //So we add blue win on red nodes
        if (node.colour == 'R'){
            node.Q += simulationResult.bWins;
        }
        else{
            node.Q += simulationResult.rWins;
        }

        final int N = Common.boardSize;
        for(int idy = 0; idy < N; idy++){
            for(int idx = 0; idx < N; idx++){
                final RAVEStat curNodeRaveCell = node.RAVEStats[idy][idx];
                curNodeRaveCell.N += totalSimulations;
                if (node.colour == 'R'){
                    curNodeRaveCell.Q += simulationResult.bWins;
                }
                else{
                    curNodeRaveCell.Q += simulationResult.rWins;
                }
            }
        }
    }
}
