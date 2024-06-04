#ifndef WIPDS4_H
#define WIPDS4_H

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>
#include "Main.h"
#include "Hallway.h"
#include "Location.h"
#include "Stack.h"
#include "HashMap.h"
#include "ArrayList.h"
#include "PriorityQueue.h"
#include "Custom_Vertex.h"
#include "Cost.h"
#include "API.h"

// Function declarations
void initGlobals();
int WIPDS4main();
void move_mouse();
void enque_paths(Custom_Vertex *current_vertex);
void enque_paths_north(Custom_Vertex *current_vertex);
void enque_paths_east(Custom_Vertex *current_vertex);
void enque_paths_south(Custom_Vertex *current_vertex);
void enque_paths_west(Custom_Vertex *current_vertex);
void search_paths(Custom_Vertex *current_vertex);
void point_to_point( char* source, char* destination);
void reorient(ORIENTATION target_orientation);
void move_to_hallway(char *destination);
void update_hallways(Location* location, HashMap* vertices, int* edges);
void head_to(Custom_Vertex* current_vertex, Custom_Vertex* vertex_to_visit, int color);
void move_towards_heading(ORIENTATION heading);
void find_next_vertex(void);
Custom_Vertex* first_vertex(void);
void return_to_first_vertex(void);
void solution_move(ORIENTATION heading);
void solution_next_vertex(void);
ORIENTATION reverse(ORIENTATION orientation);
void print_orientation(void);
void djikstras_solution();
Stack* djikstras_best_path_between(Custom_Vertex* source_vertex, Custom_Vertex* destination_vertex, Cost* path_cost, int color);

#endif // WIPDS4_H