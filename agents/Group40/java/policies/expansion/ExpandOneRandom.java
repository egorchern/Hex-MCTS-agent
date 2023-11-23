package javaV.policies.expansion;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javaV.MCTSNode;
import javaV.common.Common;
public class ExpandOneRandom {
    public static MCTSNode[] generateNewNodes(char[][] board, char colour){
        ArrayList<int[]> moves = Common.getLegalMoves(board);
        // Solves bug if moves is only length 1
        int randomIndex = 0;
        if (moves.size() > 1){
            randomIndex = ThreadLocalRandom.current().nextInt(0, moves.size() - 1);
        }
        int[] move = moves.get(randomIndex);
        MCTSNode newNode = new MCTSNode(colour, move);
        MCTSNode[] newNodes = {newNode};
        return newNodes;
    }
}
