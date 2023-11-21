package agents.Group40.java;

import java.util.ArrayList;

public class MCTSNode {
    public ArrayList<MCTSNode> children = new ArrayList<>();
    public long N;
    public long Q;
    public int[] move = null;
    public char colour;
    public MCTSNode(char cColour){
        colour = cColour;
    }

    public MCTSNode(char cColour, int[] cMove){
        colour = cColour;
        move = cMove;
    }

}
