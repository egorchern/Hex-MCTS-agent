package javaV.policies.expansion;

import javaV.MCTSNode;
import javaV.common.Common;
import javaV.common.Move;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
public class ExpandOneRandom {
    public static MCTSNode[] generateNewNodes(MCTSNode node, char[][] board, char colour){
        // Because we are expanding randomly one at a time, we might have that same move node is generated as expanded node
        Set<Move> expandedMoves = new HashSet<>();
        for(MCTSNode child : node.children){
            Move move = child.move;
            expandedMoves.add(move);
        }
        ArrayList<Move> moves = Common.getLegalMovesExcept(board, expandedMoves);
        // Solves bug if moves is only length 1
        int randomIndex = 0;
        if (moves.size() > 1){
            randomIndex = ThreadLocalRandom.current().nextInt(0, moves.size());
        }
        Move move = moves.get(randomIndex);
        MCTSNode newNode = new MCTSNode(colour, move);
        return new MCTSNode[]{newNode};
    }
}
