package javaV.policies.simulation;

import javaV.common.Common;
import javaV.policies.patterns.Bridge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BridgePattern {
    private final ThreadLocalRandom randomSource;
    public BridgePattern(ThreadLocalRandom src){
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
        int N = moves.size();
        Set<List<Integer>> excludedMoves = new HashSet<List<Integer>>();
        for(int i = 0; i < N; i++){
            int[] move = moves.get(i);
            // If we already made a move via pattern, then skip the same move that came from random
            if (excludedMoves.contains(Arrays.stream(move).boxed().toList())){
                continue;
            }
            ArrayList<int[]> patterns = Bridge.findPatterns(currentBoard, Common.charOptions[counter & 1]);
            int numPatterns = patterns.size();

            if(numPatterns == 1){
                move = patterns.get(0);
                excludedMoves.add(Arrays.stream(move).boxed().toList());
            }
            else{
                int randomIndex = randomSource.nextInt(0, numPatterns);
                move = patterns.get(randomIndex);
                excludedMoves.add(Arrays.stream(move).boxed().toList());
            }
            currentBoard[move[0]][move[1]] = Common.charOptions[counter++ & 1];
        }
        return Common.getWinnerFullBoard(currentBoard);
    }
}
