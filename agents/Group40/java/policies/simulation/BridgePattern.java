package javaV.policies.simulation;

import javaV.common.Common;
import javaV.common.Move;
import javaV.policies.patterns.Bridge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BridgePattern {
    private final ThreadLocalRandom randomSource;
    // To stop trying to find patterns in the end game, where random moves are good enough
    public static int patternFindNCuttoff = 0;
    public BridgePattern(ThreadLocalRandom src){
        randomSource = src;
    }
    public char playout(char[][] board, char startingColour){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        List<Move> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        char[][] currentBoard = Common.copy2dArray(board);
        int counter = startingColour == 'R' ? 0 : 1;
        int N = moves.size();
        Move lastMove = moves.get(0);
        Set<Move> excludedMoves = new HashSet<>();
        for(int i = 0; i < N; i++){
            Move move = moves.get(i);
            // If we already made a move via pattern, then skip the same move that came from random
            if (excludedMoves.contains(move)){
                continue;
            }

            if (N - i > patternFindNCuttoff && Common.charOptions[counter & 1] == startingColour){
                ArrayList<Move> patterns = Bridge.findPatterns(currentBoard, lastMove, Common.charOptions[counter & 1]);
                int numPatterns = patterns.size();

                if (numPatterns > 0) {
                    int randomIndex = randomSource.nextInt(0, numPatterns);
                    move = patterns.get(randomIndex);
                    excludedMoves.add(move);
                }
            }





            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            lastMove = move;
        }
        return Common.getWinnerFullBoard(currentBoard);
    }
}
