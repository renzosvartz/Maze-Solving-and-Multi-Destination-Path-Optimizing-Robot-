 /*
 * @author Renzo Svartz
 */

package src;

import src.Main.ORIENTATION;

/**
 * Class implementing a vertex
 */
public class Hallway
{
	//hallway_name, vertex_a, cost_a,  vertex_b, cost_b
	
	private String hallway_coor;
	
    private Custom_Vertex vertex_a;
    private int cost_a;
    private ORIENTATION to_a;
    private ORIENTATION from_a;

    private Custom_Vertex vertex_b;
    private int cost_b;
    private ORIENTATION to_b;
    private ORIENTATION from_b;

	private boolean visited;
	
	//get_name, visit, unvisit, is_visited, get set has predecessor, get set Cost
	
	/**
	 * Parameterized Constructor
	 * @param vertex_name vertex name
	 */
	public Hallway(String hallway_coor, Custom_Vertex vertex_a, int cost_a, ORIENTATION to_a, ORIENTATION from_a)
	{
		this.hallway_coor = hallway_coor;
        this.vertex_a = vertex_a;
		this.cost_a = cost_a;
        this.to_a = to_a;
        this.from_a = from_a;

        this.vertex_b = null;
        this.cost_b = Integer.MAX_VALUE;
        this.to_b = null;
        this.from_b = null;

        this.visited = false;
	}

    public Custom_Vertex get_a()
    {
        return vertex_a;
    }

	/** Gets the recorded cost_a of the path to this vertex.
	@return The cost_a of the path. */
	public int get_cost_a()
	{
		return cost_a;
	}

    public ORIENTATION get_to_a() 
	{
		return to_a;
	}

    public ORIENTATION get_from_a() 
	{
		return from_a;
	}

    public void add_vertex_b(Custom_Vertex vertex_b, int cost_b, ORIENTATION to_b, ORIENTATION from_b) 
	{
		this.vertex_b = vertex_b;
        this.cost_b = cost_b;
        this.to_b = to_b;
        this.from_b = from_b;
	}

    public Custom_Vertex get_b()
    {
        return vertex_b;
    }

	/** Gets the recorded cost_a of the path to this vertex.
	@return The cost_a of the path. */
	public int get_cost_b()
	{
		return cost_b;
	}

    public ORIENTATION get_to_b() 
	{
		return to_b;
	}

    public ORIENTATION get_from_b() 
	{
		return from_b;
	}
	
    /** Marks this vertex as visited. */
	public void visit()
	{
		visited = true;
	}
	
	/** Sees whether this vertex is marked as visited.
	@return True if the vertex is visited. */
	public boolean is_visited()
	{
		return visited;
	}

	public String toString()
	{
		return hallway_coor;
	}
}
