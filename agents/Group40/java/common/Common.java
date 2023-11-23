package javaV.common;

import java.util.*;

public class Common {
    public static Map<Character, Character> opp_colour = new HashMap<Character, Character>(){
        {
            put('R', 'B');
            put('B', 'R');
        }
        
    };

    public static char[][] copy2dArray(char[][] arr){
        return Arrays.stream(arr).map(char[]::clone).toArray(char[][]::new);
    }

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

    public static ArrayList<int[]> getLegalMovesExcept(char[][] board, Set<List<Integer>> exceptSet){
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                if (board[i][j] == '0'){
                    int[] move = {i, j};
                    List<Integer> key = Arrays.stream(move).boxed().toList();
                    if (!exceptSet.contains(key)){
                        moves.add(move);
                    }

                }
            }
        }
        return moves;
    }

    private static boolean DFSColourFullyConnected(int x, int y, char colour, boolean[][] visited, char[][] board){
        visited[y][x] = true;
        int yLen = visited.length;
        int xLen = visited[0].length;
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
        
        int yLen = board.length;
        int xLen = board[0].length;
        boolean[][] visited = new boolean[yLen][xLen];
        for (int idy = 0; idy < yLen; idy++){
            for (int idx = 0; idx < xLen; idx++){
                visited[idy][idx] = false;
            }
        }
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
}
