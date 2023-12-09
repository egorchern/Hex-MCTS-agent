package javaV.policies.simulation;

import javaV.common.Common;
import javaV.common.Move;
import javaV.policies.patterns.Bridge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BridgePattern {
    private final ThreadLocalRandom randomSource;
    // To stop trying to find patterns in the end game, where random moves are good
    // enough
    public static int patternFindNCuttoff = 0;

    public BridgePattern(ThreadLocalRandom src) {
        randomSource = src;
    }

    public char playout(char[][] board, char startingColour) {
        // Optimised random playout: get legal moves, shuffle that array and play in
        // that order
        // Then check who won, no need to check after each move
        final List<Move> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        final char[][] currentBoard = Common.copy2dArray(board);
        int counter = startingColour == 'R' ? 0 : 1;
        final int N = moves.size();
        Move lastMove = moves.get(0);
        int cnt = 0;
        for (int i = 0; i < N; i++){
            Move move;
            final char curColour = Common.charOptions[counter & 1];
            final ArrayList<Move> patterns = Bridge.findPatterns(currentBoard, lastMove, curColour);
            final int numPatterns = patterns.size();

            if (numPatterns > 0) {
                final int randomIndex = randomSource.nextInt(0, numPatterns);
                move = patterns.get(randomIndex);
            } else {

                do {
                    move = moves.get(cnt++);
                } while (currentBoard[move.y][move.x] != '0');
            }
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            lastMove = move;

        }
        return Common.getWinnerFullBoard(currentBoard);
    }
}
