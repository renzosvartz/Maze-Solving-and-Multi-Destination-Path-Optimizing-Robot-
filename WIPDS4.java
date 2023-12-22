package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.Main.ORIENTATION;

public class WIPDS4
{

    private ORIENTATION orientation;
    private Location location;
    private int current_cost;

    private int optimal_cost;
    private Stack<String> best_path;

    private HashMap<String, Custom_Vertex> vertices;
    private static int vertex_id = 1;
    private HashMap<String, String> ids;
    private HashMap<String, Integer> edges;

    private HashMap<String, Hallway> hallways;
    private HashMap<String, ArrayList<String>> point_to_point_hallways;

    private PriorityQueue<Custom_Vertex> pq;

    public WIPDS4()
    {
        orientation = ORIENTATION.NORTH;
        location = new Location();
        current_cost = 0;

        optimal_cost = Integer.MAX_VALUE;
        best_path = new Stack<>();

        vertices = new HashMap<>();
        ids = new HashMap<>();
        edges = new HashMap<>();

        hallways = new HashMap<>();
        point_to_point_hallways = new HashMap<String, ArrayList<String>>();
        
        pq = new PriorityQueue<>((o1, o2) -> o1.get_cost() - o2.get_cost());
    }

    public void main() 
    {

        API.setColor(7, 7, 'C');
        API.setColor(8, 7, 'C');
        API.setColor(7, 8, 'C');
        API.setColor(8, 8, 'C');

        Custom_Vertex current_vertex = first_vertex();

        boolean done = false;

        while (!done && !pq.isEmpty())
        {
            Custom_Vertex vertex_to_visit = pq.poll();

            log("Traveling from " + ids.get(current_vertex.toString()) + " to " + ids.get(vertex_to_visit.toString()));
            
            head_to(current_vertex, vertex_to_visit, true);
            
            vertex_to_visit.visit();
            API.setColor(location.x, location.y, 'G');
            search_paths(vertex_to_visit);

            current_vertex = vertex_to_visit;
        }

        djikstras_solution();

        log("Done!");

        move_mouse();

    }

    //moving from x to x is unhandled
    public void move_mouse() 
    {
        API.ackReset();
        location.x = 0;
        location.y = 0;
        orientation = ORIENTATION.NORTH;

        //log("(In progress) You can now tell the mouse to move wherever you want it to go by typing coordintes into the terminal and pressing enter, like \"(x, y)\".");
        log("Now moving between random points. Working on reading user input for this, soon.");
        API.clearAllColor();

        //Scanner keyboard = new Scanner(System.in);
        Random random = new Random();

        ArrayList<String> destinations = new ArrayList<>();
        destinations.add("(11, 4)");
        destinations.add("(12, 10)");
        destinations.add("(2, 15)");
        destinations.add("(4, 6)");
        destinations.add("(" + random.nextInt(16) + ", " + random.nextInt(16) + ")");
        destinations.add("(" + random.nextInt(16) + ", " + random.nextInt(16) + ")");
        destinations.add("(" + random.nextInt(16) + ", " + random.nextInt(16) + ")");

        
        for (String destination : destinations)
        {
            //System.out.println("Where would you like to go? Enter coordinates \"(x, y)\". Or type \"x\" to exit.");
            //destination = API.getString("");

            point_to_point(location.toString(), destination);
        }
        
    }

    public void highlight_hallway(Custom_Vertex current, String pathway)
    {

        log("Highlighting to " + current.toString());
     
        Stack<String> point_to_points = new Stack<>();

        while (current.get_predecessor() != null)
        {
            point_to_points.push(current.toString() + " " + current.get_predecessor().toString());
            log("Added highlight from " + current.toString() + " to " + current.get_predecessor().toString());
            current = current.get_predecessor();
        }

        point_to_points.push(pathway);

        for (String hallways: point_to_points)
        {
            for (String hallway : point_to_point_hallways.get(pathway))
            {
                int[] hallway_coor = parseCoordinates(hallway);
                API.setColor(hallway_coor[0], hallway_coor[1], 'R');
            }
        }

        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    public void point_to_point(String source, String destination) 
    {
        log("Traveling from " + source + " to " + destination);

        int[] source_coor = parseCoordinates(source);
        int[] destination_coor = parseCoordinates(destination);

        API.setColor(source_coor[0], source_coor[1], 'o');
        API.setColor(destination_coor[0], destination_coor[1], 'o');

        //both vertex
        if (ids.containsKey(source) && ids.containsKey(destination))
        {
            head_to(vertices.get(source), vertices.get(destination), false);
        }

        //destination is not a vertex
        else if (ids.containsKey(source) && !ids.containsKey(destination))
        {
            Hallway hallway = hallways.get(destination);
            Custom_Vertex vertex_a = hallway.get_a();
            Custom_Vertex vertex_b = hallway.get_b();

            if (vertex_b == null)
            {
                head_to(vertices.get(source), vertex_a, false);
                
                reorient(hallway.get_from_a());

                move_to_hallway(destination);
            }
            else
            {
                Cost cost_a = new Cost();
                djikstras_best_path_between(vertices.get(source), vertex_a, cost_a, false);
                cost_a.add_cost(hallway.get_cost_a());

                Cost cost_b = new Cost();
                djikstras_best_path_between(vertices.get(source), vertex_b, cost_b, false);
                cost_b.add_cost(hallway.get_cost_b());

                if (cost_a.get_cost() <= cost_b.get_cost())
                {
                    head_to(vertices.get(source), vertex_a, false);

                    reorient(hallway.get_from_a());

                    move_to_hallway(destination);
                }
                else
                {
                    head_to(vertices.get(source), vertex_b, false);

                    reorient(hallway.get_from_b());

                    move_to_hallway(destination);
                }
            }
        }

        //source is not a vertex
        else if (!ids.containsKey(source) && ids.containsKey(destination))
        {
            Hallway hallway = hallways.get(source);
            Custom_Vertex vertex_a = hallway.get_a();
            Custom_Vertex vertex_b = hallway.get_b();

            if (vertex_b == null)
            {
                reorient(hallway.get_to_a());

                find_next_vertex();

                head_to(vertex_a, vertices.get(destination), false);
            }
            else
            {
                Cost cost_a = new Cost();
                djikstras_best_path_between(vertex_a, vertices.get(destination), cost_a, false);
                cost_a.add_cost(hallway.get_cost_a());

                Cost cost_b = new Cost();
                djikstras_best_path_between(vertex_b, vertices.get(destination), cost_b, false);
                cost_b.add_cost(hallway.get_cost_b());

                if (cost_a.get_cost() <= cost_b.get_cost())
                {
                    reorient(hallway.get_to_a());

                    find_next_vertex();

                    head_to(vertex_a, vertices.get(destination), false);
                }
                else
                {
                    reorient(hallway.get_to_b());

                    find_next_vertex();

                    head_to(vertex_b, vertices.get(destination), false);
                }
            }
        }

        //both hallways
        else
        {

            Hallway source_hallway = hallways.get(source);
            Custom_Vertex vertex_sa = source_hallway.get_a();
            Custom_Vertex vertex_sb = source_hallway.get_b();

            Hallway destination_hallway = hallways.get(destination);
            Custom_Vertex vertex_da = destination_hallway.get_a();
            Custom_Vertex vertex_db = destination_hallway.get_b();
        
            ArrayList<Integer> costs = new ArrayList<>();


            Cost cost_aa = new Cost();
            djikstras_best_path_between(vertex_sa, vertex_da, cost_aa, false);
            cost_aa.add_cost(source_hallway.get_cost_a());
            cost_aa.add_cost(destination_hallway.get_cost_a());
            costs.add(cost_aa.get_cost());

            if (destination_hallway.get_b() != null)
            {
                Cost cost_ab = new Cost();
                djikstras_best_path_between(vertex_sa, vertex_db, cost_ab, false);
                cost_ab.add_cost(source_hallway.get_cost_a());
                cost_ab.add_cost(destination_hallway.get_cost_b());
                costs.add(cost_ab.get_cost());
            }
            else
            {
                costs.add(Integer.MAX_VALUE);
            }

            if (source_hallway.get_b() != null)
            {
                Cost cost_ba = new Cost();
                djikstras_best_path_between(vertex_sb, vertex_da, cost_ba, false);
                cost_ba.add_cost(source_hallway.get_cost_b());
                cost_ba.add_cost(destination_hallway.get_cost_a());
                costs.add(cost_ba.get_cost());
            }
            else
            {
                costs.add(Integer.MAX_VALUE);
            }

            if (source_hallway.get_b() != null && destination_hallway.get_b() != null)
            {
                Cost cost_bb = new Cost();
                djikstras_best_path_between(vertex_sb, vertex_db, cost_bb, false);
                cost_bb.add_cost(source_hallway.get_cost_b());
                cost_bb.add_cost(destination_hallway.get_cost_b());
                costs.add(cost_bb.get_cost());
            }
            else
            {
                costs.add(Integer.MAX_VALUE);
            }

            switch(find_min_index(costs))
            {
                case 0:
                        reorient(source_hallway.get_to_a());

                        find_next_vertex();

                        head_to(vertex_sa, vertex_da, false);

                        reorient(destination_hallway.get_from_a());

                        move_to_hallway(destination);

                        break;

                case 1:
                        reorient(source_hallway.get_to_a());

                        find_next_vertex();

                        head_to(vertex_sa, vertex_db, false);

                        reorient(destination_hallway.get_from_b());

                        move_to_hallway(destination);

                        break;
                
                case 2:
                        reorient(source_hallway.get_to_b());

                        find_next_vertex();

                        head_to(vertex_sb, vertex_da, false);

                        reorient(destination_hallway.get_from_a());

                        move_to_hallway(destination);

                        break;

                case 3:

                        reorient(source_hallway.get_to_b());

                        find_next_vertex();

                        head_to(vertex_sb, vertex_db, false);

                        reorient(destination_hallway.get_from_b()); //************ */

                        move_to_hallway(destination);

                        break;

            }

        }

        API.clearColor(source_coor[0], source_coor[1]);
        API.clearColor(destination_coor[0], destination_coor[1]);

    }

    public int find_min_index(ArrayList<Integer> list) 
    {
        if (list == null || list.isEmpty()) 
        {
            throw new IllegalArgumentException("List is empty or null.");
        }

        int min = list.get(0);
        int index = 0;

        for (int i = 1; i < list.size(); i++) 
        {
            if (list.get(i) < min) 
            {
                min = list.get(i);
                index = i;
            }
        }

        return index;
    }

    public void move_to_hallway(String destination) 
    {
        move_forward();

        while (paths_available() == 1 && !location.toString().equals(destination))
        {
            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }
        }

    }

    public void highlight_path(Custom_Vertex current, boolean found)
    {

        log("Highlighting to " + current.toString());
     
        Stack<String> point_to_points = new Stack<>();

        while (current.get_predecessor() != null)
        {
            point_to_points.push(current.toString() + " " + current.get_predecessor().toString());
            log("Added highlight from " + current.toString() + " to " + current.get_predecessor().toString());
            current = current.get_predecessor();
        }

        for (String hallways: point_to_points)
        {
            for (String hallway : point_to_point_hallways.get(hallways))
            {
                int[] hallway_coor = parseCoordinates(hallway);

                if (found)
                {
                    API.setColor(hallway_coor[0], hallway_coor[1], 'g');
                }
                else
                    API.setColor(hallway_coor[0], hallway_coor[1], 'R');
            }
        }

        try
        {
            Thread.sleep(50);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        if (found)
        {
            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void recolor_path(Custom_Vertex current)
    {
        log("Recoloring from " + current.toString());
     
        Stack<String> point_to_points = new Stack<>();

        while (current.get_predecessor() != null)
        {
            point_to_points.push(current.toString() + " " + current.get_predecessor().toString());
            current = current.get_predecessor();
        }
        
        for (String hallways: point_to_points)
        {
            for (String hallway : point_to_point_hallways.get(hallways))
            {
                int[] hallway_coor = parseCoordinates(hallway);

                if (vertices.containsKey(hallway) && vertices.get(hallway).is_visited())
                {
                    API.setColor(hallway_coor[0], hallway_coor[1], 'G');
                }
                else if (vertices.containsKey(hallway))
                {
                    API.setColor(hallway_coor[0], hallway_coor[1], 'Y');
                }
                else
                {
                    API.clearColor(hallway_coor[0], hallway_coor[1]);
                }
            }
        }
    }

    public Stack<String> djikstras_best_path_between(Custom_Vertex source_vertex, Custom_Vertex destination_vertex, Cost path_cost, boolean color)
    {
        if (source_vertex == null || destination_vertex == null)
        {
            if (path_cost != null)
            {
                path_cost.set_cost(Integer.MAX_VALUE);
            }

            return null;
        }

        PriorityQueue<Custom_Vertex> d_pq = new PriorityQueue<>((o1, o2) -> o1.get_cost() - o2.get_cost());
        HashMap<String, Custom_Vertex> d_vertices = new HashMap<>();
        Stack<String> stack_of_vertex_names = new Stack<>();

        log("Finding best path between " + source_vertex.toString() + " and " + destination_vertex.toString());

        Custom_Vertex first_vertex = new Custom_Vertex(source_vertex.toString(), 0, null, source_vertex.get_tos());
        first_vertex.visit();
        d_vertices.put(first_vertex.toString(), first_vertex);

        //add first vertex's neighbors to the pq
        for (String neighbor_name : first_vertex.get_tos().keySet())
        {
            log("Adding " + ids.get(source_vertex.toString()) +"'s neighbor " + ids.get(neighbor_name) + " to the djikstra's pq");

            //create new djikstra's motivated vertex
            Custom_Vertex temp_neighbor = vertices.get(neighbor_name);
            Custom_Vertex djikstra_vertex = new Custom_Vertex(neighbor_name, edges.get(first_vertex.toString() + " " + neighbor_name), first_vertex, temp_neighbor.get_tos());

            //add vertices to a hashmap set and priority queue
            d_vertices.put(neighbor_name, djikstra_vertex);
            d_pq.add(djikstra_vertex);
        }

        while (!d_pq.isEmpty())
        {
            Custom_Vertex current_vertex = d_pq.poll();
            current_vertex.visit();
            log("Evaluating " + current_vertex.toString() + " by djikstra's algorithm.");

            if (current_vertex.equals(destination_vertex))
            {
                log("Found the fastest way to " + ids.get(destination_vertex.toString()) + " from " + ids.get(source_vertex.toString()));

                if (color)
                {
                    highlight_path(current_vertex, true);
                    recolor_path(current_vertex);
                }

                //optionally: add up costs to return cost for comparison
                if (path_cost != null)
                {
                    path_cost.set_cost(current_vertex.get_cost());
                }

                while (!current_vertex.equals(source_vertex))
                {
                    //Stack vertices, followed by their predecessors.
                    log("Stacking " + current_vertex.toString() + " to path.");
                    stack_of_vertex_names.push(current_vertex.toString());

                    //update current vertex
                    current_vertex = current_vertex.get_predecessor();

                }

                return stack_of_vertex_names;
            }

            for (String neighbor_name : current_vertex.get_tos().keySet())
            {
                //log("Checking " + ids.get(current_vertex.toString()) +"'s neighbor " + ids.get(neighbor_name) + " to the djikstra's pq");
                
                //neighbor exists in the priority queue
                if (d_vertices.containsKey(neighbor_name))
                {

                    Custom_Vertex existing_vertex = d_vertices.get(neighbor_name);

                    //if the neighbor has been visited, the neighbor has been handled already, so continue
                    if (existing_vertex.is_visited())
                    {
                        if (color)
                        {
                            highlight_path(current_vertex, false);
                            recolor_path(current_vertex);
                        }
                        continue;
                    }

                    //if this existing vertex now has a more optimal cost, due to this new connection, "reset" it in the pq.
                    if (existing_vertex.update_cost(current_vertex.get_cost() +  edges.get(current_vertex.toString() + " " + neighbor_name), current_vertex))
                    {
                        log("Node " + ids.get(existing_vertex.toString()) + " has found a better predecessor, node " + ids.get(current_vertex.toString()));

                        //update pq
                        d_pq.remove(existing_vertex);
                        d_pq.add(existing_vertex);
                    }
                    else
                    {
                        log("Node " + ids.get(existing_vertex.toString()) + " was refound by node " + ids.get(current_vertex.toString()) + " but at a higher cost, so it was not readded to the pq.");
                    }
                    

                }
                else //neighbor does not exist in the priority queue
                {
                    //create new djikstra's motivated vertex
                    Custom_Vertex temp_neighbor = vertices.get(neighbor_name);
                    Custom_Vertex djikstra_vertex = new Custom_Vertex(neighbor_name, current_vertex.get_cost() + edges.get(current_vertex.toString() + " " + neighbor_name), current_vertex, temp_neighbor.get_tos());

                    //add vertices to hashmap and priority queue
                    d_vertices.put(neighbor_name, djikstra_vertex);
                    d_pq.add(djikstra_vertex);
                }

                if (color)
                {
                    highlight_path(current_vertex, false);
                    recolor_path(current_vertex);
                }
            }
        }

        return null;
    }

    public void head_to(Custom_Vertex current_vertex, Custom_Vertex vertex_to_visit, boolean color) 
    {
        Stack<String> stack_of_vertex_names = djikstras_best_path_between(current_vertex, vertex_to_visit, null, color);

        while (!current_vertex.equals(vertex_to_visit))
        {

            log("\t Moving from " + ids.get(current_vertex.toString()) + " to " + ids.get(stack_of_vertex_names.peek()));

            //move
            move_towards_heading(current_vertex.get_to(stack_of_vertex_names.peek()));

            //update current vertex
            current_vertex = vertices.get(stack_of_vertex_names.pop());
        }
    }

    public void move_towards_heading(ORIENTATION heading) 
    {
        reorient(heading);
        find_next_vertex();
    }

    public void find_next_vertex() 
    {
        move_forward();

        while (paths_available() == 1)
        {
            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }
        }
    }

    public Custom_Vertex first_vertex() 
    {

        Custom_Vertex current_vertex = new Custom_Vertex(vertex_id, location.toString(), current_cost);

        vertices.put(location.toString(), current_vertex);
        ids.put(location.toString(), "" + vertex_id);

        API.setColor(location.x, location.y, 'G');
        API.setText(location.x, location.y, "" + vertex_id++);

        search_paths(current_vertex);

        current_vertex.visit();

        return current_vertex;
    }

    public void search_paths(Custom_Vertex current_vertex)
    {
        log("Cost of " + ids.get(current_vertex.toString()) + ": " + current_vertex.get_cost());

        ORIENTATION original_orientation = orientation;

        enque_paths(current_vertex);

        for (ORIENTATION path: current_vertex.get_paths())
        {
            reorient(original_orientation);
            current_cost = 0;
            
            switch(path)
            {
                case NORTH: reorient(ORIENTATION.NORTH);
                            log("Reorienting & Searching NORTH");
                            search(current_vertex, ORIENTATION.NORTH);
                            break;
            
                case EAST:  reorient(ORIENTATION.EAST);
                            log("Reorienting & Searching EAST");
                            search(current_vertex, ORIENTATION.EAST);
                            break;
            
                case SOUTH: reorient(ORIENTATION.SOUTH);
                            log("Reorienting & Searching SOUTH");
                            search(current_vertex, ORIENTATION.SOUTH);
                            break;
            
                case WEST:  reorient(ORIENTATION.WEST);
                            log("Reorienting & Searching WEST");
                            search(current_vertex, ORIENTATION.WEST);
                            break;
            }
        }
    }
    
    public void search(Custom_Vertex current_vertex, ORIENTATION heading) 
    {

        ArrayList<String> vertex_hallways = new ArrayList<>();
        vertex_hallways.add(location.toString());
        move_forward();

        while (paths_available() == 1)
        {

            if (!ids.containsKey(location.toString()))
            {
                API.setText(location.x, location.y, ".");

                if (!hallways.containsKey(location.toString()))
                {
                    Hallway new_hallway = new Hallway(location.toString(), current_vertex, current_cost, reverse(orientation), heading);
                    hallways.put(location.toString(), new_hallway);
                }
            }

            vertex_hallways.add(location.toString());
            point_to_point_hallways.put(current_vertex.toString() + " " + location.toString(), vertex_hallways);
            point_to_point_hallways.put(location.toString() + " " + current_vertex.toString(), vertex_hallways);

            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }

            if (current_vertex.get_cost() + current_cost >= optimal_cost)
            {
                log("This search exceeds the optimal cost and is no longer worth following.");
                //API.setColor(location.x, location.y, 'O');
                //return_to_vertex(current_vertex.toString());
                //return;
            }
        }

        if (paths_available() == 0)
        {
            log("Deadend.");
            Hallway new_hallway = new Hallway(location.toString(), current_vertex, current_cost, reverse(orientation), heading);
            hallways.put(location.toString(), new_hallway);
            hallways.get(location.toString()).visit();
            vertex_hallways.add(location.toString());
            point_to_point_hallways.put(current_vertex.toString() + " " + location.toString(), vertex_hallways);
            point_to_point_hallways.put(location.toString() + " " + current_vertex.toString(), vertex_hallways);
            return_to_vertex(current_vertex.toString(), null);
        }

        else if (paths_available() >= 2)
        {

            //vertex exists
            if (vertices.containsKey(location.toString()))
            {

                //if this vertex has been visited, this vertex knows everthing, so return to source vertex
                //else update the vertex -- 
                if (!vertices.get(location.toString()).is_visited())
                {
                    Custom_Vertex existing_vertex = vertices.get(location.toString());

                    //update source vectors knowledge of this existing vertex. Wait until this existing vertex completes it's own search to update it's knowledge of the source vertex to avoid confusion.
                    current_vertex.add_to(existing_vertex.toString(), heading);
                    //current_vertex.add_from(existing_vertex.toString(), reverse(orientation));
                    existing_vertex.add_to(current_vertex.toString(), reverse(orientation));
                    //existing_vertex.add_from(current_vertex.toString(), heading);
                    log(ids.get(current_vertex.toString()) + " and " + ids.get(existing_vertex.toString()) + " know how to get to eachother now for " + (current_vertex.get_cost() + current_cost));
                    
                    edges.put(current_vertex.toString() + " " + existing_vertex.toString(), current_cost);
                    edges.put(existing_vertex.toString() + " " + current_vertex.toString(), current_cost);

                    //if this existing vertex now has a more optimal cost, due to this new connection, "reset" it in the pq.
                    if (existing_vertex.update(current_vertex.get_cost() + current_cost, current_vertex, heading, reverse(orientation)))
                    {
                        log("Node " + ids.get(existing_vertex.toString()) + " has found a better predecessor, node " + ids.get(current_vertex.toString()) + " with " + (current_vertex.get_cost() + current_cost));

                        //update pq
                        pq.remove(existing_vertex);
                        pq.add(existing_vertex);
                    }
                }
            }
            else //vertex does not exist
            {
                
                Custom_Vertex new_vertex = new Custom_Vertex(vertex_id, location.toString(), current_vertex.get_cost() + current_cost, current_vertex, heading, reverse(orientation));
                vertices.put(location.toString(), new_vertex);
                ids.put(location.toString(), "" + vertex_id);

                //update verteces knowledge of how to get to/from eachother
                current_vertex.add_to(location.toString(), heading);
                //current_vertex.add_from(location.toString(), reverse(orientation));
                new_vertex.add_to(current_vertex.toString(), reverse(orientation));
                //new_vertex.add_from(current_vertex.toString(), heading);
                log(ids.get(current_vertex.toString()) + " and " + ids.get(location.toString()) + " know how to get to eachother now for " + (current_vertex.get_cost() + current_cost));

                //store cost between these vertices
                edges.put(current_vertex.toString() + " " + location.toString(), current_cost);
                edges.put(location.toString() + " " + current_vertex.toString(), current_cost);

                API.setColor(location.x, location.y, 'Y');
                API.setText(location.x, location.y, "" + vertex_id++);

                //check end
                if (check_for_end(current_vertex))
                {
                    new_vertex.visit();
                }
                else //add the vertex to the search
                {
                    pq.add(new_vertex);
                }

            }

            vertex_hallways.add(location.toString());
            point_to_point_hallways.put(current_vertex.toString() + " " + location.toString(), vertex_hallways);
            point_to_point_hallways.put(location.toString() + " " + current_vertex.toString(), vertex_hallways);
            log(current_vertex.toString() + " has hallways to " + location.toString() + " now.");
            return_to_vertex(current_vertex.toString(), vertices.get(location.toString()));
        }
    }

    public boolean check_for_end(Custom_Vertex prev_vertex) 
    {
        if ((location.x == 7 && location.y == 7) || (location.x == 7 && location.y == 8) || (location.x == 8 && location.y == 7) || (location.x == 8 && location.y == 8))
        {
            API.setColor(location.x, location.y, 'C');

            int total_cost = prev_vertex.get_cost() + current_cost;
            String path = "";

            if (total_cost < optimal_cost)
            {
    
                best_path = new Stack<>();

                Custom_Vertex current_vertex = vertices.get(location.toString());

                while (current_vertex != null)
                {
                    best_path.push(current_vertex.toString());

                    path += ids.get(current_vertex.toString()) + " ";

                    current_vertex = current_vertex.get_predecessor();
                }

                optimal_cost = total_cost;
                log("Found optimal path. Cost: " + total_cost + "\n" + "Path:" + (new StringBuilder(path).reverse()));
            }
            else
            {
                log("Found suboptimal path. Shouldn't happen");
            }

            return true;
        }

        return false;
    }

    public void return_to_vertex(String prev_vertex, Custom_Vertex new_vertex) 
    {
        log("Returning to previous vertex, " + ids.get(prev_vertex));

        ArrayList<String> vertex_hallways = new ArrayList<>();
        vertex_hallways.add(location.toString());
        
        //turn around
        API.turnLeft();
        API.turnLeft();
        orientation = reverse(orientation);
        ORIENTATION from_b = orientation;

        //edge case worth exploring -- determining the cost of +0/1 for the turn required (or not) to get from a vertex to a hallway. Check Orientation upon arrival vs Orientation of travel to hallway...
        current_cost = 1;
        move_forward();

        while (paths_available() == 1)
        {
            if (!hallways.get(location.toString()).is_visited() && new_vertex != null)
            {
                Hallway existing_hallway = hallways.get(location.toString());
                existing_hallway.add_vertex_b(new_vertex, current_cost, reverse(orientation), from_b);
                existing_hallway.visit();
                log("Hallway " + location.toString() + " complete.");
            }

            vertex_hallways.add(location.toString());
            if (new_vertex != null)
            {
                point_to_point_hallways.put(new_vertex.toString() + " " + location.toString(), vertex_hallways);
                point_to_point_hallways.put(location.toString() + " " + new_vertex.toString(), vertex_hallways);
            }

            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }
        }

    }

    public int paths_available() 
    {
        int paths_available = 0;

        if (!API.wallFront())
        {
            paths_available++;
        }
        
        if (!API.wallLeft())
        {
            paths_available++;
        }
        
        if (!API.wallRight())
        {
            paths_available++;
        }

        return paths_available;
    }

    public void move_forward() 
    {
        API.moveForward();
        current_cost++;

        switch (orientation)
        {
            case NORTH: location.y++;
                        break;

            case EAST:  location.x++;
                        break;

            case SOUTH: location.y--;
                        break;

            case WEST:  location.x--;
                        break;
        }

        location.print();
    }

    public void move_left() 
    {
        API.turnLeft();
        API.moveForward();
        current_cost += 2;

        switch (orientation)
        {
            case NORTH: orientation = ORIENTATION.WEST;
                        location.x--;
                        break;

            case EAST:  orientation = ORIENTATION.NORTH;
                        location.y++;
                        break;

            case SOUTH: orientation = ORIENTATION.EAST;
                        location.x++;
                        break;

            case WEST:  orientation = ORIENTATION.SOUTH;
                        location.y--;
                        break;
        }

        location.print();
    }

    public void move_right() 
    {
        API.turnRight();
        API.moveForward();
        current_cost += 2;

        switch (orientation)
        {
            case NORTH: orientation = ORIENTATION.EAST;
                        location.x++;
                        break;

            case EAST:  orientation = ORIENTATION.SOUTH;
                        location.y--;
                        break;

            case SOUTH: orientation = ORIENTATION.WEST;
                        location.x--;
                        break;

            case WEST:  orientation = ORIENTATION.NORTH;
                        location.y++;
                        break;
        }

        location.print();
    }

    public void reorient(ORIENTATION target_orientation) 
    {

        switch (orientation)
        {
            case NORTH: north_to(target_orientation);
                        break;
                            
            case EAST:  east_to(target_orientation);                      
                        break;

            case SOUTH: south_to(target_orientation);                       
                        break;

            case WEST:  west_to(target_orientation);        
                        break;   
        }
    }

    public void west_to(ORIENTATION target_orientation) 
    {
        switch (target_orientation)
        {
            case NORTH: API.turnRight();
                        orientation = ORIENTATION.NORTH;
                        current_cost++;
                        break;

            case EAST:  API.turnRight();
                        API.turnRight();
                        current_cost += 2;
                        orientation = ORIENTATION.EAST;
                        break;

            case SOUTH: API.turnLeft();
                        orientation = ORIENTATION.SOUTH;
                        current_cost++;
                        break;

            case WEST:  
                        break;
        }
    }

    public void south_to(ORIENTATION target_orientation) 
    {
        switch (target_orientation)
        {
            case NORTH: API.turnLeft();
                        API.turnLeft();
                        current_cost += 2;
                        orientation = ORIENTATION.NORTH;
                        break;

            case EAST:  API.turnLeft();
                        orientation = ORIENTATION.EAST;
                        current_cost++;
                        break;

            case SOUTH: 
                        break;

            case WEST:  API.turnRight();
                        orientation = ORIENTATION.WEST;
                        current_cost++;
                        break;
        }
    }

    public void east_to(ORIENTATION target_orientation) 
    {
        switch (target_orientation)
        {
            case NORTH: API.turnLeft();
                        orientation = ORIENTATION.NORTH;
                        current_cost++;
                        break;

            case EAST:  
                        break;

            case SOUTH: API.turnRight();
                        orientation = ORIENTATION.SOUTH;
                        current_cost++;
                        break;

            case WEST:  API.turnLeft();
                        API.turnLeft();
                        current_cost += 2;
                        orientation = ORIENTATION.WEST;
                        break;
        }
    }

    public void north_to(ORIENTATION target_orientation)
    {
        switch (target_orientation)
        {
            case NORTH: 
                        break;

            case EAST:  API.turnRight();
                        current_cost++;
                        orientation = ORIENTATION.EAST;
                        break;

            case SOUTH: API.turnRight();
                        API.turnRight();
                        orientation = ORIENTATION.SOUTH;
                        current_cost += 2;
                        break;

            case WEST:  API.turnLeft();
                        orientation = ORIENTATION.WEST;
                        current_cost++;
                        break;
        }
    }

    public void enque_paths(Custom_Vertex current_vertex)
    {
        switch (orientation)
        {
            case NORTH: enque_paths_north(current_vertex);
                        break;

            case EAST: enque_paths_east(current_vertex);
                        break;

            case SOUTH: enque_paths_south(current_vertex);
                        break;

            case WEST: enque_paths_west(current_vertex);
                        break;

        }
    }

    public void enque_paths_north(Custom_Vertex current_vertex) 
    {
        if (!API.wallLeft()) 
        {
            current_vertex.add_path(ORIENTATION.WEST);
        }
        if (!API.wallFront()) 
        {
            current_vertex.add_path(ORIENTATION.NORTH);
        }
        if (!API.wallRight()) 
        {
            current_vertex.add_path(ORIENTATION.EAST);
        }
    }

    public void enque_paths_east(Custom_Vertex current_vertex) 
    {
        if (!API.wallLeft()) 
        {
            current_vertex.add_path(ORIENTATION.NORTH);
        }
        if (!API.wallFront()) 
        {
            current_vertex.add_path(ORIENTATION.EAST);
        }
        if (!API.wallRight()) 
        {
            current_vertex.add_path(ORIENTATION.SOUTH);
        }
    }

    public void enque_paths_south(Custom_Vertex current_vertex) 
    {
        if (!API.wallLeft()) 
        {
            current_vertex.add_path(ORIENTATION.EAST);
        }
        if (!API.wallFront()) 
        {
            current_vertex.add_path(ORIENTATION.SOUTH);
        }
        if (!API.wallRight()) 
        {
            current_vertex.add_path(ORIENTATION.WEST);
        }
    }

    public void enque_paths_west(Custom_Vertex current_vertex) 
    {
        if (!API.wallLeft()) 
        {
            current_vertex.add_path(ORIENTATION.SOUTH);
        }
        if (!API.wallFront()) 
        {
            current_vertex.add_path(ORIENTATION.WEST);
        }
        if (!API.wallRight()) 
        {
            current_vertex.add_path(ORIENTATION.NORTH);
        }
    }

    public void djikstras_solution() 
    {
        log("Djikstra's solution marked in red.");
        API.ackReset();
        location.x = 0;
        location.y = 0;
        orientation = ORIENTATION.NORTH;

        best_path.pop();
        //return_to_first_vertex();

        while(!best_path.isEmpty())
        {
            solution_move(vertices.get(location.toString()).get_to(best_path.pop()));
        }
    }

    public void return_to_first_vertex() 
    {
        while (paths_available() == 1)
        {
            API.setColor(location.x, location.y, 'R');
            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }
        }
    }

    public void solution_move(ORIENTATION heading) 
    {
        reorient(heading);
        solution_next_vertex();
    }

    public void solution_next_vertex() 
    {
        API.setColor(location.x, location.y, 'R');
        move_forward();

        while (paths_available() == 1)
        {
            API.setColor(location.x, location.y, 'R');
            if (!API.wallFront())
            {
                move_forward();
            }
            else if (!API.wallLeft())
            {
                move_left();
            }
            else if (!API.wallRight())
            {
                move_right();
            }
        }
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

    public void print_orientation()
    {
        switch(orientation)
        {
            case NORTH: log("Orientation: NORTH");
                        break;
            
            case EAST:  log("Orientation: EAST");
                        break;
            
            case SOUTH: log("Orientation: SOUTH");
                        break;
            
            case WEST:  log("Orientation: WEST");
                          break;
        }
    }

    /* from chatGPT */
    public int[] parseCoordinates(String coordinateString) 
    {
        int[] coordinates = new int[2];
        Pattern pattern = Pattern.compile("\\((-?\\d+), (-?\\d+)\\)");
        Matcher matcher = pattern.matcher(coordinateString);

        if (matcher.matches()) {
            coordinates[0] = Integer.parseInt(matcher.group(1)); // x-coordinate
            coordinates[1] = Integer.parseInt(matcher.group(2)); // y-coordinate
        } else {
            throw new IllegalArgumentException("Invalid coordinate format: " + coordinateString);
        }

        return coordinates;
    }

    public void log(String text) 
    {
        System.err.println(text);
    }

}
