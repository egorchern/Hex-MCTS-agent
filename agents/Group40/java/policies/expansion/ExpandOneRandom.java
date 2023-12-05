package javaV.policies.expansion;

import javaV.MCTSNode;
import javaV.common.Common;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
public class ExpandOneRandom {
    public static MCTSNode[] generateNewNodes(MCTSNode node, char[][] board, char colour){
        // Because we are expanding randomly one at a time, we might have that same move node is generated as expanded node
        Set<List<Integer>> expandedMoves = new HashSet<List<Integer>>();
        for(MCTSNode child : node.children){
            int[] move = child.move;
            List<Integer> key = Arrays.stream(move).boxed().toList();
            expandedMoves.add(key);
        }
        ArrayList<int[]> moves = Common.getLegalMovesExcept(board, expandedMoves);
        // Solves bug if moves is only length 1
        int randomIndex = 0;
        if (moves.size() > 1){
            randomIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
        }
        int[] move = moves.get(randomIndex);
        MCTSNode newNode = new MCTSNode(colour, move);
        return new MCTSNode[]{newNode};
    }
}
