package javaV.common;

import java.util.LinkedList;
import java.util.Queue;

public class UnionFind {
    private final int[] parentArr;
    private final int[] rank;

    public UnionFind(int size){
        parentArr = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++){
            parentArr[i] = i;
            rank[i] = 0;
        }
    }
    // Copy constructor
    public UnionFind(UnionFind prev){
        parentArr = prev.parentArr.clone();
        rank = prev.parentArr.clone();
    }

    // Find with Path compression
    public int find(int i){
        int curI = i;
        int parent = parentArr[curI];
        final Queue<Integer> path = new LinkedList<>();
        while (parent != curI){
            path.add(curI);
            curI = parent;
            parent = parentArr[curI];
        }
        for (final int idx : path) {
            parentArr[idx] = parent;
        }
        return parent;
    }
    // Union with Union by rank
    public void union(int a, int b){
        final int aRep = find(a);
        final int bRep = find(b);
        if (aRep == bRep){
            return;
        }
        final int aRank = rank[aRep];
        final int bRank = rank[bRep];
        // path compress straight away
        if (aRank < bRank){
            parentArr[aRep] = bRep;
            parentArr[a] = bRep;
        }
        else if(aRank > bRank){
            parentArr[bRep] = aRep;
            parentArr[b] = aRep;
        }
        else{
            parentArr[aRep] = bRep;
            parentArr[a] = bRep;
            rank[bRep]++;
        }

    }

}
