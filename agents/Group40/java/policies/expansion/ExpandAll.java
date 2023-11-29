package javaV.policies.expansion;

import javaV.MCTSNode;
import javaV.common.Common;

import java.util.ArrayList;

public class ExpandAll {
    public static MCTSNode[] generateNewNodes(MCTSNode node, char[][] board, char colour){
        ArrayList<int[]> moves = Common.getLegalMoves(board);
        MCTSNode[] newNodes = new MCTSNode[moves.size()];
        for (int i = 0; i < newNodes.length; i++){
            newNodes[i] = new MCTSNode(colour, moves.get(i));
        }
        return newNodes;
    }
}
