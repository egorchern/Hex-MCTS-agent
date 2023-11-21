package agents.Group40.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import agents.Group40.java.policies.expansion.ExpandOneRandom;
import agents.Group40.java.policies.selection.UCT;

public class MCTSAgent {
    // Parameters
    private long simulations_count = 15L;
    private long time_limit_seconds = 5L;
    // Policies
    private static UCT selectionPolicy = new UCT();
    private static ExpandOneRandom expansionPolicy = new ExpandOneRandom();

    private static Map<Character, Character> opp_colour = new HashMap<Character, Character>();
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
        MCTSNode[] newNodes = expansionPolicy.generateNewNodes(board, opp_colour.get(node.colour));
        for (MCTSNode newNode: newNodes){
            node.children.add(newNode);
        }
        return newNodes;
    }

    public int[] MCTS(char[][] board, char colour, long turn_count){
        root = new MCTSNode(colour);
        long msTimeLimit = time_limit_seconds * 1000;
        long start_time = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start_time) < msTimeLimit){
            MCTSNode node = root;
            char[][] current_board = board.clone();
            // Selection phase
            ArrayList<MCTSNode> path = new ArrayList<MCTSNode>();
            path.add(node);
            while (node.children.size() == getNumLegalMoves(current_board)){
                node = select(node);
                path.add(node);
                int[] move = node.move;
                current_board[move[0]][move[1]] = opp_colour.get(node.colour);
            }
            // Expansion phase
            MCTSNode[] expandedNodes = expand(node, current_board);
            // Simulation Phase
            for(MCTSNode expandedNode : expandedNodes){
                char[][] simulationBoard = current_board.clone();
                int[] move = expandedNode.move;
                simulationBoard[move[0]][move[1]] = opp_colour.get(expandedNode.colour);
                
            }
        }
    }
}
