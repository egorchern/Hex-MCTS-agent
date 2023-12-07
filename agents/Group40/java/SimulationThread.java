package javaV;

import javaV.common.SimulationResult;
import javaV.policies.simulation.BridgeOpponentOnly;
import javaV.policies.simulation.BridgePattern;
import javaV.policies.simulation.RandomPlayout;

import java.util.concurrent.ThreadLocalRandom;

public class SimulationThread implements Runnable{
    public final char[][] board;
    public final MCTSNode node;
    public final int simulationsCount;
    private final RandomPlayout simulationPolicy = new RandomPlayout(ThreadLocalRandom.current());
    public SimulationResult simulationResult = new SimulationResult();
    public SimulationThread(char[][] board, MCTSNode node, int simulationsCount){
        this.board = board;
        this.node = node;
        this.simulationsCount = simulationsCount;
    }
    @Override
    public void run(){
        int localRWins = 0;
        int localBWins = 0;
        for(int i = 0; i < simulationsCount; i++){
            char winner = simulationPolicy.playout(board, node);
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
