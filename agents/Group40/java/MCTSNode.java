package javaV;

import javaV.common.Move;

import java.util.ArrayList;

public class MCTSNode {
    public ArrayList<MCTSNode> children = new ArrayList<>();
    public long N;
    public long Q;
    public Move move = null;
    public char colour;
    public MCTSNode(char cColour){
        colour = cColour;
    }

    public MCTSNode(char cColour, Move cMove){
        colour = cColour;
        move = cMove;
    }

}
