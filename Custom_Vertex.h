#ifndef CUSTOM_VERTEX_H
#define CUSTOM_VERTEX_H

#include "LinkedList.h"
#include "HashMap.h"
#include "ArrayList.h"
#include <stdbool.h>

typedef struct Custom_Vertex {
    int id;
    char* vertex_coor;
    LinkedList* paths_list;
    struct Custom_Vertex* predecessor;
    HashMap* list_of_tos;
    HashMap* list_of_hallways;
    bool visited;
    int cost;
} Custom_Vertex;

Custom_Vertex* create_custom_vertex(int id,  char* vertex_coor, int cost);
Custom_Vertex* create_custom_vertex_with_predecessor(int id,  char* vertex_coor, int cost, Custom_Vertex* predecessor, ORIENTATION from, ORIENTATION to);
Custom_Vertex* create_djikstra_custom_vertex(int id, int cost, Custom_Vertex* predecessor, HashMap* list_of_tos);
void add_path(Custom_Vertex* vertex, ORIENTATION path);
LinkedList* get_paths( Custom_Vertex* vertex);
 char* get_coor( Custom_Vertex* vertex);
void visit(Custom_Vertex* vertex);
void unvisit(Custom_Vertex* vertex);
bool is_visited( Custom_Vertex* vertex);
void set_predecessor(Custom_Vertex* vertex, Custom_Vertex* predecessor);
Custom_Vertex* get_predecessor( Custom_Vertex* vertex);
bool has_predecessor( Custom_Vertex* vertex);
void set_cost(Custom_Vertex* vertex, int cost);
int get_cost( Custom_Vertex* vertex);
bool equals( Custom_Vertex* vertex1,  Custom_Vertex* vertex2);
bool update(Custom_Vertex* vertex, int current_cost, Custom_Vertex* new_predecessor, ORIENTATION to);
bool update_cost(Custom_Vertex* vertex, int current_cost, Custom_Vertex* current_predecessor);
char* cvToString(Custom_Vertex* vertex);
ORIENTATION reverse_orientation(ORIENTATION orientation);
void add_hallways(Custom_Vertex* vertex,  char* destination, ArrayList* hallways);
ArrayList* get_hallways( Custom_Vertex* vertex,  char* destination);
ORIENTATION get_to( Custom_Vertex* vertex,  char* target_vertex);
HashMap* get_tos( Custom_Vertex* vertex);
void add_to(Custom_Vertex* vertex,  char* to, ORIENTATION heading);

void free_custom_vertex(Custom_Vertex* vertex);

#endif // CUSTOM_VERTEX_H
