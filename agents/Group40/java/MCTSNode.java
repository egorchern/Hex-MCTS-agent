package javaV;

import javaV.common.Common;
import javaV.common.Move;
import javaV.common.RAVEStat;

import java.util.ArrayList;

public class MCTSNode {
    public ArrayList<MCTSNode> children = new ArrayList<>();
    public long N;
    public long Q;
    public Move move = null;
    public char colour;
    public RAVEStat[][] RAVEStats;
    private void initRaveStats(){
        final int N = Common.boardSize;
        RAVEStats = new RAVEStat[N][N];
        for (int idy = 0; idy < N; idy++){
            for (int idx = 0; idx < N; idx++){
                RAVEStats[idy][idx] = new RAVEStat();
            }
        }
    }
    public MCTSNode(char cColour){
        colour = cColour;
        initRaveStats();
    }

    public MCTSNode(char cColour, Move cMove){
        colour = cColour;
        move = cMove;
        initRaveStats();

    }

}
