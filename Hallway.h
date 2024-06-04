#ifndef HALLWAY_H
#define HALLWAY_H

#include "Custom_Vertex.h"
#include "Main.h" // Make sure to include the file with ORIENTATION definition

typedef struct {
    char* hallway_coor;
    Custom_Vertex* vertex_a;
    int cost_a;
    ORIENTATION to_a;
    ORIENTATION from_a;
    Custom_Vertex* vertex_b;
    int cost_b;
    ORIENTATION to_b;
    ORIENTATION from_b;
    bool visited;
} Hallway;

Hallway* createHallway(char* hallway_coor, Custom_Vertex* vertex_a, int cost_a, ORIENTATION to_a, ORIENTATION from_a);
Custom_Vertex* get_a(Hallway* hallway);
int get_cost_a(Hallway* hallway);
ORIENTATION get_to_a(Hallway* hallway);
ORIENTATION get_from_a(Hallway* hallway);
void add_vertex_b(Hallway* hallway, Custom_Vertex* vertex_b, int cost_b, ORIENTATION to_b, ORIENTATION from_b);
Custom_Vertex* get_b(Hallway* hallway);
int get_cost_b(Hallway* hallway);
ORIENTATION get_to_b(Hallway* hallway);
ORIENTATION get_from_b(Hallway* hallway);
void visitHallway(Hallway* hallway);
bool isHallwayVisited(Hallway* hallway);
char* toString(Hallway* hallway);
void destroyHallway(Hallway* hallway);

#endif /* HALLWAY_H */
