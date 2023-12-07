package javaV.common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class UnionFind {
    private final Move[][] parentArr;
    private final int[][] rank;

    public UnionFind(int size){
        parentArr = new Move[size][size];
        rank = new int[size][size];
        for (int idy = 0; idy < size; idy++){
            for(int idx = 0; idx < size; idx++){
                parentArr[idy][idx] = new Move(idy, idx);
                rank[idy][idx] = 0;
            }

        }
    }
    // Copy constructor
    public UnionFind(UnionFind prev){
        parentArr = Arrays.stream(prev.parentArr).map(Move[]::clone).toArray(Move[][]::new);
        rank = Arrays.stream(prev.rank).map(int[]::clone).toArray(int[][]::new);
    }

    // Find with Path compression
    public Move find(Move move){
        Move curMove = move;
        Move parent = parentArr[curMove.y][curMove.x];
//        final Queue<Integer> path = new LinkedList<>();
        while (parent != curMove){
//            path.add(curI);
            curMove = parent;
            parent = parentArr[curMove.y][curMove.x];
        }
//        for (final int idx : path) {
//            parentArr[idx] = parent;
//        }
        return parent;
    }
    // Union with Union by rank
    public void union(Move a, Move b){
        final Move aRep = find(a);
        final Move bRep = find(b);
        if (aRep == bRep){
            return;
        }
        final int aRank = rank[aRep.y][aRep.x];
        final int bRank = rank[bRep.y][bRep.x];
        // path compress straight away
        if (aRank < bRank){
            parentArr[aRep.y][aRep.x] = bRep;
            parentArr[a.y][a.x] = bRep;
        }
        else if(aRank > bRank){
            parentArr[bRep.y][bRep.x] = aRep;
            parentArr[b.y][b.x] = aRep;
        }
        else{
            parentArr[aRep.y][aRep.x] = bRep;
            parentArr[a.y][a.x] = bRep;
            rank[bRep.y][bRep.x]++;
        }

    }

}
