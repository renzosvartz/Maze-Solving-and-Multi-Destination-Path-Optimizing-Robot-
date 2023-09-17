package src;

public class Cost 
{
    private int cost;

    public Cost()
    {
        this.cost = 0;
    }

    public int get_cost()
    {
        return cost;
    }

    public void add_cost(int cost)
    {
        this.cost += cost;
    }

    public void set_cost(int cost)
    {
        this.cost = cost;
    }
    
}
