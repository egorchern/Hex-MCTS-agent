package javaV.policies.patterns;

import javaV.common.Common;

import java.util.ArrayList;

public class Bridge {
    public static ArrayList<int[]> findPatterns(char[][] board, char curColour){
        ArrayList<int[]> patterns = new ArrayList<>();
        int yLen = board.length;
        int xLen = board[0].length;
        for (int idy = 0; idy < yLen; idy++){
            for (int idx = 0; idx < xLen; idx++){
                char bridgeStartPoint = board[idy][idx];
                if (bridgeStartPoint != curColour){
                    continue;
                }
                // downwards bridge
                if (idx - 1 >= 0 && idy + 2 < yLen){
                    char bridgeEndPoint = board[idy + 2][idx - 1];
                    if (bridgeEndPoint == curColour){
                        // is there enemy cell between
                        char leftInBetweenCell = board[idy + 1][idx - 1];
                        char rightInBetweenCell = board[idy + 1][idx];
                        if (leftInBetweenCell == Common.opp_colour.get(curColour) && rightInBetweenCell == '0'){
                            patterns.add(new int[]{idy + 1, idx});
                        }
                        else if(rightInBetweenCell == Common.opp_colour.get(curColour) && leftInBetweenCell == '0'){
                            patterns.add(new int[]{idy + 1, idx - 1});
                        }
                    }


                }

                // downwards left diagnal
                if (idx - 2 >= 0 && idy + 1 < yLen){
                    char bridgeEndPoint = board[idy + 1][idx - 2];
                    if (bridgeEndPoint == curColour){
                        // is there enemy cell between
                        char leftInBetweenCell = board[idy][idx - 1];
                        char rightInBetweenCell = board[idy + 1][idx - 1];
                        if (leftInBetweenCell == Common.opp_colour.get(curColour) && rightInBetweenCell == '0'){
                            patterns.add(new int[]{idy + 1, idx - 1});
                        }
                        else if(rightInBetweenCell == Common.opp_colour.get(curColour) && leftInBetweenCell == '0'){
                            patterns.add(new int[]{idy, idx - 1});
                        }
                    }
                }

                // downwards right diagnal
                if (idx + 1 < xLen && idy + 1 < yLen){
                    char bridgeEndPoint = board[idy + 1][idx + 1];
                    if (bridgeEndPoint == curColour){
                        // is there enemy cell between
                        char leftInBetweenCell = board[idy][idx + 1];
                        char rightInBetweenCell = board[idy + 1][idx];
                        if (leftInBetweenCell == Common.opp_colour.get(curColour) && rightInBetweenCell == '0'){
                            patterns.add(new int[]{idy + 1, idx});
                        }
                        else if(rightInBetweenCell == Common.opp_colour.get(curColour) && leftInBetweenCell == '0'){
                            patterns.add(new int[]{idy, idx + 1});
                        }
                    }
                }
            }
        }
        return patterns;
    }
}
