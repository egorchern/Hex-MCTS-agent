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
    private final Move lastMove;
    private final char[][] referenceBoard;
    private ArrayList<Move> legalMoves;
    private final char startingColour;
    public static int patternFindNCuttoff = 0;
    public static double pNotUseBridge = 0.1;

    public BridgePattern(ThreadLocalRandom src, Move lastMove, char startingColour, char[][] board, ArrayList<Move> legalMoves) {
        this.randomSource = src;
        this.lastMove = lastMove;
        this.referenceBoard = board;
        this.startingColour = startingColour;
        this.legalMoves = legalMoves;
    }

    public char playout() {
        // Optimised random playout: get legal moves, shuffle that array and play in
        // that order
        // Then check who won, no need to check after each move
        // Shuffle in place
        Collections.shuffle(legalMoves, randomSource);
        final char[][] currentBoard = Common.copy2dArray(referenceBoard);
        int counter = startingColour == 'R' ? 0 : 1;
        final int N = legalMoves.size();
        Move curLastMove = lastMove;
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            Move move;
            final char curColour = Common.charOptions[counter & 1];

            final ArrayList<Move> patterns = Bridge.findPatterns(currentBoard, curLastMove, curColour);
            final int numPatterns = patterns.size();

            if (numPatterns > 0 && randomSource.nextDouble() <= pNotUseBridge) {
                final int randomIndex = randomSource.nextInt(0, numPatterns);
                move = patterns.get(randomIndex);
            } else {

                do {
                    move = legalMoves.get(cnt++);
                } while (currentBoard[move.y][move.x] != '0');
            }
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            curLastMove = move;

        }
        return Common.getWinnerFullBoard(currentBoard);
    }
}
