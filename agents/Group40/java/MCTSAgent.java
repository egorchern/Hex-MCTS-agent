package javaV;

import javaV.common.Common;
import javaV.common.Move;
import javaV.common.SimulationResult;
import javaV.policies.backPropogation.MCTSBackPropogation;
import javaV.policies.expansion.ExpandAll;
import javaV.policies.expansion.ExpandOneRandom;
import javaV.policies.rootMoveSelection.SecureChild;
import javaV.policies.selection.UCT;

import java.util.ArrayList;
import java.util.Collections;

public class MCTSAgent {
    // Parameters
    public static int simulationsCntPerCore = 135;
    public static double timeLimitSeconds = 6.5;
    public static double C = 0.4;
    // Policies
    private static final UCT selectionPolicy = new UCT();
    private static final ExpandOneRandom expansionPolicy = new ExpandOneRandom();
    private static final MCTSBackPropogation backPropogationPolicy = new MCTSBackPropogation();
    private static final SecureChild rootMoveSelectionPolicy = new SecureChild();
    public static int cores = Runtime.getRuntime().availableProcessors();

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
        MCTSNode[] newNodes = expansionPolicy.generateNewNodes(node, board, Common.getOppColour(node.colour));
        Collections.addAll(node.children, newNodes);
        return newNodes;
    }

    private static SimulationResult simulate(MCTSNode node, char[][] board){
        SimulationResult simulationResult = new SimulationResult();

        Thread[] threads = new Thread[cores];
        SimulationThread[] threadInfos = new SimulationThread[cores];
        for (int i = 0; i < threads.length; i++){
            threadInfos[i] = new SimulationThread(board, node.colour, simulationsCntPerCore);
            threads[i] = new Thread(threadInfos[i]);
            threads[i].start();

        }

        for (Thread thread : threads) {

            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        for (SimulationThread threadInfo : threadInfos){
            simulationResult.rWins += threadInfo.simulationResult.rWins;
            simulationResult.bWins += threadInfo.simulationResult.bWins;
        }




//        for(int i = 0; i < simulations_count; i++){
//            char winner = simulationPolicy.playout(board, node.colour);
//            if (winner == 'R'){
//                rWins++;
//            }
//            else{
//                bWins++;
//            }
//        }
        return simulationResult;

    }

    private double getTimeLimit(double turn){
        if (turn <= 33) {
            return 7.2;
        } else if (turn <= 50) {
            return 3.3;
        } else {
            return 0.5;
        }
    }

    private static Move selectBestMove(MCTSNode root){
        return rootMoveSelectionPolicy.getBestChild(root).move;
    }

    public Move MCTS(char[][] board, char colour, int turn_count){
        root = new MCTSNode(colour);
        timeLimitSeconds = getTimeLimit(Math.ceil(turn_count/2));
        final double msTimeLimit = timeLimitSeconds * 1000;
        final long start_time = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start_time) < msTimeLimit){
            MCTSNode node = root;
            char[][] current_board = Common.copy2dArray(board);
            // Selection phase
            ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
            path.add(node);
            while (node.children.size() == Common.getNumLegalMoves(current_board)){
                node = select(node);
                path.add(node);
                Move move = node.move;
                current_board[move.y][move.x] = Common.getOppColour(node.colour);
            }
            // Expansion phase
            MCTSNode[] expandedNodes = expand(node, current_board);
            // Simulation Phase
            for(MCTSNode expandedNode : expandedNodes){
                char[][] simulationBoard = Common.copy2dArray(current_board);
                Move move = expandedNode.move;
                simulationBoard[move.y][move.x] = Common.getOppColour(expandedNode.colour);
                SimulationResult simulationResult = simulate(expandedNode, simulationBoard);
                //Back-propogation phase
                //Update the newly expanded node first
                backPropogationPolicy.update(expandedNode, simulationResult);
                //Update all nodes on path, going from latest node (LIFO)
                for(int i = path.size() - 1; i >= 0; i--){
                    MCTSNode nodeOnPath = path.get(i);
                    backPropogationPolicy.update(nodeOnPath, simulationResult);
                }
            }
        }
        Move bestMove = selectBestMove(root);
        root = null;
        return bestMove;
    }
}
