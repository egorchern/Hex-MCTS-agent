package javaV;

import javaV.common.Common;
import javaV.policies.backPropogation.MCTSBackPropogation;
import javaV.policies.expansion.ExpandOneRandom;
import javaV.policies.rootMoveSelection.SecureChild;
import javaV.policies.selection.UCT;

import java.util.ArrayList;
import java.util.Collections;

public class MCTSAgent {
    // Parameters
    public static int simulationsCntPerCore = 200;
    public static double timeLimitSeconds = 7;
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
            double curValue = UCT.calculateValue(child, root);
            if (curValue > bestValue){
                bestValue = curValue;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private static MCTSNode[] expand(MCTSNode node, char[][] board){
        MCTSNode[] newNodes = ExpandOneRandom.generateNewNodes(node, board, Common.opp_colour.get(node.colour));
        Collections.addAll(node.children, newNodes);
        return newNodes;
    }

    private static int[] simulate(MCTSNode node, char[][] board){
        int rWins = 0;
        int bWins = 0;

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
            rWins += threadInfo.rWins;
            bWins += threadInfo.bWins;
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
        return new int[]{rWins, bWins};

    }

    private static int[] selectBestMove(MCTSNode root){
        return SecureChild.getBestChild(root).move;
    }

    public int[] MCTS(char[][] board, char colour, int turn_count){
        root = new MCTSNode(colour);
        final double msTimeLimit = timeLimitSeconds * 1000;
        final long start_time = System.currentTimeMillis();

        while ((System.currentTimeMillis() - start_time) < msTimeLimit){
            MCTSNode node = root;
            char[][] current_board = Common.copy2dArray(board);
            // Selection phase
            ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
            path.add(node);
            int nodeSize = node.children.size();
            while (nodeSize == Common.getNumLegalMoves(current_board)){
                node = select(node);
                nodeSize = node.children.size();
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
                MCTSBackPropogation.update(expandedNode, rWins, bWins);
                //Update all nodes on path, going from latest node (LIFO)
                for(int i = path.size() - 1; i >= 0; i--){
                    MCTSNode nodeOnPath = path.get(i);
                    MCTSBackPropogation.update(nodeOnPath, rWins, bWins);
                }
            }
        }
        int[] bestMove = selectBestMove(root);
        root = null;
        return bestMove;
    }
}
