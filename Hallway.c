#include "Hallway.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

Hallway* createHallway(char* hallway_coor, Custom_Vertex* vertex_a, int cost_a, ORIENTATION to_a, ORIENTATION from_a) {
    Hallway* newHallway = (Hallway*)malloc(sizeof(Hallway));
    if (newHallway == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    newHallway->hallway_coor = strdup(hallway_coor);
    newHallway->vertex_a = vertex_a;
    newHallway->cost_a = cost_a;
    newHallway->to_a = to_a;
    newHallway->from_a = from_a;
    newHallway->vertex_b = NULL;
    newHallway->cost_b = INT_MAX;
    newHallway->to_b = NULL;
    newHallway->from_b = NULL;
    newHallway->visited = false;
    return newHallway;
}

Custom_Vertex* get_a(Hallway* hallway) {
    return hallway->vertex_a;
}

int get_cost_a(Hallway* hallway) {
    return hallway->cost_a;
}

ORIENTATION get_to_a(Hallway* hallway) {
    return hallway->to_a;
}

ORIENTATION get_from_a(Hallway* hallway) {
    return hallway->from_a;
}

void add_vertex_b(Hallway* hallway, Custom_Vertex* vertex_b, int cost_b, ORIENTATION to_b, ORIENTATION from_b) {
    hallway->vertex_b = vertex_b;
    hallway->cost_b = cost_b;
    hallway->to_b = to_b;
    hallway->from_b = from_b;
}

Custom_Vertex* get_b(Hallway* hallway) {
    return hallway->vertex_b;
}

int get_cost_b(Hallway* hallway) {
    return hallway->cost_b;
}

ORIENTATION get_to_b(Hallway* hallway) {
    return hallway->to_b;
}

ORIENTATION get_from_b(Hallway* hallway) {
    return hallway->from_b;
}

void visit(Hallway* hallway) {
    hallway->visited = true;
}

bool is_visited(Hallway* hallway) {
    return hallway->visited;
}

char* toString(Hallway* hallway) {
    return hallway->hallway_coor;
}

void destroyHallway(Hallway* hallway) {
    free(hallway->hallway_coor);
    free(hallway);
}
