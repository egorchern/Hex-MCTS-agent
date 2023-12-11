package javaV;

import javaV.common.Common;
import javaV.common.Move;
import javaV.common.SimulationResult;
import javaV.policies.patterns.Bridge;
import javaV.policies.simulation.BridgeOpponentOnly;
import javaV.policies.simulation.BridgePattern;
import javaV.policies.simulation.RandomPlayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SimulationThread implements Runnable{
    public final char[][] board;
    public final Move lastMove;
    public final int simulationsCount;
    public final Map<Move, Move> LGR1Map = new HashMap<>();
    public final Map<List<Move>, Move> LGR2Map = new HashMap<>();
    private final BridgePattern simulationPolicy = new BridgePattern(ThreadLocalRandom.current(), LGR1Map, LGR2Map);
    public SimulationResult simulationResult = new SimulationResult();

    public SimulationThread(char[][] board, Move lastMove, int simulationsCount){
        this.board = Common.copy2dArray(board);
        this.lastMove = lastMove;
        this.simulationsCount = simulationsCount;
    }
    @Override
    public void run(){
        int localRWins = 0;
        int localBWins = 0;
        for(int i = 0; i < simulationsCount; i++){
            char winner = simulationPolicy.playout(board, lastMove);
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
