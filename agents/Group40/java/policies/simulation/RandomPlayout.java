package agents.Group40.java.policies.simulation;
import java.util.List;
import java.util.Collections;
import agents.Group40.java.common.Common;
public class RandomPlayout {

    public static char playout(char[][] board, char startingColour){
        //Optimised random playout: get legal moves, shuffle that array and play in that order
        //Then check who won, no need to check after each move
        List<int[]> moves = Common.getLegalMoves(board);
        // Shuffle in place
        Collections.shuffle(moves);
        char[][] currentBoard = board.clone();
        char curColour = startingColour;
        for (int[] move: moves){
            currentBoard[move[0]][move[1]] = curColour;
            curColour = Common.opp_colour.get(curColour);
        }
        return Common.getWinner(currentBoard);
    }

}
