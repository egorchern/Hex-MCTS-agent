package javaV;

import java.util.ArrayList;

import javaV.policies.backPropogation.MCTSBackPropogation;
import javaV.policies.expansion.ExpandAll;
import javaV.policies.expansion.ExpandOneRandom;
import javaV.policies.rootMoveSelection.RobustChild;
import javaV.policies.rootMoveSelection.SecureChild;
import javaV.policies.selection.UCT;
import javaV.policies.simulation.RandomPlayout;
import javaV.common.Common;

public class MCTSAgent {
    // Parameters
    private static int simulations_count = 350;
    private static int time_limit_seconds = 10;
    // Policies
    private static UCT selectionPolicy = new UCT();
    private static ExpandAll expansionPolicy = new ExpandAll();
    private static RandomPlayout simulationPolicy = new RandomPlayout();
    private static MCTSBackPropogation backPropogationPolicy = new MCTSBackPropogation();
    private static SecureChild rootMoveSelectionPolicy = new SecureChild();

    private MCTSNode root;

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
        MCTSNode[] newNodes = expansionPolicy.generateNewNodes(node, board, Common.opp_colour.get(node.colour));
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

    public int[] MCTS(char[][] board, char colour, int turn_count){
        root = new MCTSNode(colour);
        long msTimeLimit = time_limit_seconds * 1000L;
        long start_time = System.currentTimeMillis();
        int iterations = 0;
        while ((System.currentTimeMillis() - start_time) < msTimeLimit){

            iterations++;
            MCTSNode node = root;
            char[][] current_board = Common.copy2dArray(board);
            // Selection phase
            ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
            path.add(node);
            while (node.children.size() == Common.getNumLegalMoves(current_board)){
                node = select(node);
                path.add(node);
                int[] move = node.move;
                current_board[move[0]][move[1]] = Common.opp_colour.get(node.colour);
            }
            // Expansion phase
            MCTSNode[] expandedNodes = expand(node, current_board);
            // Simulation Phase
            for(MCTSNode expandedNode : expandedNodes){
                char[][] simulationBoard = Common.copy2dArray(current_board);
                int[] move = expandedNode.move;
                simulationBoard[move[0]][move[1]] = Common.opp_colour.get(expandedNode.colour);
                int[] temp = simulate(expandedNode, simulationBoard);
                int rWins = temp[0];
                int bWins = temp[1];
                //Back-propogation phase
                //Update the newly expanded node first
                backPropogationPolicy.update(expandedNode, rWins, bWins);
                //Update all nodes on path, going from latest node (LIFO)
                for(int i = path.size() - 1; i >= 0; i--){
                    MCTSNode nodeOnPath = path.get(i);
                    backPropogationPolicy.update(nodeOnPath, rWins, bWins);
                }
            }
        }
        System.out.println(iterations);
        int[] bestMove = selectBestMove(root);
        root = null;
        return bestMove;
    }
}
