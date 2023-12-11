package javaV.common;

public class Move {
    public int x;
    public int y;
    public char colour;
    public Move(int yNew, int xNew, char colourNew){
        x = xNew;
        y = yNew;
        colour = colourNew;
    }

    @Override
    public int hashCode(){
        return (x * 31 + y * 48 + (int) colour);

    }

    @Override
    public boolean equals (Object o){
        if(! (o instanceof Move)) return false;
        Move move = (Move) o;
        return move.x == x && move.y == y && move.colour == colour;
    }
}
