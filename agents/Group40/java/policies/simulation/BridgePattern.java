package javaV.policies.simulation;

import javaV.common.Common;
import javaV.common.Move;
import javaV.policies.patterns.Bridge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BridgePattern {
    private final ThreadLocalRandom randomSource;
    public final Map<Move, Move> LGR1Map;

    public static int patternFindNCuttoff = 0;

    public BridgePattern(ThreadLocalRandom src, Map<Move, Move> cLGRMap) {
        randomSource = src;
        LGR1Map = cLGRMap;
    }

    private static void processLGR(ArrayDeque<Move> moveHistory, Map<Move, Move> LGR1Map, char winnerColour, char startingColour) {
        // Record successfull replies
        if (winnerColour == startingColour) {
            final int N = moveHistory.size();
            for (int i = 0; i < N - 1; i++){
                final Move reply = moveHistory.removeLast();
                final Move move = moveHistory.peekLast();
                LGR1Map.put(move, reply);
            }


        }
        else{
            for (Move move : moveHistory){
                if (move.colour != startingColour){
                    LGR1Map.remove(move);
                }
            }
        }
    }


    public char playout(char[][] board, Move cLastMove) {
        // Optimised random playout: get legal moves, shuffle that array and play in
        // that order
        // Then check who won, no need to check after each move
        final List<Move> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        final char[][] currentBoard = Common.copy2dArray(board);
        final char startingColour = Common.getOppColour(cLastMove.colour);
        int counter = startingColour == 'R' ? 0 : 1;
        final int N = moves.size();
        ArrayDeque<Move> moveHistory = new ArrayDeque<>(N);
        moveHistory.addLast(cLastMove);
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            Move move;
            final char curColour = Common.charOptions[counter & 1];
            final Move lastMove = moveHistory.peekLast();

            final ArrayList<Move> patterns = Bridge.findPatterns(currentBoard, lastMove, curColour);
            final int numPatterns = patterns.size();

            if (numPatterns > 0) {
                final int randomIndex = randomSource.nextInt(0, numPatterns);
                move = patterns.get(randomIndex);
            } else {
                final Move lgrMove = LGR1Map.get(lastMove);
                // Use lgr
                if (lgrMove != null && currentBoard[lgrMove.y][lgrMove.x] == '0'){
                    move = lgrMove;
                }
                else{
                    do {
                        move = moves.get(cnt++);
                    } while (currentBoard[move.y][move.x] != '0');
                }

            }


            move.colour = curColour;
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            moveHistory.addLast(move);

        }
        char Winner = Common.getWinnerFullBoard(currentBoard);
        processLGR(moveHistory, LGR1Map, Winner, startingColour);
        return Winner;
    }
}
