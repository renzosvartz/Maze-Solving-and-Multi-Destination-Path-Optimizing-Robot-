#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Main.h"
#include "Custom_Vertex.h"
#include "LinkedList.h"
#include "HashMap.h"
#include "ArrayList.h"


// Rest of the implementation
ORIENTATION reverse_orientation(ORIENTATION orientation) {
    switch (orientation) {
        case NORTH: return SOUTH;
        case EAST: return WEST;
        case SOUTH: return NORTH;
        case WEST: return EAST;
        default: return -1;  // Invalid orientation
    }
}

void add_hallways(Custom_Vertex* vertex,  char* destination, ArrayList* hallways) {
    hash_map_put(vertex->list_of_hallways, destination, hallways);
}

ArrayList* get_hallways( Custom_Vertex* vertex,  char* destination) {
    return (ArrayList*)hash_map_get(vertex->list_of_hallways, destination);
}

void add_path(Custom_Vertex* vertex, ORIENTATION path) {
    linked_list_add(vertex->paths_list, path);
}

LinkedList* get_paths( Custom_Vertex* vertex) {
    return vertex->paths_list;
}

 char* get_coor( Custom_Vertex* vertex) {
    return vertex->vertex_coor;
}

void visit(Custom_Vertex* vertex) {
    vertex->visited = true;
}

void unvisit(Custom_Vertex* vertex) {
    vertex->visited = false;
}

bool is_visited( Custom_Vertex* vertex) {
    return vertex->visited;
}

void set_predecessor(Custom_Vertex* vertex, Custom_Vertex* predecessor) {
    vertex->predecessor = predecessor;
}

Custom_Vertex* get_predecessor( Custom_Vertex* vertex) {
    return vertex->predecessor;
}

bool has_predecessor( Custom_Vertex* vertex) {
    return vertex->predecessor != NULL;
}

void set_cost(Custom_Vertex* vertex, int cost) {
    vertex->cost = cost;
}

int get_cost( Custom_Vertex* vertex) {
    return vertex->cost;
}

bool equals( Custom_Vertex* vertex1,  Custom_Vertex* vertex2) {
    if (vertex1 == NULL || vertex2 == NULL) {
        return false;
    }
    return strcmp(vertex1->vertex_coor, vertex2->vertex_coor) == 0;
}

bool update(Custom_Vertex* vertex, int current_cost, Custom_Vertex* new_predecessor, ORIENTATION to) {
    hash_map_put(vertex->list_of_tos, new_predecessor->vertex_coor, &to);

    if (current_cost < vertex->cost) {
        vertex->cost = current_cost;
        vertex->predecessor = new_predecessor;
        return true;
    } else {
        return false;
    }
}

bool update_cost(Custom_Vertex* vertex, int current_cost, Custom_Vertex* current_predecessor) {
    if (current_cost < vertex->cost) {
        vertex->cost = current_cost;
        vertex->predecessor = current_predecessor;
        return true;
    } else {
        return false;
    }
}

char* cvToString(Custom_Vertex* vertex) {
    return vertex->vertex_coor;
}

ORIENTATION get_to( Custom_Vertex* vertex,  char* target_vertex) {
    return *(ORIENTATION*)hash_map_get(vertex->list_of_tos, target_vertex);
}

HashMap* get_tos( Custom_Vertex* vertex) {
    return vertex->list_of_tos;
}

void add_to(Custom_Vertex* vertex,  char* to, ORIENTATION heading) {
    hash_map_put(vertex->list_of_tos, to, &heading);
}

void free_custom_vertex(Custom_Vertex* vertex) {
    free(vertex->vertex_coor);
    free_linked_list(vertex->paths_list);
    free_hash_map(vertex->list_of_tos);
    free_hash_map(vertex->list_of_hallways);
    free(vertex);
}