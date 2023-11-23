package javaV.policies.expansion;

import javaV.MCTSNode;
import javaV.common.Common;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class ExpandAll {
    public static MCTSNode[] generateNewNodes(char[][] board, char colour){
        ArrayList<int[]> moves = Common.getLegalMoves(board);
        MCTSNode[] newNodes = new MCTSNode[moves.size()];
        for (int i = 0; i < newNodes.length; i++){
            newNodes[i] = new MCTSNode(colour, moves.get(i));
        }
        return newNodes;
    }
}
