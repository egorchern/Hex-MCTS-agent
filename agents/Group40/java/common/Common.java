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
        final ArrayList<Move> moves = new ArrayList<>();
        for(int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                if (board[idy][idx] == '0'){

                    moves.add(new Move(idy, idx, 'R'));
                }
            }
        }
        return moves;
    }


    public static ArrayList<Move> getLegalMovesExcept(char[][] board, Set<Move> exceptSet){
        final ArrayList<Move> moves = new ArrayList<>();
        for(int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                if (board[idy][idx] == '0'){

                    final Move move = new Move(idy, idx, 'R');
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
    public static boolean isBlueWinnerUsingConnectivity(UnionFind connectivity, char[][] board){
        LinkedList<Integer> startPoints = new LinkedList<>();
        LinkedList<Integer> endPoints = new LinkedList<>();
        for (int idy = 0; idy < boardSize; idy++){
            if (board[idy][0] == 'B'){
                final int startRowMajor = getRowMajor(idy, 0);
                startPoints.add(startRowMajor);
            }
            if (board[idy][boardSize - 1] == 'B'){
                final int endRowMajor = getRowMajor(idy, boardSize - 1);
                endPoints.add(endRowMajor);
            }
        }
        for (final int startPoint : startPoints) {
            for (final int endPoint : endPoints) {
                final boolean connected = connectivity.find(startPoint) == connectivity.find(endPoint);
                if (connected) {
                    return true;
                }
            }
        }
        return false;
    }
    public static int getRowMajor(int idy, int idx){
        return idx + (idy * boardSize);
    }
    public static void updateConnectivity(char[][] board, UnionFind connectivity, int idy, int idx){
        final int referenceRowMajor = getRowMajor(idy, idx);
        final char curColour = board[idy][idx];
        final int xLen = boardSize;
        final int yLen = boardSize;
        // Go through neighbours and union all of them with current cell
        if (idx - 1 >= 0 && board[idy][idx - 1] == curColour){
            final int curRowMajor = getRowMajor(idy, idx - 1);
            connectivity.union(referenceRowMajor, curRowMajor);
        }
        // Down
        if (idx + 1 < xLen && board[idy][idx + 1] == curColour){
            final int curRowMajor = getRowMajor(idy, idx + 1);
            connectivity.union(referenceRowMajor, curRowMajor);
        }
        // Left
        if (idy - 1 >= 0 && board[idy - 1][idx] == curColour){
            final int curRowMajor = getRowMajor(idy - 1, idx);
            connectivity.union(referenceRowMajor, curRowMajor);
        }
        // Right
        if (idy + 1 < yLen && board[idy + 1][idx] == curColour){
            final int curRowMajor = getRowMajor(idy + 1, idx);
            connectivity.union(referenceRowMajor, curRowMajor);
        }
        // Diagnal L
        if (idx - 1 >= 0 && idy + 1 < yLen && board[idy + 1][idx - 1] == curColour){
            final int curRowMajor = getRowMajor(idy + 1, idx - 1);
            connectivity.union(referenceRowMajor, curRowMajor);
        }
        // Diagnal R
        if (idx + 1 < xLen && idy - 1 >= 0 && board[idy - 1][idx + 1] == curColour){
            final int curRowMajor = getRowMajor(idy - 1, idx + 1);
            connectivity.union(referenceRowMajor, curRowMajor);
        }

    }
    public static UnionFind createConnectivity(char[][] board){
        UnionFind connectivity = new UnionFind(boardSize * boardSize);
        for (int idy = 0; idy < boardSize; idy++){
            for (int idx = 0; idx < boardSize; idx++){
                updateConnectivity(board, connectivity, idy, idx);
            }
        }
        return connectivity;
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

        final int xLen = boardSize;
        final boolean[][] visited = Arrays.stream(RefVisited).map(boolean[]::clone).toArray(boolean[][]::new);
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
        // If blue didnt win, then Red must have won it on the fully moved board, so no need to check the blue

        return 'B';
    }
}
