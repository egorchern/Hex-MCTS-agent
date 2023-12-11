package javaV;

import javaV.common.Common;
import javaV.common.Move;
import javaV.common.SimulationResult;
import javaV.policies.simulation.BridgePattern;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationThread implements Runnable{

    public final char startingColor;
    public final Move lastMove;
    public final int simulationsCount;
    private final BridgePattern simulationPolicy;
    public SimulationResult simulationResult = new SimulationResult();
    public SimulationThread(char[][] board, MCTSNode node, int simulationsCount){
        ArrayList<Move> legalMoves = Common.getLegalMoves(board);
        this.startingColor = node.colour;
        this.lastMove = node.move;
        this.simulationsCount = simulationsCount;
        simulationPolicy = new BridgePattern(ThreadLocalRandom.current(), lastMove, startingColor, board, legalMoves);
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
