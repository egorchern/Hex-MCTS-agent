package javaV.policies.simulation;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import javaV.common.Common;
public class RandomPlayout {

    public static char playout(char[][] board, char startingColour){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        List<int[]> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, ThreadLocalRandom.current());
        char[][] currentBoard = Common.copy2dArray(board);
        char curColour = startingColour;
        int counter = startingColour == 'R' ? 0 : 1;
        for(int[] move: moves){
            currentBoard[move[0]][move[1]] = curColour;
            curColour = Common.charOptions[counter++ & 1];
        }
        return Common.getWinner(currentBoard);
    }

}
