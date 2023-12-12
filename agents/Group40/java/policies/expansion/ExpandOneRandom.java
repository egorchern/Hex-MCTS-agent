package javaV.policies.expansion;

import javaV.MCTSNode;
import javaV.common.Common;
import javaV.common.Move;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ExpandOneRandom {
    public static MCTSNode[] generateNewNodes(MCTSNode node, char[][] board, char colour) {
        final Set<Move> expandedMoves = new HashSet<>();
        for (final MCTSNode child : node.children) {
            final Move move = child.move;
            expandedMoves.add(move);
        }
        
        final ArrayList<Move> moves = Common.getLegalMovesExcept(board, expandedMoves);
        if (moves.isEmpty()) {
            return new MCTSNode[0];
        }
        final int randomIndex = ThreadLocalRandom.current().nextInt(0, moves.size());

        final Move move = moves.get(randomIndex);
        final MCTSNode newNode = new MCTSNode(colour, move);

        // Initialize RAVE 
        newNode.raveN = new HashMap<>();
        newNode.raveQ = new HashMap<>();

        return new MCTSNode[]{newNode};
    }
}