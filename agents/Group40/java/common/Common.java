package javaV.common;

import java.util.*;

public class Common {

    public static char getOppColour(char curColour){
        return switch (curColour) {
            case ('R') -> 'B';
            case ('B') -> 'R';
            default -> '0';
        };
    }

    public static int boardSize;
    public static final char[] charOptions = new char[]{'R', 'B'};
    private static boolean[][] RefVisited;
    public static void initializeRefVisited(){
        RefVisited = new boolean[boardSize][boardSize];
        for (int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                RefVisited[idy][idx] = false;
            }
        }
    }

    public static char[][] copy2dArray(char[][] arr){
        return Arrays.stream(arr).map(char[]::clone).toArray(char[][]::new);
    }

    public static long getNumLegalMoves(char[][] board){
        long movesCnt = 0;
        
        for(int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                if (board[i][j] == '0'){
                    movesCnt++;
                }
            }
        }
        return movesCnt;
    }

    public static ArrayList<Move> getLegalMoves(char[][] board){
        ArrayList<Move> moves = new ArrayList<>();
        for(int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                if (board[idy][idx] == '0'){

                    moves.add(new Move(idy, idx));
                }
            }
        }
        return moves;
    }


    public static ArrayList<Move> getLegalMovesExcept(char[][] board, Set<Move> exceptSet){
        ArrayList<Move> moves = new ArrayList<>();
        for(int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                if (board[idy][idx] == '0'){

                    Move move = new Move(idy, idx);
                    if (!exceptSet.contains(move)){
                        moves.add(move);
                    }

                }
            }
        }
        return moves;
    }

    private static boolean DFSColourFullyConnected(int x, int y, char colour, boolean[][] visited, char[][] board){
        visited[y][x] = true;
        final int yLen = boardSize;
        final int xLen = boardSize;
        // Win Conds
        if (colour == 'R' && y == yLen - 1){
            return true;
        }
        else if (colour == 'B' && x == xLen - 1){
            return true;
        }
        boolean ans;
        // Visit neighbours
        // Up
        if (x - 1 >= 0 && board[y][x - 1] == colour && !visited[y][x - 1]){
            ans = DFSColourFullyConnected(x - 1, y, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        // Down
        if (x + 1 < xLen && board[y][x + 1] == colour && !visited[y][x + 1]){
            ans = DFSColourFullyConnected(x + 1, y, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        // Left
        if (y - 1 >= 0 && board[y - 1][x] == colour && !visited[y - 1][x]){
            ans = DFSColourFullyConnected(x, y - 1, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        // Right
        if (y + 1 < yLen && board[y + 1][x] == colour && !visited[y + 1][x]){
            ans = DFSColourFullyConnected(x, y + 1, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        // Diagnal L
        if (x - 1 >= 0 && y + 1 < yLen && board[y + 1][x - 1] == colour && !visited[y + 1][x - 1]){
            ans = DFSColourFullyConnected(x - 1, y + 1, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        // Diagnal R
        if (x + 1 < xLen && y - 1 >= 0 && board[y - 1][x + 1] == colour && !visited[y - 1][x + 1]){
            ans = DFSColourFullyConnected(x + 1, y - 1, colour, visited, board);
            if (ans){
                return ans;
            }
        }
        return false;

    } 

    public static char getWinner(char[][] board){
        
        int yLen = boardSize;
        int xLen = boardSize;
        boolean[][] visited = Arrays.stream(RefVisited).map(boolean[]::clone).toArray(boolean[][]::new);

        // Check Red
        for (int idx = 0; idx < xLen; idx++){
            char cell = board[0][idx];
            if (!visited[0][idx] && cell == 'R'){
                boolean isConnected = DFSColourFullyConnected(idx, 0, 'R', visited, board);
                if (isConnected){
                    return 'R';
                }
            }
        }
        // Check Blue
        for (int idy = 0; idy < yLen; idy++){
            char cell = board[idy][0];
            if (!visited[idy][0] && cell == 'B'){
                boolean isConnected = DFSColourFullyConnected(0, idy, 'B', visited, board);
                if (isConnected){
                    return 'B';
                }
            }

        }
        return '0';
    }

    public static char getWinnerFullBoard(char[][] board){

        final int yLen = boardSize;
        boolean[][] visited = Arrays.stream(RefVisited).map(boolean[]::clone).toArray(boolean[][]::new);
        // Check Blue
        for (int idy = 0; idy < yLen; idy++){
            char cell = board[idy][0];
            if (!visited[idy][0] && cell == 'B'){
                final boolean isConnected = DFSColourFullyConnected(0, idy, 'B', visited, board);
                if (isConnected){
                    return 'B';
                }
            }

        }
        // If blue didnt win, then Red must have won it on the fully moved board, so no need to check the blue

        return 'R';
    }
}
