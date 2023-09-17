package src;

public class Location
{
    public int x;
    public int y;

    public Location()
    {
        x = 0;
        y = 0;
    }

    public int get_x()
    {
        return x;
    }

    public int get_y()
    {
        return y;
    }

    public void print() 
    {
        System.err.println("(" + x + ", " + y + ")");
    }

    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }

}