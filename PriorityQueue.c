#include "PriorityQueue.h"
#include <stdio.h>
#include <stdlib.h>

PriorityQueue* createPriorityQueue(int capacity) {
    PriorityQueue* pq = (PriorityQueue*)malloc(sizeof(PriorityQueue));
    if (pq == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    pq->capacity = capacity;
    pq->size = 0;
    pq->heap = (Custom_Vertex**)malloc((capacity + 1) * sizeof(Custom_Vertex*));
    if (pq->heap == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    return pq;
}

void swap(Custom_Vertex** a, Custom_Vertex** b) {
    Custom_Vertex* temp = *a;
    *a = *b;
    *b = temp;
}

void bubbleUp(PriorityQueue* pq, int index) {
    while (index > 1 && pq->heap[index]->cost < pq->heap[index / 2]->cost) {
        swap(&pq->heap[index], &pq->heap[index / 2]);
        index /= 2;
    }
}

void bubbleDown(PriorityQueue* pq, int index) {
    int minIndex = index;
    int leftChild = 2 * index;
    int rightChild = 2 * index + 1;
    if (leftChild <= pq->size && pq->heap[leftChild]->cost < pq->heap[minIndex]->cost) {
        minIndex = leftChild;
    }
    if (rightChild <= pq->size && pq->heap[rightChild]->cost < pq->heap[minIndex]->cost) {
        minIndex = rightChild;
    }
    if (minIndex != index) {
        swap(&pq->heap[index], &pq->heap[minIndex]);
        bubbleDown(pq, minIndex);
    }
}

void enqueue(PriorityQueue* pq, Custom_Vertex* vertex) {
    if (pq->size == pq->capacity) {
        fprintf(stderr, "Error: PriorityQueue is full\n");
        exit(EXIT_FAILURE);
    }
    pq->size++;
    pq->heap[pq->size] = vertex;
    bubbleUp(pq, pq->size);
}

Custom_Vertex* dequeue(PriorityQueue* pq) {
    if (isEmpty(pq)) {
        fprintf(stderr, "Error: PriorityQueue is empty\n");
        exit(EXIT_FAILURE);
    }
    Custom_Vertex* front = pq->heap[1];
    pq->heap[1] = pq->heap[pq->size];
    pq->size--;
    bubbleDown(pq, 1);
    return front;
}

Custom_Vertex* peek(PriorityQueue* pq) {
    if (isEmpty(pq)) {
        fprintf(stderr, "Error: PriorityQueue is empty\n");
        exit(EXIT_FAILURE);
    }
    return pq->heap[1];
}

int isEmpty(PriorityQueue* pq) {
    return pq->size == 0;
}

void clearPriorityQueue(PriorityQueue* pq) {
    free(pq->heap);
    free(pq);
}
