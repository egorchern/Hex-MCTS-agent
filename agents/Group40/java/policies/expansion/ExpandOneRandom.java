package agents.Group40.java.policies.expansion;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import agents.Group40.java.MCTSNode;
import agents.Group40.java.common.Common;
public class ExpandOneRandom {
    public static MCTSNode[] generateNewNodes(char[][] board, char colour){
        ArrayList<int[]> moves = Common.getLegalMoves(board);
        int randomIndex = ThreadLocalRandom.current().nextInt(0, moves.size() - 1);
        int[] move = moves.get(randomIndex);
        MCTSNode newNode = new MCTSNode(colour, move);
        MCTSNode[] newNodes = {newNode};
        return newNodes;
    }
}
