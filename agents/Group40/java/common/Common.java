package agents.Group40.java.common;

import java.util.ArrayList;

public class Common {
    public static long getNumLegalMoves(char[][] board){
        long movesCnt = 0;
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == '0'){
                    movesCnt++;
                }
            }
        }
        return movesCnt;
    }

    public static ArrayList<int[]> getLegalMoves(char[][] board){
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == '0'){
                    int[] move = {i, j};
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
