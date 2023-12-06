package javaV.common;

public class Move {
    public int x;
    public int y;
    public Move(int yNew, int xNew){
        x = xNew;
        y = yNew;
    }

    @Override
    public int hashCode(){
        return x * 31 + y;
    }

    @Override
    public boolean equals (Object o){
        return hashCode() == o.hashCode();
    }
}
