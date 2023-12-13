package javaV;

import javaV.common.Common;
import javaV.common.Move;
import javaV.common.SimulationResult;
import javaV.common.SimulationWins;
import javaV.policies.backPropogation.MCTSBackPropogation;
import javaV.policies.backPropogation.RAVEBackPropogation;
import javaV.policies.expansion.ExpandAll;
import javaV.policies.expansion.ExpandOneRandom;
import javaV.policies.rootMoveSelection.RobustChild;
import javaV.policies.rootMoveSelection.SecureChild;
import javaV.policies.selection.RAVE;
import javaV.policies.selection.UCT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MCTSAgent {
    // Parameters
    public static int simulationsCntPerCore = 115;
    public static double timeLimitSeconds = 6.5;
    public static double C = 0.0000001;
    // Policies
    private static final RAVE selectionPolicy = new RAVE();
    private static final ExpandOneRandom expansionPolicy = new ExpandOneRandom();
    private static final RAVEBackPropogation backPropogationPolicy = new RAVEBackPropogation();
    private static final RobustChild rootMoveSelectionPolicy = new RobustChild();
    public static int cores = Runtime.getRuntime().availableProcessors();

    private MCTSNode root;

    private MCTSNode select(MCTSNode root){
        double bestValue = Double.NEGATIVE_INFINITY;
        MCTSNode bestChild = null;
        for (MCTSNode child: root.children){
            final double curValue = RAVE.calculateValue(child, root);
            if (curValue > bestValue){
                bestValue = curValue;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private static MCTSNode[] expand(MCTSNode node, char[][] board){
        MCTSNode[] newNodes = ExpandOneRandom.generateNewNodes(node, board, Common.getOppColour(node.colour));
        Collections.addAll(node.children, newNodes);
//        for (MCTSNode child : newNodes){
//
//        }
        return newNodes;
    }

    private static SimulationResult simulate(MCTSNode node, char[][] board){
        SimulationResult simulationResult = new SimulationResult(Common.boardSize);

        Thread[] threads = new Thread[cores];
        SimulationThread[] threadInfos = new SimulationThread[cores];
        for (int i = 0; i < threads.length; i++){
            threadInfos[i] = new SimulationThread(board, node, simulationsCntPerCore);
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
        // Combine results from all threads
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
        return RobustChild.getBestChild(root).move;
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
            ArrayList<MCTSNode> path = new ArrayList<>();
            path.add(node);
            while (!node.children.isEmpty() && node.children.size() == Common.getNumLegalMoves(current_board)){
                node = select(node);
                path.add(node);
                final Move move = node.move;
                current_board[move.y][move.x] = Common.getOppColour(node.colour);
            }
            // Expansion phase
            final MCTSNode[] expandedNodes = expand(node, current_board);
            // Simulation Phase
            for(MCTSNode expandedNode : expandedNodes){
                char[][] simulationBoard = Common.copy2dArray(current_board);
                final Move move = expandedNode.move;
                simulationBoard[move.y][move.x] = Common.getOppColour(expandedNode.colour);
                final SimulationResult simulationResult = simulate(expandedNode, simulationBoard);
                //Back-propogation phase
                //Update the newly expanded node first
                RAVEBackPropogation.update(expandedNode, simulationResult);
                //Update all nodes on path, going from latest node (LIFO)
                for(int i = path.size() - 1; i >= 0; i--){
                    final MCTSNode nodeOnPath = path.get(i);
                    RAVEBackPropogation.update(nodeOnPath, simulationResult);
                }
            }
            // If there are no expanded nodes : reached end, which is unlikely but may happen
            // Simply run simulations on that node
            if (expandedNodes.length == 0){

                final SimulationResult simulationResult = simulate(node, current_board);
                for(int i = path.size() - 1; i >= 0; i--){
                    final MCTSNode nodeOnPath = path.get(i);
                    RAVEBackPropogation.update(nodeOnPath, simulationResult);
                }
            }
        }
        final Move bestMove = selectBestMove(root);
        root = null;
        return bestMove;
    }
}
