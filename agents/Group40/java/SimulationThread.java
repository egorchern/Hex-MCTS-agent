package javaV;

import javaV.common.*;
import javaV.policies.simulation.BridgePattern;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationThread implements Runnable{

    public final int simulationsCount;
    public ArrayList<Move> legalMoves;
    public final MCTSNode node;
    public final char nodeColour;
    public final Move lastMove;
    private final BridgePattern simulationPolicy;
    public SimulationResult simulationResult = new SimulationResult(Common.boardSize);
    public SimulationThread(char[][] board, MCTSNode node, int simulationsCount){
        this.node = node;
        this.legalMoves = Common.getLegalMoves(board);
        this.simulationsCount = simulationsCount;
        this.nodeColour = node.colour;
        this.lastMove = node.move;
        simulationPolicy = new BridgePattern(ThreadLocalRandom.current(), lastMove, nodeColour, board, legalMoves);
    }
    @Override
    public void run(){
        int localRWins = 0;
        int localBWins = 0;
        for(int i = 0; i < simulationsCount; i++){
            char winner = simulationPolicy.playout();
            if (winner == 'R'){
                localRWins++;
            }
            else{
                localBWins++;
            }


        }

        simulationResult.rWins = localRWins;
        simulationResult.bWins = localBWins;
    }
}
