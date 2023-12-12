package javaV.common;

public class SimulationResult {
    public int rWins = 0;
    public int bWins = 0;
    public SimulationWins[][] amafStats;
    public SimulationResult(int size){
        amafStats = new SimulationWins[size][size];
        for (int idy = 0; idy < size; idy++){
            for(int idx = 0; idx < size; idx++){
                amafStats[idy][idx] = new SimulationWins();
            }
        }
    }

}
