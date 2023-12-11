package javaV.policies.simulation;

import javaV.common.Common;
import javaV.common.Move;
import javaV.policies.patterns.Bridge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BridgePattern {
    private final ThreadLocalRandom randomSource;
    public final Map<Move, Move> LGR1Map;
    public final Map<List<Move>, Move> LGR2Map;

    public static int patternFindNCuttoff = 0;

    public BridgePattern(ThreadLocalRandom src, Map<Move, Move> cLGRMap, Map<List<Move>, Move> cLGR2Map) {
        randomSource = src;
        LGR1Map = cLGRMap;
        LGR2Map = cLGR2Map;
    }
    private void updateLGR1(char winnerColour, Move move, Move reply){
        if (winnerColour == reply.colour) {
            LGR1Map.put(move, reply);
        } else {
            LGR1Map.remove(move);
        }
    }

    private void updateLGR2(char winnerColour, Move moveFirst, Move moveLast, Move reply){
        final ArrayList<Move> key = new ArrayList<>(2);
        key.add(moveFirst);
        key.add(moveLast);
        if (winnerColour == reply.colour) {
            LGR2Map.put(key, reply);
        } else {
            LGR2Map.remove(key);
        }
    }

    private void processLGR(List<Move> moveHistory, char winnerColour) {
        // Record successfull replies

        final int N = moveHistory.size();
        for (int i = N - 1; i >= 3; i--) {
            final Move reply = moveHistory.get(i);
            final Move moveLast = moveHistory.get(i - 1);
            updateLGR1(winnerColour, moveLast, reply);
            final Move moveFirst = moveHistory.get(i - 3);
            updateLGR2(winnerColour, moveFirst, moveLast, reply);

        }
        if (N >= 3){
            updateLGR1(winnerColour, moveHistory.get(1), moveHistory.get(2));
        }
        if (N >= 2){
            updateLGR1(winnerColour, moveHistory.get(0), moveHistory.get(1));
        }



    }

    private Move getLGRMove(char[][] currentBoard, List<Move> moveHistory) {
        final Move lastMove = moveHistory.get(moveHistory.size() - 1);
        final int preLastIndex = moveHistory.size() - 3;
        if (preLastIndex >= 0) {
            final Move preLastMove = moveHistory.get(preLastIndex);
            final ArrayList<Move> key = new ArrayList<>(2);
            key.add(preLastMove);
            key.add(lastMove);
            final Move lgr2Move = LGR2Map.get(key);
            if (lgr2Move != null && currentBoard[lgr2Move.y][lgr2Move.x] == '0') {
                return lgr2Move;
            }

        }
        final Move lgrMove = LGR1Map.get(lastMove);
        // Use lgr
        if (lgrMove != null && currentBoard[lgrMove.y][lgrMove.x] == '0') {
            return lgrMove;
        }
        return null;
    }

    public char playout(char[][] board, Move cLastMove) {
        // Optimised random playout: get legal moves, shuffle that array and play in
        // that order
        // Then check who won, no need to check after each move
        final ArrayList<Move> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves, randomSource);
        final char[][] currentBoard = Common.copy2dArray(board);
        final char startingColour = Common.getOppColour(cLastMove.colour);
        int counter = startingColour == 'R' ? 0 : 1;
        final int N = moves.size();
        ArrayList<Move> moveHistory = new ArrayList<>(N);
        moveHistory.add(cLastMove);
        int cnt = 0;
        for (int i = 0; i < N; i++) {
            Move move;
            final char curColour = Common.charOptions[counter & 1];
            final Move lastMove = moveHistory.get(moveHistory.size() - 1);

            final ArrayList<Move> patterns = Bridge.findPatterns(currentBoard, lastMove, curColour);
            final int numPatterns = patterns.size();

            if (numPatterns > 0) {
                final int randomIndex = randomSource.nextInt(0, numPatterns);
                move = patterns.get(randomIndex);
            } else {
                final Move lgrMove = getLGRMove(currentBoard, moveHistory);
                if (lgrMove != null) {
                    move = lgrMove;
                } else {
                    do {
                        move = moves.get(cnt++);
                    } while (currentBoard[move.y][move.x] != '0');
                }

            }


            move.colour = curColour;
            currentBoard[move.y][move.x] = Common.charOptions[counter++ & 1];
            moveHistory.add(move);

        }
        char Winner = Common.getWinnerFullBoard(currentBoard);
        processLGR(moveHistory, Winner);
        return Winner;
    }
}
