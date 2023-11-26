package javaV.policies.simulation;

import javaV.common.Common;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class RandomPlayout {
    private final ThreadLocalRandom randomSource;
    public RandomPlayout(ThreadLocalRandom src){
        randomSource = src;
    }

    public char playout(char[][] board, char startingColour){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        List<int[]> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        char[][] currentBoard = Common.copy2dArray(board);
        int counter = startingColour == 'R' ? 0 : 1;
        for(int[] move: moves){
            currentBoard[move[0]][move[1]] = Common.charOptions[counter++ & 1];
        }
        return Common.getWinnerFullBoard(currentBoard);
    }

}
