package javaV.policies.backPropogation;

import java.util.List;

import javaV.MCTSNode;
import javaV.common.SimulationResult;

public class MCTSBackPropogation {
    public static void update(MCTSNode node, SimulationResult simulationResult, List<Move> playedMoves) {
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

         // Update RAVE statistics for each move in the played moves list
        for (Move move : playedMoves) {
            node.raveN.put(move, node.raveN.getOrDefault(move, 0L) + 1);
            if ((node.colour == 'R' && simulationResult.rWins > 0) || 
                (node.colour == 'B' && simulationResult.bWins > 0)) {
                node.raveQ.put(move, node.raveQ.getOrDefault(move, 0L) + 1);
            }
        }
    }
}