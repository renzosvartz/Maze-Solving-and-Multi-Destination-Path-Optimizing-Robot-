#ifndef PRIORITYQUEUE_H
#define PRIORITYQUEUE_H

#include "Custom_Vertex.h"

typedef struct {
    Custom_Vertex** heap;
    int capacity;
    int size;
} PriorityQueue;

PriorityQueue* createPriorityQueue(int capacity);
void enqueue(PriorityQueue* pq, Custom_Vertex* vertex);
Custom_Vertex* dequeue(PriorityQueue* pq);
Custom_Vertex* peekPQ(PriorityQueue* pq);
int isEmpty(PriorityQueue* pq);
void clearPriorityQueue(PriorityQueue* pq);

#endif /* PRIORITYQUEUE_H */
