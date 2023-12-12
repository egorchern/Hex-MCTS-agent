package javaV;

import javaV.common.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MCTSNode {
    public ArrayList<MCTSNode> children = new ArrayList<>();
    public long N;
    public long Q;
    public Move move = null;
    public char colour;
    public Map<Move, Long> raveN; // AMAF visit count for each move
    public Map<Move, Long> raveQ; // AMAF score for each move
    
    public MCTSNode(char cColour){
        colour = cColour;
        raveN = new HashMap<>();
        raveQ = new HashMap<>();
    }

    public MCTSNode(char cColour, Move cMove){
        colour = cColour;
        move = cMove;
        raveN = new HashMap<>();
        raveQ = new HashMap<>();
    }

}
