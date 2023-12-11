package javaV.policies.simulation;

import javaV.common.Common;
import javaV.common.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class RandomPlayout {
    private final ThreadLocalRandom randomSource;
    // To stop trying to find patterns in the end game, where random moves are good
    // enough
    private final Move lastMove;
    private final char[][] referenceBoard;
    private ArrayList<Move> legalMoves;
    private final char startingColour;
    public static int patternFindNCuttoff = 0;

    public RandomPlayout(ThreadLocalRandom src, Move lastMove, char startingColour, char[][] board, ArrayList<Move> legalMoves) {
        this.randomSource = src;
        this.lastMove = lastMove;
        this.referenceBoard = board;
        this.startingColour = startingColour;
        this.legalMoves = legalMoves;
    }

    public char playout(){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        // Shuffle in place
        Collections.shuffle(legalMoves, randomSource);
        char[][] currentBoard = Common.copy2dArray(referenceBoard);
        int counter = startingColour == 'R' ? 0 : 1;
        for(final Move move: legalMoves){
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
        }
        return Common.getWinnerFullBoard(currentBoard);
    }

}
