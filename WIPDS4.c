#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include "Location.h"
#include "Stack.h"
#include "HashMap.h"
#include "ArrayList.h"
#include "PriorityQueue.h"
#include "Custom_Vertex.h"
#include "Cost.h"
#include "API.h"

typedef enum {
    NORTH, EAST, SOUTH, WEST
} ORIENTATION;

// Global variables
ORIENTATION orientation;
Location *location;
int current_cost;
int optimal_cost;
Stack *best_path;
HashMap *vertices;
int vertex_id;
HashMap *ids;
HashMap *edges;
HashMap *hallways;
HashMap *point_to_point_hallways;
PriorityQueue *pq;

// Function to initialize global variables
void initGlobals() {
    orientation = NORTH;
    location = createLocation();
    current_cost = 0;
    optimal_cost = INT_MAX;
    best_path = createStack();
    vertices = createHashMap();
    vertex_id = 1;
    ids = createHashMap();
    edges = createHashMap();
    hallways = createHashMap();
    point_to_point_hallways = createHashMap();
    pq = createPriorityQueue();
}

// Main function
int main() {
    initGlobals();

    // Setting colors
    API_setColor(7, 7, 'C');
    API_setColor(8, 7, 'C');
    API_setColor(7, 8, 'C');
    API_setColor(8, 8, 'C');

    // Initializing current vertex
    Custom_Vertex *current_vertex = first_vertex();

    bool done = false;

    while (!done && !isEmpty(pq)) // Assuming pq is the priority queue declared globally
    {
        Custom_Vertex *vertex_to_visit = poll(pq); // Assuming poll function retrieves and removes the head of the priority queue

        // Logging
        printf("Traveling from %s to %s\n", get(ids, toString(current_vertex)), get(ids, toString(vertex_to_visit)));

        // Head to vertex_to_visit
        head_to(current_vertex, vertex_to_visit, true);

        // Visit the vertex
        visit(vertex_to_visit);

        // Set color
        setColor(location->x, location->y, 'G'); // Assuming location is a pointer to a Location struct

        // Search paths
        search_paths(vertex_to_visit);

        // Update current_vertex
        current_vertex = vertex_to_visit;
    }

    // Perform Dijkstra's solution
    dijkstras_solution();

    // Logging
    printf("Done!\n");

    // Move mouse
    move_mouse();

    return 0;
}

void move_mouse() 
{
    API_ackReset();
    location->x = 0;
    location->y = 0;
    orientation = NORTH;

    // Logging
    log("Now moving between random points. Working on reading user input for this, soon.");
    API_clearAllColor();

    // Generating random destinations
    ArrayList *destinations = newArrayList();
    addElement(destinations, "(11, 4)");
    addElement(destinations, "(12, 10)");
    addElement(destinations, "(2, 15)");
    addElement(destinations, "(4, 6)");

    // Generating random destinations
    for (int i = 0; i < 3; i++)
    {
        char destination[15]; // Assuming maximum destination string length is 15 characters
        sprintf(destination, "(%d, %d)", rand() % 16, rand() % 16); // Generating random coordinates
        addElement(destinations, destination);

        // Moving to destination
        point_to_point(toString(location), destination);
    }
}

// Define the parseCoordinates function
int* parseCoordinates(const char* hallway) {
    static int coordinates[2]; // Assuming coordinates are (x, y)
    sscanf(hallway, "(%d, %d)", &coordinates[0], &coordinates[1]);
    return coordinates;
}

void highlight_hallway(Custom_Vertex* current, const char* pathway, HashMap* point_to_point_hallways) {
    printf("Highlighting to %s\n", current->vertex_coor);

    Stack* point_to_points = createStack();

    // Traverse the path and push the vertices to the stack
    while (current->predecessor != NULL) {
        char* highlight = malloc(strlen(current->vertex_coor) + strlen(current->predecessor->vertex_coor) + 2);
        sprintf(highlight, "%s %s", current->vertex_coor, current->predecessor->vertex_coor);
        push(point_to_points, highlight);
        printf("Added highlight from %s to %s\n", current->vertex_coor, current->predecessor->vertex_coor);
        current = current->predecessor;
    }

    // Push the pathway to the stack
    push(point_to_points, pathway);

    // Iterate through each highlight and color the hallways
    while (!isEmpty(point_to_points)) {
        char* hallways = (char*)top(point_to_points);
        pop(point_to_points);
        ArrayList* hallways_list = get(point_to_point_hallways, hallways);
        if (hallways_list != NULL) {
            for (int i = 0; i < getSize(hallways_list); i++) {
                char* hallway = (char*)getElement(hallways_list, i);
                int* hallway_coor = parseCoordinates(hallway);
                // Assuming API.setColor function is defined elsewhere
                // and properly handles coloring
                API.setColor(hallway_coor[0], hallway_coor[1], 'R');
            }
        }
        // Free dynamically allocated memory
        free(hallways);
    }

    // Cleanup
    destroyStack(point_to_points);
}

void point_to_point(const char* source, const char* destination, HashMap* point_to_point_hallways, HashMap* vertices, HashMap* hallways) {
    printf("Traveling from %s to %s\n", source, destination);

    int* source_coor = parseCoordinates(source);
    int* destination_coor = parseCoordinates(destination);

    // Assuming API.setColor and API.clearColor functions are defined elsewhere

    API.setColor(source_coor[0], source_coor[1], 'o');
    API.setColor(destination_coor[0], destination_coor[1], 'o');

    // Both source and destination are vertices
    if (containsKey(vertices, source) && containsKey(vertices, destination)) {
        Custom_Vertex* source_vertex = get(vertices, source);
        Custom_Vertex* destination_vertex = get(vertices, destination);
        head_to(source_vertex, destination_vertex, false);
    }
    // Destination is not a vertex
    else if (containsKey(vertices, source) && !containsKey(vertices, destination)) {
        Custom_Vertex* source_vertex = get(vertices, source);
        Hallway* hallway = get(hallways, destination);
        Custom_Vertex* vertex_a = get_a(hallway);
        Custom_Vertex* vertex_b = get_b(hallway);
        if (vertex_b == NULL) {
            head_to(source_vertex, vertex_a, false);
            reorient(get_from_a(hallway));
            move_to_hallway(destination);
        } else {
            Cost* cost_a = createCost();
            djikstras_best_path_between(source_vertex, vertex_a, cost_a, false);
            add_cost(cost_a, get_cost_a(hallway));
            Cost* cost_b = createCost();
            djikstras_best_path_between(source_vertex, vertex_b, cost_b, false);
            add_cost(cost_b, get_cost_b(hallway));
            if (get_cost(cost_a) <= get_cost(cost_b)) {
                head_to(source_vertex, vertex_a, false);
                reorient(get_from_a(hallway));
                move_to_hallway(destination);
            } else {
                head_to(source_vertex, vertex_b, false);
                reorient(get_from_b(hallway));
                move_to_hallway(destination);
            }
            destroyCost(cost_a);
            destroyCost(cost_b);
        }
    }
    // Source is not a vertex
    else if (!containsKey(vertices, source) && containsKey(vertices, destination)) {
        Custom_Vertex* destination_vertex = get(vertices, destination);
        Hallway* hallway = get(hallways, source);
        Custom_Vertex* vertex_a = get_a(hallway);
        Custom_Vertex* vertex_b = get_b(hallway);
        if (vertex_b == NULL) {
            reorient(get_to_a(hallway));
            find_next_vertex();
            head_to(vertex_a, destination_vertex, false);
        } else {
            Cost* cost_a = createCost();
            djikstras_best_path_between(vertex_a, destination_vertex, cost_a, false);
            add_cost(cost_a, get_cost_a(hallway));
            Cost* cost_b = createCost();
            djikstras_best_path_between(vertex_b, destination_vertex, cost_b, false);
            add_cost(cost_b, get_cost_b(hallway));
            if (get_cost(cost_a) <= get_cost(cost_b)) {
                reorient(get_to_a(hallway));
                find_next_vertex();
                head_to(vertex_a, destination_vertex, false);
            } else {
                reorient(get_to_b(hallway));
                find_next_vertex();
                head_to(vertex_b, destination_vertex, false);
            }
            destroyCost(cost_a);
            destroyCost(cost_b);
        }
    }
    // Both source and destination are hallways
    else {
        Hallway* source_hallway = get(hallways, source);
        Custom_Vertex* vertex_sa = get_a(source_hallway);
        Custom_Vertex* vertex_sb = get_b(source_hallway);
        Hallway* destination_hallway = get(hallways, destination);
        Custom_Vertex* vertex_da = get_a(destination_hallway);
        Custom_Vertex* vertex_db = get_b(destination_hallway);
        ArrayList* costs = createArrayList();

        // Calculate costs for different paths
        Cost* cost_aa = createCost();
        djikstras_best_path_between(vertex_sa, vertex_da, cost_aa, false);
        add_cost(cost_aa, get_cost_a(source_hallway));
        add_cost(cost_aa, get_cost_a(destination_hallway));
        addElement(costs, get_cost(cost_aa));
        destroyCost(cost_aa);

        if (vertex_db != NULL) {
            Cost* cost_ab = createCost();
            djikstras_best_path_between(vertex_sa, vertex_db, cost_ab, false);
            add_cost(cost_ab, get_cost_a(source_hallway));
            add_cost(cost_ab, get_cost_b(destination_hallway));
            addElement(costs, get_cost(cost_ab));
            destroyCost(cost_ab);
        } else {
            addElement(costs, Integer_MAX_VALUE);
        }

        if (vertex_sb != NULL) {
            Cost* cost_ba = createCost();
            djikstras_best_path_between(vertex_sb, vertex_da, cost_ba, false);
            add_cost(cost_ba, get_cost_b(source_hallway));
            add_cost(cost_ba, get_cost_a(destination_hallway));
            addElement(costs, get_cost(cost_ba));
            destroyCost(cost_ba);
        } else {
            addElement(costs, Integer_MAX_VALUE);
        }

        if (vertex_sb != NULL && vertex_db != NULL) {
        Cost* cost_bb = createCost();
        djikstras_best_path_between(vertex_sb, vertex_db, cost_bb, false);
        add_cost(cost_bb, get_cost_b(source_hallway));
        add_cost(cost_bb, get_cost_b(destination_hallway));
        addElement(costs, get_cost(cost_bb));
        destroyCost(cost_bb);
    } else {
        addElement(costs, Integer_MAX_VALUE);
    }

    // Find the minimum cost index
    int min_index = 0;
    for (int i = 1; i < getSize(costs); i++) {
        if (getElement(costs, i) < getElement(costs, min_index)) {
            min_index = i;
        }
    }

    switch (min_index) {
        case 0:
            reorient(get_to_a(source_hallway));
            find_next_vertex();
            head_to(vertex_sa, vertex_da, false);
            reorient(get_from_a(destination_hallway));
            move_to_hallway(destination);
            break;
        case 1:
            reorient(get_to_a(source_hallway));
            find_next_vertex();
            head_to(vertex_sa, vertex_db, false);
            reorient(get_from_b(destination_hallway));
            move_to_hallway(destination);
            break;
        case 2:
            reorient(get_to_b(source_hallway));
            find_next_vertex();
            head_to(vertex_sb, vertex_da, false);
            reorient(get_from_a(destination_hallway));
            move_to_hallway(destination);
            break;
        case 3:
            reorient(get_to_b(source_hallway));
            find_next_vertex();
            head_to(vertex_sb, vertex_db, false);
            reorient(get_from_b(destination_hallway));
            move_to_hallway(destination);
            break;
    }

    destroyArrayList(costs);

    API.clearColor(source_coor[0], source_coor[1]);
    API.clearColor(destination_coor[0], destination_coor[1]);
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

    // Add first vertex's neighbors to the pq
    for (String neighbor_name : first_vertex.get_tos().keySet())
    {
        log("Adding " + ids.get(source_vertex.toString()) + "'s neighbor " + ids.get(neighbor_name) + " to the Djikstra's pq");

        // Create new Djikstra's motivated vertex
        Custom_Vertex temp_neighbor = vertices.get(neighbor_name);
        Custom_Vertex djikstra_vertex = new Custom_Vertex(neighbor_name, edges.get(first_vertex.toString() + " " + neighbor_name), first_vertex, temp_neighbor.get_tos());

        // Add vertices to a hashmap set and priority queue
        d_vertices.put(neighbor_name, djikstra_vertex);
        d_pq.add(djikstra_vertex);
    }

    while (!d_pq.isEmpty())
    {
        Custom_Vertex current_vertex = d_pq.poll();
        current_vertex.visit();
        log("Evaluating " + current_vertex.toString() + " by Djikstra's algorithm.");

        if (current_vertex.equals(destination_vertex))
        {
            log("Found the fastest way to " + ids.get(destination_vertex.toString()) + " from " + ids.get(source_vertex.toString()));

            if (color)
            {
                highlight_path(current_vertex, true);
                recolor_path(current_vertex);
            }

            // Optionally: add up costs to return cost for comparison
            if (path_cost != null)
            {
                path_cost.set_cost(current_vertex.get_cost());
            }

            while (!current_vertex.equals(source_vertex))
            {
                // Stack vertices, followed by their predecessors.
                log("Stacking " + current_vertex.toString() + " to path.");
                stack_of_vertex_names.push(current_vertex.toString());

                // Update current vertex
                current_vertex = current_vertex.get_predecessor();
            }

            return stack_of_vertex_names;
        }

        for (String neighbor_name : current_vertex.get_tos().keySet())
        {
            // Neighbor exists in the priority queue
            if (d_vertices.containsKey(neighbor_name))
            {
                Custom_Vertex existing_vertex = d_vertices.get(neighbor_name);

                // If the neighbor has been visited, the neighbor has been handled already, so continue
                if (existing_vertex.is_visited())
                {
                    if (color)
                    {
                        highlight_path(current_vertex, false);
                        recolor_path(current_vertex);
                    }
                    continue;
                }

                // If this existing vertex now has a more optimal cost, due to this new connection, "reset" it in the pq.
                if (existing_vertex.update_cost(current_vertex.get_cost() + edges.get(current_vertex.toString() + " " + neighbor_name), current_vertex))
                {
                    log("Node " + ids.get(existing_vertex.toString()) + " has found a better predecessor, node " + ids.get(current_vertex.toString()));

                    // Update pq
                    d_pq.remove(existing_vertex);
                    d_pq.add(existing_vertex);
                }
                else
                {
                    log("Node " + ids.get(existing_vertex.toString()) + " was refound by node " + ids.get(current_vertex.toString()) + " but at a higher cost, so it was not readded to the pq.");
                }
            }
            else // Neighbor does not exist in the priority queue
            {
                // Create new Djikstra's motivated vertex
                Custom_Vertex temp_neighbor = vertices.get(neighbor_name);
                Custom_Vertex djikstra_vertex = new Custom_Vertex(neighbor_name, current_vertex.get_cost() + edges.get(current_vertex.toString() + " " + neighbor_name), current_vertex, temp_neighbor.get_tos());

                // Add vertices to hashmap and priority queue
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

int find_min_index(ArrayList *list) {
    if (list == NULL || list->size == 0) {
        // Throw an error
        return -1;
    }

    int min = list->array[0];
    int index = 0;

    for (int i = 1; i < list->size; i++) {
        if (list->array[i] < min) {
            min = list->array[i];
            index = i;
        }
    }

    return index;
}

void move_to_hallway(char *destination) {
    move_forward();

    while (paths_available() == 1 && strcmp(locationToString(), destination) != 0) {
        if (!API.wallFront()) {
            move_forward();
        } else if (!API.wallLeft()) {
            move_left();
        } else if (!API.wallRight()) {
            move_right();
        }
    }
}

void highlight_path(Custom_Vertex *current, int found) {
    log("Highlighting to %s", current->toString);
    Stack *point_to_points = createStack();

    while (current->predecessor != NULL) {
        char *highlight = malloc(sizeof(char) * (strlen(current->toString) + strlen(current->predecessor->toString) + 2));
        sprintf(highlight, "%s %s", current->toString, current->predecessor->toString);
        push(point_to_points, highlight);
        log("Added highlight from %s to %s", current->toString, current->predecessor->toString);
        current = current->predecessor;
    }

    while (!isEmpty(point_to_points)) {
        char *hallways = pop(point_to_points);
        for (int i = 0; i < point_to_point_hallways->size; i++) {
            char *hallway = point_to_point_hallways->array[i];
            int *hallway_coor = parseCoordinates(hallway);
            if (found) {
                API.setColor(hallway_coor[0], hallway_coor[1], 'g');
            } else {
                API.setColor(hallway_coor[0], hallway_coor[1], 'R');
            }
            free(hallway_coor);
        }
        free(hallways);
    }

    // Sleep for a short time
    sleep(50);

    if (found) {
        // Sleep for a longer time if the path was found
        sleep(200);
    }
}

void recolor_path(Custom_Vertex *current) {
    log("Recoloring from %s", current->toString);
    Stack *point_to_points = createStack();

    while (current->predecessor != NULL) {
        char *highlight = malloc(sizeof(char) * (strlen(current->toString) + strlen(current->predecessor->toString) + 2));
        sprintf(highlight, "%s %s", current->toString, current->predecessor->toString);
        push(point_to_points, highlight);
        current = current->predecessor;
    }

    while (!isEmpty(point_to_points)) {
        char *hallways = pop(point_to_points);
        for (int i = 0; i < point_to_point_hallways->size; i++) {
            char *hallway = point_to_point_hallways->array[i];
            int *hallway_coor = parseCoordinates(hallway);
            if (vertices->containsKey(hallway) && vertices->get(hallway)->is_visited) {
                API.setColor(hallway_coor[0], hallway_coor[1], 'G');
            } else if (vertices->containsKey(hallway)) {
                API.setColor(hallway_coor[0], hallway_coor[1], 'Y');
            } else {
                API.clearColor(hallway_coor[0], hallway_coor[1]);
            }
            free(hallway_coor);
        }
        free(hallways);
    }
}

void head_to(Custom_Vertex *current_vertex, Custom_Vertex *vertex_to_visit, int color) {
    Stack *stack_of_vertex_names = djikstras_best_path_between(current_vertex, vertex_to_visit, NULL, color);

    while (!equals(current_vertex, vertex_to_visit)) {
        char *current_vertex_id = ids->get(current_vertex->toString);
        char *stack_top = peek(stack_of_vertex_names);
        char *stack_vertex_id = ids->get(stack_top);

        log("\t Moving from %s to %s", current_vertex_id, stack_vertex_id);

        //move
        move_towards_heading(current_vertex->to[stack_top[0] - '0']);

        //update current vertex
        current_vertex = vertices->get(stack_pop(stack_of_vertex_names));
        free(current_vertex_id);
        free(stack_top);
    }
}

void move_towards_heading(ORIENTATION heading) {
    reorient(heading);
    find_next_vertex();
}

void find_next_vertex() {
    move_forward();

    while (paths_available() == 1) {
        if (!API.wallFront()) {
            move_forward();
        } else if (!API.wallLeft()) {
            move_left();
        } else if (!API.wallRight()) {
            move_right();
        }
    }
}

Custom_Vertex *first_vertex() {
    Custom_Vertex *current_vertex = create_Custom_Vertex(vertex_id, location->toString(), current_cost);

    vertices->put(location->toString(), current_vertex);
    ids->put(location->toString(), itoa(vertex_id));

    API->setColor(location->x, location->y, 'G');
    API->setText(location->x, location->y, itoa(vertex_id++));

    search_paths(current_vertex);

    current_vertex->visit();

    return current_vertex;
}


void search_paths(Custom_Vertex *current_vertex) {
    log("Cost of " + ids->get(current_vertex->toString()) + ": " + itoa(current_vertex->get_cost()));

    ORIENTATION original_orientation = orientation;

    enque_paths(current_vertex);

    ORIENTATION *paths = current_vertex->get_paths();
    for (int i = 0; i < current_vertex->get_num_paths(); i++) {
        reorient(original_orientation);
        current_cost = 0;

        switch(paths[i]) {
            case NORTH:
                reorient(ORIENTATION_NORTH);
                log("Reorienting & Searching NORTH");
                search(current_vertex, ORIENTATION_NORTH);
                break;
            case EAST:
                reorient(ORIENTATION_EAST);
                log("Reorienting & Searching EAST");
                search(current_vertex, ORIENTATION_EAST);
                break;
            case SOUTH:
                reorient(ORIENTATION_SOUTH);
                log("Reorienting & Searching SOUTH");
                search(current_vertex, ORIENTATION_SOUTH);
                break;
            case WEST:
                reorient(ORIENTATION_WEST);
                log("Reorienting & Searching WEST");
                search(current_vertex, ORIENTATION_WEST);
                break;
        }
    }
}


void search(Custom_Vertex *current_vertex, ORIENTATION heading) {
    ArrayList *vertex_hallways = createArrayList();
    addElement(vertex_hallways, current_vertex->toString());
    move_forward();

    while (paths_available() == 1) {
        if (!ids_containsKey(location->toString())) {
            API_setText(location->x, location->y, ".");
            if (!hallways_containsKey(location->toString())) {
                Hallway *new_hallway = createHallway(location->toString(), current_vertex, current_cost, reverse(orientation), heading);
                hallways_put(location->toString(), new_hallway);
            }
        }

        addElement(vertex_hallways, location->toString());
        point_to_point_hallways_put(concat(current_vertex->toString(), " ", location->toString()), vertex_hallways);
        point_to_point_hallways_put(concat(location->toString(), " ", current_vertex->toString()), vertex_hallways);

        if (!API_wallFront()) {
            move_forward();
        } else if (!API_wallLeft()) {
            move_left();
        } else if (!API_wallRight()) {
            move_right();
        }

        if (current_vertex->get_cost() + current_cost >= optimal_cost) {
            log("This search exceeds the optimal cost and is no longer worth following.");
            break;
        }
    }

    if (paths_available() == 0) {
        log("Deadend.");
        Hallway *new_hallway = createHallway(location->toString(), current_vertex, current_cost, reverse(orientation), heading);
        hallways_put(location->toString(), new_hallway);
        Hallway *hallway = hallways_get(location->toString());
        hallway_visit(hallway);
        addElement(vertex_hallways, location->toString());
        point_to_point_hallways_put(concat(current_vertex->toString(), " ", location->toString()), vertex_hallways);
        point_to_point_hallways_put(concat(location->toString(), " ", current_vertex->toString()), vertex_hallways);
        return_to_vertex(current_vertex->toString(), NULL);
    } else if (paths_available() >= 2) {
        // vertex exists
        if (vertices_containsKey(location->toString())) {
            // if this vertex has been visited, this vertex knows everything, so return to source vertex
            // else update the vertex --
            if (!vertices_get(location->toString())->is_visited()) {
                Custom_Vertex *existing_vertex = vertices_get(location->toString());
                current_vertex_add_to(existing_vertex, heading);
                existing_vertex_add_to(current_vertex, reverse(orientation));
                edges_put(concat(current_vertex->toString(), " ", existing_vertex->toString()), current_cost);
                edges_put(concat(existing_vertex->toString(), " ", current_vertex->toString()), current_cost);
                if (existing_vertex_update(existing_vertex, current_vertex->get_cost() + current_cost, current_vertex, heading, reverse(orientation))) {
                    log(concat("Node ", ids_get(existing_vertex->toString()), " has found a better predecessor, node ", ids_get(current_vertex->toString()), " with ", current_vertex->get_cost() + current_cost));
                    pq_remove(existing_vertex);
                    pq_add(existing_vertex);
                }
            }
        } else { // vertex does not exist
            Custom_Vertex *new_vertex = createCustom_Vertex(vertex_id, location->toString(), current_vertex->get_cost() + current_cost, current_vertex, heading, reverse(orientation));
            vertices_put(location->toString(), new_vertex);
            ids_put(location->toString(), itoa(vertex_id));
            current_vertex_add_to(new_vertex, reverse(orientation));
            new_vertex_add_to(current_vertex, heading);
            edges_put(concat(current_vertex->toString(), " ", location->toString()), current_cost);
            edges_put(concat(location->toString(), " ", current_vertex->toString()), current_cost);
            API_setColor(location->x, location->y, 'Y');
            API_setText(location->x, location->y, itoa(vertex_id++));
            if (check_for_end(current_vertex)) {
                visit(new_vertex);
            } else {
                pq_add(new_vertex);
            }
        }
        addElement(vertex_hallways, location->toString());
        point_to_point_hallways_put(concat(current_vertex->toString(), " ", location->toString()), vertex_hallways);
        point_to_point_hallways_put(concat(location->toString(), " ", current_vertex->toString()), vertex_hallways);
        log(concat(current_vertex->toString(), " has hallways to ", location->toString(), " now."));
        return_to_vertex(current_vertex->toString(), vertices_get(location->toString()));
    }
}

bool check_for_end(Custom_Vertex *prev_vertex) {
    if ((location->x == 7 && location->y == 7) || (location->x == 7 && location->y == 8) || (location->x == 8 && location->y == 7) || (location->x == 8 && location->y == 8)) {
        API_setColor(location->x, location->y, 'C');

        int total_cost = prev_vertex->get_cost() + current_cost;
        char path[MAX_PATH_LENGTH];
        memset(path, 0, sizeof(path));

        if (total_cost < optimal_cost) {
            best_path = createStack();
            Custom_Vertex *current_vertex = vertices_get(location->toString());

            while (current_vertex != NULL) {
                push(best_path, current_vertex->toString());
                char temp[MAX_ID_LENGTH];
                sprintf(temp, "%s ", ids_get(current_vertex->toString()));
                strcat(path, temp);
                current_vertex = current_vertex->get_predecessor();
            }

            optimal_cost = total_cost;
            log(concat("Found optimal path. Cost: ", itoa(total_cost), "\nPath:", reverseString(path)));
        } else {
            log("Found suboptimal path. Shouldn't happen");
        }

        return true;
    }

    return false;
}

void return_to_vertex(char *prev_vertex, Custom_Vertex *new_vertex) {
    log(concat("Returning to previous vertex, ", ids_get(prev_vertex)));

    ArrayList *vertex_hallways = createArrayList();
    add(vertex_hallways, location->toString());

    // Turn around
    API_turnLeft();
    API_turnLeft();
    orientation = reverse(orientation);
    ORIENTATION from_b = orientation;

    // Edge case worth exploring -- determining the cost of +0/1 for the turn required (or not) to get from a vertex to a hallway. Check Orientation upon arrival vs Orientation of travel to hallway...
    current_cost = 1;
    move_forward();

    while (paths_available() == 1) {
        if (!hallways_get(location->toString())->is_visited && new_vertex != NULL) {
            Hallway *existing_hallway = hallways_get(location->toString());
            add_vertex_b(existing_hallway, new_vertex, current_cost, reverse(orientation), from_b);
            existing_hallway->visit();
            log(concat("Hallway ", location->toString(), " complete."));
        }

        add(vertex_hallways, location->toString());
        if (new_vertex != NULL) {
            add(point_to_point_hallways, concat(new_vertex->toString(), concat(" ", location->toString())));
            add(point_to_point_hallways, concat(location->toString(), concat(" ", new_vertex->toString())));
        }

        if (!API_wallFront()) {
            move_forward();
        } else if (!API_wallLeft()) {
            move_left();
        } else if (!API_wallRight()) {
            move_right();
        }
    }
}

int paths_available() {
    int paths_available = 0;

    if (!API_wallFront()) {
        paths_available++;
    }

    if (!API_wallLeft()) {
        paths_available++;
    }

    if (!API_wallRight()) {
        paths_available++;
    }

    return paths_available;
}

void move_forward() {
    API_moveForward();
    current_cost++;

    switch (orientation) {
        case NORTH:
            location->y++;
            break;

        case EAST:
            location->x++;
            break;

        case SOUTH:
            location->y--;
            break;

        case WEST:
            location->x--;
            break;
    }

    location_print(location);
}

void move_left() {
    API_turnLeft();
    API_moveForward();
    current_cost += 2;

    switch (orientation) {
        case NORTH:
            orientation = ORIENTATION_WEST;
            location->x--;
            break;

        case EAST:
            orientation = ORIENTATION_NORTH;
            location->y++;
            break;

        case SOUTH:
            orientation = ORIENTATION_EAST;
            location->x++;
            break;

        case WEST:
            orientation = ORIENTATION_SOUTH;
            location->y--;
            break;
    }

    location_print(location);
}

void move_right() {
    API_turnRight();
    API_moveForward();
    current_cost += 2;

    switch (orientation) {
        case NORTH:
            orientation = ORIENTATION_EAST;
            location->x++;
            break;

        case EAST:
            orientation = ORIENTATION_SOUTH;
            location->y--;
            break;

        case SOUTH:
            orientation = ORIENTATION_WEST;
            location->x--;
            break;

        case WEST:
            orientation = ORIENTATION_NORTH;
            location->y++;
            break;
    }

    location_print(location);
}

void reorient(ORIENTATION target_orientation) {
    switch (orientation) {
        case NORTH:
            north_to(target_orientation);
            break;

        case EAST:
            east_to(target_orientation);
            break;

        case SOUTH:
            south_to(target_orientation);
            break;

        case WEST:
            west_to(target_orientation);
            break;
    }
}

void west_to(ORIENTATION target_orientation) {
    switch (target_orientation) {
        case NORTH:
            API_turnRight();
            orientation = ORIENTATION_NORTH;
            current_cost++;
            break;

        case EAST:
            API_turnRight();
            API_turnRight();
            current_cost += 2;
            orientation = ORIENTATION_EAST;
            break;

        case SOUTH:
            API_turnLeft();
            orientation = ORIENTATION_SOUTH;
            current_cost++;
            break;

        case WEST:
            break;
    }
}

void south_to(ORIENTATION target_orientation) {
    switch (target_orientation) {
        case NORTH:
            API_turnLeft();
            API_turnLeft();
            current_cost += 2;
            orientation = ORIENTATION_NORTH;
            break;

        case EAST:
            API_turnLeft();
            orientation = ORIENTATION_EAST;
            current_cost++;
            break;

        case SOUTH:
            break;

        case WEST:
            API_turnRight();
            orientation = ORIENTATION_WEST;
            current_cost++;
            break;
    }
}

void east_to(ORIENTATION target_orientation) {
    switch (target_orientation) {
        case NORTH:
            API_turnLeft();
            orientation = ORIENTATION_NORTH;
            current_cost++;
            break;

        case EAST:
            break;

        case SOUTH:
            API_turnRight();
            orientation = ORIENTATION_SOUTH;
            current_cost++;
            break;

        case WEST:
            API_turnLeft();
            API_turnLeft();
            current_cost += 2;
            orientation = ORIENTATION_WEST;
            break;
    }
}

void north_to(ORIENTATION target_orientation) {
    switch (target_orientation) {
        case NORTH:
            break;

        case EAST:
            API_turnRight();
            current_cost++;
            orientation = ORIENTATION_EAST;
            break;

        case SOUTH:
            API_turnRight();
            API_turnRight();
            orientation = ORIENTATION_SOUTH;
            current_cost += 2;
            break;

        case WEST:
            API_turnLeft();
            orientation = ORIENTATION_WEST;
            current_cost++;
            break;
    }
}

void enque_paths(Custom_Vertex *current_vertex) {
    switch (orientation) {
        case NORTH:
            enque_paths_north(current_vertex);
            break;

        case EAST:
            enque_paths_east(current_vertex);
            break;

        case SOUTH:
            enque_paths_south(current_vertex);
            break;

        case WEST:
            enque_paths_west(current_vertex);
            break;
    }
}

void enque_paths_north(Custom_Vertex *current_vertex) {
    if (!API_wallLeft()) {
        add_path(current_vertex, ORIENTATION_WEST);
    }
    if (!API_wallFront()) {
        add_path(current_vertex, ORIENTATION_NORTH);
    }
    if (!API_wallRight()) {
        add_path(current_vertex, ORIENTATION_EAST);
    }
}

void enque_paths_east(Custom_Vertex *current_vertex) {
    if (!API_wallLeft()) {
        add_path(current_vertex, ORIENTATION_NORTH);
    }
    if (!API_wallFront()) {
        add_path(current_vertex, ORIENTATION_EAST);
    }
    if (!API_wallRight()) {
        add_path(current_vertex, ORIENTATION_SOUTH);
    }
}

void enque_paths_south(Custom_Vertex *current_vertex) {
    if (!API_wallLeft()) {
        add_path(current_vertex, ORIENTATION_EAST);
    }
    if (!API_wallFront()) {
        add_path(current_vertex, ORIENTATION_SOUTH);
    }
    if (!API_wallRight()) {
        add_path(current_vertex, ORIENTATION_WEST);
    }
}

void enque_paths_west(Custom_Vertex *current_vertex) {
    if (!API_wallLeft()) {
        add_path(current_vertex, ORIENTATION_SOUTH);
    }
    if (!API_wallFront()) {
        add_path(current_vertex, ORIENTATION_WEST);
    }
    if (!API_wallRight()) {
        add_path(current_vertex, ORIENTATION_NORTH);
    }
}

void djikstras_solution() 
{
    log("Djikstra's solution marked in red.");
    API_ackReset();
    location.x = 0;
    location.y = 0;
    orientation = ORIENTATION_NORTH;

    best_path.pop();
    //return_to_first_vertex();

    while (!best_path.isEmpty())
    {
        solution_move(vertices.get(location.toString())->get_to(best_path.pop()));
    }
}

void return_to_first_vertex() 
{
    while (paths_available() == 1)
    {
        API_setColor(location.x, location.y, 'R');
        if (!API_wallFront())
        {
            move_forward();
        }
        else if (!API_wallLeft())
        {
            move_left();
        }
        else if (!API_wallRight())
        {
            move_right();
        }
    }
}

void solution_move(ORIENTATION heading) 
{
    reorient(heading);
    solution_next_vertex();
}

void solution_next_vertex() 
{
    API_setColor(location.x, location.y, 'R');
    move_forward();

    while (paths_available() == 1)
    {
        API_setColor(location.x, location.y, 'R');
        if (!API_wallFront())
        {
            move_forward();
        }
        else if (!API_wallLeft())
        {
            move_left();
        }
        else if (!API_wallRight())
        {
            move_right();
        }
    }
}

ORIENTATION reverse(ORIENTATION orientation) 
{
    switch (orientation)
    {
        case ORIENTATION_NORTH: return ORIENTATION_SOUTH;

        case ORIENTATION_EAST: return ORIENTATION_WEST;

        case ORIENTATION_SOUTH: return ORIENTATION_NORTH;

        case ORIENTATION_WEST: return ORIENTATION_EAST;

        default:
            return null;
    }
}

void print_orientation()
{
    switch (orientation)
    {
        case ORIENTATION_NORTH: log("Orientation: NORTH");
                                break;

        case ORIENTATION_EAST: log("Orientation: EAST");
                                break;

        case ORIENTATION_SOUTH: log("Orientation: SOUTH");
                                break;

        case ORIENTATION_WEST: log("Orientation: WEST");
                                break;
    }
}


void log(char* text) {
    fprintf(stderr, "%s\n", text);
    fflush(stderr);
}