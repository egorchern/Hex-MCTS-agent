package agents.Group40.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import agents.Group40.java.policies.backPropogation.MCTSBackPropogation;
import agents.Group40.java.policies.expansion.ExpandOneRandom;
import agents.Group40.java.policies.rootMoveSelection.RobustChild;
import agents.Group40.java.policies.selection.UCT;
import agents.Group40.java.policies.simulation.RandomPlayout;
import agents.Group40.java.common.Common;

public class MCTSAgent {
    // Parameters
    private static int simulations_count = 15;
    private static int time_limit_seconds = 8;
    // Policies
    private static UCT selectionPolicy = new UCT();
    private static ExpandOneRandom expansionPolicy = new ExpandOneRandom();
    private static RandomPlayout simulationPolicy = new RandomPlayout();
    private static MCTSBackPropogation backPropogationPolicy = new MCTSBackPropogation();
    private static RobustChild rootMoveSelectionPolicy = new RobustChild();

    private MCTSNode root;
    
    private long getNumLegalMoves(char[][] board){
        long movesCnt = 0;
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == '0'){
                    movesCnt++;
                }
            }
        }
        return movesCnt;
    }
    private MCTSNode select(MCTSNode root){
        double bestValue = Double.NEGATIVE_INFINITY;
        MCTSNode bestChild = null;
        for (MCTSNode child: root.children){
            double curValue = selectionPolicy.calculateValue(child, root);
            if (curValue > bestValue){
                bestValue = curValue;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private static MCTSNode[] expand(MCTSNode node, char[][] board){
        MCTSNode[] newNodes = expansionPolicy.generateNewNodes(board, Common.opp_colour.get(node.colour));
        for (MCTSNode newNode: newNodes){
            node.children.add(newNode);
        }
        return newNodes;
    }

    private static int[] simulate(MCTSNode node, char[][] board){
        int rWins = 0;
        int bWins = 0;
        for(int i = 0; i < simulations_count; i++){
            char winner = simulationPolicy.playout(board, node.colour);
            if (winner == 'R'){
                rWins++;
            }
            else{
                bWins++;
            }
        }
        int[] temp = {rWins, bWins};
        return temp;

    }

    private static int[] selectBestMove(MCTSNode root){
        return rootMoveSelectionPolicy.getBestChild(root).move;
    }

    public int[] MCTS(char[][] board, char colour, long turn_count){
        root = new MCTSNode(colour);
        long msTimeLimit = time_limit_seconds * 1000;
        long start_time = System.currentTimeMillis();
        int iterations = 0;
        while ((System.currentTimeMillis() - start_time) < msTimeLimit){
            iterations++;
            MCTSNode node = root;
            char[][] current_board = board.clone();
            // Selection phase
            ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
            path.add(node);
            while (node.children.size() == getNumLegalMoves(current_board)){
                node = select(node);
                path.add(node);
                int[] move = node.move;
                current_board[move[0]][move[1]] = Common.opp_colour.get(node.colour);
            }
            // Expansion phase
            MCTSNode[] expandedNodes = expand(node, current_board);
            // Simulation Phase
            for(MCTSNode expandedNode : expandedNodes){
                char[][] simulationBoard = current_board.clone();
                int[] move = expandedNode.move;
                simulationBoard[move[0]][move[1]] = Common.opp_colour.get(expandedNode.colour);
                int[] temp = simulate(expandedNode, simulationBoard);
                int rWins = temp[0];
                int bWins = temp[1];
                //Back-propogation phase
                //Update the newly expanded node first
                backPropogationPolicy.update(expandedNode, rWins, bWins);
                //Update all nodes on path, going from latest node (LIFO)
                for(int i = path.size() - 1; i > 0; i--){
                    MCTSNode nodeOnPath = path.get(i);
                    backPropogationPolicy.update(nodeOnPath, rWins, bWins);
                }
            }
        }
        System.out.println(iterations);
        return selectBestMove(root);
    }
}
