#ifndef LINKEDLIST_H
#define LINKEDLIST_H

#include "WIPDS4.h"

typedef struct Node {
    ORIENTATION data;
    struct Node* next;
} Node;

typedef struct {
    Node* head;
    int size;
} LinkedList;

LinkedList* createLinkedList();
void append(LinkedList* list, ORIENTATION data);
void clearLinkedList(LinkedList* list);

#endif /* LINKEDLIST_H */
