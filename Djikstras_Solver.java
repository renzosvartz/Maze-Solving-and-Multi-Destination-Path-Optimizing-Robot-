package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;


//exhaust
//graph
public class Djikstras_Solver 
{
    public enum ORIENTATION 
    {
        NORTH, EAST, SOUTH, WEST
    }

    private ORIENTATION orientation;
    private Location location;
    private double current_cost;
    //private double current_total_cost;
    private double min_cost;
    private Stack<String> best_path;
    private HashMap<String, Custom_Vertex> vertices;
    private PriorityQueue<Custom_Vertex> pq;
    private static int node_number = 1;

    public Djikstras_Solver()
    {
        orientation = ORIENTATION.NORTH;
        location = new Location();
        current_cost = 0;
        min_cost = Double.MAX_VALUE;
        best_path = new Stack<>();
        vertices = new HashMap<>();
        pq = new PriorityQueue<>((o1, o2) -> (int) o1.get_cost() - (int) o2.get_cost());
    }

    public void main() 
    {
        //location.print();
        Custom_Vertex current_vertex = find_first_vertex();

        boolean done = false;

        while (!done && !pq.isEmpty())
        {
            Custom_Vertex vertex_to_visit = pq.poll();
            log("Starting " + current_vertex.toString() + " to " + vertex_to_visit.toString());
            head_to(current_vertex, vertex_to_visit);
            vertex_to_visit.visit();

            enque_paths(vertex_to_visit);
            search_paths(vertex_to_visit);
            current_vertex = vertex_to_visit;
        }

        log(" best path");
        API.ackReset();
        location.x = 0;
        location.y = 0;
        orientation = ORIENTATION.NORTH;
        //location.print();
        return_to_first_vertex();
        //location.print();
        best_path.pop();
        while(!best_path.isEmpty())
        {
            move2(vertices.get(location.toString()).get_to(best_path.pop()));
        }
    }

    private void move2(ORIENTATION heading) 
    {
        reorient(heading);
        find_next_vertex2();
    }

    private void find_next_vertex2() 
    {
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

    private void return_to_first_vertex() 
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

    private void head_to(Custom_Vertex current_vertex, Custom_Vertex vertex_to_visit) 
    {

        Stack<Custom_Vertex> stack = new Stack<>();
        Custom_Vertex seeker = vertex_to_visit;

        while (seeker != null && seeker.get_from(current_vertex.toString()) == null)
        {
            stack.add(seeker);
            log("stacked " + seeker.toString());
            seeker = seeker.get_predecessor();
        }

        while (seeker == null)
        {

            //north to find 4, but cannot use that to return
            move(current_vertex.get_to(current_vertex.get_predecessor().toString()));
            current_vertex = current_vertex.get_predecessor();
            if (current_vertex.get_to(stack.peek().toString()) == null)
            {
                continue;
            }
            else
            {
                seeker = stack.pop();
                break;
            }
        }

        while (!current_vertex.equals(vertex_to_visit))
        {
            
            log(current_vertex.toString() + " to " + seeker.toString());
            move(current_vertex.get_to(seeker.toString()));
            current_vertex = seeker;
            if (!stack.isEmpty())
            {
                seeker = stack.pop();
            }
        }
    }

    private void print_orientation()
    {
        switch(orientation)
        {
            case NORTH: log("xNORTH");
                        break;
            
            case EAST:  log("xEAST");
                        break;
            
            case SOUTH: log("xSOUTH");
                        break;
            
            case WEST:  log("xWEST");
                          break;
        }
    }

    private void move(ORIENTATION heading) 
    {
        reorient(heading);
        find_next_vertex();
    }

    private void find_next_vertex() 
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

    private Custom_Vertex find_first_vertex() 
    {
        while (paths_available() == 1)
        {
            //API.setColor(location.x, location.y, 'R');
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

        if (paths_available() == 0)
        {
            return null;
        }
        else if (paths_available() >= 2)
        {
            Custom_Vertex current_vertex = new Custom_Vertex(location.toString(), current_cost);
            vertices.put(current_vertex.toString(), current_vertex);

            API.setColor(location.x, location.y, 'G');
            API.setText(location.x, location.y, "" + node_number++);

            enque_paths(current_vertex);
            search_paths(current_vertex);
            current_vertex.visit();

            return current_vertex;
        }

        return null;
    }

    private void search_paths(Custom_Vertex current_vertex)
    {
        ORIENTATION original_orientation = orientation;

        for (ORIENTATION path: current_vertex.get_paths())
        {
            reorient(original_orientation);
            current_cost = 0;
            
            switch(path)
            {
                case NORTH: reorient(ORIENTATION.NORTH);
                            search(current_vertex, ORIENTATION.NORTH);
                            break;
            
                case EAST:  reorient(ORIENTATION.EAST);
                            search(current_vertex, ORIENTATION.EAST);
                            break;
            
                case SOUTH: reorient(ORIENTATION.SOUTH);
                            search(current_vertex, ORIENTATION.SOUTH);
                            break;
            
                case WEST:  reorient(ORIENTATION.WEST);
                            search(current_vertex, ORIENTATION.WEST);
                            break;
            }
        }
    }
    
    private void search(Custom_Vertex current_vertex, ORIENTATION heading) 
    {
        move_forward();

        while (paths_available() == 1)
        {
            //API.setColor(location.x, location.y, 'R');
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

            if (current_vertex.get_cost() + current_cost >= min_cost)
            {
                log("not worth");
                return_to_vertex();
                return;
            }
        }

        if (paths_available() == 0)
        {
            return_to_vertex();
        }
        else //if (paths_available() >= 2)
        {

            //vertex exists
            if (vertices.containsKey(location.toString()))
            {
                if (vertices.get(location.toString()).is_visited())
                {
                    return_to_vertex();
                    return;
                }
                else
                {
                    String existing_vertex = location.toString();
                    current_vertex.add_to(existing_vertex, heading);
                    current_vertex.add_from(existing_vertex, reverse(orientation));
                    
                    if (vertices.get(existing_vertex).update(current_vertex.get_cost() + current_cost, current_vertex, heading, reverse(orientation)));
                    {
                        pq.remove(vertices.get(existing_vertex));
                        pq.add(vertices.get(existing_vertex));
                    }
                }
            }
            else
            {

                Custom_Vertex new_vertex = new Custom_Vertex(location.toString(), current_vertex.get_cost() + current_cost, current_vertex, heading, reverse(orientation));
                vertices.put(location.toString(), new_vertex);
                pq.add(new_vertex);

                current_vertex.add_to(new_vertex.toString(), heading);
                current_vertex.add_from(new_vertex.toString(), reverse(orientation));

                //log(current_vertex.toString() + " can point to " + new_vertex.toString());

                API.setColor(location.x, location.y, 'G');
                API.setText(location.x, location.y, "" + node_number++);
            }
    
            check_for_end(current_vertex);

            return_to_vertex();
        }
    }

    private void check_for_end(Custom_Vertex prev_vertex) 
    {
        if ((location.x == 7 && location.y == 7) || (location.x == 7 && location.y == 8) || (location.x == 8 && location.y == 7) || (location.x == 8 && location.y == 8))
        {
            API.setColor(7, 7, 'B');
            API.setColor(7, 8, 'B');
            API.setColor(8, 7, 'B');
            API.setColor(8, 8, 'B');

            double total_cost = prev_vertex.get_cost() + current_cost;

            if (total_cost < min_cost)
            {
                best_path = new Stack<>();

                Custom_Vertex current_vertex = vertices.get(location.toString());

                while (current_vertex != null)
                {
                    best_path.push(current_vertex.toString());
                    current_vertex = current_vertex.get_predecessor();
                }
            }
        }
    }

    private ORIENTATION reverse(ORIENTATION orientation) 
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

    private void return_to_vertex() 
    {
        API.turnLeft();
        API.turnLeft();

        switch(orientation)
        {
            case NORTH: orientation = ORIENTATION.SOUTH;
                        break;

            case EAST:  orientation = ORIENTATION.WEST;
                        break;

            case SOUTH: orientation = ORIENTATION.NORTH;
                        break;

            case WEST:  orientation = ORIENTATION.EAST;
                        break;
        }

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

    private int paths_available() 
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

    private void move_forward() 
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
    }

    private void move_left() 
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
    }

    private void move_right() 
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
    }

    private void reorient(ORIENTATION target_orientation) 
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

    private void west_to(ORIENTATION target_orientation) 
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

    private void south_to(ORIENTATION target_orientation) 
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

    private void east_to(ORIENTATION target_orientation) 
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

    private void north_to(ORIENTATION target_orientation)
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

    private void enque_paths(Custom_Vertex current_vertex)
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

    private void enque_paths_north(Custom_Vertex current_vertex) 
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

    private void enque_paths_east(Custom_Vertex current_vertex) 
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

    private void enque_paths_south(Custom_Vertex current_vertex) 
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

    private void enque_paths_west(Custom_Vertex current_vertex) 
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

    private void log(String text) 
    {
        System.err.println(text);
    }

    private class Location
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

        public String toString()
        {
            return "(" + x + ", " + y + ")";
        }

        public void print()
        {
            log("(" + x + ", " + y + ")");
        }
    }
}
