package javaV.policies.simulation;

import javaV.MCTSNode;
import javaV.common.Common;
import javaV.common.Move;
import javaV.common.UnionFind;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class RandomPlayout {
    private final ThreadLocalRandom randomSource;
    public RandomPlayout(ThreadLocalRandom src){
        randomSource = src;
    }

    public final char playout(char[][] board, MCTSNode node){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        final List<Move> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        final char[][] currentBoard = Common.copy2dArray(board);
        final UnionFind curConnectivity = new UnionFind(node.connectivity);
        int counter = node.colour == 'R' ? 0 : 1;
        for(final Move move: moves){
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            Common.updateConnectivity(currentBoard, curConnectivity, move.y, move.x);
        }
        final boolean isBlueWinner = Common.isBlueWinnerUsingConnectivity(curConnectivity, currentBoard);
        return isBlueWinner ? 'B' : 'R';
    }

}
