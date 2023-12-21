 /*
 * @author Renzo Svartz
 */

package src;

import java.util.*;

import src.Main.ORIENTATION;

/**
 * Class implementing a vertex
 */
public class Custom_Vertex
{
	//vertex_name, paths_list (adjacency list), predecessor, visited, cost
	
	private int id;
	private String vertex_coor;
	private LinkedList<ORIENTATION> paths_list;
	private Custom_Vertex predecessor;
	//private HashMap<String, ORIENTATION> list_of_froms;
	private HashMap<String, ORIENTATION> list_of_tos;
	private HashMap<String, ArrayList<String>> list_of_hallways;
	private boolean visited;
	private int cost;
	
	//get_name, visit, unvisit, is_visited, get set has predecessor, get set Cost
	
	/**
	 * Parameterized Constructor
	 * @param vertex_name vertex name
	 */
	public Custom_Vertex(int id, String vertex_coor, int cost)
	{
		this.id = id;
		this.vertex_coor = vertex_coor;
		this.paths_list = new LinkedList<ORIENTATION>();
		this.predecessor = null;
		//this.list_of_froms = new HashMap<>();
		this.list_of_tos = new HashMap<>();
		this.visited = false;
		this.cost = cost;
		this.list_of_hallways = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * Parameterized Constructor
	 * @param vertex_name vertex name
	 */
	public Custom_Vertex(int id, String vertex_coor, int cost, Custom_Vertex predecessor, ORIENTATION from, ORIENTATION to)
	{
		this.id = id;
		this.vertex_coor = vertex_coor;
		this.paths_list = new LinkedList<ORIENTATION>();
		this.predecessor = predecessor;
		//this.list_of_froms = new HashMap<>();
		this.list_of_tos = new HashMap<>();
		//list_of_froms.put(predecessor.toString(), from);
		list_of_tos.put(predecessor.toString(), to);
		this.visited = false;
		this.cost = cost;
		this.list_of_hallways = new HashMap<String, ArrayList<String>>();
	}

	/**
	 * Parameterized Constructor used for creating vertices to apply djikstra's algo.
	 * @param vertex_name vertex name
	 */
	public Custom_Vertex(String vertex_coor, int cost, Custom_Vertex predecessor, HashMap<String, ORIENTATION> list_of_tos)
	{
		this.id = id;
		this.vertex_coor = vertex_coor;
		this.paths_list = null;
		this.predecessor = predecessor;
		//this.list_of_froms = null;
		this.list_of_tos = list_of_tos;
		this.visited = false;
		this.cost = cost;
		this.list_of_hallways = new HashMap<String, ArrayList<String>>();
	}
	
	public ORIENTATION reverse(ORIENTATION orientation) 
	{
		switch(orientation)
        {
            case NORTH: return ORIENTATION.SOUTH;
        
            case EAST:  return ORIENTATION.WEST;
        
            case SOUTH: return ORIENTATION.NORTH;
        
            case WEST:  return ORIENTATION.EAST;

			default:
					return null;
        }
	}

	public void add_hallways(String destination, ArrayList<String> hallways) 
	{
		list_of_hallways.put(destination, hallways);
	}

	public ArrayList<String> get_hallways(String destination)
	{
		return list_of_hallways.get(destination);
	}

	/** Adds a path to the path list. */
	public void add_path(ORIENTATION path) 
	{
		paths_list.add(path);
	}

	public LinkedList<ORIENTATION> get_paths()
	{
		return paths_list;
	}

	/** Gets this vertex's name.
	@return vertex name */
	public String get_coor()
	{
		return vertex_coor;
	}
	
	/** Marks this vertex as visited. */
	public void visit()
	{
		visited = true;
	}
	
	/** Removes this vertex's visited mark. */
	public void unvisit()
	{
		visited = false;
	}
	
	/** Sees whether this vertex is marked as visited.
	@return True if the vertex is visited. */
	public boolean is_visited()
	{
		return visited;
	}
	
	/** Records the previous vertex on a path to this vertex.
	@param predecessor The vertex previous to this one along a path. */
	public void set_predecessor(Custom_Vertex predecessor)
	{
		this.predecessor = predecessor;
	}
	
	/** Gets the recorded predecessor of this vertex.
	@return Either this vertex's predecessor or null if no predecessor
	was recorded. */
	public Custom_Vertex get_predecessor()
	{
		return predecessor;
	}
	
	/** Sees whether a predecessor was recorded for this vertex.
	@return True if a predecessor was recorded. */
	public boolean has_predecessor()
	{
		return predecessor != null;
	}
	
	/** Records the cost of a path to this vertex.
	@param cost The cost of the path. */
	public void set_cost(int cost)
	{
		this.cost = cost;
	}
	
	/** Gets the recorded cost of the path to this vertex.
	@return The cost of the path. */
	public int get_cost()
	{
		return cost;
	}
	
	/**
	 * equals method tests labels
	 */
	@Override
	public boolean equals(Object o2)
	{
		if (o2 == null || getClass() != o2.getClass())
		{
			return false;
		}
		else
		{
			return vertex_coor.equals(((Custom_Vertex) o2).get_coor());
		}
	}

    public boolean update(int current_cost, Custom_Vertex new_predecessor, ORIENTATION from, ORIENTATION to) 
	{
		//update this vertex's knowledge of this new other vertex
		//list_of_froms.put(new_predecessor.toString(), from);
		list_of_tos.put(new_predecessor.toString(), to);

		//update optimal predecessor
		if (current_cost < this.cost)
		{
			System.err.println(current_cost + " vs " + this.cost);
			this.cost = current_cost;
			this.predecessor = new_predecessor;
			return true;
		}
		else
		{
			return false;
		}
    }

	public boolean update_cost(int current_cost, Custom_Vertex current_predecessor) 
	{
		if (current_cost < this.cost)
		{
			System.err.println(current_cost + " vs " + this.cost);
			this.cost = current_cost;
			this.predecessor = current_predecessor;
			return true;
		}
		else
		{
			return false;
		}
    }

	public String toString()
	{
		return get_coor();
	}

	/*
	public ORIENTATION get_from(String target_vertex) 
	{
		return list_of_froms.get(target_vertex);
	}
	*/

	public ORIENTATION get_to(String target_vertex) 
	{
		return list_of_tos.get(target_vertex);
	}

	public HashMap<String, ORIENTATION> get_tos()
	{
		return list_of_tos;
	}

	public void add_to(String to, ORIENTATION heading) 
	{
		list_of_tos.put(to, heading);
	}

	/*
	public void add_from(String from, ORIENTATION heading) 
	{
		list_of_froms.put(from, heading);
	}
	*/
}
